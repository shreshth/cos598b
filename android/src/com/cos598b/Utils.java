package com.cos598b;

import android.content.Context;
import android.widget.Toast;

public class Utils {

    // Show a toast notification
    public static void toast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

}
