package analog;

import org.aigps.wqgps.common.util.StrUtil;

/**
 * ��������ģ����
 * ����2011-7-4��2011-12-31�� ���üƻ����ݷüƻ���Χ����������
 * @author Snake
 *
 */
public class DataAnalog {
	
	private static final String ID_������ = "402882e53417b48c0134304a8bb40008";
	private static final String ID_������ = "402882e53417b48c0134304940b50005";
	private static final String ID_������ = "402882e53417b48c01343049b4eb0006";
	private static final String ID_������ = "402882e53417b48c0134304afbf20009";
	private static final String ID_�´�ΰ = "402882e53417b48c0134304a20860007";
	
	private static final String REG_Ҧ��԰���ָ� = "402882e53417b48c013430cc423f0015";
	private static final String REG_�ڻ����� = "402882e53417b48c013430cba5050013";
	private static final String REG_ʯ��Ӫ�׳����� = "402882e53417b48c013430cbfc4c0014";
	private static final String REG_��ֱ����ѧ = "402882e53417b48c013430cd35490018";
	private static final String REG_��ֱ�ż�ï = "402882e53417b48c013430cce46a0017";
	private static final String REG_�½ֿڰٻ� = "402882e53417b48c013430cd77340019";
	private static final String REG_�Ӿ���¡ = "402882e53417b48c013430cca4930016";
	private static final String REG_�������ó� = "402882e53417b48c013430cb37180012";

	private static final String REG_��˾ = "402882e53417b48c013430459fb10001";
	
	private static final String REG_��ֱ�Ŵ��� = "402882e53417b48c013430cdf01e001a";
//	
//	--���� �ͻ��ݷüƻ�
//	delete from WQ_VISIT_PLAN t where t.company_id = '402882e53417b48c013430459fb10001';
//	--���� ���üƻ�
//	delete from wq_travel_plan t where t.company_id = '402882e53417b48c013430459fb10001';
//	--���� ����
//	delete from wq_rule t where t.company_id = '402882e53417b48c013430459fb10001';
//	--�������Ա��ȥȨ��
//	delete from wq_staff_auth t where t.company_id = '402882e53417b48c013430459fb10001' and t.obj_id = '4';
//
//	--��������������
//	delete from WQ_REGION_VISIT t where t.company_id = '402882e53417b48c013430459fb10001';
//	delete from WQ_REGION_VISIT_HIS t where t.staff_id in (select b.id from wq_staff_info b where b.company_id = '402882e53417b48c013430459fb10001');
//
//	--�������������������
//	delete from DC_RG_AREA_HIS t where t.tmn_code in (select w.mobile_number from wq_staff_info w where w.company_id = '402882e53417b48c013430459fb10001');
//	--����GPS��λ����
//	delete from DC_GPS_REAL t where t.tmn_code in (select w.mobile_number from wq_staff_info w where w.company_id = '402882e53417b48c013430459fb10001');
//	delete from DC_GPS_HIS t where t.tmn_code in (select w.mobile_number from wq_staff_info w where w.company_id = '402882e53417b48c013430459fb10001');
//	--���������ͳ��
//	delete from DC_DAY_MILE t where t.tmn_code in (select w.mobile_number from wq_staff_info w where w.company_id = '402882e53417b48c013430459fb10001');
//	--�������
//	delete from wq_alarm_info t where t.company_id = '402882e53417b48c013430459fb10001';
//	
	private void createVisitPlan(String staffId, String clientId,String visitDate) {
		StringBuilder sql = new StringBuilder("");
		sql.append("INSERT INTO WQ_VISIT_PLAN(ID,VISIT_DATE,CLIENT_ID,COMPANY_ID,STAFF_ID) values(");
		sql.append("'" + StrUtil.getUuidSequence() + "',");
		sql.append(visitDate + ",");
		sql.append("'" + clientId + "',");
		sql.append("'"+ REG_��˾ +"',");
		sql.append("'" + staffId + "'");
		sql.append(");");
		System.out.println(sql.toString());
	}

	private void createTravelPlan(String areaIds, String startTime,String endTime, String staffId) {
		StringBuilder sql = new StringBuilder("");
		sql.append("INSERT INTO WQ_TRAVEL_PLAN(ID,AREA_IDS,START_TIME,END_TIME,STAFF_ID,COMPANY_ID) values(");
		sql.append("'" + StrUtil.getUuidSequence() + "',");
		sql.append("'" + areaIds + "',");
		sql.append(startTime + ",");
		sql.append(endTime + ",");
		sql.append("'" + staffId + "',");
		sql.append("'"+ REG_��˾ +"'");
		sql.append(");");
		System.out.println(sql.toString());
	}
	
