package com.qrobot.mm;


import com.qrobot.mm.bluetooth.BluetoothActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class HomeActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		
		Button initButton = (Button)findViewById(R.id.home_init_set);
		Button rtcButton = (Button)findViewById(R.id.home_mobile_rtc);
		
		initButton.setOnClickListener(initClickListener);
		rtcButton.setOnClickListener(rtcClickListener);
	}
	
	private OnClickListener initClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {

			startActivity(new Intent(HomeActivity.this,BluetoothActivity.class));
		}
	};
	
	private OnClickListener rtcClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {

			startActivity(new Intent(HomeActivity.this,LoginActivity.class));
		}
	};
	
}
