package ar.com.gazer.letsrun;

import android.os.Build;

/**
 * Created by gazer on 8/9/14.
 */
public abstract class Constants {
    public static final boolean SUPPORT_KITKAT = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    public static final boolean SUPPORT_HONEYCOMB = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    public static final boolean SUPPORT_ICECREAM = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    public static final boolean SUPPORT_JELLY_BEAN = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
}
