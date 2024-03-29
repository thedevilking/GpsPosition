package stepposition;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;


public class StepPosition implements StepTrigger
{
	float peak=1f; // threshold for step detection(0.6f为默认值)

	float filter=0.18f; // value for low pass filter(0.3为默认值)

	int step_timeout_ms=450; // distance in ms between each step(400为默认值)

	float stepSize=0.6f;//0.6f为默认值
	
	private StepDetection stepDetection;
	
	double forientation=0;
	boolean addorientation=false;
	double aver_orientation=0;
//	double numori=0;
	
	int stepnum=0;
	
	//位置坐标：x为北方向的距离，y为东方向上的距离
	double x=0,y=0;
	
	Context context=null;
	
	Handler handler=null;
	
	//输出需要打印文件的数据
//	String testdata="";
	
	/**
	 * 计步定位的初始化
	 * @param context activity的引用
	 * @param handler 对获取计步位置变化的响应
	 */
	public StepPosition(Context context,Handler handler)
	{
		this.context=context;
		this.handler=handler;
		init();
	}
	
	/**
	 * 计步定位及变量初始化
	 * @param context activity的引用
	 * @param handler 对获取计步位置变化的响应
	 * @param peak threshold for step detection
	 * @param filter 过滤器 value for low pass filter
	 * @param step_timeout_ms 计步检测间隔 distance in ms between each step
	 * @param stepSize 步长
	 */
	public StepPosition(Context context,Handler handler,float peak,float filter,int step_timeout_ms ,float stepSize)
	{
		this.context=context;
		this.handler=handler;
		this.peak=peak;
		this.filter=filter;
		this.step_timeout_ms=step_timeout_ms;
		this.stepSize=stepSize;
		
		init();
	}
	
	public void init()
	{
		SensorManager sm=(SensorManager)context.getSystemService(context.SENSOR_SERVICE);
		Sensor orientation=sm.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		SensorEventListener selistener=new SensorEventListener() {
			
			@Override
			public void onSensorChanged(SensorEvent event) {
				// TODO Auto-generated method stub\
				float[] value=event.values;
				forientation=value[0];
				if(addorientation)
				{
					aver_orientation=(aver_orientation+value[0])/2;
//					sumorientation+=value[0];
//					++numori;
				}
				else
					aver_orientation=value[0];
			}
			
			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// TODO Auto-generated method stub
				
			}
		};
		sm.registerListener(selistener, orientation, SensorManager.SENSOR_DELAY_NORMAL);
		
		stepDetection = new StepDetection(context, this, filter, peak, step_timeout_ms);
		
		stepDetection.load(SensorManager.SENSOR_DELAY_FASTEST);
	}
	
	//获取返回的位置信息
	public Position getPosition()
	{
		return new Position(x, y, forientation, stepnum);
	}
	
	@Override
	public void onStepDetected(long now_ms, double compDir) {
		// TODO Auto-generated method stub
		++stepnum;
		
		
		
		
		double angle=(double)aver_orientation/((double)180)*Math.PI;
		x+=stepSize*Math.cos(angle);
		y+=stepSize*Math.sin(angle);
		
//		testdata+=x+","+y+"\n";
		
		aver_orientation=forientation;
		
		Message msg=handler.obtainMessage();
		handler.sendMessage(msg);
	}

	@Override
	public void onAccelerometerDataReceived(long now_ms, double x, double y,
			double z) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCompassDataReceived(long now_ms, double x, double y, double z) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTimerElapsed(long now_ms, double[] acc, double[] comp) {
		// TODO Auto-generated method stub
		
	}
	
	public void clear()
	{
		x=0;
		y=0;
		stepnum=0;
	}

	/*public void printLog()
	{
		try 
		{
			SimpleDateFormat   formatter   =   new   SimpleDateFormat   ("yyyy年MM月dd日 HH,mm,ss");     
			Date   curDate   =   new   Date(System.currentTimeMillis());//获取当前时间     
			
			String parentFilename=Environment.getExternalStorageDirectory()+"/StepPosition";
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
			write.write(testdata.getBytes());
			write.close();
		} catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
}
