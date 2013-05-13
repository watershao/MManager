package com.qrobot.mm;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qrobot.mm.clock.ClockFragment;
import com.qrobot.mm.clock.ClockSync;
import com.qrobot.mm.datalistener.LoginDataListener;
import com.qrobot.mm.netty.NettyClientManager;
import com.qrobot.mm.pet.PetFragment;
import com.qrobot.mm.photo.PhotoFragment;
import com.qrobot.mm.reminder.ReminderFragment;

public class MainUIActivity	extends FragmentActivity{

	private TextView tv01;
	
	private Fragment mContent;
	private static Context mContext;
	
	private static final String TAG = "MainUIActivity";
	
	private static final String JOYCTL_ACTION = "com.qrobot.joycontrol.mobile";
	
	private static String robId;
	
	private static RelativeLayout navFooterLayout;
	private static RadioGroup radioGroup;
	private RadioButton clockRB;
	private RadioButton petRB;
	private RadioButton photoRB;
	private RadioButton joyRB;
	private RadioButton moreRB;
	
	
	/**
	 * netty client manager
	 */
	protected static NettyClientManager nClientManager;
	
	/**
	 * 是否登陆成功
	 */
	protected static volatile boolean login = false;
	
	private static ClockSync clockSync;
	
	public static ClockSync getClockSyncInstance(){
		if (clockSync != null) {
			return clockSync;
		}
		clockSync = new ClockSync(mContext, nClientManager);
		return clockSync;
	}

	public static String getRobId() {
		return robId;
	}

	public static void setRobId(String robId) {
		MainUIActivity.robId = robId;
	}

	public static void setLogin(boolean login){
		MainUIActivity.login = login;
	}
	
	public static boolean getLogin(){
		return login;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		mContext = MainUIActivity.this;
		
		Intent intent = getIntent();
		String qq = intent.getStringExtra("qq");
		
		Log.d(TAG, "qq:"+qq);
		
		setContentView(R.layout.main);
		
		Log.d(TAG, "qq:"+qq);
		
		initView();
		
		nClientManager = new NettyClientManager(mContext);
		
		registerDataReceiver();
		
		nClientManager.bindService();
		
		clockSync = new ClockSync(mContext, nClientManager);

		navFooterLayout.setVisibility(View.GONE);
		
		// set the Above View Fragment
		if (savedInstanceState != null)
			mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
		if (mContent == null)
			mContent = new QListFragment(mContext,qq);	//"395884462"
		
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.content_frame, mContent)
		.commit();
	}
	
	
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		nClientManager.addHeartBeat();
		
		print("login on start:"+login);
		if (!login) {
//			login(robId);
		}
		print("login on start 1:"+login);
	}

	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		nClientManager.removeHeartBeat();
		print("login on stop:"+login);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterDataReceiver();
		unbindNettyService();
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		//按下键盘上返回按钮
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
			
			finish();
