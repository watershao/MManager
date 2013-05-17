package com.qrobot.mm.pet.login;

import java.util.HashMap;
import java.util.List;

import android.util.Log;

public class QRClientManager {

	private static final String UPDATE_URL = "http://app31363.qzoneapp.com/qr";
	private QRClient client = null;
	
	QrResServerInfo serverInfo = null;
	
	
	public QRClientManager(){
		super();
		client = new QRClient(UPDATE_URL);;
	}


	/**
	 * ��֤����½
	 * @return
	 */
	public boolean checkToLogin(String loginInfo){
	
		String loginStr = "5A1B0000000000000198b1d8fcf84062ff40debe9e69af8890";
//		String loginStr = getLocalID();
		byte[] bData = QRUtil.HexString2Bytes(loginStr);
		client.setLoginData(0, bData);
		boolean ret = client.login();
		
		return ret;
	}
	
	/**
	 * ����СQ ID
	 * @param chipId
	 * @param sellId
	 * @return
	 */
	public String activateRobID(String chipId, String sellId){
		if (client != null) {
			HashMap mapResult = client.activeCode(0, chipId, sellId);
			if (mapResult == null || mapResult.isEmpty()) {
				return null;
			}
			String robId = String.valueOf(mapResult.get("robotCode"));
			String robKey = (String)mapResult.get("robotKey");
			String expireDate = (String)mapResult.get("expiredDate");
			Log.w("QRClientManager", "robId:"+robId+",robKey:"+robKey+",expireDate:"+expireDate);
			return robId;
		}
		return null;
	}
	/**
	 * ��ȡ�汾��Ϣ
	 * @return
	 */
	public List<QrModuleVerInfo> getModuleVerInfos(){
		int[] arModuleId = new int[]{2};
		List<QrModuleVerInfo> list = client.querySpecifiedSysModuleVers(5, arModuleId);
		
		return list;
	}
	
	/**
	 * ��ȡ�����ļ���Ϣ
	 * @return
	 */
	public List<QrModuleFileInfo> getModuleFileInfos(int oldVersion, int newVersion){
		List<QrModuleFileInfo> moduleFileInfos = client.querySpecifiedModuleUpdateInfo(5, 2, oldVersion, newVersion);
		
		return moduleFileInfos;
	}
	
	/**
	 * ���ظ����ļ���
	 * @param fileVer
	 * @param fileName
	 * @param startOffset
	 * @param destFile
	 * @return
	 */
	public int downloadSpecifiedModuleFile(int fileVer, String fileName,int startOffset, String destFile){
		if (serverInfo == null) {
			serverInfo = client.queryResServer();
		}
		int num = client.DownloadSpecifiedModuleFile(serverInfo.getServer(), serverInfo.getToken(), 5, 2, fileVer, fileName, startOffset, destFile);
		return num;		
	}
	
	/**
	 * ��ȡ����ʶ��
	 * @return
	 */
	private String getLocalID(){
		String localID = "";
		
		return localID;
	}
	
	/**
	 * ���³�����Ϣ
	 * @param nickname
	 * @param image
	 * @return
	 */
	public boolean updatePetInfo(String nickname, String image){
		boolean bUpdate = client.UpdateRobotInfo(nickname, image);
		return bUpdate;
	}
	
	/**
	 * ��ȡ������Ϣ
	 * @return
	 */
	public QrRobotInfo getPetInfo(){
		
		QrRobotInfo info = client.QueryRobotInfo();
		
		return info;
	}
}
