package stepposition;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class GpsPosition extends Thread
{
	private final double meterPerAngle=111.31955*1000;
	
	private Context context=null;
	private Handler activityHandler=null;
	
	StepPosition stepposition=null;
	Gps gps=null;
	
	int interval=150;//��ȡ����λ�õļ��������Ϊ��λ������ȷ���̻߳�ȡ����λ����Ϣ��Ƶ�ʣ�
	
	boolean alive=true;//�����Ƿ������־����ΪFALSE�������������
	
	boolean gpsisvalue=true;//��⵱ǰGPS�Ƿ���Ч��TRUE��ʾGPS������ʹ��;������Ϊ����ʱ��־������GPS��λ�������GPS��λ
	Position position=null;//ͨ���Ʋ������õĽ��
	Location location=null;//ͨ��GPS��ȡ��λ����Ϣ
	Location lastLocation=null;//���һ����������ʹ�õ�GPS
	double lastLongtitude=0;
	double lastLatitude=0;
	long lastLocationTime=System.currentTimeMillis();//�ϴζ�λʱ��
	
	int gpsOutOfTime=3000;//gps������û�ж�λָʾgpsʧȥ���ӣ�����Ʋ���λģʽ
	
	/*//д����־������ʹ�ã�
	String log="";*/
	//����GPS�Ƿ�λ��ʱ����
//	String gpsout="";
	
	//Ĭ��λ����Ϣ�������޷���ȡ�״�GPS��ϢҲ����ʵ�ֶ�λ
	Location defaultLocation=null;
	
	Handler myhandler=new Handler()
	{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			position=stepposition.getPosition();
			super.handleMessage(msg);
			
		}
		
	};
	
	//�Ե�ǰ��position���Ʋ������ȡ��λ�ý��г�ʼ����
	public void resetStepPosition()
	{
		if(position!=null)
			position.reset();
	}
	
	//��ȡ��ǰ�Ķ�λ��λ�ã�activity����
	public FullLocation getLocation()
	{
		if(lastLocation==null)
			return null;
		else
		{
			FullLocation flocation=new FullLocation(lastLocation, gpsisvalue);
//			flocation.s=gpsout;
			return flocation;
		}
	}
	
	
	/**
	 * Ĭ�ϳ�ʼ��
	 */
	public GpsPosition(Context context,Handler activityHandler)
	{
		this.context=context;
		gps=new Gps(context);
		this.activityHandler=activityHandler;
		
//		stepposition=new StepPosition(context, myhandler);
		
		//�����������ڼƲ�����
//		lastLocation=new Location("Ĭ��");
//		stepposition=
	}
	
	/**
	 * �г�ʼλ�õĳ�ʼ��
	 */
	public GpsPosition(Context context,Handler activityHandler,double longtitude,double latitude)
	{
		this.context=context;
		gps=new Gps(context);
		this.activityHandler=activityHandler;
		
		defaultLocation=new Location("Ĭ��");
		defaultLocation.setLongitude(longtitude);
		defaultLocation.setLatitude(latitude);
		defaultLocation.setTime(System.currentTimeMillis());
		
		lastLocation=new Location(defaultLocation);
		//�����������ڼƲ�����
//		lastLocation=new Location("Ĭ��");
//		stepposition=
	}
	
	public void computeRelativePosition()
	{
		if(lastLocation!=null && position!=null)
		{
			//x���򱱵�ƫ�ƣ�Ҳ����γ�ȵı仯��y���򶫵�ƫ��Ҳ���Ǿ��ȵı仯
			//��������������λ���Ǳ����򣬾����Ƕ����������򶫾��Ȼ�����γ�Ȼ���
			double addlongtitude=position.getY()/meterPerAngle;
			double angle=lastLocation.getLatitude()/((double)180)*Math.PI;
			double addlatitude=position.getX()/(meterPerAngle*Math.cos(angle));
			
//			Log.i("γ����xƫ����","x="+position.getX()+"γ��:"+addlatitude+"��ĸ��"+Math.cos(angle)+"γ�ȣ�"+lastLocation.getLatitude());
			
			lastLocation.setLongitude(lastLongtitude+addlongtitude);
			lastLocation.setLatitude(lastLatitude+addlatitude);
		}
	}
	
	@Override
	public void run() 
	{
		while(alive)
		{
			location=gps.getlocation();
			//location��Ϊnull˵��GPS�Ѿ��ɹ�����
			if(location!=null)
			{
				lastLocationTime=location.getTime();
				//gps��λ��ʱ
				if(Math.abs(System.currentTimeMillis()-lastLocationTime)>gpsOutOfTime)
				{
//					gpsout="�Ʋ���λ��";
					
					if(gpsisvalue)
						gpsisvalue=false;
					
					if(stepposition==null)
					{
						stepposition=new StepPosition(context, myhandler);
						resetStepPosition();
					}
					
					computeRelativePosition();
				}
				//gps������λ
				else
				{
//					gpsout="gps��λ��";
					
					if(!gpsisvalue)
						gpsisvalue=true;
					
					lastLocation=location;
					lastLongtitude=lastLocation.getLongitude();
					lastLatitude=lastLocation.getLatitude();
					
					if(stepposition!=null)
					{
//						stepposition=null;
//						resetStepPosition();
						stepposition.clear();
					}
				}
			}
			//gpsΪnull,��Ĭ�ϳ�ʼ����λ��
			else if(defaultLocation!=null)
			{
				if(gpsisvalue)
					gpsisvalue=false;
				
				if(stepposition==null)
				{
					stepposition=new StepPosition(context, myhandler);
					resetStepPosition();
				}
				
				if(position!=null)
				{
					
					//x���򱱵�ƫ�ƣ�Ҳ����γ�ȵı仯��y���򶫵�ƫ��Ҳ���Ǿ��ȵı仯
					//��������������λ���Ǳ����򣬾����Ƕ����������򶫾��Ȼ�����γ�Ȼ���
					double addlongtitude=position.getY()/meterPerAngle;
					double angle=defaultLocation.getLatitude()/((double)180)*Math.PI;
					double addlatitude=position.getX()/(meterPerAngle*Math.cos(angle));
					
//					Log.i("γ����xƫ����","x="+position.getX()+"γ��:"+addlatitude+"��ĸ��"+Math.cos(angle)+"γ�ȣ�"+lastLocation.getLatitude());
					
					lastLocation.setLongitude(defaultLocation.getLongitude()+addlongtitude);
					lastLocation.setLatitude(defaultLocation.getLatitude()+addlatitude);
				}
			}
			/*else
				gpsout="��û��gps��λҲû�мƲ�";*/
			
			/*if(location!=null)
			{
				System.out.println("gps����ʹ��");
				
				if(!gpsisvalue)
					gpsisvalue=true;
				
				lastLocation=location;
				lastLongtitude=lastLocation.getLongitude();
				lastLatitude=lastLocation.getLatitude();
								
				if(stepposition!=null)
				{
					stepposition=null;
					resetStepPosition();
				}
			}
			else//gps�޷�������λ
			{
				System.out.println("gps�޷�ʹ��");
				
				if(gpsisvalue)
					gpsisvalue=false;
				
				if(stepposition==null)
				{
					stepposition=new StepPosition(context, myhandler);
					resetStepPosition();
				}
				
				computeRelativePosition();
			}*/
			
			/*//д����־,�Ѿ�����������
			if(lastLocation!=null)
				log+=lastLocation.getLongitude()+","+lastLocation.getLatitude()+"\n";*/
			
			try 
			{
				sleep(interval);
				Message mesg=activityHandler.obtainMessage();
				activityHandler.sendMessage(mesg);
			} 
			catch (InterruptedException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/*public void writelog()
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
			write.write(log.getBytes());
			write.close();
			
		} catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	
	public void close()
	{
		alive=false;
	}
}
