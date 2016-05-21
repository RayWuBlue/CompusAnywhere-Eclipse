package com.yuol.smile.config;

import com.yuol.smile.utils.Sha1Util;

public class DecodeConfig {
	
	//ͷ������
	public static String decodeHeadImg(String uid){
		return new Sha1Util().getDigestOfString(("p"+uid+"w").getBytes());
	}
	
	//��ȡͷ�����ص�ַ
	public static String getHeadUrlById(String uid){
		return ServerConfig.HOST+"/schoolknow/manage/head/"+DecodeConfig.decodeHeadImg(uid)+".pw";
	}

}