/*			new AlertDialog.Builder(this)
				.setTitle("退出提醒")
				.setMessage("您确定要退出管理程序吗")
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					
					}
				})
				.setPositiveButton("退出", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						finish();
					}
				}).show();*/
			
			return true;
		}else{		
			return super.onKeyDown(keyCode, event);
		}
	}
	
    
    private void initView(){
    	navFooterLayout = (RelativeLayout)findViewById(R.id.nav_footer);
		radioGroup = (RadioGroup)findViewById(R.id.main_radio);
		clockRB = (RadioButton)findViewById(R.id.radio_clock);
		petRB = (RadioButton)findViewById(R.id.radio_pet);
		photoRB = (RadioButton)findViewById(R.id.radio_photo);
		joyRB = (RadioButton)findViewById(R.id.radio_joyctl);
		moreRB = (RadioButton)findViewById(R.id.radio_more);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub

				Fragment newContent = null;
				
				if (checkedId == clockRB.getId()) {
					
//					newContent = new QListFragment(mContext,"395884462");	
					newContent = new ClockFragment(mContext, nClientManager);
					
//					Toast.makeText(mContext, "checked clock", Toast.LENGTH_LONG).show();
				}
				if (checkedId == petRB.getId()) {
					
					newContent = new PetFragment(mContext, "");
//					Toast.makeText(mContext, "checked pet", Toast.LENGTH_LONG).show();
				}
				
				if (checkedId == photoRB.getId()) {
					
					newContent = new PhotoFragment(mContext, nClientManager);
//					Toast.makeText(mContext, "checked photo", Toast.LENGTH_LONG).show();
				}
				
				if (checkedId == joyRB.getId()) {
					
					if (checkApkExist(mContext, JOYCTL_ACTION)) {

						logout();
//						Intent intent = new Intent(JOYCTL_ACTION);
						Intent intent = new Intent();
						intent.setClassName(JOYCTL_ACTION, JOYCTL_ACTION+".MainUIActivity");
						
						robId = "112233";
						intent.putExtra("sRobID", robId);
						startActivity(intent);
					} else {
						Toast.makeText(mContext, "对不起，您没有安装欢乐控，请安装后使用。", Toast.LENGTH_LONG).show();
					}
					
//					Toast.makeText(mContext, "checked joy", Toast.LENGTH_LONG).show();
				}
				
				if (checkedId == moreRB.getId()) {
					
					newContent = new ReminderFragment(mContext, nClientManager);
//					Toast.makeText(mContext, "checked more", Toast.LENGTH_LONG).show();
				}
				
				if (newContent != null){
					Log.d(TAG, "switch");
					switchFragment(newContent);
				}
			}
		});
    }

	/**
	 * 切换显示内容
	 * @param fragment
	 */
	private void switchFragment(Fragment fragment) {		
		
		mContent = fragment;
		
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.content_frame, fragment)
//				.commit();
				.commitAllowingStateLoss();

	}
	
	/**
	 * 通过包名检测系统中是否安装某个应用程序
	 * @param context
	 * @param packageName
	 * @return
	 */
	private boolean checkApkExist(Context context, String packageName) {
		if (packageName == null || "".equals(packageName)) {
			return false;
		}
		try {
			context.getPackageManager().getApplicationInfo(packageName,
					PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}
	
	/**
	 * 显示导航
	 */
	public static void showNavigativon(){
		
		navFooterLayout.setVisibility(View.VISIBLE);
		radioGroup.check(R.id.radio_clock);
	}
	
	protected void registerDataReceiver(){
		if (nClientManager != null) {
			nClientManager.registerDataReceiver();
		}
	}
	
	protected void unregisterDataReceiver(){
		if (nClientManager != null) {
			nClientManager.unregisterDataReceiver();
		}
	}
	
	protected void unbindNettyService(){
		if (nClientManager != null) {
			nClientManager.unbindService();
		}
	}
	
    private static Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				
				setLogin(true);
				showNavigativon();
				
				break;
			case 2:

				setLogin(false);
				break;
			case -1:
				toastShow("登陆失败，请重试！");
				setLogin(false);
				break;
			default:
				break;
			}
		}
    	
    };
	
	public static void login(String robId) {
			final String id = "112233";
			if (id == null || id.trim().length() == 0 ) {
				toastShow("您的输入为空或不合法，请重新输入！");
				return;
			}
			
			Thread loginThread = new Thread(){
					public void run(){
						Looper.prepare();
						try {
							int loginId = Integer.parseInt(id.trim());
							print("login id:"+loginId);
							nClientManager.setLoginDataListener(loginDataListener);
							int bLogin = nClientManager.login(loginId);
							
							if (bLogin == -1) {
								mHandler.obtainMessage(-1).sendToTarget();
							}
							print("bLogin:"+bLogin);
							
						} catch (Exception e) {
							e.printStackTrace();
							toastShow("您的输入为空或不合法，请重新输入！");
						}
					}
				};
			
				loginThread.start();
			
		}
	
	private void logout() {
		if (nClientManager != null) {
			int ret = nClientManager.logout();
			if (ret == 1) {
				mHandler.obtainMessage(2).sendToTarget();
			}
		}
	}
	
	
	private static LoginDataListener loginDataListener = new LoginDataListener() {
		
		@Override
		public void OnLoginDataListener(int flag) {
			// TODO Auto-generated method stub
			if (flag == -1) {
				mHandler.obtainMessage(-1).sendToTarget();
			} else {
				mHandler.obtainMessage(1).sendToTarget();
			}
		}
	};
	
	
	private static void toastShow(String msg){
		Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
	}
	
	private static void print(String msg){
		Log.w(TAG, msg);
	}
	
}
