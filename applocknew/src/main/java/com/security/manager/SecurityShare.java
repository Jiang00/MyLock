package com.security.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import java.util.Locale;

/**
 * Created by superjoy on 2014/11/5.
 */
public class SecurityShare {
    public static final String GOOGLE_PALY_URL = "https://play.google.com/store/apps/details?id=";

    public static void share(Context context, String title, String text)
    {
        Intent it = new Intent(Intent.ACTION_SEND);
        it.setType("text/plain");
        it.putExtra(Intent.EXTRA_SUBJECT, title);
        it.putExtra(Intent.EXTRA_TEXT, text);
        context.startActivity(Intent.createChooser(it, title));
    }

    public static void rate(Context context)
    {
//        Intent i = new Intent(Intent.ACTION_VIEW);
//        i.setData(Uri.parse(url));
//        context.startActivity(i);
        Tools.openPlayStore(context, context.getPackageName());
    }

    private static int[] MNC_NUMS = new int[] {
            202, 204, 206, 208, 212, 213, 214, 216, 218, 219, 220, 222,	225,
            226, 228, 230, 231,	232, 234, 235, 238,	240, 242, 244, 246,	247,
            248, 250, 255, 257,	259, 260, 262, 266,	268, 270, 272, 274,	276,
            278, 280, 282, 283,	284, 286, 288, 290,	292, 293, 294, 295,	297,
            302, 308, 310, 311,	312, 313, 314, 315, 316, 330, 332, 334, 338,
            340, 341, 342, 344, 346, 348, 350, 352,	354, 356, 358, 360, 362,
            363, 364, 365, 366,	368, 370, 372, 374, 376, 400, 401, 402, 404,
            405, 406, 410, 412, 413, 414, 415, 416,	417, 418, 419, 420, 421,
            422, 424, 425, 425, 426, 427, 428, 429, 430, 431, 432, 434, 436,
            437, 438, 440, 441, 450, 452, 454, 455, 456, 457, 460, 461, 466,
            467, 470, 472, 502, 505, 510, 514, 515, 520, 525, 528, 530, 534,
            535, 536, 537, 539, 540, 541, 542, 543, 544, 545, 546, 547, 548,
            549, 550, 551, 552, 555, 602, 603, 604,	605, 606, 607, 608,	609,
            610, 611, 612, 613,	614, 615, 616, 617,	618, 619, 620, 621,	622,
            623, 624, 625, 626, 627, 628, 629, 630,	631, 632, 633, 634,	635,
            636, 637, 638, 639, 640, 641, 642, 643, 645, 646, 647, 648, 649,
            650, 651, 652, 653, 654, 655, 657, 702, 704, 706, 708, 710, 712,
            714, 716, 722, 724, 730, 732, 734, 736, 738, 740, 742, 744, 746,
            748, 750
    };
    private static String[] MNC_CODES = new String[] {
            "GR","NL","BE","FR","MC","AD","ES","HU","BA","HR","RS","IT","VA",
            "RO","CH","CZ","SK","AT","GB","GB","DK","SE","NO","FI","LT","LV",
            "EE","RU","UA","BY","MD","PL","DE","GI","PT","LU","IE","IS","AL",
            "MT","CY","GE","AM","BG","TR","FO","GL","SM","SI","MK","LI","ME",
            "CA","PM","US","US","US","US","US","US","US","PR","VI","MX","JM",
            "GP","MQ","BB","AG","KY","VG","BM","GD","MS","KN","LC","VC","CW",
            "AW","BS","AI","DM","CU","DO","HT","TT","TC","AZ","KZ","BT","IN",
            "IN","IN","PK","AF","LK","MM","LB","JO","SY","IQ","KW","SA","YE",
            "OM","AE","IL","PS","BH","QA","MN","NP","AE","AE","IR","UZ","TJ",
            "KG","TM","JP","JP","KR","VN","HK","MO","KH","LA","CN","CN","TW",
            "KP","BD","MV","MY","AU","ID","TL","PH","TH","SG","BN","NZ","MP",
            "GU","NR","PG","TO","SB","VU","FJ","WF","AS","KI","NC","PF","CK",
            "WS","FM","MH","PW","NU","EG","DZ","MA","TN","LY","GM","SN","MR",
            "ML","GN","CI","BF","NE","TG","BJ","MU","LR","SL","GH","NG","TD",
            "CF","CM","CV","ST","GQ","GA","CG","CD","AO","GW","SC","SD","RW",
            "ET","SO","DJ","KE","TZ","UG","BI","MZ","ZM","MG","RE","ZW","NA",
            "MW","LS","BW","SZ","KM","ZA","ER","BZ","GT","SV","HN","NI","CR",
            "PA","PE","AR","BR","CL","CO","VE","BO","GY","EC","GF","PY","SR",
            "UY","FK"
    };

