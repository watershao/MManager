package com.qrobot.mm.datalistener;

import android.os.Bundle;

public interface ErrorDataListener {

	public void OnErrorDataListener(int errorCode, byte[] msg);
	
	public void OnErrorDataListener(Bundle bundle);
}
