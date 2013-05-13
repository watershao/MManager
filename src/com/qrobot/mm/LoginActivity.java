package com.qrobot.mm;


import java.io.PrintWriter;
import java.io.StringWriter;

import oicq.wlogin_sdk.request.WUserSigInfo;
import oicq.wlogin_sdk.request.WtloginHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 登录界面
 * @author Dustin
 * @date 2012-12-4
 */
public class LoginActivity extends Activity{
	
	private TextView mText; 
	private ImageButton loginBtn;
	private EditText mName;
	private EditText mPwd;
	private boolean isWifiConnected = false;
	private AlertDialog alertDialog;

	private WtloginHelperAsync helper = null;
    private Drawable mIconClear; // 文本框清除文本内容图标
    
    private String qq = null;
    
    private CheckBox remCheckBox = null;

    public class WtloginHelperAsync extends WtloginHelper { 
		public WtloginHelperAsync(Context context){
			super(context);
		}
		public void OnGetStWithPasswd(String userAccount, long dwAppid,  String userPasswd, WUserSigInfo userSigInfo, int ret)
		{ 
	      	{ 

//	      		if(true){//登录qq成功
	      		if(ret==0){//登录qq成功
	      			savePreference();
	      			Intent intent = new Intent();
	      			intent.putExtra("qq", qq);
					intent.setClass(LoginActivity.this, MainUIActivity.class);
					startActivityForResult(intent, 0);
					finish();
	      		} else {
	      			alertDialog.setTitle("登陆提示");
//		            alertDialog.setMessage("请重新输入或通过电脑绑定帐号");
	      			String msg = helper.GetLastErrMsg();
	      			if (msg == null || msg.trim().isEmpty()) {
						msg = "登陆失败，请检查账号和密码是否正确或者网络连接状况";
					}
	      			alertDialog.setMessage(msg);
		            alertDialog.show();
	      		}
	      	}	
		}
				
		public void OnException(Exception e)
		{ 
			Log.d("OnException", e.toString());
      		char[] buffer = new char[1];
      		mText.setText(buffer, 0, 0);
      		String str = e.toString();
      		mText.append( "exception:"+e.toString());
      		
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw, true);
            e.printStackTrace(pw);
            pw.flush();
            sw.flush();
            String s = sw.toString(); 
            mText.append(s); 
            Log.d("exception", s);
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		  switch (resultCode) {
		  case RESULT_OK:
		   Bundle bunde = data.getExtras();
		   String show = bunde.getString("key");
		   mName.setText(show);
		   break;
		  default:
		   break;
		  }
	}
	
	/**
	 * 文本框内容变化响应
	 */
	private TextWatcher mTextChanged = new TextWatcher() {
		//缓存上一次文本框内是否为空
		private boolean isnull = true;
		@Override
		public void afterTextChanged(Editable s) {

			if (TextUtils.isEmpty(s)) {
				if (!isnull) {
					mName.setCompoundDrawablesWithIntrinsicBounds(null,null, 
							null, null);
					isnull = true;
				}
			} else {
				if (isnull) {
					mName.setCompoundDrawablesWithIntrinsicBounds(null,null, 
							mIconClear, null);
					isnull = false;
				}
			}	
		}	
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,int after) {
		}
		@Override
		public void onTextChanged(CharSequence s, int start, int before,int count) {
		}
	
	};
	
	// 文本框触摸事件
	private OnTouchListener mTextOnTouch = new OnTouchListener() {		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
				int curX = (int) event.getX();
				String strQQ = mName.getText().toString();
				if (strQQ.equals(getResources().getString(R.string.login_name)) || 
						(curX > v.getWidth() - 50 && !TextUtils.isEmpty(mName.getText()))) {
					mName.setText("");
					int cacheInputType = mName.getInputType();
					mName.setInputType(InputType.TYPE_NULL);
					mName.onTouchEvent(event);
					mName.setInputType(cacheInputType);
					return true;
				}
				break;
			}
			return false;
		}
	};
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.qqlogin);
		getWindow().setBackgroundDrawableResource(R.drawable.loginui_bg);
		
		  
		helper = new WtloginHelperAsync(this);
		mText = (TextView) findViewById(R.id.login_text);
        mPwd = (EditText)findViewById(R.id.login_pwd);
        
        remCheckBox = (CheckBox)findViewById(R.id.login_pwd_remember);
        remCheckBox.setChecked(true);
        
        final Resources res = getResources();
        mIconClear = res.getDrawable(R.drawable.loginui_qqnumber_clear);

		mName = (EditText)findViewById(R.id.login_name);
        mName.addTextChangedListener(mTextChanged);
        mName.setOnTouchListener(mTextOnTouch);
         
        checkPreference();
        
        alertDialog = new AlertDialog.Builder(this).
        	setMessage("").
	        setNegativeButton("取消", new DialogInterface.OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	             // TODO Auto-generated method stub
	            }
	        }).
	        setPositiveButton("确定", new DialogInterface.OnClickListener() {
	        	@Override
	        	public void onClick(DialogInterface dialog, int which) {
	        		// TODO Auto-generated method stub
	        	}
	        }).create();
        
        
        	
	    /**
	     * 登录按钮及事件
	     */
	    loginBtn = (ImageButton) findViewById(R.id.loginBtn);
	    loginBtn.setBackgroundResource(R.drawable.loginui_btn_loading);

	    loginBtn.setOnTouchListener(new OnTouchListener(){   
            @Override  
            public boolean onTouch(View v, MotionEvent event) {   
                if(event.getAction() == MotionEvent.ACTION_DOWN){   
                    //更改为按下时的背景图片   
                    v.setBackgroundResource(R.drawable.loginui_btn_loading_active);   
                }else if(event.getAction() == MotionEvent.ACTION_UP){   
                    //改为抬起时的图片   
                    v.setBackgroundResource(R.drawable.loginui_btn_loading);  
                    WUserSigInfo info = new WUserSigInfo();
                    int ret;
    	        	{
    	        		String strName = mName.getText().toString();
    	        		qq = strName;
    	        		strName += "@qq.com";
    	        		String pwd = mPwd.getText().toString();
    	        		if (qq.trim().isEmpty()||pwd.trim().isEmpty()) {
							Toast.makeText(LoginActivity.this, "对不起，您的用户名或者密码为空，请重新输入。", Toast.LENGTH_SHORT).show();
							return false;
						}
    	        		ret = helper.GetStWithPasswd(strName, 0x1, pwd, info, 0);
    	        		
    	        	}
                }   
                return false;   
            }   
	    }); 
	}
	
	private static final String PREFER_QQ = "qq_preference";
	
	private void checkPreference(){
		if (remCheckBox.isChecked()) {
			SharedPreferences sp = getSharedPreferences(PREFER_QQ, MODE_PRIVATE);
			String qq = sp.getString("qq", "");
			String pwd = sp.getString("pwd", "");
			mName.setText(qq);
			mPwd.setText(pwd);
		}
		
	}
	
	private void savePreference(){
		SharedPreferences sp = getSharedPreferences(PREFER_QQ, MODE_PRIVATE);
		Editor editor = sp.edit();
		String qq = mName.getText().toString();
		String pwd = mPwd.getText().toString();
		if (remCheckBox.isChecked()) {
			editor.putString("qq", qq);
			editor.putString("pwd", pwd);
			editor.apply();
		}else {
			editor.putString("qq", "");
			editor.putString("pwd", "");
			editor.apply();
		}
	}
}
