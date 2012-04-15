package com.cos598b;

import android.content.Context;
import android.provider.Settings.Secure;
import android.util.Log;
import android.widget.Toast;

public class Utils {

    // Show a toast notification only in test mode
    public static void toast_test(Context context, String text) {
        if (isTestMode(context)) {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        }
        Log.d("Toast Message", text);
    }

    // Show a toast notification, even if we are not in test_mode
    public static void toast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
        Log.d("Toast Message", text);
    }

    // whether to enable test only features
    public static boolean isTestMode(Context context) {
        for (String allowed_device : Consts.TEST_DEVICE_WHITELIST) {
            if (getDeviceID(context).equals(allowed_device)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method to join array elements of type string
     * @author Hendrik Will, imwill.com
     * @param inputArray Array which contains strings
     * @param glueString String between each array element
     * @return String containing all array elements seperated by glue string
     */
    public static String implode(String[] inputArray, String glueString) {

        /** Output variable */
        String output = "";

        if (inputArray.length > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(inputArray[0]);

            for (int i=1; i<inputArray.length; i++) {
                sb.append(glueString);
                sb.append(inputArray[i]);
            }

            output = sb.toString();
        }

        return output;
    }

    // return device ID (unique for each android device)
    public static String getDeviceID(Context context) {
        return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
    }

}
