
package org.aigps.wqgps.module.report.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.aigps.wqgps.common.entity.WqClientInfo;

/**
 * @Title��<�����>
 * @Description��<������>
 *
 * @author xiexueze
 * @version 1.0
 *
 * Create Date��  2011-6-29����02:17:11
 * Modified By��  <�޸�����������ƴ����д>
 * Modified Date��<�޸����ڣ���ʽ:YYYY-MM-DD>
 *
 * Copyright��Copyright(C),1995-2011 ��IPC��09004804��
 * Company�������е��Ƽ��������޹�˾
 */
public class VisitReportModel {
	public String staffId;//Ա�����
	public String staffName;//Ա������
	public int planVisitCount = 0;//�ƻ��ݷÿͻ���
	public int actualVisitCount = 0;//ʵ�ʰݷÿͻ���
	public int validVisitCount = 0;//��Ч�ݷÿͻ���
	public int visitTimes = 0;//�ݷÿͻ�����
	public int validVisitTimes = 0;//��Ч�ݷÿͻ�����
	public long visitTotalLong = 0;//�ݷ���ʱ��
	public long validVisitLong = 0;//��Ч�ݷ���ʱ��
	public String visitDate;//�ݷ�����
	public List<WqClientInfo> notVisitClientList;//δ���ƻ��ݷõĿͻ�
	public List<WqClientInfo> visitClientList;//���ƻ��ݷõĿͻ�
	public List<Map<String,Object>> visitList;//Ա���ݷõ�ÿ����¼
	
	public VisitReportModel(String staffId, String staffName, String visitDate){
		this.staffId = staffId;
		this.staffName = staffName;
		this.visitDate = visitDate;
	}
	public void addVisitRecord(Map<String,Object> map){
		if(visitList == null){
			visitList = new ArrayList<Map<String,Object>>();
		}
		visitList.add(map);
	}
	public void addNotVisitClientRecord(WqClientInfo client){
		if(notVisitClientList == null){
			notVisitClientList = new ArrayList<WqClientInfo>();
		}
		notVisitClientList.add(client);
	}
	public void addVisitClient(WqClientInfo client){
		if(visitClientList==null){
			visitClientList = new ArrayList<WqClientInfo>();
		}
		if(!visitClientList.contains(client)){
			visitClientList.add(client);
		}
	}
	public void removeNotVisitClient(String clientId){
		if(notVisitClientList == null){
			return;
		}
		for(Iterator<WqClientInfo> it=notVisitClientList.iterator(); it.hasNext(); ){
			if(it.next().getId().equals(clientId)){
				it.remove();
			}
		}
	}
}

