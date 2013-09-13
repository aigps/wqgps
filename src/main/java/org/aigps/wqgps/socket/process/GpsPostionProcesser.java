package org.aigps.wqgps.socket.process;

import java.util.Map;

import org.aigps.wqgps.common.cache.CmdStateCache;
import org.aigps.wqgps.common.cache.DataCache;
import org.aigps.wqgps.common.dao.PublicService;
import org.aigps.wqgps.common.entity.DcGpsReal;
import org.aigps.wqgps.common.entity.WqCompanyInfo;
import org.aigps.wqgps.common.entity.WqPlanLocateDetail;
import org.aigps.wqgps.common.entity.WqStaffInfo;
import org.aigps.wqgps.common.enums.LocateTypeEnum;
import org.aigps.wqgps.common.util.AppUtil;
import org.aigps.wqgps.common.util.DateUtil;
import org.aigps.wqgps.module.sysmanager.service.SysManagerService;
import org.aigps.wqgps.module.webservice.WqServiceImpl;
import org.aigps.wqgps.timing.CachePlanLocateMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.ym.model.YmAccessMsg;

/**
 * ��λ��Ϣ������
 * @author admins
 *
 */

public class GpsPostionProcesser implements MsgProcesser {
	protected static final Log log = LogFactory.getLog(GpsPostionProcesser.class);
	
	/**
	 * ��Ӧ��λ��Ϣ��������
	 */
	public static final String PROCESS_TYPE = "GPS";
	
	public void process(YmAccessMsg accessMsg) {
		if(accessMsg == null || StringUtils.isBlank(accessMsg.getData())){
			return;
		}
		try{
			String tmnCode = accessMsg.getDeviceCode();//�ֻ��ż��ն˺�
			String staffId = DataCache.phoneStaffIdMap.get(tmnCode);
			if(StringUtils.isBlank(staffId)){
				return;
			}
			WqStaffInfo staff = DataCache.staffMap.get(staffId);
			if(staff != null){
				//�ж�MSID����һ�µĻ���Ҫ���³����µ�
				scanStaffMsid(staff,accessMsg.getDeviceParam());
			}
			
			//��λ��Ϣ
			DcGpsReal dcGpsReal = DcGpsReal.convertDataGpsReal(tmnCode,staffId, accessMsg.getData());
			
			//2��������������Ͷ�λ��Ϣ����û���յ�����ɹ��Ļظ���Ϣ����Ҳ�㼤��ɹ�
			if(LocateTypeEnum.ACTIVE_LCS_POS.getValue().equals(dcGpsReal.getGpsType()) && CmdStateCache.activeTmnNumMap.containsKey(tmnCode)){
				CmdStateCache.activeTmnNumMap.remove(tmnCode);
				SysManagerService service = (SysManagerService) AppUtil.getBean("sysManagerService");
				service.updateActiveState(tmnCode,"2");//�ɹ�
			}
	
			//���������Ч�Ķ�λ��Ϣ
			if(!isValidLocation(dcGpsReal)){
				return;
			}
			if(staff != null){
				//���涨λ��Ϣ��������
				saveDcGpsRealToCache(staff,dcGpsReal);
				//ɨ��ƻ���λ
				scanPlanPocateTask(dcGpsReal);
			}
			
			//����û�лظ���http������
			WqServiceImpl.sendLocationToHttp(staffId, dcGpsReal);
			
		}catch(Exception e){
			log.error(e.getMessage(), e);
		}
	}
	
	private boolean isValidLocation(DcGpsReal dcGpsReal){
		if(dcGpsReal.getLat()!=null && dcGpsReal.getLat().intValue()>0){
			return true;
		}
		if(dcGpsReal.getLogit()!=null && dcGpsReal.getLogit().intValue()>0){
			return true;
		}
		return false;
	}
	
