package com.yuol.smile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.yuol.smile.base.BaseActivity;
import com.yuol.smile.utils.Api;
import com.yuol.smile.utils.T;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.Spinner;
import android.widget.TextView;

public class RadioActivity extends BaseActivity {
	private Spinner radio_sender_school=null;
	private EditText radio_sender_name=null;
	private Spinner radio_receiver_school=null;
	private EditText radio_receiver_name=null;
	private EditText radio_sender_time=null;
	private EditText radio_music=null;
	private EditText radio_bless=null;
	private EditText radio_code_input=null;
	private TextView radio_code_output=null;
	private Button radio_submit=null;
	private String[] items={"东校区","西校区","南校区","城中校区","武汉校区"};
	private ArrayAdapter<String> adapter=null;
	private ArrayList<NameValuePair> params=null; 
	private Button gettime;
	private String code_output="";
	private String code_input="";
	private String receiver_school="";
	private String receiver_name="";
	private String sender_school="";
	private String sender_time="";
	private String sender_name="";
	private String music="";
	private String bless="";
	private ImageSwitcher is;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_radio);
		setTitle("广播点歌");
		radio_sender_school=(Spinner)super.findViewById(R.id.radio_sender_school);
		radio_receiver_school=(Spinner)super.findViewById(R.id.radio_receiver_school);
		radio_receiver_name=(EditText)super.findViewById(R.id.radio_receiver_name);
		radio_sender_name=(EditText)super.findViewById(R.id.radio_sender_name);
		radio_music=(EditText)super.findViewById(R.id.radio_music);
		radio_bless=(EditText)super.findViewById(R.id.radio_bless);
		radio_code_input=(EditText)super.findViewById(R.id.radio_code_input);
		radio_code_output=(TextView)super.findViewById(R.id.radio_code_output);
		radio_sender_time=(EditText)super.findViewById(R.id.radio_sender_time);
		radio_submit=(Button)super.findViewById(R.id.radio_submit);
		adapter=new ArrayAdapter<String>(this,R.layout.item_spinner,items);
		radio_receiver_school.setAdapter(adapter);
		radio_sender_school.setAdapter(adapter);
        int random_1=(int)(Math.random()*10);
        int random_3=(int)(Math.random()*10);
        char random_2=(char)(Math.random()*25+97);
        char random_4=(char)(Math.random()*25+97);
        code_output=String.valueOf(random_1)+String.valueOf(random_2)+String.valueOf(random_3)+String.valueOf(random_4);
        System.out.println(code_output);
        radio_code_output.setText(code_output);
        params=new ArrayList<NameValuePair>();
        radio_submit.setOnClickListener(new Button.OnClickListener(){
			@Override
		   public void onClick(View v) {
		        receiver_name=radio_receiver_name.getText().toString();
		        sender_name=radio_sender_name.getText().toString();
		        sender_time=radio_sender_time.getText().toString();
		        music=radio_music.getText().toString();
		        bless=radio_bless.getText().toString();
		        code_input=radio_code_input.getText().toString();
		        sender_school=items[radio_sender_school.getSelectedItemPosition()];
				receiver_school=items[radio_receiver_school.getSelectedItemPosition()];
				params.add(new BasicNameValuePair("toplace",receiver_school));
		        params.add(new BasicNameValuePair("to",receiver_name));
		        params.add(new BasicNameValuePair("fromplace",sender_school));
		        params.add(new BasicNameValuePair("from",sender_name));
		        params.add(new BasicNameValuePair("date",sender_time));
		        params.add(new BasicNameValuePair("words",bless));
		        params.add(new BasicNameValuePair("song",music));
		        params.add(new BasicNameValuePair("checkcode",""));
		        params.add(new BasicNameValuePair("action","add"));
		        params.add(new BasicNameValuePair("submit","submit"));
		        params.add(new BasicNameValuePair("","reset"));
				if(receiver_name.equalsIgnoreCase("")||bless.equalsIgnoreCase("")||sender_name.equalsIgnoreCase("")||music.equalsIgnoreCase("")||sender_time.equalsIgnoreCase(""))
				{
					T.showShort(RadioActivity.this,"请认真填写！");
				}
				else {
					if(code_output.equalsIgnoreCase(code_input)){
						 AsyncTask_Radio asyncTask=new AsyncTask_Radio(RadioActivity.this,params);
						 asyncTask.execute();
						 Boolean info=false;
						 try{
							info=asyncTask.get();
						 }
						 catch(Exception e){
							 e.printStackTrace();
						 }
						 if(info)
							 T.showShort(RadioActivity.this,"恭喜你点歌成功，欢迎下次光临");
							 else
								 T.showShort(RadioActivity.this,"很抱歉，点歌失败，请重试或在设置中提交反馈信息");
						  }
						  else T.showShort(RadioActivity.this,"验证码输入错误，请重新输入！");
				}
			}
        
			  
        });
        radio_sender_time.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Calendar calendar=Calendar.getInstance();
				calendar.setTime(new Date());
				DatePickerDialog picker=new DatePickerDialog(RadioActivity.this,new MyListener(),calendar.get(calendar.YEAR), calendar.get(calendar.MONTH), calendar.get(calendar.DAY_OF_MONTH));
				picker.show();
			}
		});
	}
	public class MyListener implements  DatePickerDialog.OnDateSetListener {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			radio_sender_time.setText(year+"-"+(monthOfYear+1)+"-"+dayOfMonth);
		}
		
	}
	public void doBack(View v)
	{
		finish();
	}
	class AsyncTask_Radio extends AsyncTask<Integer,String,Boolean> {
		Context context=null;
		ArrayList<NameValuePair> list=null;
		public AsyncTask_Radio(Context context,ArrayList<NameValuePair> list){
			this.context=context;
			this.list=list;
		}
		@Override
		protected Boolean doInBackground(Integer... params) {
			HttpResponse httpResponse = null; 
			HttpPost httppost=null;
			Boolean info=false;
			String action=Api.URL_SONG;
	        httppost=new HttpPost(action);
	        try{
	        	httppost.setEntity(new UrlEncodedFormEntity(list,"gb2312"));
	    	    httpResponse = new DefaultHttpClient().execute(httppost); 
	        }
	        catch(Exception e)
	          {System.out.println(e);}
	        if(httpResponse.getStatusLine().getStatusCode()==200)
	        { 
	        	info=true;
	        	String result=null;
				try {
					result = EntityUtils.toString(httpResponse.getEntity(),"gb2312");
				} catch (ParseException e) {
					e.printStackTrace();
				} catch (IOException e) {

					e.printStackTrace();
				} 
	        }
	        else {
	        	info=false;
	        }
		    return info;
		
	}
	}

}
