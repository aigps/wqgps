package analog;

import org.sunleads.common.util.StrUtil;

/**
 * 外勤数据模拟类
 * 包含2011-7-4至2011-12-31的 差旅计划、拜访计划、围栏规则数据
 * @author Snake
 *
 */
public class DataAnalog {
	
	private static final String ID_李晓国 = "402882e53417b48c0134304a8bb40008";
	private static final String ID_李卫国 = "402882e53417b48c0134304940b50005";
	private static final String ID_杨乐乐 = "402882e53417b48c01343049b4eb0006";
	private static final String ID_王丽丽 = "402882e53417b48c0134304afbf20009";
	private static final String ID_陈大伟 = "402882e53417b48c0134304a20860007";
	
	private static final String REG_姚家园家乐福 = "402882e53417b48c013430cc423f0015";
	private static final String REG_冠华大厦 = "402882e53417b48c013430cba5050013";
	private static final String REG_石佛营易初莲花 = "402882e53417b48c013430cbfc4c0014";
	private static final String REG_东直门中学 = "402882e53417b48c013430cd35490018";
	private static final String REG_西直门嘉茂 = "402882e53417b48c013430cce46a0017";
	private static final String REG_新街口百货 = "402882e53417b48c013430cd77340019";
	private static final String REG_坝京客隆 = "402882e53417b48c013430cca4930016";
	private static final String REG_朝阳大悦城 = "402882e53417b48c013430cb37180012";

	private static final String REG_公司 = "402882e53417b48c013430459fb10001";
	
	private static final String REG_西直门促销 = "402882e53417b48c013430cdf01e001a";
//	
//	--清理 客户拜访计划
//	delete from WQ_VISIT_PLAN t where t.company_id = '402882e53417b48c013430459fb10001';
//	--清理 差旅计划
//	delete from wq_travel_plan t where t.company_id = '402882e53417b48c013430459fb10001';
//	--清理 规则
//	delete from wq_rule t where t.company_id = '402882e53417b48c013430459fb10001';
//	--清理规则员工去权限
//	delete from wq_staff_auth t where t.company_id = '402882e53417b48c013430459fb10001' and t.obj_id = '4';
//
//	--清空区域进出数据
//	delete from WQ_REGION_VISIT t where t.company_id = '402882e53417b48c013430459fb10001';
//	delete from WQ_REGION_VISIT_HIS t where t.staff_id in (select b.id from wq_staff_info b where b.company_id = '402882e53417b48c013430459fb10001');
//
//	--清理行政区域进出数据
//	delete from DC_RG_AREA_HIS t where t.tmn_code in (select w.mobile_number from wq_staff_info w where w.company_id = '402882e53417b48c013430459fb10001');
//	--清理GPS定位数据
//	delete from DC_GPS_REAL t where t.tmn_code in (select w.mobile_number from wq_staff_info w where w.company_id = '402882e53417b48c013430459fb10001');
//	delete from DC_GPS_HIS t where t.tmn_code in (select w.mobile_number from wq_staff_info w where w.company_id = '402882e53417b48c013430459fb10001');
//	--清理日里程统计
//	delete from DC_DAY_MILE t where t.tmn_code in (select w.mobile_number from wq_staff_info w where w.company_id = '402882e53417b48c013430459fb10001');
//	--清除报警
//	delete from wq_alarm_info t where t.company_id = '402882e53417b48c013430459fb10001';
//	
	private void createVisitPlan(String staffId, String clientId,String visitDate) {
		StringBuilder sql = new StringBuilder("");
		sql.append("INSERT INTO WQ_VISIT_PLAN(ID,VISIT_DATE,CLIENT_ID,COMPANY_ID,STAFF_ID) values(");
		sql.append("'" + StrUtil.getUuidSequence() + "',");
		sql.append(visitDate + ",");
		sql.append("'" + clientId + "',");
		sql.append("'"+ REG_公司 +"',");
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
		sql.append("'"+ REG_公司 +"'");
		sql.append(");");
		System.out.println(sql.toString());
	}
	
