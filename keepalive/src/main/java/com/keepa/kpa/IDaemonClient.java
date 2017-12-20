package com.keepa.kpa;

import android.content.Context;

/**
 * 
 * @author renqingyou
 *
 */
public interface IDaemonClient {
	/**
	 * override this method by {@link android.app.Application}</br></br>
	 * ****************************************************************</br>
	 * <b>DO super.attchBaseContext() first !</b></br>
	 * ****************************************************************</br>
	 * 
	 * @param base
	 */
	void onAttachBaseContext(Context base);
}
