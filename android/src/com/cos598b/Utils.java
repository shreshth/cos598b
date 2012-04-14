package com.cos598b;

import android.content.Context;
import android.provider.Settings.Secure;
import android.util.Log;
import android.widget.Toast;

public class Utils {

    // Show a toast notification only in test mode
    public static void toast_test(Context context, String text) {
        if (Consts.TEST_MODE) {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            Log.d("Toast Message", text);
        }
    }

    // Show a toast notification, even if we are not in test_mode
    public static void toast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
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
