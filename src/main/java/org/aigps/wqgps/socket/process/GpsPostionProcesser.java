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
 * 定位信息处理者
 * @author admins
 *
 */

public class GpsPostionProcesser implements MsgProcesser {
	protected static final Log log = LogFactory.getLog(GpsPostionProcesser.class);
	
	/**
	 * 对应定位信息处理类型
	 */
	public static final String PROCESS_TYPE = "GPS";
	
	public void process(YmAccessMsg accessMsg) {
		if(accessMsg == null || StringUtils.isBlank(accessMsg.getData())){
			return;
		}
		try{
			String tmnCode = accessMsg.getDeviceCode();//手机号即终端号
			String staffId = DataCache.phoneStaffIdMap.get(tmnCode);
			if(StringUtils.isBlank(staffId)){
				return;
			}
			WqStaffInfo staff = DataCache.staffMap.get(staffId);
			if(staff != null){
				//判断MSID，不一致的话，要更新成最新的
				scanStaffMsid(staff,accessMsg.getDeviceParam());
			}
			
			//定位信息
			DcGpsReal dcGpsReal = DcGpsReal.convertDataGpsReal(tmnCode,staffId, accessMsg.getData());
			
			//2如果上来激活类型定位信息，但没有收到激活成功的回复消息，则也算激活成功
			if(LocateTypeEnum.ACTIVE_LCS_POS.getValue().equals(dcGpsReal.getGpsType()) && CmdStateCache.activeTmnNumMap.containsKey(tmnCode)){
				CmdStateCache.activeTmnNumMap.remove(tmnCode);
				SysManagerService service = (SysManagerService) AppUtil.getBean("sysManagerService");
				service.updateActiveState(tmnCode,"2");//成功
			}
	
			//如果不是有效的定位信息
			if(!isValidLocation(dcGpsReal)){
				return;
			}
			if(staff != null){
				//保存定位信息到缓存中
				saveDcGpsRealToCache(staff,dcGpsReal);
				//扫描计划定位
				scanPlanPocateTask(dcGpsReal);
			}
			
			//看有没有回复到http的请求
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
	 * 保存定位信息到缓存
	 * @param newGisModel
	 */
	private void saveDcGpsRealToCache(WqStaffInfo staff,DcGpsReal newGisModel){
		WqCompanyInfo company = DataCache.companyInfoMap.get(staff.getCompanyId());
		if(company==null){
			return;
		}
		//如果公司不使用无效定位，该定位又是无效的，直接丢掉
		if(company.getUseInvalidLoc()!=true && newGisModel.getIsValidGps()!=true){
			return;
		}
		try{
			DcGpsReal oldGisModel = DataCache.staffPostionMap.get(newGisModel.getTmnAlias());
			if(oldGisModel!=null){//存在老模型
				String oldReportTime = oldGisModel.getReportTime();
				String newReportTime = newGisModel.getReportTime();
				if(StringUtils.isNotBlank(oldReportTime) && StringUtils.isNotBlank(newReportTime)){
					if(newReportTime.compareTo(oldReportTime)>=0){//新上报的时间比老上报的时间要晚才保存
						String newLocDesc = newGisModel.getLocDesc();
						if(StringUtils.isBlank(newLocDesc)){//新地理信息为空，则先取老信息
							newGisModel.setLocDesc(oldGisModel.getLocDesc());
						}
						DataCache.staffPostionMap.put(newGisModel.getTmnAlias(), newGisModel);
					}
				}
			}else{//不存在老模型
				DataCache.staffPostionMap.put(newGisModel.getTmnAlias(), newGisModel);
			}
			
		}catch(Exception e){
			log.error("saveDcGpsRealToCache fail",e);
		}
	}
	
	//判断MSID是否一样,不一样的话,更新到员工表里
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
	
	//扫描计划定位任务
	private void scanPlanPocateTask(DcGpsReal dcGpsReal){
		//有效定位，才进行计划定位的接收
		if(dcGpsReal.getIsValidGps()==false){
			return;
		}
		String staffId = dcGpsReal.getTmnAlias();
		Map<String,WqPlanLocateDetail> updateMap = DataCache.planLocateDetailStaffIdMap.get(staffId);
		if(updateMap!=null){
			CachePlanLocateMap refresh = (CachePlanLocateMap) AppUtil.getBean("cachePlanLocateMap");
			String nowDateTime = DateUtil.getCurDate();//系统时间
			for(Map.Entry<String,WqPlanLocateDetail> entry : updateMap.entrySet()){
				String planLocateTime = entry.getKey();
				if(nowDateTime.compareTo(planLocateTime+":00")>=0){//大于计划时间的归属于该次计划定位
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
