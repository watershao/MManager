package com.qrobot.mm.active;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.qrobot.mm.util.EncryptionUtil;

import android.util.Log;


/**Interface ��Query CVM Server 
 * author :wu langlang&jolen
 * date : 2013/04/12
 */

public class QroInfoManager {
	// private String strURL = "http://app31363-10.qzoneapp.com/ac/action.php";
	private String m_strIpServer = "app31363-10.qzoneapp.com";
	private String m_strSubApp = "/ac/action.php";
	private String m_strJSONString;
	private int SO_TIMEOUT = 3000; // 5000 yuants
	private int CONNECTION_TIMEOUT = 4000; // 6000 yuants
	private int m_hostPort = 80;
	public String m_strErrorMsg = "";
	Map<String, String> CodeMap = null;

	public QroInfoManager() {
		this.CodeMap = new HashMap<String, String>();
	}

	/**
	 * ͨ��qq�Ż�ȡСQ��
	 * @param qqCode
	 * @return
	 */
	public List<String> getRobCodeByQQ(String qqCode){
		putData(AppCode.App_QQCode, qqCode);	    			
		getJson(AppCode.App_Query_CodeQ);
		
		if (queryRobCodeByQQ())	{	

			String robInfo = getRevData();
			
			try {
				JSONObject rObject = new JSONObject(robInfo);
				if (rObject.getString("sucess").equalsIgnoreCase("true")) {
					JSONArray jArray = rObject.getJSONArray("RobCode");
					if (jArray != null && jArray.length() > 0) {
						List<String> robList = new ArrayList<String>();
						for (int i = 0; i < jArray.length(); i++) {
							int robCode = jArray.getInt(i);
							robList.add(String.valueOf(robCode));
							
						}
						return robList;
					}
				} 
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		return null;
	}
	
	/**
	 * ��ȡСQоƬid
	 * @param qqCode
	 * @param robCode
	 * @return
	 */
	public String getRobKey(String qqCode, String robCode){
		putData(AppCode.App_QQCode, qqCode);
//		putData(AppCode.App_AvatorCode, "1608847893");
		putData(AppCode.App_RobCode, robCode);
		getJson(AppCode.App_Query_Key);
		if (queryAction(AppCode.App_Query_Key))	{

			return getRevData();
		}
		return null;
	}
	
	/**
	 * ͨ��avatorCode��ȡСQ��
	 * @param avatorCode
	 * @return
	 */
	public String getRobCodeByAvatorCode(String avatorCode){
		putData(AppCode.App_AvatorCode, "1608847893");
		getJson(AppCode.App_Query_CodeA);
		if (queryRobCodeByAvator()) {	
			
			return getRevData();
		}
		return null;
	}
	
	/**
	 *  ����������д���� 
	 */
	private void putData(int type, String data) {
		switch (type) {
		case AppCode.App_QQCode: {
			CodeMap.put("QQCode", data);
			break;
		}
		case AppCode.App_RobCode: {
			CodeMap.put("RobCode", data);
			break;
		}
		case AppCode.App_KeyCode: {
			CodeMap.put("KeyCode", data);
			break;
		}
		case AppCode.App_SelCode: {
			CodeMap.put("SelCode", data);
			break;
		}
		case AppCode.App_AvatorCode: {
			CodeMap.put("AvatorCode", data);
			break;
		}
		}
	}

	/**
	 * �����������ͣ�����json���������ʽ
	 * 
	 * @param type ��������
	 * @return json string
	 */
	private String getJson(int type) {
		Map<String, String> map = new HashMap<String, String>();
		String temp = "";
		switch (type) {
		case AppCode.App_Query_Key:// ��ѯ������key
		{
			if (!CodeMap.containsKey("QQCode")
					|| CodeMap.get("QQCode").isEmpty()
					|| CodeMap.get("RobCode").isEmpty()
					|| !CodeMap.containsKey("RobCode")) {
				map.clear();
				break;
			}
			temp = CodeMap.get("QQCode");
			map.put("QQCode", temp);
			temp = CodeMap.get("RobCode");
			map.put("RobCode", temp);
			map.put("type", String.valueOf(8));
			break;
		}
		case AppCode.App_Query_CodeQ: // ��ѯ���˵Ļ����˺���
		{
			if (!CodeMap.containsKey("QQCode")
					|| CodeMap.get("QQCode").isEmpty()) {
				map.clear();
				break;
			}
			temp = CodeMap.get("QQCode");
			map.put("QQCode", temp);
			map.put("type", String.valueOf(6));
			break;
		}
		case AppCode.App_Query_CodeA: // �������˵Ļ����˺����Ӧ��Avator������˺���
		{
			if (!CodeMap.containsKey("AvatorCode")
					|| CodeMap.get("AvatorCode").isEmpty()) {
				map.clear();
				break;
			}
			temp = CodeMap.get("AvatorCode");
			map.put("AvatorCode", temp);
			map.put("type", String.valueOf(7));
			break;
		}
		case AppCode.App_Query_HostQQ: // ��ѯ���˺���
		{
			if (!CodeMap.containsKey("RobCode")
					|| CodeMap.get("RobCode").isEmpty()) {
				map.clear();
				break;
			}
			temp = CodeMap.get("RobCode");
			map.put("RobCode", temp);
			map.put("type", String.valueOf(2));
		}
		case AppCode.App_Query_Avator: // �������˵Ļ����˺����Ӧ��Avator
		{
			if (!CodeMap.containsKey("AvatorCode")
					|| CodeMap.get("AvatorCode").isEmpty()) {
				map.clear();
				break;
			}
			temp = CodeMap.get("AvatorCode");
			map.put("AvatorCode", temp);
			map.put("type", String.valueOf(3));
			break;
		}
		case AppCode.App_UnBind_QQ:// �����
		{
			if (!CodeMap.containsKey("QQCode")
					|| CodeMap.get("QQCode").isEmpty()
					|| CodeMap.get("RobCode").isEmpty()
					|| !CodeMap.containsKey("RobCode")) {
				map.clear();
				break;
			}
			temp = CodeMap.get("QQCode");
			map.put("QQCode", temp);
			temp = CodeMap.get("RobCode");
			map.put("RobCode", temp);
			map.put("type", String.valueOf(5));
			break;
		}
		case AppCode.App_Bind_QQ:// ��QQ and RobCode
		{
			if (!CodeMap.containsKey("QQCode")
					|| CodeMap.get("QQCode").isEmpty()
					|| CodeMap.get("RobCode").isEmpty()
					|| !CodeMap.containsKey("RobCode")) {
				map.clear();
				break;
			}
			temp = CodeMap.get("QQCode");
			map.put("QQCode", temp);
			temp = CodeMap.get("RobCode");
			map.put("RobCode", temp);
			map.put("type", String.valueOf(1));
			break;
		}
		case AppCode.App_Bind_Avator:// ��Avator
		{
			if (!CodeMap.containsKey("AvatorCode")
					|| CodeMap.get("AvatorCode").isEmpty()
					|| CodeMap.get("RobCode").isEmpty()
					|| !CodeMap.containsKey("RobCode")) {
				map.clear();
				break;
			}
			temp = CodeMap.get("AvatorCode");
			map.put("AvatorCode", temp);
			temp = CodeMap.get("RobCode");
			map.put("RobCode", temp);
			map.put("type", String.valueOf(4));
			break;
		}
		default:
			map.clear();
			break;
		}

		if (map.isEmpty()) {
			ThrowError("error code format.");
			return "";
		}
		JSONStringer js = new JSONStringer();
		try {
			js.object();
			Set keySet = map.keySet();
			Object[] keyArray = keySet.toArray();
			String key = "";
			// String[] keys = new String[keySet.size()];
			for (int i = 0; i < keyArray.length; i++) {
				key = keyArray[i].toString();
				if (key.contains("Code")) {
					js.key(key).value(EncryptionUtil.numEncode(map.get(key)));
				} else
					js.key(key).value(map.get(key));
			}
			js.endObject();
		} catch (JSONException e) {
			// e.printStackTrace();
			return "";
		}
		return js.toString();
	}

	/**
	 * ͨ��ѡ��ͬ��type ִ����ع���
	 * 
	 * @param type ����
	 * @current AppCode.App_Query_Key | AppCode.App_Query_Code: //��ѯ���˵Ļ����˺���
	 * return
	 */
	private boolean queryAction(int type) {
		String json = this.getJson(type);
		if (json.isEmpty())
			return false;
		return SendRequest(json);
	}

	/**
	 * ͨ��QQ��ѯ�����˺���
	 * 
	 * @param strQQ QQ����
	 * 
	 * @current AppCode.App_Query_Key | AppCode.App_Query_Code: //��ѯ���˵Ļ����˺���
	 * return
	 */
	private boolean queryRobCodeByQQ() {
		String json = this.getJson(AppCode.App_Query_CodeQ);
		if (json.isEmpty())
			return false;
		return SendRequest(json);
	}

	/**
	 * ͨ��QQ��ѯ�����˺���
	 * 
	 * @param strQQ QQ����
	 * 
	 * @current AppCode.App_Query_Key | AppCode.App_Query_Code: //��ѯ���˵Ļ����˺���
	 * return
	 */
	private boolean queryRobCodeByAvator() {
		String json = this.getJson(AppCode.App_Query_CodeA);
		if (json.isEmpty())
			return false;
		return SendRequest(json);
	}

	/**
	 * ͨ��QQ��ѯ�����˺���
	 * @param strQQ QQ����
	 * @current AppCode.App_Query_Key | AppCode.App_Query_Code: //��ѯ���˵Ļ����˺���
	 * return
	 */
	private boolean queryRobKey() {
		String json = this.getJson(AppCode.App_Query_Key);
		if (json.isEmpty())
			return false;
		return SendRequest(json);
	}

	/**
	 * ����json���󵽷�����
	 * @param json string����
	 * @return true
	 */
	private boolean SendRequest(String json) {
		if (json.isEmpty())
			return false;
		m_strJSONString = "";
		try {
			DefaultHttpClient client = new DefaultHttpClient();

			// ����Socket��ʱʱ��
			client.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT,
					SO_TIMEOUT);
			// �������ӳ�ʱ��
			client.getParams()
					.setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT,
							CONNECTION_TIMEOUT);
			// �����ʽ����
			StringEntity entity = new StringEntity(json, "UTF-8");
			// ��������
			HttpPost httpost = new HttpPost(m_strSubApp);
			// ���÷���˿�
			HttpHost host = new HttpHost(m_strIpServer, m_hostPort, "http");

			httpost.setEntity(entity);

			httpost.addHeader("Content-Type", "application/json");

			HttpResponse response = client.execute(host, httpost);

			// ����״̬�룬����ɹ���������
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				m_strJSONString = getStringFromHttp(response.getEntity());
				return true;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * ��ȡ���е���ҳ��Ϣ��String ����
	 * 
	 * @param entity
	 * @return
	 */
	private String getStringFromHttp(HttpEntity entity) {
		StringBuffer buffer = new StringBuffer();
		try {
			// ��ȡ������
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					entity.getContent()));
			// �����ص����ݶ���buffer��
			String temp = null;
			while ((temp = reader.readLine()) != null) {
				buffer.append(temp);
			}
		} catch (IllegalStateException e) {

			// e.printStackTrace();
		} catch (IOException e) {
			// e.printStackTrace();
		}
		return buffer.toString();
	}