	/**
	 * ���涨λ��Ϣ������
	 * @param newGisModel
	 */
	private void saveDcGpsRealToCache(WqStaffInfo staff,DcGpsReal newGisModel){
		WqCompanyInfo company = DataCache.companyInfoMap.get(staff.getCompanyId());
		if(company==null){
			return;
		}
		//�����˾��ʹ����Ч��λ���ö�λ������Ч�ģ�ֱ�Ӷ���
		if(company.getUseInvalidLoc()!=true && newGisModel.getIsValidGps()!=true){
			return;
		}
		try{
			DcGpsReal oldGisModel = DataCache.staffPostionMap.get(newGisModel.getTmnAlias());
			if(oldGisModel!=null){//������ģ��
				String oldReportTime = oldGisModel.getReportTime();
				String newReportTime = newGisModel.getReportTime();
				if(StringUtils.isNotBlank(oldReportTime) && StringUtils.isNotBlank(newReportTime)){
					if(newReportTime.compareTo(oldReportTime)>=0){//���ϱ���ʱ������ϱ���ʱ��Ҫ��ű���
						String newLocDesc = newGisModel.getLocDesc();
						if(StringUtils.isBlank(newLocDesc)){//�µ�����ϢΪ�գ�����ȡ����Ϣ
							newGisModel.setLocDesc(oldGisModel.getLocDesc());
						}
						DataCache.staffPostionMap.put(newGisModel.getTmnAlias(), newGisModel);
					}
				}
			}else{//��������ģ��
				DataCache.staffPostionMap.put(newGisModel.getTmnAlias(), newGisModel);
			}
			
		}catch(Exception e){
			log.error("saveDcGpsRealToCache fail",e);
		}
	}
	
	//�ж�MSID�Ƿ�һ��,��һ���Ļ�,���µ�Ա������
	private void scanStaffMsid(WqStaffInfo staff,String[] deviceParam){
		String newMsid = null;
		if(deviceParam!=null && deviceParam.length>0){
			newMsid = deviceParam[0];
		}
		
		if(newMsid == null || staff.getMsid()==newMsid){
			return;
		}
		if(staff.getMsid()==null || !staff.getMsid().equals(newMsid)){
			log.error("================newMsid===="+newMsid);
			staff.setMsid(newMsid);
			
			PublicService service = (PublicService)AppUtil.getBean("publicService");
			service.save(staff);
			
			for(Map.Entry<String, WqStaffInfo> entry : DataCache.staffMap.entrySet()){
				WqStaffInfo s = entry.getValue();
				if(newMsid.equals(s.getMsid()) && s!=staff){
					s.setMsid(null);
					service.save(s);
				}
			}
		}
	}
	
	//ɨ��ƻ���λ����
	private void scanPlanPocateTask(DcGpsReal dcGpsReal){
		//��Ч��λ���Ž��мƻ���λ�Ľ���
		if(dcGpsReal.getIsValidGps()==false){
			return;
		}
		String staffId = dcGpsReal.getTmnAlias();
		Map<String,WqPlanLocateDetail> updateMap = DataCache.planLocateDetailStaffIdMap.get(staffId);
		if(updateMap!=null){
			CachePlanLocateMap refresh = (CachePlanLocateMap) AppUtil.getBean("cachePlanLocateMap");
			String nowDateTime = DateUtil.getCurDate();//ϵͳʱ��
			for(Map.Entry<String,WqPlanLocateDetail> entry : updateMap.entrySet()){
				String planLocateTime = entry.getKey();
				if(nowDateTime.compareTo(planLocateTime+":00")>=0){//���ڼƻ�ʱ��Ĺ����ڸôμƻ���λ
					WqPlanLocateDetail detail = entry.getValue();
					detail.setLogit(dcGpsReal.getLogit());
					detail.setLogitOffset(dcGpsReal.getLogitOffset());
					detail.setLat(dcGpsReal.getLat());
					detail.setLatOffset(dcGpsReal.getLatOffset());
					detail.setLocDesc(dcGpsReal.getLocDesc());
					detail.setReportTime(dcGpsReal.getReportTime());
					detail.setSpeed(dcGpsReal.getSpeed());
					detail.setHeight(dcGpsReal.getHeight());
					detail.setAngle(dcGpsReal.getAngle());
					refresh.updateWqPlanLocateDetail(detail);
					
					updateMap.remove(planLocateTime);
				}
			}
			
			if(updateMap.isEmpty()){
				DataCache.planLocateDetailStaffIdMap.remove(staffId);
			}
		}
	}
}
