package ivy.battery.cooling;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.android.client.AndroidSdk;
import com.google.android.gms.common.api.GoogleApiClient;
import com.ivy.dialog.CrossPromotionDialog;
import com.ivy.dialog.DialogManager;

public class CrossTranslate extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private String value = "ad1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_cross_translate);
        Intent intent = getIntent();
        if (intent != null) {
            value = intent.getStringExtra("value");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        }

        DialogManager.showCrossPromotionDialog(this, AndroidSdk.getExtraData(), value, new CrossPromotionDialog.OnClickListener() {
            @Override
            public void onCancelClick(CrossPromotionDialog crossPromotionDialog) {
            }

            @Override
            public void onInstallClick(CrossPromotionDialog crossPromotionDialog) {

            }

            @Override
            public void onDismiss(CrossPromotionDialog crossPromotionDialog) {
                finish();

            }

            @Override
            public void onIsShowDialog(boolean b) {

//                Log.e("tagvalue",value+"---"+b);
//                if (value .equals("ad3")) {
//                    if(!b){
//                        Log.e("tagvalue",3+"-");
//                        Uri uri = Uri.parse("https://play.google.com/store/apps/developer?id=IVYMOBILE");
//                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                        startActivity(intent);
//                        finish();
//
//                    }
//
//                } else {

                if (!b) {
                    finish();
                }
                //}


            }
        });
    }


    @Override
    public void onBackPressed() {
        Log.e("finish", "finihs----2");
        super.onBackPressed();
    }


}
