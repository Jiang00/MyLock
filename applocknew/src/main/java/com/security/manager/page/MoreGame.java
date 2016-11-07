package com.security.manager.page;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MoreGame extends Activity {
	WebView wv;
	ProgressDialog pd;
    public static String GOOGLE_MARKET = "market://";
    public static String GOOGLE_MARKET_HTTPS = "https://play.google.com/store/apps/";
    public static String GOOGLE_PLAY_STORE_PACKAGE = "com.android.vending";
    public static int LOAD_TIME_OUT = 30000;

	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	public void onCreate(Bundle b) {
		super.onCreate(b);
		wv = new WebView(this);
		WebSettings s = wv.getSettings();
		s.setJavaScriptEnabled(true);
		s.setLoadsImagesAutomatically(true);
		s.setAllowFileAccess(true);
		s.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//		s.setCacheMode(WebSettings.LOAD_DEFAULT);
		s.setDefaultTextEncodingName("utf-8");
		wv.requestFocus();
		pd = ProgressDialog.show(this, null, null);
		pd.setCancelable(true);
		pd.setContentView(new ProgressBar(this));
		
		// wv.setOnKeyListener(new View.OnKeyListener() {
		// public boolean onKey(View v, int keyCode, KeyEvent event) {
		// if (keyCode == KeyEvent.KEYCODE_BACK && wv.canGoBack()) {
		// wv.goBack();
		// return true;
		// } else {
		// finish();
		// }
		// return false;
		// }
		// });
		WebViewClient wvc = new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.startsWith(GOOGLE_MARKET)) {
					try {
						Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url))
								.setPackage(GOOGLE_PLAY_STORE_PACKAGE);
						if (MoreGame.this.getPackageManager().queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY).size() > 0) {
							MoreGame.this.startActivity(i);
						} else {
							MoreGame.this.startActivity(new Intent(Intent.ACTION_VIEW,
											Uri.parse(url.replace(GOOGLE_MARKET, GOOGLE_MARKET_HTTPS))));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					return true;
				} else {
					return false;
				}
			}

			boolean isTimeOut = true;

			@Override
			public void onPageStarted(final WebView view, String url,
					Bitmap favicon) {
				new Thread() {
					@Override
					public void run() {
						try {
							Thread.sleep(LOAD_TIME_OUT);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if (isTimeOut) {
							dismissPd();
							loadFailure();
						}
					}
				}.start();
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				isTimeOut = false;
				dismissPd();				
			}

			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				Log.w("MoreGameWeb", "errorcode " + errorCode + " desc " + description + " failing url " + failingUrl);
//				if (!failingUrl.equals(PluginConfig.FETCH_GAME_ERROR_URL)) {
					loadFailure();
//				}
				dismissPd();
			}
		};
		wv.addJavascriptInterface(new JavaScriptObject(this), JavaScriptObject.NAME);
		wv.setWebViewClient(wvc);
		String url = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        loadUrl(url);
        this.setContentView(wv);
	}
	
	private void dismissPd() {
		if(pd != null) {
			try {
				pd.dismiss();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	protected void loadFailure() {
//		this.runOnUiThread(new Runnable() {
//
//Override
//			public void run() {
//				wv.stopLoading();
//				wv.loadUrl(PluginConfig.FETCH_GAME_ERROR_URL);
//			}
//		});
	}

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        return (info != null && info.isConnected());
    }
	
	private void loadUrl(final String url) {
		if (isNetworkAvailable(this)) {
//			this.checkVersion();
			MoreGame.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					wv.stopLoading();
					wv.loadUrl(url);
				}
			});
		} else {
			this.loadFailure();
		}
	}

