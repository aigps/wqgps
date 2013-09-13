/**
 * 
 */
package org.aigps.wqgps.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**�ַ���������
 * @author xiongwb
 *
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class SqlStringUtil {
	
	public final static Log log = LogFactory.getLog(SqlStringUtil.class);
	/**
	 * ���ݲ���ֵ��ȡ�������Ͻṹ��
	 * @param paramsList
	 * @return
	 * @throws Exception
	 */
	public static Object[] getSqlParams(List paramsList) throws Exception{
		Object[] params=null;
		if(paramsList!=null && paramsList.size()>0){
			params = new Object[paramsList.size()];
			for (int i = 0; i < paramsList.size(); i++) {
				params[i] = paramsList.get(i);
			}
		}else{
			params = new Object[0];
		}
		return params;
	}
	
	/**����sql�����in��ֵ
	 * @param valueList �����ַ���ֵ��List����
	 * @return
	 */
	public final static String getInSqlByStringList(String colName,List<String> valueList){
		return getInSqlByStringList(colName,valueList,true);
	}
	
	/**����sql�����in��ֵ
	 * @param colName ����
	 * @param valueList �����ַ���ֵ��List����
	 * @param isAppendStrFlag ������ַ������Ͳ�����true��������������Ͳ�����false
	 * @return
	 */
	public final static String getInSqlByStringList(String colName,List<String> valueList,boolean isAppendStrFlag){
		int buff_length = 0;
	    int spIndex = 500;
		StringBuffer stringBuffer = new StringBuffer();
		if(valueList!=null && valueList.size()>0){
			int count = valueList.size();
			int loopNum = count/spIndex;
	        if(count%spIndex!=0){
	        	loopNum += 1;
	        }
	        stringBuffer.append(" and (");
	        for(int i=0;i<loopNum;i++){
	        	stringBuffer.append(" ").append(colName + " IN(");
	            for(int j=i*spIndex,k=0;j<count && k<spIndex;j++,k++){
	            	if(isAppendStrFlag){
						stringBuffer.append("'");
					}
					stringBuffer.append(valueList.get(j));
					if(isAppendStrFlag){
						stringBuffer.append("'");
					}
					stringBuffer.append(",");
	            }
	            buff_length = stringBuffer.length();
	            stringBuffer = stringBuffer.delete(buff_length-1, buff_length);
	            stringBuffer.append(")");
	            if(i<loopNum-1){
	            	stringBuffer.append(" OR");
	            }
	        }   
	        stringBuffer.append(")");
		}
//		System.out.println(stringBuffer.toString());
		return stringBuffer.toString();
	}
	
	/**
	 * �γ�sql����е�in��䣬in�������ִ��1000��ֵ
	 * @param colName
	 * @param list
	 * @param isIn
	 * @return
	 */
    public static String formatListToSQLIn(String colName,Collection list,boolean isIn){
    	if(list==null){
    		return isIn? colName+" is null " : colName+" is not null ";
    	}
    	if(list.isEmpty()){
    		return isIn? " 1=2 " : " 1=1 ";
    	}
		StringBuffer ids = new StringBuffer("(");
        List<String> returnList = new ArrayList<String>();
        int i=0,j=999;
        for(Iterator<Object> it=list.iterator();it.hasNext();i++){
        	ids.append("'").append(it.next()).append("',");
        	if(i==j){
        		j=j+1000;
        		ids.deleteCharAt(ids.length()-1).append(")");
        		returnList.add(ids.toString());
        		ids = new StringBuffer("(");
        	}
        }
		if((j+1)/1000!=0 && ids.length()>1){
			ids.deleteCharAt(ids.length()-1).append(")");
			returnList.add(ids.toString());
        }
		StringBuffer hql = new StringBuffer(" (");
		for(Iterator<String> it=returnList.iterator();it.hasNext();){
			hql.append(colName);
			hql.append(isIn?" in":" not in");
			hql.append(it.next());
			hql.append(isIn?"  or ":" and ");
		}
		hql.delete(hql.length()-5, hql.length());
		hql.append(") ");
		
		return hql.toString();
    }
    
    /**
	 * ��ȡ���ݿ�������ΨһID(���µ�ID��ȡ��ʽ)
	 * @return
	 * @throws Exception
	 */
	public static String getUuidSequence() {
		try{
			return UUID.randomUUID().toString().replaceAll("-", "");  
		}catch (Exception e) {
			log.error("getUuidSequence fail",e);
		}
		return null;
	}
}
