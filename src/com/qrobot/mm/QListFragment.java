package com.qrobot.mm;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.qrobot.mm.activate.QroInfoManager;

public class QListFragment extends ListFragment {

	private Context mContext;
	
	private QroInfoManager qInfoManager;
	
	private List<String> qNumList = null;
	
	private QNumAdapter adapter = null;
	
	private String qqCode = null;
	
	public QListFragment(){
		
	}
	
	public QListFragment(Context context,String qq){
		mContext = context;
		qInfoManager = new QroInfoManager();
		qqCode = qq;
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.list, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		adapter = new QNumAdapter(mContext);
		
		qThread.start();

	}
	
	
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		
		if (qNumList != null && qNumList.size() > 0) {
			
			String robId = qNumList.get(position);
			
			MainUIActivity.setRobId(robId);
			MainUIActivity.login(robId);
//			Toast.makeText(getActivity(), robId+"current position." + position, Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(getActivity(), "对不起，请先绑定小Q.", Toast.LENGTH_LONG).show();
			
		}
		
	}

	/**
	 * 每一项
	 * @author water
	 *
	 */
	private class QNumItem {
		public String tag;
		public QNumItem(String tag) {
			this.tag = tag; 
		}
	}

	/**
	 * xiao q适配器
	 * @author water
	 *
	 */
	public class QNumAdapter extends ArrayAdapter<QNumItem> {

		public QNumAdapter(Context context) {
			super(context, 0);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.qnum_row ,null);
			}
			TextView title = (TextView) convertView.findViewById(R.id.row_title);
			title.setText(getItem(position).tag);

			return convertView;
		}

	}
	
	/**
	 * 获取小Q号列表
	 * @return
	 */
	private List<String> getQNumList(String qqCode){
		return qInfoManager.getRobCodeByQQ(qqCode);

	}
	
	Thread qThread = new Thread(){
		public void run(){
			qNumList = getQNumList(qqCode);
			
			String robKey = qInfoManager.getRobKey("395884462", "63241");
			Log.d("QList", "robKey:"+robKey);
			if (qNumList != null && qNumList.size() > 0) {
				
				mHandler.obtainMessage(0).sendToTarget();
			} else {
				mHandler.obtainMessage(-1).sendToTarget();
			}
			
		}
		
	};
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			
			switch (msg.what) {
			case 0:
				
				for (int i = 0; i < qNumList.size(); i++) {
					adapter.add(new QNumItem(qNumList.get(i)));
				}
				setListAdapter(adapter);
				
				break;
			case -1:
				adapter.add(new QNumItem("对不起，请先绑定小Q"));
				setListAdapter(adapter);
				
				break;	

			default:
				break;
			}
		}
	};
	
}
