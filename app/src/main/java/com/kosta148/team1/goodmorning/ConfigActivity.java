package com.kosta148.team1.goodmorning;

import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.tsengvn.typekit.Typekit;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Daehee on 2017-04-28.
 */

public class ConfigActivity extends PreferenceActivity {

    Preference prefTime, prefLoc, prefNews;
    private int pickHour, pickMin;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);

        Typekit.getInstance()
                .addCustom1(Typekit.createFromAsset(this, "NanumBarunGothic.ttf"))
                .addCustom2(Typekit.createFromAsset(this, "NanumBarunGothicBold.ttf"))
                .addNormal(Typekit.createFromAsset(this, "NanumBarunGothic.ttf"))
                .addBold(Typekit.createFromAsset(this, "NanumBarunGothicBold.ttf"));

        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.toolbar, root, false);
        
        bar.setTitleTextColor(Color.WHITE);
        root.addView(bar, 0); // insert at top
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        prefTime = (Preference) findPreference("time");
        prefTime.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {

                // 현재시간
                SimpleDateFormat sdfTime = new SimpleDateFormat("hhmma");
                String time = sdfTime.format(new Date());
                pickHour = Integer.valueOf(time.substring(0,2));
                pickMin = Integer.valueOf(time.substring(2,4));
                TimePickerDialog dialog = new TimePickerDialog(ConfigActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        pickHour = hourOfDay;
                        pickMin = minute;

                        new AlarmHATT(getApplicationContext()).Alarm(pickHour,pickMin);
                        Toast.makeText(getApplicationContext(),pickHour+"시 "+pickMin+"분에 설정되었습니다",Toast.LENGTH_SHORT).show();

                    }
                }, pickHour, pickMin, true);
                dialog.show();
                return false;
            }
        });

        prefLoc = (Preference) findPreference("loc");
        prefLoc.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                // Toast.makeText(getApplicationContext(), "나 눌림",
                // Toast.LENGTH_SHORT).show();

                AlertDialog.Builder dialog = new AlertDialog.Builder(ConfigActivity.this);
                dialog.setTitle("삭제");
                dialog.setMessage("정말 삭제하시겠습니까?");
                dialog.setPositiveButton("예", null);
                dialog.setNegativeButton("아니오", null);
                dialog.show();
                return false;
            }

        });

        prefNews = (Preference) findPreference("news");

        prefNews.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {


                AlertDialog.Builder dialog = new AlertDialog.Builder(ConfigActivity.this);
                dialog.setTitle("삭제");
                dialog.setMessage("정말 삭제하시겠습니까?");
                dialog.setPositiveButton("예", null);
                dialog.setNegativeButton("아니오", null);
                dialog.show();
                return false;
            }

        });


    } // end of onCreate

    public class AlarmHATT {
        private Context context;
        public AlarmHATT(Context context) {
            this.context=context;
        }
        public void Alarm(int pickHour,int pickMin) {
            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(ConfigActivity.this, NotificationReceiver.class);

            PendingIntent sender = PendingIntent.getBroadcast(ConfigActivity.this, 0, intent, 0);

            Calendar calendar = Calendar.getInstance();
            //알람시간 calendar에 set해주기

            // 년 월 일 시 분 초 설정 부분
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), pickHour, pickMin, 0);
            //알람 예약
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
} // end of class
