package com.android.theme.internal.data;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.common.SdkCache;
import com.android.common.SdkEnv;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created by song on 2016/10/17.
 */

public class ThemeInfo {
    private String themePackageName;
    private Context themeContext;
    private String name;
    private HashMap<String, WeakReference<Bitmap>> bitmapCache = new HashMap<>();
    private HashMap<String, Typeface> fonts = new HashMap<>();
    private Properties config;
    private boolean isLocal;
    private String baseDir;

    private String formatName(String name) {
        return String.format("%s/%s", baseDir, name);
    }

    private class CW extends ContextWrapper {
        private AssetManager mAssetManager;
        private Resources mResources;
        private Resources.Theme mTheme;

        public CW(Context base, String apkFileName) {
            super(base);
            mAssetManager = new AssetManager();
            callFunc(mAssetManager, "addAssetPath", apkFileName);
            Resources superRes = super.getResources();
            mResources = new Resources(mAssetManager, superRes.getDisplayMetrics(),
                    superRes.getConfiguration());
            mTheme = mResources.newTheme();
            mTheme.setTo(super.getTheme());
            themePackageName = base.getPackageManager().getPackageArchiveInfo(apkFileName, 0).packageName;
            setField(base, "mOuterContext", this);
        }

        @Override
        public AssetManager getAssets() {
            return mAssetManager;
        }

        @Override
        public Resources getResources() {
            return mResources;
        }

        @Override
        public Resources.Theme getTheme() {
            return mTheme;
        }

        @Override
        public String getPackageName() {
            return themePackageName;
        }
    }

    ThemeInfo(Context context, String baseDir, String packageName, boolean local) throws Exception {
        isLocal = local;
        this.name = packageName;
        this.baseDir = baseDir;
        if (local) {
            if (!SdkCache.cache().has(packageName, false)) {
                SdkCache.cache().cacheAsset(packageName, false);
            }

            final String archiveFilePath = SdkCache.cache().makeName(packageName, false);
            themeContext = new CW(context.createPackageContext(context.getPackageName(), Context.CONTEXT_IGNORE_SECURITY), archiveFilePath);
        } else {
            if (context.getPackageName().equals(packageName)) {
                themeContext = context.getApplicationContext();
            } else {
                themeContext = context.createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY);
            }
        }

        themePackageName = themeContext.getPackageName();
        final ClassLoader themeClassLoader = themeContext.getClassLoader();
        final ClassLoader contextClassLoader = context.getClassLoader();
        if (themeClassLoader != contextClassLoader) {
            setField(themeClassLoader, "parent", contextClassLoader);
        }

