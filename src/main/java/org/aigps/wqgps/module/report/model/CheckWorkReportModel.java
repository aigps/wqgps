
package org.sunleads.module.report.model;

import java.util.ArrayList;
import java.util.List;


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
public class CheckWorkReportModel {
	public String staffId;//Ա�����
	public String staffName;//Ա������
	public int normal = 0;//��������
	public int travel = 0;//������
	public int offwork = 0;//�Ѹ���
	public int late = 0;//�ٵ���
	public int early = 0;//������
	
	public boolean needSignIn = false;
	public boolean needSignOut = false;
	
	public List<String> dateState;//ÿ��Ŀ���״̬
	
	public CheckWorkReportModel(String staffId, String staffName){
		this.staffId = staffId;
		this.staffName = staffName;
	}
	
	public void addDateState(String date,String state){
		if(dateState == null){
			dateState = new ArrayList<String>();
		}
		dateState.add(date+"_"+state);
	}
}

