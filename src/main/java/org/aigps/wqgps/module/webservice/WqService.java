
package org.aigps.wqgps.module.webservice;

import javax.jws.WebService;

@WebService
public interface WqService {

	/**
	 * 创建或者修改公司
	 * 
	 * @param adminName			公司管理员用户名
	 * @param adminPassword　	公司管理员密码
	 * @param companyName　		公司名称
	 * @param companyLinkMan	公司联系人
	 * @param companyPhone　		公司电话
	 * 
	 * @return true:创建或修改成功;其它:错误的提示信息
	 */
	public String createOrUpdateCompany(String adminName,String adminPassword,String companyName,String companyLinkMan,String companyPhone);
	
	/**
	 * 创建单个员工
	 * 
	 * @param adminName 		公司管理员用户名
	 * @param adminPassword　	公司管理员密码
	 * @param staffName　		员工姓名
	 * @param staffPhone　		员工手机号
	 * @param startWorkTime　	上班时间(09:00)
	 * @param endWorkTime　		下班时间(18:00)
	 * @param locateInterval　	定位间隔(秒)
	 * @param locateModel　		定位方式(0:MSA;1:GOOGLE;2:GPS;3:MSA->GPS;4:GPS->GOOGLE;5:GPS->MSA;6:GPS->MSA->GOOGLE;)
	 *
	 * @return true:创建成功;其它:错误的提示信息
	 */
	public String createStaff(String adminName,String adminPassword,String staffName,String staffPhone,String startWorkTime,String endWorkTime,String locateInterval,String locateModel);
	
	/**
	 * 删除单个员工
	 * @param adminName 公司管理员用户名
	 * @param adminPassword　公司管理员密码
	 * @param staffPhone 删除的员工手机号
	 * @return true:创建成功;其它:错误的提示信息
	 */
	public String deleteStaff(String adminName,String adminPassword,String staffPhone);

	/**
	 * 获取多个员工的定位信息
	 * 
	 * @param adminName 公司管理员用户名
	 * @param adminPassword 公司管理员密码
	 * @param staffPhones 员工手机号，多个员工以逗号分开  13438764746,13438764748,13438764749
	 * @param second 获取最近second秒的定位，没有则进行即时定位；second为0时直接进行即时定位
	 * @param responseHttpUrl 定位结果通过HTTP URL返回
	 * 返回格式: responseHttpUrl?time=时间如(20120514160059)&lng=经度如(121.600939)&lat=纬度如(30.03622949)&precision=精度如(19)&height=海拔如(54)&direction=方向如(90)&speed=速度如(0)&addr=地址如(北京市海淀区)
	 *
	 * @return true:定位操作成功;其它:错误的提示信息
	 */
	public String getStaffsLocation(String adminName,String adminPassword,String staffPhones,long second,String responseHttpUrl);

	/**
	 * 通过地址，查询包括该地址位置的电子围栏信息
	 * @param userName 登录系统的用户名
	 * @param password 登录系统的密码
	 * @param addr 查询的地址
	 * @param customer 查询的客户名称
	 * @return	查询异常：-1
	 * 			用户登录失败：0
	 * 			查询无数据：1
	 * 			有数据：区域名称|区域编号|网点编号|网点名称|围栏类型|经纬度|半径
	 * 			围栏类型:1矩形；2多边形；3圆形
	 * 			经纬度:lng1,lat1;lng2,lat2...
	 * 			半径:当围栏类型是圆形有值，否则为0
	 */
	public String getRegionByAddr(String userName, String password, String addr, String customer);
}

