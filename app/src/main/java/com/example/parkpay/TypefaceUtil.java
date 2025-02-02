package com.example.parkpay;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import java.lang.reflect.Field;

    class TypefaceUtil {

        private static final String TAG = "myLogs";


        public static void overrideFont(Context context, String defaultFontNameToOverride, String customFontFileNameInAssets) {
            try {
                final Typeface customFontTypeface = Typeface.createFromAsset(context.getAssets(), customFontFileNameInAssets);
                final Field defaultFontTypefaceField = Typeface.class.getDeclaredField(defaultFontNameToOverride);
                defaultFontTypefaceField.setAccessible(true);
                defaultFontTypefaceField.set(null, customFontTypeface);
            } catch (Exception e) {

                Log.e(TAG,"Невозможно установить шрифт " + customFontFileNameInAssets + " вместо " + defaultFontNameToOverride);
            }
        }
    }



