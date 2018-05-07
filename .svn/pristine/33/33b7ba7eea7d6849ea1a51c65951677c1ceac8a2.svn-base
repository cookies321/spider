/** 
 * Project Name:spider-seeds-1229 
 * File Name:MD5Util.java 
 * Package Name:cn.jj.utils 
 * Date:2018年1月31日 下午5:37:26 
 * author 汤玉林
 */ 
package cn.jj.utils;

import java.security.MessageDigest;

/**
 * @Description: TODO
 * @author 汤玉林
 * @date 2018年1月31日 下午5:37:26 
 */
public class MD5Util {
		
	    public static void main(String[] args) {
	        String pwd = getMD5("password");
	        System.out.println(pwd);
	    }
	 
	   //生成MD5
	    public static String getMD5(String message) {
	        String md5 = "";
	        try {
	            MessageDigest md = MessageDigest.getInstance("MD5");  // 创建一个md5算法对象
	            byte[] messageByte = message.getBytes("UTF-8");
	            byte[] md5Byte = md.digest(messageByte);              // 获得MD5字节数组,16*8=128位
	            md5 = bytesToHex(md5Byte);                            // 转换为16进制字符串
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return md5;
	    }
	 
	     // 二进制转十六进制
	    public static String bytesToHex(byte[] bytes) {
	        StringBuffer hexStr = new StringBuffer();
	        int num;
	        for (int i = 0; i < bytes.length; i++) {
	        	num = bytes[i];
	             if(num < 0) {
	            	 num += 256;
	            }
	            if(num < 16){
	            	hexStr.append("0");
	            }
	            hexStr.append(Integer.toHexString(num));
	        }
	        return hexStr.toString();
	    }
}
