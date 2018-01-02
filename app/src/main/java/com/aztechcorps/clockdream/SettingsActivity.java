package com.aztechcorps.clockdream;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.aztechcorps.clockdream.Utils.ClockDreamsUtils;

public class SettingsActivity extends AppCompatActivity implements Switch.OnCheckedChangeListener {

    private static CollapsingToolbarLayout tbCollapsing;
    private SharedPreferences sharedPreferences;

    private Switch swInteractive;
    private Switch swClock;
    private Switch swBattery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        tbCollapsing = (CollapsingToolbarLayout)findViewById(R.id.tbCollapsing);
        tbCollapsing.setTitle("Settings");

        swInteractive = (Switch)findViewById(R.id.swInteractive);
        swClock = (Switch)findViewById(R.id.swClock);
        swBattery = (Switch)findViewById(R.id.swBattery);

        ClockDreamsUtils clockDreamsUtils = new ClockDreamsUtils(this);
        sharedPreferences = clockDreamsUtils.getSharedPreferences();

        Boolean isInteractive = sharedPreferences.getBoolean(ClockDreamsUtils.INTERACTIVE, true);
        Boolean isClock = sharedPreferences.getBoolean(ClockDreamsUtils.CLOCK, false);
        Boolean isBattery = sharedPreferences.getBoolean(ClockDreamsUtils.BATTERY, false);

        swInteractive.setChecked(isInteractive);
        swClock.setChecked(isClock);
        swBattery.setChecked(isBattery);

        swInteractive.setOnCheckedChangeListener(this);
        swClock.setOnCheckedChangeListener(this);
        swBattery.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        int item_id = compoundButton.getId();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        switch(item_id) {
            case R.id.swInteractive:
                //Log.d(TAG, "onCheckedChanged: Interactive");
                editor.putBoolean(ClockDreamsUtils.INTERACTIVE, b);
                break;
            case R.id.swClock:
                //Log.d(TAG, "onCheckedChanged: Clock");
                editor.putBoolean(ClockDreamsUtils.CLOCK, b);
                break;
            case R.id.swBattery:
                //Log.d(TAG, "onCheckedChanged: Battery");
                editor.putBoolean(ClockDreamsUtils.BATTERY, b);
                break;
            default:
                //Log.d(TAG, "onCheckedChanged: How is it default?");
        }

        editor.apply();
    }
}