        config = new Properties();
        try {
            config.load(themeContext.getAssets().open(baseDir + "/config.properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setField(Object obj, String fieldName, Object fieldValue) {
        try {
            final Field field = getField(obj, fieldName);
            assert field != null;
            field.setAccessible(true);
            field.set(obj, fieldValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callFunc(Object obj, String funcName, Object... params) {
        try {
            final Method func = getFunc(obj, funcName, params);
            assert func != null;
            func.setAccessible(true);
            func.invoke(obj, params);
        } catch (Exception | Error e) {
            e.printStackTrace();
        }
    }

    private Method getFunc(Object obj, String funcName, Object... params) {
        Class[] ps = new Class[params.length];
        for (int i = 0; i < ps.length; i++) {
            ps[i] = params[i].getClass();
        }
        Class<?> aClass = obj.getClass();
        while (aClass != null) {
            try {
                return aClass.getDeclaredMethod(funcName, ps);
            } catch (NoSuchMethodException e) {
                aClass = aClass.getSuperclass();
            }
        }
        return null;
    }

    private Field getField(Object obj, String fieldName) {
        Class<?> aClass = obj.getClass();
        while (aClass != null) {
            try {
                return aClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                aClass = aClass.getSuperclass();
            }
        }
        return null;
    }

    void destroy() {
        try {
            final Context baseContext = ((ContextWrapper) themeContext).getBaseContext();
            if (isLocal) {
                setField(baseContext, "mOuterContext", null);
            }
            final ClassLoader themeClassLoader = themeContext.getClassLoader();
            final ClassLoader classLoader = baseContext.getClassLoader();
            if (themeClassLoader != classLoader) {
                setField(themeClassLoader, "parent", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            themeContext = null;
            fonts.clear();
            bitmapCache.clear();
            config.clear();
            hasFontMap.clear();
        }
    }

    public final String getPackageName() {
        return themePackageName;
    }

    public final String getName() {
        return name;
    }

    public InputStream getInputStream(String name) {
        name = formatName(name);
        try {
            return themeContext.getAssets().open(name);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Bitmap getBitmap(String path) {
        try {
            path = formatName(path);
            if (bitmapCache.containsKey(path)) {
                final WeakReference<Bitmap> bmp = bitmapCache.get(path);
                if (bmp != null) {
                    final Bitmap bitmap = bmp.get();
                    if (bitmap != null && !bitmap.isRecycled()) {
                        return bitmap;
                    }
                    bitmapCache.remove(path);
                }
            }
            final Bitmap bitmap = BitmapFactory.decodeStream(themeContext.getAssets().open(path));
            if (bitmap != null) {
                bitmapCache.put(path, new WeakReference<>(bitmap));
            }
            return bitmap;
        } catch (Exception|Error e) {
            return null;
        }
    }

    /**
     * if your view's size is less than the source bitmap size,
     * you can call this function for a better memory usage
     *
     * @param name
     * @param width
     * @param height
     * @return bitmap or null
     */
    public Bitmap getBitmap(String name, int width, int height) {
        try {
            name = formatName(name);
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(themeContext.getAssets().open(name), null, opt);
            int realWidth = opt.outWidth;
            int realHeight = opt.outHeight;
            if (width >= realWidth || height >= realHeight) {
                return BitmapFactory.decodeStream(themeContext.getAssets().open(name));
            } else {
                opt.inJustDecodeBounds = false;
                opt.inSampleSize = 2;
                return BitmapFactory.decodeStream(themeContext.getAssets().open(name), null, opt);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * get color value that defined in config.properties
     *
     * @param name
     * @return the color if exists and valid, otherwise 0
     */
    public int getInternalColor(String name) {
        try {
            return Color.parseColor(config.getProperty(name));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * get dimension value in pixel unit that defined in config.properties
     *
     * @param name
     * @return the dimension value if exists otherwise {@link Integer#MIN_VALUE}
     */
    public int getInternalDimen(String name) {
        try {
            return (int) (Float.parseFloat(config.getProperty(name)) * SdkEnv.env().screenDensity);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return Integer.MIN_VALUE;
        }
    }

    /**
     * get int value that defined in config.properties
     *
     * @param name
     * @return the int value otherwise {@link Integer#MIN_VALUE}
     */
    public int getInternalInt(String name) {
        try {
            return Integer.parseInt(config.getProperty(name));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return Integer.MIN_VALUE;
        }
    }

    /**
     * get float value that defined in config.properties
     *
     * @param name
     * @return the float value if exists otherwise {@link Float#MIN_VALUE}
     */
    public float getInternalFloat(String name) {
        try {
            return Float.parseFloat(config.getProperty(name));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return Float.MIN_VALUE;
        }
    }

    /**
     * get string that defined in config.properties
     *
     * @param name
     * @return the string if exists otherwise null
     */
    public String getInternalString(String name) {
        return config.getProperty(name);
    }

    public View inflate(String layoutName, ViewGroup parent) {
        return LayoutInflater.from(themeContext).inflate(getId(layoutName, "layout"), parent, false);
    }

    public int getColor(String name) {
        final Resources resources = themeContext.getResources();
        return resources.getColor(resources.getIdentifier(name, "color", themePackageName));
    }

    public int getDimen(String name) {
        final Resources resources = themeContext.getResources();
        return resources.getDimensionPixelSize(resources.getIdentifier(name, "dimen", themePackageName));
    }

    public float getFraction(String name) {
        final Resources resources = themeContext.getResources();
        return resources.getFraction(resources.getIdentifier(name, "fraction", themePackageName), 1, 1);
    }

    public int getInt(String name) {
        final Resources resources = themeContext.getResources();
        return resources.getInteger(resources.getIdentifier(name, "integer", themePackageName));
    }

    public int getId(String name, String type) {
        return themeContext.getResources().getIdentifier(name, type, themePackageName);
    }

    public Drawable getDrawable(String name) {
        final Resources resources = themeContext.getResources();
        return resources.getDrawable(resources.getIdentifier(name, "drawable", themePackageName));
    }

    public String getString(String name, Object... args) {
        final Resources resources = themeContext.getResources();
        return resources.getString(resources.getIdentifier(name, "string", themePackageName), args);
    }

    /**
     * get string content of the text file in the specified folder
     *
     * @param name   text file tag
     * @return content of the file if exists otherwise null
     */
    public String getTextFile(String name) {
        name = formatName(name);
        try {
            final InputStream e = themeContext.getAssets().open(name);
            byte[] buffer = new byte[e.available()];
            e.read(buffer);
            e.close();
            return new String(buffer);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static final String FONT_REGULAR = "sans-serif";
    private static final String FONT_REGULAR_COMPACT = "sans-serif-regular";
    private static final String FONT_LIGHT = "sans-serif-light";
    private static final String FONT_THIN = "sans-serif-thin";
    private static final String FONT_MEDIUM = "sans-serif-medium";
    private static final String FONT_BLACK = "sans-serif-black";

    private static final Typeface ROBOTO_REGULAR = Typeface.create(FONT_REGULAR, Typeface.NORMAL);
    private static final Typeface ROBOTO_LIGHT = Typeface.create(FONT_LIGHT, Typeface.NORMAL);
    private static final Typeface ROBOTO_THIN = Typeface.create(FONT_THIN, Typeface.NORMAL);
    private static final Typeface ROBOTO_MEDIUM = Typeface.create(FONT_MEDIUM, Typeface.NORMAL);
    private static final Typeface ROBOTO_BLACK = Typeface.create(FONT_BLACK, Typeface.NORMAL);

    public Typeface getFont(String name) {
        if (fonts.containsKey(name)) {
            return fonts.get(name);
        } else {

            final String fontName = config.getProperty(name);
            try {
                final Typeface typeface = getTypeface(name, true);
                if (typeface != null) {
                    fonts.put(name, typeface);
                    return typeface;
                } else {
                    final Typeface font = getTypeface(fontName, false);
                    fonts.put(name, font);
                    return font;
                }
            } catch (Exception e) {
                fonts.put(name, ROBOTO_REGULAR);
                e.printStackTrace();
                return ROBOTO_REGULAR;
            }
        }
    }

    private Typeface getTypeface(String fontName, boolean checkOnly) {
        final Typeface font;
        switch (fontName) {
            case FONT_LIGHT:
                font = ROBOTO_LIGHT;
                break;

            case FONT_REGULAR:
            case FONT_REGULAR_COMPACT:
                font = ROBOTO_REGULAR;
                break;

            case FONT_THIN:
                font = ROBOTO_THIN;
                break;

            case FONT_MEDIUM:
                font = ROBOTO_MEDIUM;
                break;

            case FONT_BLACK:
                font = ROBOTO_BLACK;
                break;

            default:
                if (!checkOnly) {
                    font = Typeface.createFromAsset(themeContext.getAssets(), formatName(fontName));
                } else {
                    font = null;
                }
        }
        return font;
    }

    private HashMap<String, Boolean> hasFontMap = new HashMap<>();

    public boolean hasFont(String name) {
        switch (name) {
            case FONT_BLACK:
            case FONT_LIGHT:
            case FONT_MEDIUM:
            case FONT_REGULAR:
            case FONT_THIN:
            case FONT_REGULAR_COMPACT:
                hasFontMap.put(name, true);
                return true;
        }

        final String fontName = config.getProperty(name);
        if (hasFontMap.containsKey(fontName)) {
            return hasFontMap.get(fontName);
        }
        try {
            switch (fontName) {
                case FONT_LIGHT:
                case FONT_REGULAR:
                case FONT_THIN:
                case FONT_MEDIUM:
                case FONT_REGULAR_COMPACT:
                case FONT_BLACK:
                    hasFontMap.put(fontName, true);
                    return true;

                default:
                    themeContext.getAssets().open(formatName(fontName));
                    hasFontMap.put(fontName, true);
                    return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            hasFontMap.put(fontName, false);
            return false;
        }
    }

    public Context getThemeContext() {
        return themeContext;
    }
}