	private void createRule(String startDate,String staffId,String staffName) {
		String resId = StrUtil.getUuidSequence();
		StringBuilder sql = new StringBuilder("");
		sql.append("INSERT INTO WQ_RULE(ID,ELE_FENCE_ID,NAME,START_DATE,END_DATE,START_TIME,END_TIME,WEEK_DAYS,TYPE,COMPANY_ID,IS_ENABLE) values(");
		sql.append("'" + resId + "',");
		sql.append("'"+REG_��ֱ�Ŵ���+"',");
		sql.append("('" + staffName+ "'||" + startDate + "||"+"'������ֱ�Ŵ������'),");
		sql.append(startDate + ",");
		sql.append(startDate + ",");
		sql.append("'09:00',");
		sql.append("'18:00',");
		sql.append("'1,2,3,4,5',");
		sql.append("'00',");
		sql.append("'"+ REG_��˾ +"',");
		sql.append("'1'");
		sql.append(");");
		System.out.println(sql.toString());
		
		StringBuilder sql1 = new StringBuilder("");
		sql1.append("INSERT INTO WQ_STAFF_AUTH(STAFF_ID,RES_ID,OBJ_ID,COMPANY_ID) values(");
		sql1.append("'" + staffId + "',");
		sql1.append("'" + resId + "',");
		sql1.append("'4',");
		sql1.append("'"+ REG_��˾ +"'");
		sql1.append(");");
		System.out.println(sql1.toString());
	}