	private void createRule(String startDate,String staffId,String staffName) {
		String resId = StrUtil.getUuidSequence();
		StringBuilder sql = new StringBuilder("");
		sql.append("INSERT INTO WQ_RULE(ID,ELE_FENCE_ID,NAME,START_DATE,END_DATE,START_TIME,END_TIME,WEEK_DAYS,TYPE,COMPANY_ID,IS_ENABLE) values(");
		sql.append("'" + resId + "',");
		sql.append("'"+REG_西直门促销+"',");
		sql.append("('" + staffName+ "'||" + startDate + "||"+"'禁出西直门促销活动区'),");
		sql.append(startDate + ",");
		sql.append(startDate + ",");
		sql.append("'09:00',");
		sql.append("'18:00',");
		sql.append("'1,2,3,4,5',");
		sql.append("'00',");
		sql.append("'"+ REG_公司 +"',");
		sql.append("'1'");
		sql.append(");");
		System.out.println(sql.toString());
		
		StringBuilder sql1 = new StringBuilder("");
		sql1.append("INSERT INTO WQ_STAFF_AUTH(STAFF_ID,RES_ID,OBJ_ID,COMPANY_ID) values(");
		sql1.append("'" + staffId + "',");
		sql1.append("'" + resId + "',");
		sql1.append("'4',");
		sql1.append("'"+ REG_公司 +"'");
		sql1.append(");");
		System.out.println(sql1.toString());
	}

