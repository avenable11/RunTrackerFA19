package edu.ivytech.runtracker;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.NumberFormat;
import java.util.Timer;
import java.util.TimerTask;

import edu.ivytech.runtracker.database.RunTrackerDB;

public class StopwatchActivity extends AppCompatActivity {
    private TextView hoursTextView;
    private TextView minsTextView;
    private TextView secsTextView;
    private TextView tenthsTextView;

    private Button resetButton;
    private Button startStopButton;
    private Button mapButton;

    private long startTimeMillis;
    private long elapsedTimeMillis;

    private int elapsedHours;
    private int elapsedMins;
    private int elapsedSecs;
    private int elapsedTenths;

    private Timer timer;
    private NumberFormat number;

    private SharedPreferences prefs;
    private boolean stopwatchOn;

    private Intent serviceIntent;

    private final int NOTIFICATION_ID = 1;
    private NotificationManager notificationManager;

    private final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);

        if (Build.VERSION.SDK_INT > 22) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}
                        ,MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            }
        }

        // get references to widgets
        hoursTextView =  findViewById(R.id.textViewHoursValue);
        minsTextView =  findViewById(R.id.textViewMinsValue);
        secsTextView =  findViewById(R.id.textViewSecsValue);
        tenthsTextView =  findViewById(R.id.textViewTenthsValue);
        resetButton = findViewById(R.id.buttonReset);
        startStopButton = findViewById(R.id.buttonStartStop);
        mapButton =  findViewById(R.id.buttonViewMap);

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent runMap = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(runMap);
            }
        });
        startStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(stopwatchOn) {
                    stop();
                } else {
                    start();
                }
            }
        });
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
            }
        });

        serviceIntent = new Intent(this, RunTrackerService.class);
        prefs = getSharedPreferences("Prefs", MODE_PRIVATE);


    }

    private void start() {
        if (timer != null) {
            timer.cancel();
        }

        if(stopwatchOn == false) {
            startTimeMillis = System.currentTimeMillis()- elapsedTimeMillis;
        }

        stopwatchOn = true;
        startStopButton.setText(R.string.stop);

        startService(serviceIntent);


        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                elapsedTimeMillis = System.currentTimeMillis() - startTimeMillis;
                updateViews(elapsedTimeMillis);
            }
        };

        timer = new Timer(true);
        timer.scheduleAtFixedRate(task,0,100);
    }

    private void stop() {
        stopwatchOn = false;
        if(timer != null)
            timer.cancel();
        startStopButton.setText(R.string.start);
        stopService(serviceIntent);

        updateViews(elapsedTimeMillis);
    }

    private void reset() {
        this.stop();
        RunTrackerDB db = RunTrackerDB.get(this);
        db.deleteLocations();
        elapsedTimeMillis = 0;
        updateViews(elapsedTimeMillis);
    }


    private void updateViews(final long elapsedMillis) {
        elapsedTenths = (int) ((elapsedMillis/100) % 10);
        elapsedSecs = (int) ((elapsedMillis/1000) % 60);
        elapsedMins = (int) ((elapsedMillis/(60*1000)) % 60);
        elapsedHours = (int) (elapsedMillis/(60*60*1000));

        if (elapsedHours > 0) {
            updateView(hoursTextView, elapsedHours, 1);
        }
        updateView(minsTextView, elapsedMins, 2);
        updateView(secsTextView, elapsedSecs, 2);
        updateView(tenthsTextView, elapsedTenths, 1);
    }

    private void updateView(final TextView textView,
                            final long elapsedTime, final int minIntDigits) {

        // post changes to UI thread
        number = NumberFormat.getInstance();
        textView.post(new Runnable() {

            @Override
            public void run() {
                number.setMinimumIntegerDigits(minIntDigits);
                textView.setText(number.format(elapsedTime));
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean("stopwatchOn", stopwatchOn);
        edit.putLong("startTimeMillis", startTimeMillis);
        edit.putLong("elapsedTimeMillis", elapsedTimeMillis);
        edit.commit();
    }
    protected void onResume() {
        super.onResume();

        stopwatchOn = prefs.getBoolean("stopwatchOn", false);
        startTimeMillis = prefs.getLong("startTimeMillis", System.currentTimeMillis());
        elapsedTimeMillis = prefs.getLong("elapsedTimeMillis", 0);

        if (stopwatchOn) {
            start();
        }
        else {
            updateViews(elapsedTimeMillis);
        }
    }
}