	/**
	 * prase json data
	 * @param
	 * @return Map
	 */
	private Map<String, String> getRevData1() {
		if (m_strJSONString == null || m_strJSONString.length() == 0)
			return null;
		Log.d("AA", m_strJSONString);
		JSONObject objf;
		try {
			objf = new JSONObject(m_strJSONString);

			JSONArray a = objf.names();
			Map<String, String> map = new HashMap<String, String>();
			for (int i = 0; i < a.length(); i++) {
				String key = a.getString(i);
				if (key.contains("Code"))
					map.put(key, EncryptionUtil.numDecode(objf.getString(key)));
				else
					map.put(key, objf.getString(key));
			}
			return map;
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}

	
	public String getRevData(){
		if (m_strJSONString==null || m_strJSONString.length()==0) return null;
		Log.d("AA", m_strJSONString);
		JSONObject obt;
		JSONObject objf;
		try {
			objf = new JSONObject(m_strJSONString);
			
			JSONArray a = objf.names();
			obt = new JSONObject();
			for(int i = 0 ; i < a.length() ; i++)
			{
				String key = a.getString(i);				
				if (key.contains("Code")){
					if (key.equalsIgnoreCase("RobCode")){
						JSONArray j= objf.getJSONArray("RobCode");
						//Log.w("JSON", j.getString(0));
						JSONArray out = new JSONArray();
						for (int t = 0 ; t < j.length(); t++){
							out.put(EncryptionUtil.numDecode(j.getString(t)));
						}
						obt.put(key, out);
						
					}else
						obt.put(key, EncryptionUtil.numDecode(objf.getString(key)));						
				}
				else
					obt.put(key, objf.getString(key));
			}			
			return obt.toString();
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
		return null;
	}
	
	
	private void ThrowError(String err) {
		this.m_strErrorMsg = err;
	}

	public String GetErrorMsg() {
		return this.m_strErrorMsg;
	}

	static class AppCode {
		// ��������
		public final static int App_Query_Key = 0; // key by robcode and hostqq
		public final static int App_Query_CodeQ = 1; // rob code by host qq
		public final static int App_Query_CodeA = 2; // rob code by avator
		public final static int App_Query_HostQQ = 3; // query qq by robcode
		public final static int App_Query_Avator = 4; // query avator code
		public final static int App_Bind_QQ = 5; // qq bind
		public final static int App_Bind_Avator = 6; // avator bind
		public final static int App_UnBind_QQ = 7; // remove bind

		// ���ݴ���
		public final static int App_QQCode = 0;
		public final static int App_AvatorCode = 1;
		public final static int App_RobCode = 2;
		public final static int App_SelCode = 3;
		public final static int App_KeyCode = 4;

		// Map<String,String> map = new HashMap<String,String>();
	}
	
	/*
	 * ����󶨲�ѯ��
	 *  code 1�������˺��� �ύjson ��RobCode��QQCode��type=1 
	 *  code 2����ѯ���˺��� �ύjson ��RobCode��type=2 
	 *  code 3����ѯAvator���� �ύjson ��RobCode��type=3 
	 *  code 4����Avator���� �ύjson ��RobCode��AvatorCode��type=4 
	 *  code 5��������˺���ͻ����˵İ� �ύjson ��RobCode��QQCode��type=5 
	 *  code 6���������˺����ѯСQ�� �ύjson ��QQCode��type=6
	 *  code 7������Avator�����ѯСQ�� �ύjson ��AvatorCode��type=7 
	 *  code 8������СQ��,��ѯkey�ύjson ��AvatorCode��type=8
	 * 
	 * ����json��{"QQCode":"","AvatorCode":"","sucess":true,"time":
	 * "2013-04-12 16:21:38","err":"","RobCode0":"07685"}
	 */
}
