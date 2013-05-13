package com.qrobot.mm.pet;

import java.io.IOException;
import java.io.InputStream;

import com.qrobot.mm.R;
import com.qrobot.mm.pet.login.QRClientManager;
import com.qrobot.mm.pet.login.QrRobotInfo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PetFragment extends Fragment {
	
	private static final String SHAREPREFERENCE_NAME = "set_pet";
	private LinearLayout petLayout;

	private Context mContext;
	
	private Button setInfoView;
	
	private TextView nicknamView;
	
	private TextView nicklevelView;
	
	private ImageView portraitView;
	
	private String loginInfo;
	
	private Pet pet;
	
	private OnClickListener petSetListener;
	
	public PetFragment(Context context,String loginInfo){
		mContext = context;
		this.loginInfo = loginInfo;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		petLayout = (LinearLayout)inflater.inflate(R.layout.pet_main, null);
		setInfoView = (Button)petLayout.findViewById(R.id.pet_information_set);
		nicknamView = (TextView)petLayout.findViewById(R.id.pet_nickname);
		portraitView = (ImageView)petLayout.findViewById(R.id.pet_portrait);
		nicklevelView = (TextView)petLayout.findViewById(R.id.pet_nicklevel);
		
		portraitView.setOnClickListener(petSetListener);
		setInfoView.setOnClickListener(petSetListener);
		return petLayout;
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		petSetListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, SetPet.class);
				if (pet == null) {
					pet = new Pet();
				}
				intent.putExtra("pet_info", pet);
				startActivityForResult(intent, 1);
			}
		};
	}

	

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 1:
			if (resultCode == 0) {
				Toast.makeText(mContext, "修改成功", Toast.LENGTH_LONG).show();
			} if (resultCode == 1) {
				Toast.makeText(mContext, "修改失败，请重试!", Toast.LENGTH_LONG).show();
			}if (resultCode == 2){
				Log.d("PetFragment", "cancel");
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Thread  getThread = new Thread(){
			public void run(){
				Looper.prepare();
				getServerPetInfo();
			}
		};
		try {
			getThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getThread.start();
//		initSet();
	}
    
	private void initSet(){
        SharedPreferences petSP = mContext.getSharedPreferences(SHAREPREFERENCE_NAME, 1);
        String nickname = petSP.getString("nickname", "xiao Q");
        String portrait = petSP.getString("portrait", "robot_male.png");
        int   level = petSP.getInt("nicklevel", 1);
        pet = new Pet();
        pet.nickname = nickname;
        pet.portrait = portrait;
        pet.nicklevel = level;
        
        nicknamView.setText(nickname);
        nicklevelView.setText(level + "");
        
        Bitmap bitmap = getPortraitBitmap("portrait/"+portrait);
        if (bitmap != null) {
        	portraitView.setImageBitmap(bitmap);
		}else {
			portraitView.setImageResource(R.drawable.ic_launcher);			
		}       
	}
	
	
	private Bitmap getPortraitBitmap(String fileName){
		AssetManager am = mContext.getResources().getAssets();
		InputStream is;
		try {
			is = am.open(fileName);
			Bitmap bitmap = BitmapFactory.decodeStream(is);
			return bitmap;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private boolean getServerPetInfo(){
		QRClientManager clientManager = new QRClientManager();
		boolean check = clientManager.checkToLogin(loginInfo);
		if (check){
			QrRobotInfo petInfo = clientManager.getPetInfo();
			if(petInfo != null){
				SharedPreferences petSP = mContext.getSharedPreferences(SHAREPREFERENCE_NAME, 1);
				Editor petEditor = petSP.edit();
		        petEditor.putString("nickname", petInfo.getNickName());
		        petEditor.putString("portrait", petInfo.getImage());
		        //增加宠物等级
		        petEditor.putInt("nicklevel", petInfo.getLevel());
		        Log.w("get", petInfo.getNickName()+"*"+petInfo.getImage()+"*"+petInfo.getLevel());
		        petEditor.commit();
		        mHandler.obtainMessage(1).sendToTarget();
		        return true;
			}
		}
		mHandler.obtainMessage(-1).sendToTarget();
		return false;
		
	}
	
    private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				initSet();
				
				break;
			case 2:
				
				break;
			case -1:
				Toast.makeText(mContext, "对不起，宠物信息获取失败，请重试！", Toast.LENGTH_LONG).show();
				initSet();
				break;
			default:
				break;
			}
		}
    	
    };
}
