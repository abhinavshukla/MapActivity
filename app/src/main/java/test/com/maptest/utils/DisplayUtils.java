package test.com.maptest.utils;

import android.util.TypedValue;

import test.com.maptest.MapsActivity;

public class DisplayUtils {

    private DisplayUtils() {
    }

    public static float getPixelsFromDP(int dp, MapsActivity activity) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, activity.getResources().getDisplayMetrics());
    }
}
