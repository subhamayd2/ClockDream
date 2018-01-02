package com.aztechcorps.clockdream.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class ClockDreamsUtils {
    private Context context;
    private SharedPreferences sp;

    public static final String INTERACTIVE = "i";
    public static final String CLOCK = "c";
    public static final String BATTERY = "b";

    public ClockDreamsUtils(Context ctx) {
        context = ctx;
        sp = context.getSharedPreferences("ClockDreamPref", Context.MODE_PRIVATE);
    }

    public SharedPreferences getSharedPreferences() {
        return sp;
    }

}
