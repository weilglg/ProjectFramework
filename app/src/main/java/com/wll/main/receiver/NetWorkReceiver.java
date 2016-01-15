package com.wll.main.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * 网络监听
 * 
 * 
 */
public class NetWorkReceiver extends BroadcastReceiver {
	public static final int NETWORK_CONNECTED = 89100;
	public static final int NETWORK_DISCONNECTED = -89100;
	private Handler mHanlder;

	public NetWorkReceiver(Handler hanlder) {
		mHanlder = hanlder;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent == null || TextUtils.isEmpty(intent.getAction())) {
			return;
		}
		if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())
				| "android.net.conn.CONNECTIVITY_CHANGE".equals(intent
						.getAction())) {
			Parcelable parcelableExtra = intent
					.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			if (null != parcelableExtra) {
				NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
				State state = networkInfo.getState();
				boolean isConnected = state == State.CONNECTED;
				if (isConnected) {
					mHanlder.sendEmptyMessage(NETWORK_CONNECTED);
				} else {
					mHanlder.sendEmptyMessage(NETWORK_DISCONNECTED);
				}
			}
		}
	}

}
