package com.qrobot.mm.netty;

import com.qrobot.mm.datalistener.ClockDataListener;
import com.qrobot.mm.datalistener.ErrorDataListener;
import com.qrobot.mm.datalistener.LoginDataListener;
import com.qrobot.mm.datalistener.UserDataListener;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class NettyClientManager {

	private static final String TAG = "NettyClientManager";
	private Context mContext;
	
	private QRClientService clientService;
	
	private DataReceiver dataReceiver;
	
	private int nSelfID;
	private int nRemoteID;
	private static final int nIDmax = 4;
	private static final int nIDmin = 1;
	private static final int nDeltMobile = 160000;
	private static final int nDeltRobot = 150000;
	
	private LoginDataListener loginDataListener;
	private ClockDataListener clockDataListener;
	private UserDataListener userDataListener;
	private ErrorDataListener errorDataListener;
	
	public NettyClientManager(){
		
	}
	
	public NettyClientManager(Context context){
		mContext = context;
	}
	
	
	
	public void setLoginDataListener(LoginDataListener loginDataListener) {
		this.loginDataListener = loginDataListener;
	}
	
	public void setClockDataListener(ClockDataListener clockDataListener) {
		this.clockDataListener = clockDataListener;
	}

	public void setUserDataListener(UserDataListener userDataListener) {
		this.userDataListener = userDataListener;
	}

	public void setErrorDataListener(ErrorDataListener errorDataListener) {
		this.errorDataListener = errorDataListener;
	}

	/**
	 * 绑定netty服务
	 */
	public void bindService(){
        Log.d(TAG, "binding.....");
        Intent intent = new Intent("com.qrobot.mm.netty.QRClientService");
        mContext.bindService(intent, nettyServiceConnection, Context.BIND_AUTO_CREATE);
    }
	
	public void unbindService(){
		mContext.unbindService(nettyServiceConnection);
	}
	
	private ServiceConnection nettyServiceConnection = new ServiceConnection() {
		
        @Override
        public void onServiceDisconnected(ComponentName name) {
        	clientService = null;
            Log.d(TAG, "in onServiceDisconnected");
        }
         
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
        	clientService = ((QRClientService.MyBinder)(service)).getService();
            Log.d(TAG, "in onServiceConnected");
        }
	};
	
	
	public QRClientService getQrClientService(){
		return clientService;
	}
	
	/**
	 * 登陆服务器
	 * @param id
	 * @return
	 */
	public int login(int id){
		System.out.println(" you click btnLogin:" +id);
		if(clientService != null){
			nSelfID = -id;
			nRemoteID = id;
			id = -id;
			int login = clientService.login(id);
			Log.d(TAG, id+"login:"+login);
//			nRemoteID = 112233;
			return login;
        }
		return -1;
	}
	
	/**
	 * 退出登陆
	 * @return
	 */
	public int logout(){
		if (clientService != null) {
			int logout = clientService.logout();
			return logout;
		}
		return -1;
	}
	
	/**
	 * 获取远程小Q id
	 * @return
	 */
	public int getRemoteId(){
		return nRemoteID;
	}
	
	/**
	 * 发送闹钟数据
	 * @param robotNo
	 * @param clockData
	 * @param dataSize
	 * @return
	 */
	public int sendClockData(int robotNo, byte[] clockData, int dataSize){
		if (clientService != null) {
			int ret = clientService.setClockData(robotNo, clockData, dataSize);
			return ret;
		}
		return -1;
	}
	
	/**
	 * 发送用户数据
	 * @param robotNo
	 * @param userData
	 * @param dataSize
	 * @return
	 */
	public int sendUserData(int robotNo, byte[] userData, int dataSize){
		if (clientService != null) {
			int ret = clientService.sendUserData(robotNo, userData, dataSize);
			Log.d(TAG, ret+",sendUserData:"+new String(userData));
			return ret;
		}
		return -1;
	}
	
	private Handler handler = new Handler();
	
	/**
	 * 添加心跳包
	 */
	public void addHeartBeat(){
		handler.postDelayed(runnable,60000); //1分钟后开始执行
	}
		
	
	/**
	 * 移除心跳包
	 */
	public void removeHeartBeat(){
		handler.removeCallbacks(runnable); //停止Timer
	}
	
	private Runnable runnable = new Runnable() {
		public void run () {
			System.out.println("sending heart beat");
			if(clientService != null){
				clientService.sendHeartBeat();
	        }
			handler.postDelayed(this,600000);//10分钟发一次心跳包
		}
	};
	
	
	
	/**
	 * 注册数据接收广播
	 */
	public void registerDataReceiver(){
		Log.d("NettyClientManager:", "register");
		dataReceiver = new DataReceiver();
        IntentFilter filter = new IntentFilter();//创建IntentFilter对象
        filter.addAction("loginNotify");
        filter.addAction("batchUsersNotify");
        filter.addAction("statusNotify");
        filter.addAction("chatDataNotify");
        filter.addAction("userDataNotify");
        filter.addAction("clockDataNotify");
        filter.addAction("errorNotify");
        mContext.registerReceiver(dataReceiver, filter);//注册Broadcast Receiver
	}
	
	/**
	 * 注销数据接收广播
	 */
	public void unregisterDataReceiver(){
		if(dataReceiver != null){
			mContext.unregisterReceiver(dataReceiver);
		}
	}
	
    private class DataReceiver extends BroadcastReceiver{//继承自BroadcastReceiver的子类
    	@Override
    	public void onReceive(Context context, Intent intent) {//重写onReceive方法
    		Log.d("NettyClientManager:", "intent:"+intent.getAction());
    		try {  	
            	Bundle bundle = intent.getExtras();	
		        //反序列化，在本地重建数据
            	String action = intent.getAction();
            	if(action.equals("loginNotify")){
//            		loginResult(bundle);
            		int ret = bundle.getInt("loginResult");
            		loginDataListener.OnLoginDataListener(ret);
		        }
            	
            	if(action.equals("batchUsersNotify")){
//            		getOnlineUserResult(bundle);
		        }
            	
            	if(action.equals("statusNotify")){
            		int qrobotNo = bundle.getInt("qrobotNo");
            		int online = bundle.getInt("online");
            		String str0 = "User "+String.valueOf(qrobotNo)+"\'s online status is "+
            			String.valueOf(online);
//            		tv01.setText(str0);
		        }
            	
            	if(action.equals("chatDataNotify")){
//            		receiveChatMsg(bundle);
		        }
            	
            	if(action.equals("userDataNotify")){
            		int robotNo = bundle.getInt("robotNo");
            		byte[] msg = bundle.getByteArray("msg");
            		int dataSize = bundle.getInt("dataSize");
            		Log.d(TAG, "receiver sendUserData:"+dataSize);
            		userDataListener.OnUserDataListener(robotNo, msg, dataSize);
            		
		        }
            	
            	if(action.equalsIgnoreCase("clockDataNotify")){
            		int robotNo = bundle.getInt("robotNo");
            		byte[] clockData = bundle.getByteArray("clockData");
            		int dataSize = bundle.getInt("dataSize");
            		
            		clockDataListener.OnClockDataListener(robotNo, clockData, dataSize);
            		
            		if(dataSize > 0){
            			String str = new String(clockData,"UTF-8");
            			Log.d("Netty clock:", "str:"+str);
            			
            		}
		        }
            	
            	if(action.equalsIgnoreCase("errorNotify")){
            		int errCode = bundle.getInt("errCode");
            		byte[] msg = bundle.getByteArray("errMsg");
            		Log.d(TAG, "receiver error:"+errCode);
            		errorDataListener.OnErrorDataListener(errCode, msg);
		        }
            	
            	
            } catch (Exception e) {
            	e.printStackTrace();
            	Log.v("test", e.toString());
            }
    	}              
    }
}
