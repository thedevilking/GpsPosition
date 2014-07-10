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
	
	//д����־������ʹ�ã�
	String log="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
//		(new GpsPosition(this)).start();
		tv=(TextView)findViewById(R.id.tv);
		tv.setText("���ȣ�\nγ�ȣ�");
		
		Handler handler=new Handler()
		{

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				FullLocation flocation=gpsposition.getLocation();
				if(flocation!=null)
				{
					Location location=flocation.getLocation();
					tv.setText("���ȣ�"+location.getLongitude()+"\nγ�ȣ�"+location.getLatitude());
					
					if(flocation.isFullGps())
					{
						tv.setText(tv.getText()+"\nGPS��λ��");
					}
					else
					{
						tv.setText(tv.getText()+"\n�Ʋ���λ��");
					}
					
//					tv.setText(tv.getText()+"\n"+flocation.s);
					
					//д����־
					log+=location.getLongitude()+","+location.getLatitude()+"\n";
				}
				else
				{
					tv.setText("gpsû�л�ȡ�״ζ�λ");
				}
				
				super.handleMessage(msg);
			}
			
		};
		
		gpsposition=new GpsPosition(this, handler);//��ͨ��ʽ
//		gpsposition=new GpsPosition(this, handler,0,0);//��Ĭ�ϳ�ʼֵ�ķ�ʽ
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
			SimpleDateFormat   formatter   =   new   SimpleDateFormat   ("yyyy��MM��dd�� HH,mm,ss");     
			Date   curDate   =   new   Date(System.currentTimeMillis());//��ȡ��ǰʱ��     
			
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
