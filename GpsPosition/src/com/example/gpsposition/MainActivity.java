package com.example.gpsposition;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import stepposition.FullLocation;
import stepposition.GpsPosition;
import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {

	GpsPosition gpsposition=null;
	TextView tv;
	
	//写入日志（测试使用）
	String log="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
//		(new GpsPosition(this)).start();
		tv=(TextView)findViewById(R.id.tv);
		tv.setText("经度：\n纬度：");
		
		Handler handler=new Handler()
		{

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				FullLocation flocation=gpsposition.getLocation();
				if(flocation!=null)
				{
					Location location=flocation.getLocation();
					tv.setText("经度："+location.getLongitude()+"\n纬度："+location.getLatitude());
					
					if(flocation.isFullGps())
					{
						tv.setText(tv.getText()+"\nGPS定位中");
					}
					else
					{
						tv.setText(tv.getText()+"\n计步定位中");
					}
					
//					tv.setText(tv.getText()+"\n"+flocation.s);
					
					//写入日志
					log+=location.getLongitude()+","+location.getLatitude()+"\n";
				}
				else
				{
					tv.setText("gps没有获取首次定位");
				}
				
				super.handleMessage(msg);
			}
			
		};
		
		gpsposition=new GpsPosition(this, handler);//普通方式
//		gpsposition=new GpsPosition(this, handler,0,0);//有默认初始值的方式
		gpsposition.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
		gpsposition.close();
//		gpsposition.writelog();
//		gpsposition=null;
		
		writelog();
		super.onDestroy();
	}

	public void writelog()
	{
		try 
		{
			SimpleDateFormat   formatter   =   new   SimpleDateFormat   ("yyyy年MM月dd日 HH,mm,ss");     
			Date   curDate   =   new   Date(System.currentTimeMillis());//获取当前时间     
			
			String parentFilename=Environment.getExternalStorageDirectory()+"/GpsPosition";
//			Log.i("fwne",parentFilename);
			File parentFile=new File(parentFilename);
			if(!parentFile.isDirectory())
			{
				parentFile.mkdirs();
			}
			String   filename   = parentFilename+"/"+  formatter.format(curDate)+".csv";
			File file=new File(filename);
			if(!file.exists())
			{
				file.createNewFile();
			}
			BufferedOutputStream write=new BufferedOutputStream(new FileOutputStream(file));
//			if(!log.equals(""))
				write.write(log.getBytes());
			write.close();
			
		} catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