	// 李晓国 402882e5314b569901314bdeb0dc0015
	private void createLiXiaoguoData() {
		for (int i = 0; i <= 12; i++) {
			//7.5 早上8：50到客户姚家园家乐福，9：30离开。下午15：00到客户冠华大厦，15：05离开。17：40到客户石佛营易初莲花，18：30离开。
			createVisitPlan(ID_李晓国,REG_姚家园家乐福,"to_char(to_date('2011-07-05','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_李晓国,REG_冠华大厦,"to_char(to_date('2011-07-05','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_李晓国,REG_石佛营易初莲花,"to_char(to_date('2011-07-05','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.6 7.7  北京出差去天津、太原，回到北京
			createTravelPlan("12,1401","to_char(to_date('2011-07-06','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')","to_char(to_date('2011-07-07','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_李晓国);
			//7.8 早上8：50到客户冠华大厦，9：40离开。12：10到公司总部，13：30离开。16：40到客户石佛营易初莲花，17：25离开。
			createVisitPlan(ID_李晓国,REG_冠华大厦,"to_char(to_date('2011-07-08','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_李晓国,REG_石佛营易初莲花,"to_char(to_date('2011-07-08','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			/*************************************/
			//7.11 早上8：50到客户石佛营易初莲花，9：35离开。下午15：00到客户冠华大厦，15：15离开。17：10到客户姚家园家乐福，18：00离开。
			createVisitPlan(ID_李晓国,REG_石佛营易初莲花,"to_char(to_date('2011-07-11','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_李晓国,REG_冠华大厦,"to_char(to_date('2011-07-11','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_李晓国,REG_姚家园家乐福,"to_char(to_date('2011-07-11','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.13西直门促销活动围栏内，出入10次。
			createRule("to_char(to_date('2011-07-13','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_李晓国,"李晓国");
			//7.14 早上8：40到公司总部，9：00离开。10：50到客户冠华大厦，11：00离开。17：00到客户石佛营易初莲花，18：10离开。
			createVisitPlan(ID_李晓国,REG_冠华大厦,"to_char(to_date('2011-07-14','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_李晓国,REG_石佛营易初莲花,"to_char(to_date('2011-07-14','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.15 北京出差去石家庄，当天回北京。
			createTravelPlan("1301","to_char(to_date('2011-07-15','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')","to_char(to_date('2011-07-15','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_李晓国);
			/*************************************/
			//7.20 7.21周三北京出差去上海，周四晚上回到北京。
			createTravelPlan("31","to_char(to_date('2011-07-20','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')","to_char(to_date('2011-07-21','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_李晓国);
			//7.22早上8：55到客户东直门中学，9：30离开。下午14：00到客户石佛营易初莲花，14：45离开。17：30到客户冠华大厦，21：20离开。
			createVisitPlan(ID_李晓国,REG_东直门中学,"to_char(to_date('2011-07-22','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_李晓国,REG_石佛营易初莲花,"to_char(to_date('2011-07-22','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_李晓国,REG_冠华大厦,"to_char(to_date('2011-07-22','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			/*************************************/
			//7.25 7.26 周一北京出差去湖北、湖南，周二回到北京
			createTravelPlan("42,43","to_char(to_date('2011-07-25','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')","to_char(to_date('2011-07-26','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_李晓国);
			//7.27 早上8：38到客户西直门嘉茂，9：00离开。下午14：00到公司总部，14：15离开。16：40到东直门中学，17：25离开。
			createVisitPlan(ID_李晓国,REG_西直门嘉茂,"to_char(to_date('2011-07-27','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_李晓国,REG_东直门中学,"to_char(to_date('2011-07-27','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.29 早上9：25到客户姚家园家乐福，10：00离开。下午13：00到新街口百货，13：00离开。18：00到东直门中学，19：20离开。
			createVisitPlan(ID_李晓国,REG_姚家园家乐福,"to_char(to_date('2011-07-29','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_李晓国,REG_新街口百货,"to_char(to_date('2011-07-29','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_李晓国,REG_东直门中学,"to_char(to_date('2011-07-29','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			/*************************************/
			//8.2 早上9：25到客户新街口百货，10：00离开。下午13：00到姚家园家乐福，14：30离开。16：20到东直门中学，17：10离开。
			createVisitPlan(ID_李晓国,REG_新街口百货,"to_char(to_date('2011-08-02','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_李晓国,REG_姚家园家乐福,"to_char(to_date('2011-08-02','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_李晓国,REG_东直门中学,"to_char(to_date('2011-08-02','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//周三北京出差去河北廊坊，当天回北京。
			createTravelPlan("1310","to_char(to_date('2011-08-04','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')","to_char(to_date('2011-08-04','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_李晓国);
			//8.5 西直门促销活动围栏内，出入8次。
			createRule("to_char(to_date('2011-08-05','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_李晓国,"李晓国");
		}
	}

	// 李卫国 402882e5314b569901314c2ba4240022
	private void createLiWeiguoData() {
		for (int i = 0; i <= 12; i++) {
			//7.4 早上8：50到客户冠华大厦，9：45离开。下午13：00到客户西直门嘉茂，13：15离开。15：00到客户新街口百货，16：00离开。
			createVisitPlan(ID_李卫国,REG_冠华大厦,  "to_char(to_date('2011-07-04','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_李卫国,REG_西直门嘉茂,  "to_char(to_date('2011-07-04','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_李卫国,REG_新街口百货,  "to_char(to_date('2011-07-04','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.5西直门促销活动围栏内，出入12次。
			createRule("to_char(to_date('2011-07-05','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_李卫国,"李卫国");
			//7.7 早上8：40到东坝京客隆，9：20离开。10：50到东直门中学，11：30离开。17：00到客户石佛营易初莲花，18：10离开。
			createVisitPlan(ID_李卫国,REG_坝京客隆,  "to_char(to_date('2011-07-07','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_李卫国,REG_东直门中学,  "to_char(to_date('2011-07-07','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_李卫国,REG_石佛营易初莲花,  "to_char(to_date('2011-07-07','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.8 早上8：55到西直门嘉茂，9：30离开。10：50到客户冠华大厦，11：00离开。17：00到客户姚家园家乐福，18：10离开。
			createVisitPlan(ID_李卫国,REG_西直门嘉茂,"to_char(to_date('2011-07-08','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_李卫国,REG_冠华大厦,"to_char(to_date('2011-07-08','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_李卫国,REG_姚家园家乐福,"to_char(to_date('2011-07-08','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.11北京出差去廊坊，当天回北京。
			createTravelPlan("1310","to_char(to_date('2011-07-11','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')","to_char(to_date('2011-07-11','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_李卫国);
			//7.13早上8：55到客户客户冠华大厦，9：35离开。下午15：00到客户石佛营易初莲花，15：45离开。下午16：30到东坝京客隆，16：45离开。17：10到客户姚家园家乐福，18：00离开
			createVisitPlan(ID_李卫国,REG_冠华大厦,  "to_char(to_date('2011-07-13','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_李卫国,REG_石佛营易初莲花,  "to_char(to_date('2011-07-13','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_李卫国,REG_坝京客隆,  "to_char(to_date('2011-07-13','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_李卫国,REG_姚家园家乐福,  "to_char(to_date('2011-07-13','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.14西直门促销活动围栏内，出入10次。
			createRule("to_char(to_date('2011-07-14','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_李卫国,"李卫国");
			
			//7.15早上8：40到西直门嘉茂，9：30离开。10：50到客户冠华大厦，11：30离开。下午15：00到西直门中学，15：25离开。17：00到客户新街口百货，18：10离开。
			createVisitPlan(ID_李卫国,REG_西直门嘉茂,  "to_char(to_date('2011-07-15','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_李卫国,REG_冠华大厦,  "to_char(to_date('2011-07-15','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_李卫国,REG_东直门中学,  "to_char(to_date('2011-07-15','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_李卫国,REG_新街口百货,  "to_char(to_date('2011-07-15','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.20早上8：35到公司总部，9：35离开。下午15：10到客户大悦城，15：55离开。下午16：40到东坝京客隆，16：45离开。17：10到客户姚家园家乐福，18：00离开
			createVisitPlan(ID_李卫国,REG_朝阳大悦城,  "to_char(to_date('2011-07-20','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_李卫国,REG_坝京客隆,  "to_char(to_date('2011-07-20','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_李卫国,REG_姚家园家乐福,  "to_char(to_date('2011-07-20','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.21早上8：35到公司总部，9：35离开。下午15：10到客户大悦城，15：55离开。下午16：40到东坝京客隆，16：45离开。17：10到客户姚家园家乐福，18：00离开
			createVisitPlan(ID_李卫国,REG_朝阳大悦城,  "to_char(to_date('2011-07-21','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_李卫国,REG_坝京客隆,  "to_char(to_date('2011-07-21','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_李卫国,REG_姚家园家乐福,  "to_char(to_date('2011-07-21','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.22早上8：55到客户客户冠华大厦，9：35离开。下午15：00到客户石佛营易初莲花，15：45离开。下午16：30到石佛营易初莲花，16：45离开。17：10到客户姚家园家乐福，18：00离开
			createVisitPlan(ID_李卫国,REG_冠华大厦,  "to_char(to_date('2011-07-22','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_李卫国,REG_石佛营易初莲花,  "to_char(to_date('2011-07-22','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_李卫国,REG_石佛营易初莲花,  "to_char(to_date('2011-07-22','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_李卫国,REG_姚家园家乐福,  "to_char(to_date('2011-07-22','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.25  7.26周一、二北京出差去湖北、湖南，周四回到北京
			createTravelPlan("42,43","to_char(to_date('2011-07-25','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')","to_char(to_date('2011-07-26','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_李卫国);
			//7.27早上8：55到客户客户新街口百货，9：35离开。下午15：00到东直门中学，15：45离开。下午16：30到西直门嘉茂，16：45离开。17：10到客户姚家园家乐福，18：00离开
			createVisitPlan(ID_李卫国,REG_新街口百货,  "to_char(to_date('2011-07-27','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_李卫国,REG_东直门中学,  "to_char(to_date('2011-07-27','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_李卫国,REG_西直门嘉茂,  "to_char(to_date('2011-07-27','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_李卫国,REG_姚家园家乐福,  "to_char(to_date('2011-07-27','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.29早上8：55到客户客户大悦城，9：35离开。下午14：40到客户姚家园家乐福，15：25离开。下午16：00到东直门中学，16：45离开。17：10到东坝京客隆，18：00离开
			createVisitPlan(ID_李卫国,REG_朝阳大悦城,  "to_char(to_date('2011-07-29','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_李卫国,REG_姚家园家乐福,  "to_char(to_date('2011-07-29','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_李卫国,REG_东直门中学,  "to_char(to_date('2011-07-29','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_李卫国,REG_坝京客隆,  "to_char(to_date('2011-07-29','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//8.1早上8：55到公司总部，9：35离开。下午15：10到客户姚家园家乐福，15：55离开。下午16：35到新街口百货，16：45离开。17：20到客户大悦城，18：00离开
			createVisitPlan(ID_李卫国,REG_姚家园家乐福,  "to_char(to_date('2011-08-01','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_李卫国,REG_新街口百货,  "to_char(to_date('2011-08-01','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_李卫国,REG_朝阳大悦城,  "to_char(to_date('2011-08-01','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//8.2早上8：55到客户客户冠华大厦，9：35离开。下午15：00到客户石佛营易初莲花，15：45离开。下午16：30到新街口百货，16：45离开。17：10到客户姚家园家乐福，18：00离开
			createVisitPlan(ID_李卫国,REG_冠华大厦,  "to_char(to_date('2011-08-02','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_李卫国,REG_石佛营易初莲花,  "to_char(to_date('2011-08-02','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_李卫国,REG_新街口百货,  "to_char(to_date('2011-08-02','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_李卫国,REG_姚家园家乐福,  "to_char(to_date('2011-08-02','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//8.4周四北京出差去河北廊坊 
			createTravelPlan("1310","to_char(to_date('2011-08-04','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')","to_char(to_date('2011-08-04','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_李卫国);
			//8.5周五廊坊出差去太原。
			createTravelPlan("1401","to_char(to_date('2011-08-05','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')","to_char(to_date('2011-08-05','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_李卫国);
		}
	}

	// 杨乐乐 402882e5314b569901314c2c154d0023
	private void createYangleleData() {
		for (int i = 0; i <= 12; i++) {
			//7.4 早上8：50到公司总部，9：00离开。早上9：50到冠华大厦，10：50离开。下午13：00到客户西直门嘉茂，13：55离开。15：00到客户新街口百货，16：00离开。
			createVisitPlan(ID_杨乐乐,REG_冠华大厦,  "to_char(to_date('2011-07-04','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_杨乐乐,REG_西直门嘉茂,  "to_char(to_date('2011-07-04','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_杨乐乐,REG_新街口百货,  "to_char(to_date('2011-07-04','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.5西直门促销活动围栏内，出入16次。
			createRule("to_char(to_date('2011-07-05','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_杨乐乐,"杨乐乐");
			//7.7 早上8：45到新街口百货，9：30离开。10：50到客户冠华大厦，11：00离开。17：00到客户姚家园家乐福，18：10离开。
			createVisitPlan(ID_杨乐乐,REG_新街口百货,  "to_char(to_date('2011-07-07','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_杨乐乐,REG_冠华大厦,  "to_char(to_date('2011-07-07','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_杨乐乐,REG_姚家园家乐福,  "to_char(to_date('2011-07-07','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.8 早上8：40到东坝京客隆，9：20离开。10：50到大悦城，11：30离开。17：00到客户石佛营易初莲花，18：10离开。
			createVisitPlan(ID_杨乐乐,REG_坝京客隆,  "to_char(to_date('2011-07-08','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_杨乐乐,REG_朝阳大悦城,  "to_char(to_date('2011-07-08','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_杨乐乐,REG_石佛营易初莲花,  "to_char(to_date('2011-07-08','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.11 北京出差去天津，当天回北京。
			createTravelPlan("12","to_char(to_date('2011-07-11','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')","to_char(to_date('2011-07-11','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_杨乐乐);
			//7.13 早上9：55到客户客户大悦城，10：35离开。下午15：00到客户石佛营易初莲花，15：45离开。下午16：30到东直门中学，16：45离开。17：10到客户东坝京客隆，18：00离开
			createVisitPlan(ID_杨乐乐,REG_朝阳大悦城,  "to_char(to_date('2011-07-13','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_杨乐乐,REG_石佛营易初莲花,  "to_char(to_date('2011-07-13','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_杨乐乐,REG_东直门中学,  "to_char(to_date('2011-07-13','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_杨乐乐,REG_坝京客隆,  "to_char(to_date('2011-07-13','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.14 早上8：50到客户客户冠华大厦，9：35离开。下午15：00到客户东直门中学，15：45离开。下午16：30到东坝京客隆，16：45离开。17：10到客户姚家园家乐福，18：00离开
			createVisitPlan(ID_杨乐乐,REG_冠华大厦,  "to_char(to_date('2011-07-14','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_杨乐乐,REG_东直门中学,  "to_char(to_date('2011-07-14','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_杨乐乐,REG_坝京客隆,  "to_char(to_date('2011-07-14','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_杨乐乐,REG_姚家园家乐福,  "to_char(to_date('2011-07-14','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.15 早上8：40到冠华大厦，9：30离开。10：50到客户西直门嘉茂，11：30离开。下午15：00到大悦城，15：25离开。17：00到客户新街口百货，18：10离开。
			createVisitPlan(ID_杨乐乐,REG_冠华大厦,  "to_char(to_date('2011-07-15','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_杨乐乐,REG_西直门嘉茂,  "to_char(to_date('2011-07-15','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_杨乐乐,REG_朝阳大悦城,  "to_char(to_date('2011-07-15','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_杨乐乐,REG_新街口百货,  "to_char(to_date('2011-07-15','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.18西直门促销活动围栏内，出入6次。
			createRule("to_char(to_date('2011-07-18','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_杨乐乐,"杨乐乐");
			//7.20早上8：55到客户客户西直门嘉茂，9：55离开。下午15：12到客户石佛营易初莲花，15：45离开。下午16：30到大悦城，16：45离开。17：10到客户姚家园家乐福，18：10离开
			createVisitPlan(ID_杨乐乐,REG_西直门嘉茂,  "to_char(to_date('2011-07-20','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_杨乐乐,REG_石佛营易初莲花,  "to_char(to_date('2011-07-20','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_杨乐乐,REG_朝阳大悦城,  "to_char(to_date('2011-07-20','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_杨乐乐,REG_姚家园家乐福,  "to_char(to_date('2011-07-20','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.21早上8：35到公司总部，9：35离开。下午15：10到客户大悦城，15：55离开。下午16：40到东坝京客隆，16：45离开。17：10到客户姚家园家乐福，18：00离开
			createVisitPlan(ID_杨乐乐,REG_朝阳大悦城,  "to_char(to_date('2011-07-21','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_杨乐乐,REG_坝京客隆,  "to_char(to_date('2011-07-21','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_杨乐乐,REG_姚家园家乐福,  "to_char(to_date('2011-07-21','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.22早上8：55到客户客户冠华大厦，9：45离开。下午15：00到客户东坝京客隆，15：45离开。下午16：30到石佛营易初莲花，16：45离开。17：10到客户新街口百货，18：20离开
			createVisitPlan(ID_杨乐乐,REG_冠华大厦,  "to_char(to_date('2011-07-22','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_杨乐乐,REG_坝京客隆,  "to_char(to_date('2011-07-22','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_杨乐乐,REG_石佛营易初莲花,  "to_char(to_date('2011-07-22','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_杨乐乐,REG_新街口百货,  "to_char(to_date('2011-07-22','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.25  7.26周一北京出差去西安，周二返回北京
			createTravelPlan("6101","to_char(to_date('2011-07-25','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')","to_char(to_date('2011-07-26','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_杨乐乐);
			//7.27早上8：55到客户客户东直门中学，9：25离开。下午15：00到新街口百货，15：45离开。下午16：20到大悦城，16：55离开。17：10到客户姚家园家乐福，17：40离开
			createVisitPlan(ID_杨乐乐,REG_东直门中学,  "to_char(to_date('2011-07-27','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_杨乐乐,REG_新街口百货,  "to_char(to_date('2011-07-27','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_杨乐乐,REG_朝阳大悦城,  "to_char(to_date('2011-07-27','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_杨乐乐,REG_姚家园家乐福,  "to_char(to_date('2011-07-27','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.29早上8：45到客户客户大悦城，9：35离开。下午14：40到客户石佛营易初莲花，15：25离开。下午16：00到东坝京客隆，16：45离开。17：10到东直门中学，18：12离开
			createVisitPlan(ID_杨乐乐,REG_朝阳大悦城,  "to_char(to_date('2011-07-29','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_杨乐乐,REG_石佛营易初莲花,  "to_char(to_date('2011-07-29','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_杨乐乐,REG_坝京客隆,  "to_char(to_date('2011-07-29','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_杨乐乐,REG_东直门中学,  "to_char(to_date('2011-07-29','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//8.1早上8：30公司总部，9：45离开。下午15：40到客户大悦城，15：55离开。下午16：35到新街口百货，16：45离开。17：20到西直门嘉茂，18：05离开
			createVisitPlan(ID_杨乐乐,REG_朝阳大悦城,  "to_char(to_date('2011-08-01','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_杨乐乐,REG_新街口百货,  "to_char(to_date('2011-08-01','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_杨乐乐,REG_西直门嘉茂,  "to_char(to_date('2011-08-01','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//8.2早上8：46到客户客户冠华大厦，9：35离开。下午15：00到客户新街口百货，15：45离开。下午16：30到客户石佛营易初莲花，17：00离开。17：30到客户姚家园家乐福，18：00离开
			createVisitPlan(ID_杨乐乐,REG_冠华大厦,  "to_char(to_date('2011-08-02','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_杨乐乐,REG_新街口百货,  "to_char(to_date('2011-08-02','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_杨乐乐,REG_石佛营易初莲花,  "to_char(to_date('2011-08-02','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_杨乐乐,REG_姚家园家乐福,  "to_char(to_date('2011-08-02','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//8.4 周四北京出差去杭州廊坊 
			createTravelPlan("3301,1310","to_char(to_date('2011-08-04','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')","to_char(to_date('2011-08-04','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_杨乐乐);
			//8.5周五杭州出差去上海。
			createTravelPlan("31","to_char(to_date('2011-08-05','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')","to_char(to_date('2011-08-05','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_杨乐乐);
		
		}
	}
	
	// 王丽丽  402882e5314b569901314be146ca0018
	private void createWangliliData() {
		for (int i = 0; i <= 12; i++) {
			//7.5早上8：50到客户姚家园家乐福，9：30离开。下午15：00到客户冠华大厦，15：05离开。17：40到客户石佛营易初莲花，18：30离开。
			createVisitPlan(ID_王丽丽,REG_姚家园家乐福,  "to_char(to_date('2011-07-05','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_王丽丽,REG_冠华大厦,  "to_char(to_date('2011-07-05','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_王丽丽,REG_石佛营易初莲花,  "to_char(to_date('2011-07-05','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.6西直门促销活动围栏内，出入10次。
			createRule("to_char(to_date('2011-07-06','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_杨乐乐,"王丽丽");
			//7.7周三北京出差去河北廊坊，当天回北京。
			createTravelPlan("1310","to_char(to_date('2011-07-07','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')","to_char(to_date('2011-07-07','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_王丽丽);
			//7.8早上9：25到客户姚家园家乐福，10：00离开。下午13：00到新街口百货，13：00离开。18：00到东直门中学，19：20离开。
			createVisitPlan(ID_王丽丽,REG_姚家园家乐福,  "to_char(to_date('2011-07-08','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_王丽丽,REG_新街口百货,  "to_char(to_date('2011-07-08','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_王丽丽,REG_东直门中学,  "to_char(to_date('2011-07-08','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.11  7.12周三北京出差去湖北、湖南，周四回到北京
			createTravelPlan("42,43","to_char(to_date('2011-07-11','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')","to_char(to_date('2011-07-12','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_王丽丽);
			//7.13早上8：38到客户西直门嘉茂，9：00离开。下午14：00到公司总部，14：15离开。16：40到东直门中学，17：25离开。
			createVisitPlan(ID_王丽丽,REG_西直门嘉茂,  "to_char(to_date('2011-07-13','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_王丽丽,REG_东直门中学,  "to_char(to_date('2011-07-13','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.15西直门促销活动围栏内，出入8次。
			createRule("to_char(to_date('2011-07-15','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_杨乐乐,"王丽丽");
			//7.20 早上8：50到客户冠华大厦，9：40离开。12：10到公司总部，13：30离开。16：40到客户石佛营易初莲花，17：25离开。
			createVisitPlan(ID_王丽丽,REG_冠华大厦,  "to_char(to_date('2011-07-20','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_王丽丽,REG_石佛营易初莲花,  "to_char(to_date('2011-07-20','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.21 7.22北京出差去天津、太原，回到北京
			createTravelPlan("12,1401","to_char(to_date('2011-07-21','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')","to_char(to_date('2011-07-22','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_王丽丽);
			//7.26早上9：25到客户新街口百货，10：00离开。下午13：00到姚家园家乐福，14：30离开。16：20到东直门中学，17：10离开。
			createVisitPlan(ID_王丽丽,REG_新街口百货,  "to_char(to_date('2011-07-26','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_王丽丽,REG_姚家园家乐福,  "to_char(to_date('2011-07-26','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_王丽丽,REG_东直门中学,  "to_char(to_date('2011-07-26','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.28早上8：40到公司总部，9：00离开。10：50到客户冠华大厦，11：00离开。17：00到客户石佛营易初莲花，18：10离开。
			createVisitPlan(ID_王丽丽,REG_冠华大厦,  "to_char(to_date('2011-07-28','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_王丽丽,REG_石佛营易初莲花,  "to_char(to_date('2011-07-28','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.29北京出差去石家庄，当天回北京。
			createTravelPlan("1301","to_char(to_date('2011-07-29','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')","to_char(to_date('2011-07-29','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_王丽丽);
			//8.1早上8：50到客户石佛营易初莲花，9：35离开。下午15：00到客户冠华大厦，15：15离开。17：10到客户姚家园家乐福，18：00离开。
			createVisitPlan(ID_王丽丽,REG_石佛营易初莲花,  "to_char(to_date('2011-08-01','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_王丽丽,REG_冠华大厦,  "to_char(to_date('2011-08-01','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_王丽丽,REG_姚家园家乐福,  "to_char(to_date('2011-08-01','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//8.3 8.4周三北京出差去上海，周四晚上回到北京。
			createTravelPlan("31","to_char(to_date('2011-08-03','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')","to_char(to_date('2011-08-04','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_王丽丽);
			//8.5 早上8：55到客户东直门中学，9：30离开。下午14：00到客户石佛营易初莲花，14：45离开。17：30到客户冠华大厦，21：20离开。
			createVisitPlan(ID_王丽丽,REG_东直门中学,  "to_char(to_date('2011-08-05','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_王丽丽,REG_石佛营易初莲花,  "to_char(to_date('2011-08-05','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_王丽丽,REG_冠华大厦,  "to_char(to_date('2011-08-05','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
		}
	}
	// 陈大伟 ID：402882e5314c7a0601314f40c1530001
	private void createChenDaweiData() {
		for (int i = 0; i <= 12; i++) {
			//7.4早上8：50到客户石佛营易初莲花，9：35离开。下午15：00到客户冠华大厦，15：15离开。17：10到客户姚家园家乐福，18：00离开。
			createVisitPlan(ID_陈大伟,REG_石佛营易初莲花,  "to_char(to_date('2011-07-04','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_陈大伟,REG_冠华大厦,  "to_char(to_date('2011-07-04','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_陈大伟,REG_姚家园家乐福,  "to_char(to_date('2011-07-04','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.6西直门促销活动围栏内，出入10次。
			createRule("to_char(to_date('2011-07-06','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_陈大伟,"陈大伟");
			//7.7早上8：40到公司总部，9：00离开。10：50到客户冠华大厦，11：00离开。17：00到客户石佛营易初莲花，18：10离开。
			createVisitPlan(ID_陈大伟,REG_冠华大厦,  "to_char(to_date('2011-07-07','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_陈大伟,REG_石佛营易初莲花,  "to_char(to_date('2011-07-07','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.8北京出差去石家庄，当天回北京。
			createTravelPlan("1301","to_char(to_date('2011-07-08','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')","to_char(to_date('2011-07-08','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_陈大伟);
			//7.11 7.12 周三北京出差去湖北、湖南，周四回到北京
			createTravelPlan("42,43","to_char(to_date('2011-07-11','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')","to_char(to_date('2011-07-12','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_陈大伟);
			//7.13早上8：38到客户西直门嘉茂，9：00离开。下午14：00到公司总部，14：15离开。16：40到东直门中学，17：25离开。
			createVisitPlan(ID_陈大伟,REG_西直门嘉茂,  "to_char(to_date('2011-07-13','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_陈大伟,REG_东直门中学,  "to_char(to_date('2011-07-13','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.15西直门促销活动围栏内，出入8次。
			createRule("to_char(to_date('2011-07-15','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_陈大伟,"陈大伟");
			//7.20 7.21 周三北京出差去上海，周四晚上回到北京。
			createTravelPlan("31","to_char(to_date('2011-07-20','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')","to_char(to_date('2011-07-21','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_陈大伟);
			//7.22 早上8：55到客户东直门中学，9：30离开。下午14：00到客户石佛营易初莲花，14：45离开。17：30到客户冠华大厦，21：20离开。
			createVisitPlan(ID_陈大伟,REG_东直门中学,  "to_char(to_date('2011-07-22','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_陈大伟,REG_石佛营易初莲花,  "to_char(to_date('2011-07-22','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_陈大伟,REG_冠华大厦,  "to_char(to_date('2011-07-22','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.25 早上8：50到客户石佛营易初莲花，9：35离开。下午15：00到客户冠华大厦，15：15离开。17：10到客户姚家园家乐福，18：00离开。
			createVisitPlan(ID_陈大伟,REG_石佛营易初莲花,  "to_char(to_date('2011-07-25','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_陈大伟,REG_冠华大厦,  "to_char(to_date('2011-07-25','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_陈大伟,REG_姚家园家乐福,  "to_char(to_date('2011-07-25','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//7.27 7.28 周三北京出差去上海，周四晚上回到北京。
			createTravelPlan("31","to_char(to_date('2011-07-27','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')","to_char(to_date('2011-07-28','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_陈大伟);
			//7.29 早上8：55到客户东直门中学，9：30离开。下午14：00到客户石佛营易初莲花，14：45离开。17：30到客户冠华大厦，21：20离开。
			createVisitPlan(ID_陈大伟,REG_东直门中学,  "to_char(to_date('2011-07-29','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_陈大伟,REG_石佛营易初莲花,  "to_char(to_date('2011-07-29','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_陈大伟,REG_冠华大厦,  "to_char(to_date('2011-07-29','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//8.2 早上8：50到客户姚家园家乐福，9：30离开。下午15：00到客户冠华大厦，15：05离开。17：40到客户石佛营易初莲花，18：30离开。
			createVisitPlan(ID_陈大伟,REG_姚家园家乐福,  "to_char(to_date('2011-08-02','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_陈大伟,REG_冠华大厦,  "to_char(to_date('2011-08-02','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_陈大伟,REG_石佛营易初莲花,  "to_char(to_date('2011-08-02','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			//8.3  8.4 北京出差去天津、太原，回到北京
			createTravelPlan("12,1401","to_char(to_date('2011-08-03','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')","to_char(to_date('2011-08-04','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')",ID_陈大伟);
			//8.5 早上8：50到客户冠华大厦，9：40离开。12：10到公司总部，13：30离开。16：40到客户石佛营易初莲花，17：25离开。
			createVisitPlan(ID_陈大伟,REG_冠华大厦,  "to_char(to_date('2011-08-05','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
			createVisitPlan(ID_陈大伟,REG_石佛营易初莲花,  "to_char(to_date('2011-08-05','yyyy-mm-dd') + " + i * 35 + ",'yyyy-mm-dd')");
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
