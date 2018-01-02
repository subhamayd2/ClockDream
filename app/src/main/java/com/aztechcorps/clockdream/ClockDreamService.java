package com.aztechcorps.clockdream;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Looper;
import android.service.dreams.DreamService;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aztechcorps.clockdream.Utils.ClockDreamsUtils;
import com.aztechcorps.clockdream.Utils.HelperConstants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.aztechcorps.clockdream.Utils.HelperConstants.DATE_FORMAT;
import static com.aztechcorps.clockdream.Utils.HelperConstants.AMPM_FORMAT;
import static com.aztechcorps.clockdream.Utils.HelperConstants.FADE_IN_DURATION;
import static com.aztechcorps.clockdream.Utils.HelperConstants.START_DELAY;
import static com.aztechcorps.clockdream.Utils.HelperConstants.TIME_FORMAT_12;
import static com.aztechcorps.clockdream.Utils.HelperConstants.TIME_FORMAT_24;

public class ClockDreamService extends DreamService {
    private LinearLayout linearLayout;
    private TextView tvDate;
    private TextView tvTime;
    private TextView tvAMPM;
    private TextView tvBattery;
    private TextView tvFullCharged;

    private TextView tvDivider;

    private Thread t;
    private boolean RUN_THREAD = true;

    private int batteryLevel = 0;
    private int batteryStatus = 0;

    private Handler handler;// = new Handler(Looper.getMainLooper());

    private SharedPreferences sharedPreferences;

    private BroadcastReceiver mBatteryInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            batteryStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

            //Handler _handler_battery = new Handler(Looper.getMainLooper());
            handler.post(battery_runnable);
        }
    };

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        ClockDreamsUtils clockDreamsUtils = new ClockDreamsUtils(getBaseContext());
        sharedPreferences = clockDreamsUtils.getSharedPreferences();

        setInteractive(sharedPreferences.getBoolean(ClockDreamsUtils.INTERACTIVE, false));
        setFullscreen(true);
        setScreenBright(false);

        setContentView(R.layout.clock_dream);

        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvAMPM = (TextView) findViewById(R.id.tvAMPM);
        tvBattery = (TextView) findViewById(R.id.tvBattery);
        tvFullCharged = (TextView) findViewById(R.id.tvFullCharged);

        tvDivider = (TextView) findViewById(R.id.tvDivider);

        linearLayout.setAlpha(0f);
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onDreamingStarted() {
        super.onDreamingStarted();

        t = new Thread(new ClockThread());
        t.start();

        if (!sharedPreferences.getBoolean(ClockDreamsUtils.BATTERY, false)) {
            this.registerReceiver(mBatteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        } else {
            tvBattery.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            tvDivider.setText("");
        }

        linearLayout.animate()
                .alpha(1f)
                .setStartDelay(START_DELAY)
                .setDuration(FADE_IN_DURATION)
                .setListener(null);
    }

    @Override
    public void onDreamingStopped() {
        if (!sharedPreferences.getBoolean(ClockDreamsUtils.BATTERY, false))
            this.unregisterReceiver(mBatteryInfoReceiver);
        RUN_THREAD = false;
        t = null;
        super.onDreamingStopped();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private class ClockThread implements Runnable {

        @Override
        public void run() {
            //Handler _handler = new Handler(Looper.getMainLooper());
            while (RUN_THREAD) {
                try {
                    handler.post(date_time_runnable);
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


    }

    //    Runnables:
    Runnable date_time_runnable = new Runnable() {
        @Override
        public void run() {
            Date d;
            SimpleDateFormat sdfDate;
            SimpleDateFormat sdfTime;
            SimpleDateFormat sdfAMPM;
            String time;
            String ampm;
            String date;

            //Log.d("testing", "Clock running");

            //while (RUN_THREAD) {
            try {
                //handler.post(r);
                d = new Date();
                sdfDate = new SimpleDateFormat(DATE_FORMAT, Locale.US);
                if (!sharedPreferences.getBoolean(ClockDreamsUtils.CLOCK, false)) {
                    sdfTime = new SimpleDateFormat(TIME_FORMAT_12, Locale.US);
                } else {
                    sdfTime = new SimpleDateFormat(TIME_FORMAT_24, Locale.US);
                }
                sdfAMPM = new SimpleDateFormat(AMPM_FORMAT, Locale.US);

                date = sdfDate.format(d);
                time = sdfTime.format(d);
                ampm = sdfAMPM.format(d);

                tvDate.setText(date);
                tvTime.setText(time);
                if (!sharedPreferences.getBoolean(ClockDreamsUtils.CLOCK, false))
                    tvAMPM.setText(ampm.toLowerCase());
                //Log.d("testing", (new Date()).toString());
                //Thread.sleep(60 * 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //}
        }
    };

    Runnable battery_runnable = new Runnable() {
        @Override
        public void run() {
            if (batteryStatus != BatteryManager.BATTERY_STATUS_CHARGING && batteryLevel < 15) {
                tvBattery.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_battery_alert_black_18sp, 0, 0, 0);
            }

//            if(status == BatteryManager.BATTERY_STATUS_CHARGING && level == 100) {
//                tvFullCharged.setVisibility(View.VISIBLE);
//            } else {
//                tvFullCharged.setVisibility(View.GONE);
//            }

            /*switch (batteryStatus) {
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    tvBattery.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_battery_charging_50_black_18sp, 0, 0, 0);
                    break;
                case BatteryManager.BATTERY_STATUS_FULL:
                    tvBattery.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_battery_charging_full_black_18sp, 0, 0, 0);
                    tvFullCharged.setText(R.string.full_charged);
                    break;
                default:
                    tvBattery.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_battery_std_black_18sp, 0, 0, 0);
                    tvFullCharged.setText("");
            }*/
        //Log.d("testbattery", batteryStatus+"");
            if(batteryStatus == BatteryManager.BATTERY_STATUS_CHARGING) {
                if(batteryLevel == 100) {
                    tvBattery.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_battery_charging_full_black_18sp, 0, 0, 0);
                    tvFullCharged.setText(R.string.full_charged);
                } else {
                    tvBattery.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_battery_charging_50_black_18sp, 0, 0, 0);
                }
            } else {
                tvBattery.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_battery_std_black_18sp, 0, 0, 0);
                tvFullCharged.setText("");
            }

            tvBattery.setText(String.format(HelperConstants.BATTERY_FORMAT, String.valueOf(batteryLevel)));
            //tvBattery.setText(charging.toString());
        }
    };
}
