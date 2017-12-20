package com.keepa.kpa.nativ;

import com.keepa.kpa.NativeDaemonBase;

import android.content.Context;

/**
 * native code to watch each other when api over 21 (contains 21)
 * @author renqingyou
 *
 */
public class NativeDaemonAPIL extends NativeDaemonBase {

	public NativeDaemonAPIL(Context context) {
		super(context);
	}

	static{
		try {
			System.loadLibrary("daemon_apil");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public native void doDaemon(String indicatorSelfPath, String indicatorDaemonPath, String observerSelfPath, String observerDaemonPath);
}
