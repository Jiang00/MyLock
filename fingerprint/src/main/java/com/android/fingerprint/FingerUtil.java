package com.android.fingerprint;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.pass.Spass;
import com.samsung.android.sdk.pass.SpassFingerprint;
import com.samsung.android.sdk.pass.SpassInvalidStateException;

/**
 * Created by Kl on 2016/6/8.
 */
public class FingerUtil implements Handler.Callback {
    private Context context;
    private Spass mSpass;
    private boolean needRetryIdentify = false;
    private boolean onReadyIdentify = false;
    private boolean onReadyEnroll = false;
    private boolean hasRegisteredFinger = false;

    public boolean isFeatureEnabled_fingerprint = false;
    public static final int START = 1;
    private SpassFingerprint mSpassFingerprint;
    public boolean hasFingerprint = false;
    private Handler mHandler;

    public void init(Context context) {
        this.context = context;
        if (mSpass != null) {
            mSpass = null;
            mSpass = new Spass();
        } else {
            mSpass = new Spass();
        }
        try {
            Log.e("FingerUtil", "进入了init");
            mSpass.initialize(context);
            isFeatureEnabled_fingerprint = mSpass
                    .isFeatureEnabled(Spass.DEVICE_FINGERPRINT);
            if (isFeatureEnabled_fingerprint) {
                if (mSpassFingerprint != null) {
                    mSpassFingerprint = null;
                    mSpassFingerprint = new SpassFingerprint(context);
                } else {
                    mSpassFingerprint = new SpassFingerprint(context);
                }
                mHandler = new Handler(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startIdentify() {
        Log.i("finger", "进入start" + "mIdentifyListener =" + mIdentifyListener);
        if (onReadyIdentify == false) {
            try {
                onReadyIdentify = true;

                if (mSpassFingerprint != null) {
                    mSpassFingerprint.startIdentify(mIdentifyListener);
                }
            } catch (SpassInvalidStateException ise) {
                onReadyIdentify = false;
                if (ise.getType() == SpassInvalidStateException.STATUS_OPERATION_DENIED) {
//					Log.e("Exception" + ise.getMessage());
                    ise.printStackTrace();
                }
                Log.e("samsungFingerPrint", "start 第一个出bug了");
            }
            catch (Exception e) {
                onReadyIdentify = false;
                e.printStackTrace();
                Log.e("FingerUtil", "start 第二个出bug了");

            }
        }
    }

    public boolean checkhasFingerPrint() throws SsdkUnsupportedException {
        if (mSpassFingerprint != null) {
            hasFingerprint = mSpassFingerprint.hasRegisteredFinger();
        }

        return hasFingerprint;
    }

    public void startFingerprint() {
        mHandler.sendEmptyMessage(START);
    }

    public void registerFinger(Context intentContext) {
        if (mSpassFingerprint != null) {
            mSpassFingerprint.registerFinger(intentContext, mRegisterListener);
        }
    }

    private SpassFingerprint.IdentifyListener mIdentifyListener = new SpassFingerprint.IdentifyListener() {
        @Override
        public void onFinished(int eventStatus) {
            Log.i("FingerUtil", "onFinish");
            String FingerprintGuideText = null;
            if (eventStatus == SpassFingerprint.STATUS_AUTHENTIFICATION_SUCCESS) {
                if (listener != null) listener.AfterUnlock();
                Log.i("FingerUtil", "STATUS_AUTHENTIFICATION_SUCCESS");
                needRetryIdentify = false;
            } else if (eventStatus == SpassFingerprint.STATUS_AUTHENTIFICATION_PASSWORD_SUCCESS) {
                Log.i("FingerUtil", "STATUS_AUTHENTIFICATION_PASSWORD_SUCCESS");
            } else if (eventStatus == SpassFingerprint.STATUS_OPERATION_DENIED) {
                Log.i("FingerUtil", "STATUS_OPERATION_DENIED");
            } else if (eventStatus == SpassFingerprint.STATUS_USER_CANCELLED) {
                Log.i("FingerUtil", "STATUS_USER_CANCELLED");
                needRetryIdentify = true;
            } else if (eventStatus == SpassFingerprint.STATUS_TIMEOUT_FAILED) {
                Log.i("FingerUtil", "STATUS_TIMEOUT_FAILED");
                needRetryIdentify = true;

//                mHandler.sendEmptyMessageDelayed(START, 100);

            } else if (eventStatus == SpassFingerprint.STATUS_QUALITY_FAILED) {
                Log.i("FingerUtil", "STATUS_QUALITY_FAILED");
                needRetryIdentify = true;
                 FingerprintGuideText = mSpassFingerprint.getGuideForPoorQuality();
                Toast.makeText(context,FingerprintGuideText,Toast.LENGTH_LONG).show();

                // context.startService(new Intent(context,
                // ScreenService.class).setAction(ScreenService.ACTION_SET_FINGERPRINTTEXT)
                // .putExtra("fingerprinttext", FingerprintGuideText));
                if (listener != null) listener.unlockFailed();


//                mHandler.sendEmptyMessageDelayed(START, 1000);
            } else {
                Log.i("FingerUtil", "ELSE = " + eventStatus);
                needRetryIdentify = true;


//                mHandler.sendEmptyMessageDelayed(START, 100);
            }

//            if (needRetryIdentify) {
//                Log.i("FingerUtil", "onCompleted" + "进入判断了");
//                needRetryIdentify = false;
//                mHandler.sendEmptyMessageDelayed(START, 500);
//            }
        }

        @Override
        public void onReady() {
            Log.i("FingerUtil", "onReady");
        }

        @Override
        public void onStarted() {
            Log.i("FingerUtil", "onStarted");
        }

        @Override
        public void onCompleted() {
            Log.i("FingerUtil", "onCompleted" + "\n" + "---------------------");

            Log.i("FingerUtil", needRetryIdentify + "\n" + "");

            onReadyIdentify = false;

            if (needRetryIdentify) {
                Log.i("FingerUtil", "onCompleted" + "进入判断了");
                needRetryIdentify = false;
                mHandler.sendEmptyMessageDelayed(START, 500);
            }
            // if (needRetryIdentify) {
            // Log.i("FingerUtil", "onCompleted" + "进入判断了");
            // needRetryIdentify = false;
            // mHandler.sendEmptyMessageDelayed(START, 500);
            // }
        }
    };

    public void cancelIdentify() {
        // if (onReadyIdentify == true) {
        try {
            if (mSpassFingerprint != null) {
                mSpassFingerprint.cancelIdentify();
            }
            // log("cancelIdentify is called");
        } catch (IllegalStateException ise) {
            // log(ise.getMessage());
            Log.e("FingerUtil", "进入了cancelIdentify");
        }
        onReadyIdentify = false;
        needRetryIdentify = false;
    }

    private SpassFingerprint.RegisterListener mRegisterListener = new SpassFingerprint.RegisterListener() {
        @Override
        public void onFinished() {
            onReadyEnroll = false;
        }
    };

    /**
     * 解锁成功后的接口
     *
     * @author Ivy
     */
    public interface onFingerPrintCompletedListener {
        public void AfterUnlock();

        public void unlockFailed();
    }

    private onFingerPrintCompletedListener listener;

    public void setListener(onFingerPrintCompletedListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean handleMessage(Message msg) {
        // TODO Auto-generated method stub
        switch (msg.what) {
            case START:
                startIdentify();
                break;
            default:
                break;
        }
        return true;
    }
}
