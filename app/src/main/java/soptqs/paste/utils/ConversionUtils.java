package soptqs.paste.utils;

import android.content.res.Resources;

/**
 * Created by S0ptq on 2018/2/8.
 */

public class ConversionUtils {

    public static float getPixelsFromDp(int dp) {
        return dp * Resources.getSystem().getDisplayMetrics().density;
    }

    public static float getDpFromPixels(int px) {
        return px / Resources.getSystem().getDisplayMetrics().density;
    }

}
