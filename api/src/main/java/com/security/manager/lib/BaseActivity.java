package com.security.manager.lib;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

/**
 * Created by SongHualin on 6/12/2015.
 */
public abstract class BaseActivity extends AppCompatActivity {
    public static final int REQ_CODE_USER = 1000;
    private boolean hasRequestCodeResult = false;
    private Intent requestData;
    private static final String REQ_CODE_KEY = "_code_";
    private static final String REQ_RESULT_KEY = "_result_";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hasRequestCodeResult = false;
        if (savedInstanceState != null){
            onRestoreInstanceStateOnCreate(savedInstanceState);
        } else {
            onIntent(getIntent());
        }
    }

    protected void onRestoreInstanceStateOnCreate(Bundle savedInstanceState){
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        onIntent(intent);
    }

    protected abstract void onIntent(Intent intent);

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (hasRequestCodeResult){
            hasRequestCodeResult = false;
            Intent requestData = this.requestData;
            this.requestData = null;
            int requestCode = requestData.getIntExtra(REQ_CODE_KEY, REQ_CODE_USER);
            int resultCode = requestData.getIntExtra(REQ_RESULT_KEY, RESULT_OK);
            requestData.removeExtra(REQ_CODE_KEY);
            requestData.removeExtra(REQ_RESULT_KEY);
            onReceiveActivityResult(requestCode, resultCode, requestData);
        }
    }

    /**
     * <pre>
     * 如果你希望在恢复此activity的时候进行结果处理，就可以使用本方法
     * 一般的应用场景是，另一个activity影响了数据量，可以在这里触发数据查询
     * 在你调用startActivityForResult的时候，requestCode必须 >= {@link #REQ_CODE_USER}
     * </pre>
     * @param requestCode >= {@link #REQ_CODE_USER}
     * @param resultCode 永远不会有 {@link #RESULT_CANCELED}，因为取消了操作以后，原则上数据应该保持不变
     * @param data 原始数据
     * @see #onActivityResult
     */
    protected void onReceiveActivityResult(int requestCode, int resultCode, @NonNull Intent data){

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode >= REQ_CODE_USER){
            hasRequestCodeResult = (resultCode != RESULT_CANCELED);
            if (data == null){
                data = new Intent();
            }
            requestData = data;
            data.putExtra(REQ_CODE_KEY, requestCode).putExtra(REQ_RESULT_KEY, resultCode);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean isDestroyed(){
        return destroyed;
    }

    private boolean destroyed;
    @Override
    protected void onDestroy() {
        destroyed = true;
        super.onDestroy();
    }
}
