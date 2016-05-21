package com.yuol.smile.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.app.Activity;
import android.content.Context;

public class DataCache {
	private Context context;
	public DataCache(Context context){
		this.context = context;
	}
	public void save(String url,String content) {
		try {
			String fileName = MD5Util.Md5(url);
			FileOutputStream gameData=context.openFileOutput(fileName, Activity.MODE_WORLD_READABLE);
			OutputStreamWriter dataWriter=new OutputStreamWriter(gameData);
			try {
				dataWriter.write(content);
				dataWriter.flush();
				dataWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	public String load(String url)
	{
		String str="";
		try {
			String fileName = MD5Util.Md5(url);
			FileInputStream data=context.openFileInput(fileName);
			InputStreamReader gameDataReader=new InputStreamReader(data);
			char[] inputBuffer=new char[100];
			int charRead;
			try {
				while((charRead=gameDataReader.read(inputBuffer))>0)
				{
					String readString=String.copyValueOf(inputBuffer,0,charRead);
					str+=readString;
					inputBuffer=new char[100];
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return str;
		
	}
}