	// ������ 402882e5314b569901314bdeb0dc0015
	private void createLiXiaoguoData() {
		for (int i = 0; i <= 12; i++) {
			//7.5 ����8��50���ͻ�Ҧ��԰���ָ���9��30�뿪������15��00���ͻ��ڻ����ã�15��05�뿪��17��40���ͻ�ʯ��Ӫ�׳�������18��30�뿪��
			createVisitPlan(ID_������,REG_Ҧ��԰���ָ�,"to_char(to_date('2011-07-05','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_�ڻ�����,"to_char(to_date('2011-07-05','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_ʯ��Ӫ�׳�����,"to_char(to_date('2011-07-05','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.6 7.7  ��������ȥ���̫ԭ���ص�����
			createTravelPlan("12,1401","to_char(to_date('2011-07-06','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')","to_char(to_date('2011-07-07','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_������);
			//7.8 ����8��50���ͻ��ڻ����ã�9��40�뿪��12��10����˾�ܲ���13��30�뿪��16��40���ͻ�ʯ��Ӫ�׳�������17��25�뿪��
			createVisitPlan(ID_������,REG_�ڻ�����,"to_char(to_date('2011-07-08','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_ʯ��Ӫ�׳�����,"to_char(to_date('2011-07-08','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			/*************************************/
			//7.11 ����8��50���ͻ�ʯ��Ӫ�׳�������9��35�뿪������15��00���ͻ��ڻ����ã�15��15�뿪��17��10���ͻ�Ҧ��԰���ָ���18��00�뿪��
			createVisitPlan(ID_������,REG_ʯ��Ӫ�׳�����,"to_char(to_date('2011-07-11','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_�ڻ�����,"to_char(to_date('2011-07-11','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_Ҧ��԰���ָ�,"to_char(to_date('2011-07-11','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.13��ֱ�Ŵ����Χ���ڣ�����10�Ρ�
			createRule("to_char(to_date('2011-07-13','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_������,"������");
			//7.14 ����8��40����˾�ܲ���9��00�뿪��10��50���ͻ��ڻ����ã�11��00�뿪��17��00���ͻ�ʯ��Ӫ�׳�������18��10�뿪��
			createVisitPlan(ID_������,REG_�ڻ�����,"to_char(to_date('2011-07-14','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_ʯ��Ӫ�׳�����,"to_char(to_date('2011-07-14','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.15 ��������ȥʯ��ׯ������ر�����
			createTravelPlan("1301","to_char(to_date('2011-07-15','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')","to_char(to_date('2011-07-15','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_������);
			/*************************************/
			//7.20 7.21������������ȥ�Ϻ����������ϻص�������
			createTravelPlan("31","to_char(to_date('2011-07-20','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')","to_char(to_date('2011-07-21','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_������);
			//7.22����8��55���ͻ���ֱ����ѧ��9��30�뿪������14��00���ͻ�ʯ��Ӫ�׳�������14��45�뿪��17��30���ͻ��ڻ����ã�21��20�뿪��
			createVisitPlan(ID_������,REG_��ֱ����ѧ,"to_char(to_date('2011-07-22','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_ʯ��Ӫ�׳�����,"to_char(to_date('2011-07-22','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_�ڻ�����,"to_char(to_date('2011-07-22','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			/*************************************/
			//7.25 7.26 ��һ��������ȥ���������ϣ��ܶ��ص�����
			createTravelPlan("42,43","to_char(to_date('2011-07-25','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')","to_char(to_date('2011-07-26','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_������);
			//7.27 ����8��38���ͻ���ֱ�ż�ï��9��00�뿪������14��00����˾�ܲ���14��15�뿪��16��40����ֱ����ѧ��17��25�뿪��
			createVisitPlan(ID_������,REG_��ֱ�ż�ï,"to_char(to_date('2011-07-27','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_��ֱ����ѧ,"to_char(to_date('2011-07-27','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.29 ����9��25���ͻ�Ҧ��԰���ָ���10��00�뿪������13��00���½ֿڰٻ���13��00�뿪��18��00����ֱ����ѧ��19��20�뿪��
			createVisitPlan(ID_������,REG_Ҧ��԰���ָ�,"to_char(to_date('2011-07-29','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_�½ֿڰٻ�,"to_char(to_date('2011-07-29','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_��ֱ����ѧ,"to_char(to_date('2011-07-29','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			/*************************************/
			//8.2 ����9��25���ͻ��½ֿڰٻ���10��00�뿪������13��00��Ҧ��԰���ָ���14��30�뿪��16��20����ֱ����ѧ��17��10�뿪��
			createVisitPlan(ID_������,REG_�½ֿڰٻ�,"to_char(to_date('2011-08-02','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_Ҧ��԰���ָ�,"to_char(to_date('2011-08-02','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_��ֱ����ѧ,"to_char(to_date('2011-08-02','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//������������ȥ�ӱ��ȷ�������ر�����
			createTravelPlan("1310","to_char(to_date('2011-08-04','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')","to_char(to_date('2011-08-04','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_������);
			//8.5 ��ֱ�Ŵ����Χ���ڣ�����8�Ρ�
			createRule("to_char(to_date('2011-08-05','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_������,"������");
		}
	}

	// ������ 402882e5314b569901314c2ba4240022
	private void createLiWeiguoData() {
		for (int i = 0; i <= 12; i++) {
			//7.4 ����8��50���ͻ��ڻ����ã�9��45�뿪������13��00���ͻ���ֱ�ż�ï��13��15�뿪��15��00���ͻ��½ֿڰٻ���16��00�뿪��
			createVisitPlan(ID_������,REG_�ڻ�����,  "to_char(to_date('2011-07-04','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_��ֱ�ż�ï,  "to_char(to_date('2011-07-04','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_�½ֿڰٻ�,  "to_char(to_date('2011-07-04','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.5��ֱ�Ŵ����Χ���ڣ�����12�Ρ�
			createRule("to_char(to_date('2011-07-05','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_������,"������");
			//7.7 ����8��40�����Ӿ���¡��9��20�뿪��10��50����ֱ����ѧ��11��30�뿪��17��00���ͻ�ʯ��Ӫ�׳�������18��10�뿪��
			createVisitPlan(ID_������,REG_�Ӿ���¡,  "to_char(to_date('2011-07-07','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_��ֱ����ѧ,  "to_char(to_date('2011-07-07','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_ʯ��Ӫ�׳�����,  "to_char(to_date('2011-07-07','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.8 ����8��55����ֱ�ż�ï��9��30�뿪��10��50���ͻ��ڻ����ã�11��00�뿪��17��00���ͻ�Ҧ��԰���ָ���18��10�뿪��
			createVisitPlan(ID_������,REG_��ֱ�ż�ï,"to_char(to_date('2011-07-08','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_�ڻ�����,"to_char(to_date('2011-07-08','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_Ҧ��԰���ָ�,"to_char(to_date('2011-07-08','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.11��������ȥ�ȷ�������ر�����
			createTravelPlan("1310","to_char(to_date('2011-07-11','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')","to_char(to_date('2011-07-11','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_������);
			//7.13����8��55���ͻ��ͻ��ڻ����ã�9��35�뿪������15��00���ͻ�ʯ��Ӫ�׳�������15��45�뿪������16��30�����Ӿ���¡��16��45�뿪��17��10���ͻ�Ҧ��԰���ָ���18��00�뿪
			createVisitPlan(ID_������,REG_�ڻ�����,  "to_char(to_date('2011-07-13','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_ʯ��Ӫ�׳�����,  "to_char(to_date('2011-07-13','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_�Ӿ���¡,  "to_char(to_date('2011-07-13','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_Ҧ��԰���ָ�,  "to_char(to_date('2011-07-13','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.14��ֱ�Ŵ����Χ���ڣ�����10�Ρ�
			createRule("to_char(to_date('2011-07-14','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_������,"������");
			
			//7.15����8��40����ֱ�ż�ï��9��30�뿪��10��50���ͻ��ڻ����ã�11��30�뿪������15��00����ֱ����ѧ��15��25�뿪��17��00���ͻ��½ֿڰٻ���18��10�뿪��
			createVisitPlan(ID_������,REG_��ֱ�ż�ï,  "to_char(to_date('2011-07-15','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_�ڻ�����,  "to_char(to_date('2011-07-15','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_��ֱ����ѧ,  "to_char(to_date('2011-07-15','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_�½ֿڰٻ�,  "to_char(to_date('2011-07-15','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.20����8��35����˾�ܲ���9��35�뿪������15��10���ͻ����óǣ�15��55�뿪������16��40�����Ӿ���¡��16��45�뿪��17��10���ͻ�Ҧ��԰���ָ���18��00�뿪
			createVisitPlan(ID_������,REG_�������ó�,  "to_char(to_date('2011-07-20','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_�Ӿ���¡,  "to_char(to_date('2011-07-20','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_Ҧ��԰���ָ�,  "to_char(to_date('2011-07-20','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.21����8��35����˾�ܲ���9��35�뿪������15��10���ͻ����óǣ�15��55�뿪������16��40�����Ӿ���¡��16��45�뿪��17��10���ͻ�Ҧ��԰���ָ���18��00�뿪
			createVisitPlan(ID_������,REG_�������ó�,  "to_char(to_date('2011-07-21','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_�Ӿ���¡,  "to_char(to_date('2011-07-21','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_Ҧ��԰���ָ�,  "to_char(to_date('2011-07-21','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.22����8��55���ͻ��ͻ��ڻ����ã�9��35�뿪������15��00���ͻ�ʯ��Ӫ�׳�������15��45�뿪������16��30��ʯ��Ӫ�׳�������16��45�뿪��17��10���ͻ�Ҧ��԰���ָ���18��00�뿪
			createVisitPlan(ID_������,REG_�ڻ�����,  "to_char(to_date('2011-07-22','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_ʯ��Ӫ�׳�����,  "to_char(to_date('2011-07-22','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_ʯ��Ӫ�׳�����,  "to_char(to_date('2011-07-22','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_Ҧ��԰���ָ�,  "to_char(to_date('2011-07-22','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.25  7.26��һ������������ȥ���������ϣ����Ļص�����
			createTravelPlan("42,43","to_char(to_date('2011-07-25','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')","to_char(to_date('2011-07-26','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_������);
			//7.27����8��55���ͻ��ͻ��½ֿڰٻ���9��35�뿪������15��00����ֱ����ѧ��15��45�뿪������16��30����ֱ�ż�ï��16��45�뿪��17��10���ͻ�Ҧ��԰���ָ���18��00�뿪
			createVisitPlan(ID_������,REG_�½ֿڰٻ�,  "to_char(to_date('2011-07-27','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_��ֱ����ѧ,  "to_char(to_date('2011-07-27','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_��ֱ�ż�ï,  "to_char(to_date('2011-07-27','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_Ҧ��԰���ָ�,  "to_char(to_date('2011-07-27','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.29����8��55���ͻ��ͻ����óǣ�9��35�뿪������14��40���ͻ�Ҧ��԰���ָ���15��25�뿪������16��00����ֱ����ѧ��16��45�뿪��17��10�����Ӿ���¡��18��00�뿪
			createVisitPlan(ID_������,REG_�������ó�,  "to_char(to_date('2011-07-29','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_Ҧ��԰���ָ�,  "to_char(to_date('2011-07-29','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_��ֱ����ѧ,  "to_char(to_date('2011-07-29','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_�Ӿ���¡,  "to_char(to_date('2011-07-29','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//8.1����8��55����˾�ܲ���9��35�뿪������15��10���ͻ�Ҧ��԰���ָ���15��55�뿪������16��35���½ֿڰٻ���16��45�뿪��17��20���ͻ����óǣ�18��00�뿪
			createVisitPlan(ID_������,REG_Ҧ��԰���ָ�,  "to_char(to_date('2011-08-01','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_�½ֿڰٻ�,  "to_char(to_date('2011-08-01','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_�������ó�,  "to_char(to_date('2011-08-01','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//8.2����8��55���ͻ��ͻ��ڻ����ã�9��35�뿪������15��00���ͻ�ʯ��Ӫ�׳�������15��45�뿪������16��30���½ֿڰٻ���16��45�뿪��17��10���ͻ�Ҧ��԰���ָ���18��00�뿪
			createVisitPlan(ID_������,REG_�ڻ�����,  "to_char(to_date('2011-08-02','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_ʯ��Ӫ�׳�����,  "to_char(to_date('2011-08-02','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_�½ֿڰٻ�,  "to_char(to_date('2011-08-02','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_Ҧ��԰���ָ�,  "to_char(to_date('2011-08-02','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//8.4���ı�������ȥ�ӱ��ȷ� 
			createTravelPlan("1310","to_char(to_date('2011-08-04','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')","to_char(to_date('2011-08-04','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_������);
			//8.5�����ȷ�����ȥ̫ԭ��
			createTravelPlan("1401","to_char(to_date('2011-08-05','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')","to_char(to_date('2011-08-05','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_������);
		}
	}

	// ������ 402882e5314b569901314c2c154d0023
	private void createYangleleData() {
		for (int i = 0; i <= 12; i++) {
			//7.4 ����8��50����˾�ܲ���9��00�뿪������9��50���ڻ����ã�10��50�뿪������13��00���ͻ���ֱ�ż�ï��13��55�뿪��15��00���ͻ��½ֿڰٻ���16��00�뿪��
			createVisitPlan(ID_������,REG_�ڻ�����,  "to_char(to_date('2011-07-04','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_��ֱ�ż�ï,  "to_char(to_date('2011-07-04','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_�½ֿڰٻ�,  "to_char(to_date('2011-07-04','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.5��ֱ�Ŵ����Χ���ڣ�����16�Ρ�
			createRule("to_char(to_date('2011-07-05','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_������,"������");
			//7.7 ����8��45���½ֿڰٻ���9��30�뿪��10��50���ͻ��ڻ����ã�11��00�뿪��17��00���ͻ�Ҧ��԰���ָ���18��10�뿪��
			createVisitPlan(ID_������,REG_�½ֿڰٻ�,  "to_char(to_date('2011-07-07','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_�ڻ�����,  "to_char(to_date('2011-07-07','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_Ҧ��԰���ָ�,  "to_char(to_date('2011-07-07','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.8 ����8��40�����Ӿ���¡��9��20�뿪��10��50�����óǣ�11��30�뿪��17��00���ͻ�ʯ��Ӫ�׳�������18��10�뿪��
			createVisitPlan(ID_������,REG_�Ӿ���¡,  "to_char(to_date('2011-07-08','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_�������ó�,  "to_char(to_date('2011-07-08','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_ʯ��Ӫ�׳�����,  "to_char(to_date('2011-07-08','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.11 ��������ȥ��򣬵���ر�����
			createTravelPlan("12","to_char(to_date('2011-07-11','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')","to_char(to_date('2011-07-11','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_������);
			//7.13 ����9��55���ͻ��ͻ����óǣ�10��35�뿪������15��00���ͻ�ʯ��Ӫ�׳�������15��45�뿪������16��30����ֱ����ѧ��16��45�뿪��17��10���ͻ����Ӿ���¡��18��00�뿪
			createVisitPlan(ID_������,REG_�������ó�,  "to_char(to_date('2011-07-13','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_ʯ��Ӫ�׳�����,  "to_char(to_date('2011-07-13','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_��ֱ����ѧ,  "to_char(to_date('2011-07-13','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_�Ӿ���¡,  "to_char(to_date('2011-07-13','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.14 ����8��50���ͻ��ͻ��ڻ����ã�9��35�뿪������15��00���ͻ���ֱ����ѧ��15��45�뿪������16��30�����Ӿ���¡��16��45�뿪��17��10���ͻ�Ҧ��԰���ָ���18��00�뿪
			createVisitPlan(ID_������,REG_�ڻ�����,  "to_char(to_date('2011-07-14','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_��ֱ����ѧ,  "to_char(to_date('2011-07-14','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_�Ӿ���¡,  "to_char(to_date('2011-07-14','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_Ҧ��԰���ָ�,  "to_char(to_date('2011-07-14','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.15 ����8��40���ڻ����ã�9��30�뿪��10��50���ͻ���ֱ�ż�ï��11��30�뿪������15��00�����óǣ�15��25�뿪��17��00���ͻ��½ֿڰٻ���18��10�뿪��
			createVisitPlan(ID_������,REG_�ڻ�����,  "to_char(to_date('2011-07-15','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_��ֱ�ż�ï,  "to_char(to_date('2011-07-15','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_�������ó�,  "to_char(to_date('2011-07-15','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_�½ֿڰٻ�,  "to_char(to_date('2011-07-15','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.18��ֱ�Ŵ����Χ���ڣ�����6�Ρ�
			createRule("to_char(to_date('2011-07-18','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_������,"������");
			//7.20����8��55���ͻ��ͻ���ֱ�ż�ï��9��55�뿪������15��12���ͻ�ʯ��Ӫ�׳�������15��45�뿪������16��30�����óǣ�16��45�뿪��17��10���ͻ�Ҧ��԰���ָ���18��10�뿪
			createVisitPlan(ID_������,REG_��ֱ�ż�ï,  "to_char(to_date('2011-07-20','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_ʯ��Ӫ�׳�����,  "to_char(to_date('2011-07-20','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_�������ó�,  "to_char(to_date('2011-07-20','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_Ҧ��԰���ָ�,  "to_char(to_date('2011-07-20','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.21����8��35����˾�ܲ���9��35�뿪������15��10���ͻ����óǣ�15��55�뿪������16��40�����Ӿ���¡��16��45�뿪��17��10���ͻ�Ҧ��԰���ָ���18��00�뿪
			createVisitPlan(ID_������,REG_�������ó�,  "to_char(to_date('2011-07-21','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_�Ӿ���¡,  "to_char(to_date('2011-07-21','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_Ҧ��԰���ָ�,  "to_char(to_date('2011-07-21','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.22����8��55���ͻ��ͻ��ڻ����ã�9��45�뿪������15��00���ͻ����Ӿ���¡��15��45�뿪������16��30��ʯ��Ӫ�׳�������16��45�뿪��17��10���ͻ��½ֿڰٻ���18��20�뿪
			createVisitPlan(ID_������,REG_�ڻ�����,  "to_char(to_date('2011-07-22','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_�Ӿ���¡,  "to_char(to_date('2011-07-22','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_ʯ��Ӫ�׳�����,  "to_char(to_date('2011-07-22','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_�½ֿڰٻ�,  "to_char(to_date('2011-07-22','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.25  7.26��һ��������ȥ�������ܶ����ر���
			createTravelPlan("6101","to_char(to_date('2011-07-25','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')","to_char(to_date('2011-07-26','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_������);
			//7.27����8��55���ͻ��ͻ���ֱ����ѧ��9��25�뿪������15��00���½ֿڰٻ���15��45�뿪������16��20�����óǣ�16��55�뿪��17��10���ͻ�Ҧ��԰���ָ���17��40�뿪
			createVisitPlan(ID_������,REG_��ֱ����ѧ,  "to_char(to_date('2011-07-27','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_�½ֿڰٻ�,  "to_char(to_date('2011-07-27','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_�������ó�,  "to_char(to_date('2011-07-27','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_Ҧ��԰���ָ�,  "to_char(to_date('2011-07-27','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.29����8��45���ͻ��ͻ����óǣ�9��35�뿪������14��40���ͻ�ʯ��Ӫ�׳�������15��25�뿪������16��00�����Ӿ���¡��16��45�뿪��17��10����ֱ����ѧ��18��12�뿪
			createVisitPlan(ID_������,REG_�������ó�,  "to_char(to_date('2011-07-29','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_ʯ��Ӫ�׳�����,  "to_char(to_date('2011-07-29','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_�Ӿ���¡,  "to_char(to_date('2011-07-29','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_��ֱ����ѧ,  "to_char(to_date('2011-07-29','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//8.1����8��30��˾�ܲ���9��45�뿪������15��40���ͻ����óǣ�15��55�뿪������16��35���½ֿڰٻ���16��45�뿪��17��20����ֱ�ż�ï��18��05�뿪
			createVisitPlan(ID_������,REG_�������ó�,  "to_char(to_date('2011-08-01','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_�½ֿڰٻ�,  "to_char(to_date('2011-08-01','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_��ֱ�ż�ï,  "to_char(to_date('2011-08-01','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//8.2����8��46���ͻ��ͻ��ڻ����ã�9��35�뿪������15��00���ͻ��½ֿڰٻ���15��45�뿪������16��30���ͻ�ʯ��Ӫ�׳�������17��00�뿪��17��30���ͻ�Ҧ��԰���ָ���18��00�뿪
			createVisitPlan(ID_������,REG_�ڻ�����,  "to_char(to_date('2011-08-02','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_�½ֿڰٻ�,  "to_char(to_date('2011-08-02','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_ʯ��Ӫ�׳�����,  "to_char(to_date('2011-08-02','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_Ҧ��԰���ָ�,  "to_char(to_date('2011-08-02','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//8.4 ���ı�������ȥ�����ȷ� 
			createTravelPlan("3301,1310","to_char(to_date('2011-08-04','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')","to_char(to_date('2011-08-04','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_������);
			//8.5���庼�ݳ���ȥ�Ϻ���
			createTravelPlan("31","to_char(to_date('2011-08-05','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')","to_char(to_date('2011-08-05','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_������);
		
		}
	}
	
	// ������  402882e5314b569901314be146ca0018
	private void createWangliliData() {
		for (int i = 0; i <= 12; i++) {
			//7.5����8��50���ͻ�Ҧ��԰���ָ���9��30�뿪������15��00���ͻ��ڻ����ã�15��05�뿪��17��40���ͻ�ʯ��Ӫ�׳�������18��30�뿪��
			createVisitPlan(ID_������,REG_Ҧ��԰���ָ�,  "to_char(to_date('2011-07-05','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_�ڻ�����,  "to_char(to_date('2011-07-05','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_ʯ��Ӫ�׳�����,  "to_char(to_date('2011-07-05','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.6��ֱ�Ŵ����Χ���ڣ�����10�Ρ�
			createRule("to_char(to_date('2011-07-06','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_������,"������");
			//7.7������������ȥ�ӱ��ȷ�������ر�����
			createTravelPlan("1310","to_char(to_date('2011-07-07','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')","to_char(to_date('2011-07-07','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_������);
			//7.8����9��25���ͻ�Ҧ��԰���ָ���10��00�뿪������13��00���½ֿڰٻ���13��00�뿪��18��00����ֱ����ѧ��19��20�뿪��
			createVisitPlan(ID_������,REG_Ҧ��԰���ָ�,  "to_char(to_date('2011-07-08','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_�½ֿڰٻ�,  "to_char(to_date('2011-07-08','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_��ֱ����ѧ,  "to_char(to_date('2011-07-08','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.11  7.12������������ȥ���������ϣ����Ļص�����
			createTravelPlan("42,43","to_char(to_date('2011-07-11','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')","to_char(to_date('2011-07-12','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_������);
			//7.13����8��38���ͻ���ֱ�ż�ï��9��00�뿪������14��00����˾�ܲ���14��15�뿪��16��40����ֱ����ѧ��17��25�뿪��
			createVisitPlan(ID_������,REG_��ֱ�ż�ï,  "to_char(to_date('2011-07-13','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_��ֱ����ѧ,  "to_char(to_date('2011-07-13','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.15��ֱ�Ŵ����Χ���ڣ�����8�Ρ�
			createRule("to_char(to_date('2011-07-15','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_������,"������");
			//7.20 ����8��50���ͻ��ڻ����ã�9��40�뿪��12��10����˾�ܲ���13��30�뿪��16��40���ͻ�ʯ��Ӫ�׳�������17��25�뿪��
			createVisitPlan(ID_������,REG_�ڻ�����,  "to_char(to_date('2011-07-20','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_ʯ��Ӫ�׳�����,  "to_char(to_date('2011-07-20','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.21 7.22��������ȥ���̫ԭ���ص�����
			createTravelPlan("12,1401","to_char(to_date('2011-07-21','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')","to_char(to_date('2011-07-22','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_������);
			//7.26����9��25���ͻ��½ֿڰٻ���10��00�뿪������13��00��Ҧ��԰���ָ���14��30�뿪��16��20����ֱ����ѧ��17��10�뿪��
			createVisitPlan(ID_������,REG_�½ֿڰٻ�,  "to_char(to_date('2011-07-26','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_Ҧ��԰���ָ�,  "to_char(to_date('2011-07-26','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_��ֱ����ѧ,  "to_char(to_date('2011-07-26','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.28����8��40����˾�ܲ���9��00�뿪��10��50���ͻ��ڻ����ã�11��00�뿪��17��00���ͻ�ʯ��Ӫ�׳�������18��10�뿪��
			createVisitPlan(ID_������,REG_�ڻ�����,  "to_char(to_date('2011-07-28','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_ʯ��Ӫ�׳�����,  "to_char(to_date('2011-07-28','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.29��������ȥʯ��ׯ������ر�����
			createTravelPlan("1301","to_char(to_date('2011-07-29','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')","to_char(to_date('2011-07-29','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_������);
			//8.1����8��50���ͻ�ʯ��Ӫ�׳�������9��35�뿪������15��00���ͻ��ڻ����ã�15��15�뿪��17��10���ͻ�Ҧ��԰���ָ���18��00�뿪��
			createVisitPlan(ID_������,REG_ʯ��Ӫ�׳�����,  "to_char(to_date('2011-08-01','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_�ڻ�����,  "to_char(to_date('2011-08-01','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_Ҧ��԰���ָ�,  "to_char(to_date('2011-08-01','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//8.3 8.4������������ȥ�Ϻ����������ϻص�������
			createTravelPlan("31","to_char(to_date('2011-08-03','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')","to_char(to_date('2011-08-04','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_������);
			//8.5 ����8��55���ͻ���ֱ����ѧ��9��30�뿪������14��00���ͻ�ʯ��Ӫ�׳�������14��45�뿪��17��30���ͻ��ڻ����ã�21��20�뿪��
			createVisitPlan(ID_������,REG_��ֱ����ѧ,  "to_char(to_date('2011-08-05','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_ʯ��Ӫ�׳�����,  "to_char(to_date('2011-08-05','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_������,REG_�ڻ�����,  "to_char(to_date('2011-08-05','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
		}
	}
	// �´�ΰ ID��402882e5314c7a0601314f40c1530001
	private void createChenDaweiData() {
		for (int i = 0; i <= 12; i++) {
			//7.4����8��50���ͻ�ʯ��Ӫ�׳�������9��35�뿪������15��00���ͻ��ڻ����ã�15��15�뿪��17��10���ͻ�Ҧ��԰���ָ���18��00�뿪��
			createVisitPlan(ID_�´�ΰ,REG_ʯ��Ӫ�׳�����,  "to_char(to_date('2011-07-04','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_�´�ΰ,REG_�ڻ�����,  "to_char(to_date('2011-07-04','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_�´�ΰ,REG_Ҧ��԰���ָ�,  "to_char(to_date('2011-07-04','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.6��ֱ�Ŵ����Χ���ڣ�����10�Ρ�
			createRule("to_char(to_date('2011-07-06','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_�´�ΰ,"�´�ΰ");
			//7.7����8��40����˾�ܲ���9��00�뿪��10��50���ͻ��ڻ����ã�11��00�뿪��17��00���ͻ�ʯ��Ӫ�׳�������18��10�뿪��
			createVisitPlan(ID_�´�ΰ,REG_�ڻ�����,  "to_char(to_date('2011-07-07','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_�´�ΰ,REG_ʯ��Ӫ�׳�����,  "to_char(to_date('2011-07-07','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.8��������ȥʯ��ׯ������ر�����
			createTravelPlan("1301","to_char(to_date('2011-07-08','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')","to_char(to_date('2011-07-08','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_�´�ΰ);
			//7.11 7.12 ������������ȥ���������ϣ����Ļص�����
			createTravelPlan("42,43","to_char(to_date('2011-07-11','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')","to_char(to_date('2011-07-12','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_�´�ΰ);
			//7.13����8��38���ͻ���ֱ�ż�ï��9��00�뿪������14��00����˾�ܲ���14��15�뿪��16��40����ֱ����ѧ��17��25�뿪��
			createVisitPlan(ID_�´�ΰ,REG_��ֱ�ż�ï,  "to_char(to_date('2011-07-13','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_�´�ΰ,REG_��ֱ����ѧ,  "to_char(to_date('2011-07-13','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.15��ֱ�Ŵ����Χ���ڣ�����8�Ρ�
			createRule("to_char(to_date('2011-07-15','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_�´�ΰ,"�´�ΰ");
			//7.20 7.21 ������������ȥ�Ϻ����������ϻص�������
			createTravelPlan("31","to_char(to_date('2011-07-20','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')","to_char(to_date('2011-07-21','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_�´�ΰ);
			//7.22 ����8��55���ͻ���ֱ����ѧ��9��30�뿪������14��00���ͻ�ʯ��Ӫ�׳�������14��45�뿪��17��30���ͻ��ڻ����ã�21��20�뿪��
			createVisitPlan(ID_�´�ΰ,REG_��ֱ����ѧ,  "to_char(to_date('2011-07-22','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_�´�ΰ,REG_ʯ��Ӫ�׳�����,  "to_char(to_date('2011-07-22','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_�´�ΰ,REG_�ڻ�����,  "to_char(to_date('2011-07-22','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.25 ����8��50���ͻ�ʯ��Ӫ�׳�������9��35�뿪������15��00���ͻ��ڻ����ã�15��15�뿪��17��10���ͻ�Ҧ��԰���ָ���18��00�뿪��
			createVisitPlan(ID_�´�ΰ,REG_ʯ��Ӫ�׳�����,  "to_char(to_date('2011-07-25','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_�´�ΰ,REG_�ڻ�����,  "to_char(to_date('2011-07-25','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_�´�ΰ,REG_Ҧ��԰���ָ�,  "to_char(to_date('2011-07-25','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.27 7.28 ������������ȥ�Ϻ����������ϻص�������
			createTravelPlan("31","to_char(to_date('2011-07-27','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')","to_char(to_date('2011-07-28','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_�´�ΰ);
			//7.29 ����8��55���ͻ���ֱ����ѧ��9��30�뿪������14��00���ͻ�ʯ��Ӫ�׳�������14��45�뿪��17��30���ͻ��ڻ����ã�21��20�뿪��
			createVisitPlan(ID_�´�ΰ,REG_��ֱ����ѧ,  "to_char(to_date('2011-07-29','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_�´�ΰ,REG_ʯ��Ӫ�׳�����,  "to_char(to_date('2011-07-29','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_�´�ΰ,REG_�ڻ�����,  "to_char(to_date('2011-07-29','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//8.2 ����8��50���ͻ�Ҧ��԰���ָ���9��30�뿪������15��00���ͻ��ڻ����ã�15��05�뿪��17��40���ͻ�ʯ��Ӫ�׳�������18��30�뿪��
			createVisitPlan(ID_�´�ΰ,REG_Ҧ��԰���ָ�,  "to_char(to_date('2011-08-02','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_�´�ΰ,REG_�ڻ�����,  "to_char(to_date('2011-08-02','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_�´�ΰ,REG_ʯ��Ӫ�׳�����,  "to_char(to_date('2011-08-02','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//8.3  8.4 ��������ȥ���̫ԭ���ص�����
			createTravelPlan("12,1401","to_char(to_date('2011-08-03','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')","to_char(to_date('2011-08-04','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_�´�ΰ);
			//8.5 ����8��50���ͻ��ڻ����ã�9��40�뿪��12��10����˾�ܲ���13��30�뿪��16��40���ͻ�ʯ��Ӫ�׳�������17��25�뿪��
			createVisitPlan(ID_�´�ΰ,REG_�ڻ�����,  "to_char(to_date('2011-08-05','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_�´�ΰ,REG_ʯ��Ӫ�׳�����,  "to_char(to_date('2011-08-05','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
		}
	}
	
	public static void main(String[] args) {
		DataAnalog da = new DataAnalog();
//		da.createLiXiaoguoData();
//		da.createLiWeiguoData();
//		da.createYangleleData();
//		da.createWangliliData();
		da.createChenDaweiData();
	}
}