    private static String country;

    public static String getCountryCode(Context context) {
        if(country == null) {
            if(context != null) {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Activity.TELEPHONY_SERVICE);
                if(tm != null) {
                    String imsi = tm.getSubscriberId();
                    if(imsi != null && imsi.length() > 5) {
                        try{
                            int co = Integer.parseInt(imsi.substring(0, 3));// maybe is invalid int
                            int no = Integer.parseInt(imsi.substring(3, 5));
                            for (int i = 0; i < MNC_NUMS.length; i++) {
                                if(co == MNC_NUMS[i]) {
                                    country = MNC_CODES[i];
                                    break;
                                }
                            }
                        } catch (Exception e){

                        }
                    }
                }
            }
            country = country == null ? Locale.getDefault().getCountry() : country;
        }
        return country;
    }

    /*

    private Bitmap capture() {
        View root = _context.getWindow().getDecorView().getRootView();
        Bitmap img = null;
        root.setDrawingCacheEnabled(true);
        img = Bitmap.createBitmap(root.getDrawingCache());
        root.setDrawingCacheEnabled(false);
        return img;
    }

    public void share(final String title, final String text, final Bitmap img) {
        if (_context != null) {
            _context.runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    if(img != null) {
                        it.setType("image/jpeg");
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.TITLE, title);
                        values.put(MediaStore.Images.Media.DESCRIPTION, text);
                        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                        Uri uri = _context.getContentResolver().insert(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                        OutputStream outstream;
                        try {
                            outstream = _context.getContentResolver().openOutputStream(
                                    uri);
                            img.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
                            outstream.close();
                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }
                        it.putExtra(Intent.EXTRA_STREAM, uri);
                    } else {
                        it.setType("text/plain");
                    }

                }
            });
        }
    }

    public void share(final String title, final String text, final boolean capture) {
        if (_context != null) {
            _context.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Intent it = new Intent(Intent.ACTION_SEND);
                    if (capture) {
                        it.setType("image/jpeg");
                        Bitmap img = capture();
                        ContentValues values = new ContentValues();
                        values.put(Images.Media.TITLE, title);
                        values.put(Images.Media.DESCRIPTION, text);
                        values.put(Images.Media.MIME_TYPE, "image/jpeg");
                        Uri uri = _context.getContentResolver().insert(
                                Media.EXTERNAL_CONTENT_URI, values);
                        OutputStream outstream;
                        try {
                            outstream = _context.getContentResolver().openOutputStream(
                                    uri);
                            img.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
                            outstream.close();
                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }
                        it.putExtra(Intent.EXTRA_STREAM, uri);
                    } else {
                        it.setType("text/plain");
                    }
                    it.putExtra(Intent.EXTRA_SUBJECT, title);
                    it.putExtra(Intent.EXTRA_TEXT, text);
                    _context.startActivity(Intent.createChooser(it, title));
                }
            });
        }
    }

    public void share(final Bitmap img) {
        if (_context != null) {
            _context.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    //#if FL_SO
                    JSONObject games = (JSONObject) SOMaster.execute(_context,
                            "cachedGameList", new Class[] {}, new Object[] {});
                    //#else
//@					JSONObject games = s.cachedGameList();
                    //#endif
                    String appid = String.valueOf(PluginConfig.CONF_APPID);
                    if (games.has(appid)) {
                        try {
                            JSONObject obj = (JSONObject) games.get(appid);
                            if (obj.has("sharetitle") && obj.has("sharedesc")) {
                                share((String) obj.get("sharetitle"),
                                        (String) obj.get("sharedesc"), img);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    public void share(final boolean capture) {
        if (_context != null) {
            _context.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    //#if FL_SO
                    JSONObject games = (JSONObject) SOMaster.execute(_context,
                            "cachedGameList", new Class[] {}, new Object[] {});
                    //#else
//@					JSONObject games = s.cachedGameList();
                    //#endif
                    String appid = String.valueOf(PluginConfig.CONF_APPID);
                    if (games.has(appid)) {
                        try {
                            JSONObject obj = (JSONObject) games.get(appid);
                            if (obj.has("sharetitle") && obj.has("sharedesc")) {
                                share((String) obj.get("sharetitle"),
                                        (String) obj.get("sharedesc"), capture);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }
    */
}
