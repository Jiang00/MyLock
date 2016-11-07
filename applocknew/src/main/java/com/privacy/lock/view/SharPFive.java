package com.privacy.lock.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharPFive {
	
	private Context context;
	

	public SharPFive(Context context) {
		super();
		this.context = context;
	}

     
	
	public void setFiveRate(boolean value){
		SharedPreferences preferences = context.getSharedPreferences("five_", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean("five_rate", value);
		editor.commit();
	
	}
	
	
	public boolean getFiveRate(){
		SharedPreferences preferences = context.getSharedPreferences("five_", Context.MODE_PRIVATE);
	   boolean name=preferences.getBoolean("five_rate", false);
	return name;
	}
	

	public void setEnterAppsTimes(int value){
		SharedPreferences preferences = context.getSharedPreferences("enter_apps_t", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putInt("enter_apps_t_", value);
		editor.commit();

	}

	public int getEnterAppsTimes(){
		SharedPreferences preferences = context.getSharedPreferences("enter_apps_t", Context.MODE_PRIVATE);
		int name=	preferences.getInt("enter_apps_t_", 0);
		return name;
	}

	public void setTime(int value){
		SharedPreferences preferences = context.getSharedPreferences("time_sy_s", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putInt("time_s_sj", value);
		editor.commit();

	}

	public int getTime(){
		SharedPreferences preferences = context.getSharedPreferences("time_sy_s", Context.MODE_PRIVATE);
		int name=	preferences.getInt("time_s_sj", 0);
		return name;
	}

	public boolean getFirstEnter(){
		SharedPreferences preferences = context.getSharedPreferences("firs_com", Context.MODE_PRIVATE);
		boolean name=preferences.getBoolean("fir_k", true);
		return name;
	}


	public void setFirstEnter(boolean  value){
		SharedPreferences preferences = context.getSharedPreferences("firs_com", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean("fir_k", value);
		editor.commit();

	}

	public int getFirstcometime(){
		SharedPreferences preferences = context.getSharedPreferences("first_time_ts", Context.MODE_PRIVATE);
		int name=	preferences.getInt("first_time_ts_", 0);
		return name;
	}

	public void setFirstcometime(int value){
		SharedPreferences preferences = context.getSharedPreferences("first_time_ts", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putInt("first_time_ts_", value);
		editor.commit();

	}

}
	