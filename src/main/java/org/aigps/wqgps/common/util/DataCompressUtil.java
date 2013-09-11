package org.sunleads.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import flex.messaging.io.SerializationContext;
import flex.messaging.io.amf.Amf3Input;
import flex.messaging.io.amf.Amf3Output;

/**
 * 数据压缩工具类
 * @author admin
 *
 */
public class DataCompressUtil {
	private static final Log log = LogFactory.getLog(DataCompressUtil.class);
	private static int cachesize = 1024; 
	private static Deflater compresser = new Deflater(); 
	private static Inflater decompresser = new Inflater(); 
	/**
	 * java序列化转换字节并压缩
	 * @param dataObj
	 */
	public static byte[] compress(Object dataObj){
		byte[] messageBytes = null;
		ByteArrayOutputStream outStream = null;
		DataOutputStream dataOutStream = null;
		try {
			SerializationContext serializationContext = new SerializationContext(); 
			Amf3Output amf3Output = new Amf3Output(serializationContext); //格式化输出流
			outStream = new ByteArrayOutputStream(); 
			dataOutStream = new DataOutputStream(outStream); 
		    amf3Output.setOutputStream(dataOutStream); 
		    amf3Output.writeObject(dataObj); 
		    dataOutStream.flush(); 
		    messageBytes = outStream.toByteArray();
		    long beinTime = System.currentTimeMillis();
		    log.info("*********************************************");
		    log.info("[compresser before size]:"+messageBytes.length);
		    messageBytes = compressBytes(messageBytes);
		    long endTime = System.currentTimeMillis();
		    log.info("[compresser after size]:"+messageBytes.length);
		    log.info("cast time:"+(endTime-beinTime));
		    log.info("*********************************************");
		} catch (Exception e) {
			log.error("",e);
		}finally{
			try {
				if(outStream!=null){
					outStream.close();
				}
				if(dataOutStream!=null){
					dataOutStream.close();
				}
			} catch (IOException e) {
			}
		}
		return messageBytes;
	}
	
	/**
	 * flex byteArray 解压缩并反序列化
	 * @param dataByte
	 * @return
	 */
	public static Object decompress(byte[] dataByte){
		ByteArrayInputStream inStream = null;
		DataInputStream dataInputStream = null;
		Object resultObj = null;
		try {
			long beinTime = System.currentTimeMillis();
		    log.info("*********************************************");
		    log.info("[decompress before size]:"+dataByte.length);
		    byte[] newDataByte = decompressBytes(dataByte);
		    long endTime = System.currentTimeMillis();
		    log.info("[decompress after size]:"+newDataByte.length);
		    log.info("cast time:"+(endTime-beinTime));
		    log.info("*********************************************");
			
			SerializationContext serializationContext = new SerializationContext();
			Amf3Input amf3Input = new Amf3Input(serializationContext);//格式化输入流
			inStream = new ByteArrayInputStream(newDataByte);
			dataInputStream = new DataInputStream(inStream);
			amf3Input.setInputStream(inStream);
			resultObj = amf3Input.readObject();
		} catch (Exception e) {
			log.error("",e);
		}finally{
			try {
				if(inStream!=null){
					dataInputStream.close();
				}
				if(dataInputStream!=null){
					dataInputStream.close();
				}
			} catch (IOException e) {
			}
		}
	    return resultObj;
	}
	
	//压缩
	private static byte[] compressBytes(byte input[]) { 
		compresser.reset(); 
		compresser.setInput(input); 
		compresser.finish(); 
		byte output[] = new byte[0]; 
		ByteArrayOutputStream o = new ByteArrayOutputStream(input.length); 
		try{ 
			byte[] buf = new byte[cachesize]; 
			int got; 
			while (!compresser.finished()) { 
				got = compresser.deflate(buf); 
				o.write(buf, 0, got); 
			} 
			output = o.toByteArray(); 
		} finally { 
			try { 
				if(o!=null){
					o.close(); 
				}
			} catch (IOException e) { 
			} 
		} 
		return output; 
	}
	
	//解压
	private static byte[] decompressBytes(byte input[]) { 
		decompresser.reset(); 
		decompresser.setInput(input); 
		byte output[] = new byte[0]; 
		ByteArrayOutputStream o = new ByteArrayOutputStream(input.length); 
		try { 
			byte[] buf = new byte[cachesize]; 
			int got; 
			while (!decompresser.finished()) { 
				got = decompresser.inflate(buf); 
				o.write(buf, 0, got); 
			} 
			output = o.toByteArray(); 
		}catch (Exception e) {
		}finally { 
			try { 
				if(o!=null){
					o.close(); 
				}
			} catch (IOException e){ 
			} 
		} 
		return output; 
	}

}
