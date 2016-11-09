package com.security.manager;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;

import com.privacy.lock.R;
import com.security.manager.page.MessageBox;

/**
 * Created by superjoy on 2014/9/28.
 */
public class SecurityResetPattern extends SecurityAbsActivity {
    public static final int REQ_CONFIRM = 2;

    static boolean isOk = false;

    @Override
    public void setupView() {
        if (getIntent().getBooleanExtra("direct-confirm", false)){
            confirm(this);
        } else {
            showDialog(this, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    isOk = true;
                    confirm(context);
                }
            }, new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (!isOk)
                        finish();
                }
            }, false);
        }
    }

    public static AlertDialog showDialog(Context context, DialogInterface.OnClickListener yes, DialogInterface.OnDismissListener dismiss, boolean alert) {
        MessageBox.Data data = new MessageBox.Data();
        data.msg = R.string.security_login_with_email;
        data.title = R.string.security_forget_password;
        data.cancelable = true;
        data.onyes = yes;
        data.style = R.style.MessageBox;
        data.yes = R.string.security_verify;
        data.button = MessageBox.BUTTON_YES_NO;
        data.alert = alert;
        data.ondismiss = dismiss;
        return MessageBox.show_(context, data);
    }

    public static void confirm(final Activity context) {
        /*
        AccountManager manager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account[] accounts = manager.getAccounts();
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        boolean hasDone = false;
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                manager.confirmCredentials(account, null, context, new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> future) {
                        try {
                            success = true;
                            if (future.getResult().getBoolean(AccountManager.KEY_BOOLEAN_RESULT)) {
                                context.startActivity(new Intent(context, SecuritySetPattern.class).putExtra("set", SecuritySetPattern.SET_GRAPH_PASSWD));
                                context.finish();
                            } else {
                                MessageBox.Data data = new MessageBox.Data();
                                data.msg = R.string.id_verified_fail;
                                data.title = R.string.forgot_passwd;
                                data.alert = true;
                                data.yes = R.string.retry;
                                data.button = MessageBox.BUTTON_YES_CANCEL;
                                isOk = false;
                                data.onyes = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        success = false;
                                        isOk = true;
                                        confirm(context);
                                    }
                                };
                                data.oncancel = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        context.finish();
                                    }
                                };
                                data.ondismiss = new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        if (!isOk)
                                            context.finish();
                                    }
                                };
                                MessageBox.show_(context, data);
                            }
                        } catch (Exception ignore) {
                            ignore.printStackTrace();
                            context.finish();
                        }
                    }
                }, null);
                hasDone = true;
                break;
            }
        }
        */
        boolean hasDone = false;
        if (!hasDone){
            MessageBox.Data data = new MessageBox.Data();
            data.msg = R.string.security_no_google_account;
            data.title = R.string.security_forget_password;
            data.ondismiss = new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    context.finish();
                }
            };
            MessageBox.show(context, data);
        }
    }

    boolean paused = false;
    Handler handler;
    static boolean success = false;

    @Override
    protected void onPause() {
        super.onPause();
        paused = true;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (paused) {
            paused = false;
            if (handler == null){
                handler = new Handler(getMainLooper());
            }
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!success)
                        finish();
                }
            }, 200);
        }
    }

    @Override
    protected void onDestroy() {
        handler = null;
        super.onDestroy();
    }

    @Override
    protected boolean hasHelp() {
        return false;
    }

    @Override
    protected int getBackImage() {
        return R.drawable.ic_launcher;
    }
}