//	protected void checkVersion() {
//		final int version = this.getPreferences(MODE_PRIVATE).getInt("version", 0);
//		HttpUtils.makeRequest(false, PluginConfig.FETCH_GAME_INFO_URL,
//				new HttpUtils.IReceiveListener() {
//
//Override
//					public void onSuccess(byte[] buffer) throws Exception {
//						String json = new String(buffer);
//						final JSONObject obj = new JSONObject(json);
//
//						if (obj.getInt("version") > version) {
//							if (obj.getInt("zip") != 0
//									&& (Environment.getExternalStorageState()
//											.equals(Environment.MEDIA_MOUNTED))) {
//								cacheZipfile(obj);
//								return;
//							}
//						}
//
//						String _url;
//						if (obj.getInt("zip") != 0
//								&& (Environment
//										.getExternalStorageState()
//										.equals(Environment.MEDIA_MOUNTED_READ_ONLY) || Environment
//										.getExternalStorageState().equals(
//												Environment.MEDIA_MOUNTED))) {
//							_url = PluginConfig.FETCH_GAME_INFO_LOCAL_URL;
//						} else {
//							_url = obj
//									.getString("index")
//									.replace("$PLATFORM", PluginConfig.CONF_PLATFORM)
//									.replace("$LANG",
//											PluginUtils.lang(MoreGameWeb.this));
//						}
//						final String url = _url;
//						MoreGameWeb.this.runOnUiThread(new Runnable() {
//Override
//							public void run() {
//								wv.loadUrl(url);
//							}
//						});
//						getPreferences(MODE_PRIVATE).edit()
//								.putInt("version", obj.getInt("version"))
//								.commit();
//					}
//
//					private void cacheZipfile(final JSONObject obj) {
//						try {
//							HttpUtils.makeRequest(
//									false,
//									obj.getString("index").replace("$PLATFORM",
//											PluginConfig.CONF_PLATFORM),
//									new HttpUtils.IReceiveListener() {
//
//Override
//										public void onFailure(String message) {
//											loadFailure();
//										}
//
//Override
//										public void onSuccess(byte[] buffer)
//												throws Exception {
//											Zip.extract(
//													new ZipInputStream(
//															new ByteArrayInputStream(
//																	buffer)),
//													Environment.getExternalStorageDirectory()
//															.getAbsolutePath()
//															+ "/.more/");
//											getPreferences(MODE_PRIVATE)
//													.edit().putInt("version", obj.getInt("version"))
//													.commit();
//											MoreGameWeb.this.runOnUiThread(new Runnable() {
//Override
//														public void run() {
//															wv.clearCache(true);
//															wv.loadUrl(PluginConfig.FETCH_GAME_INFO_LOCAL_URL);
//														}
//													});
//										}
//									}, false);
//						} catch (JSONException e) {
//							e.printStackTrace();
//						}
//					}
//
//Override
//					public void onFailure(String message) {
//						loadFailure();
//						pd.dismiss();
//					}
//				}, false);
//	}
	
	public static void show(String url) {
		_context.startActivity(new Intent(_context, MoreGame.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra(Intent.EXTRA_TEXT, url));
	}
	
	public static void show(Context context, String url) {
		if(_context == null)
			_context = context;
		_context.getApplicationContext().startActivity(new Intent(_context, MoreGame.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra(Intent.EXTRA_TEXT, url));
	}

	public static void setContext(Activity context) {
		_context = context;	
	}

	private static Context _context = null;
}

class JavaScriptObject {
	public static final String NAME = "hlsdk";
	private Context context;
	public JavaScriptObject(Context context) {
		this.context = context;
	}
	@JavascriptInterface
	public void share(String title, String text) {
		Intent it = new Intent(Intent.ACTION_SEND);
		it.setType("text/plain");
		it.putExtra(Intent.EXTRA_SUBJECT, title);
		it.putExtra(Intent.EXTRA_TEXT, text);
		context.startActivity(Intent.createChooser(it, text));
	}

	@JavascriptInterface
	public void makeText(String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	@JavascriptInterface
	public void access(String url) {
		try {
			if (url.indexOf("://") < 0) {
				String market = "market://details?id=" + url;
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(market)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
			} else {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
//				Toast.makeText(context, intent.toString() + context.toString(), Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			String market = "https://play.google.com/store/apps/details?id=" + url;
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(market)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
	}

}
