package me.tittojose.www.timerangepicker_library;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TabHost;
import android.widget.TimePicker;

import java.util.Calendar;


/**
 * Created by Jose on 24/05/15.
 */
public class TimeRangePickerDialog extends DialogFragment implements View.OnClickListener, NumberPicker.OnValueChangeListener {
    TabHost tabs;
    Button setTimeRange;
    TimePicker startTimePicker, endTimePicker;
    NumberPicker dayPicker;
    OnTimeRangeSelectedListener onTimeRangeSelectedListener;
    boolean is24HourMode;

    final String days[] = { "Sunday", "Monday", "Tuesday", "Wendesday", "Thursday", "Friday", "Saturday"};

    TabHost.TabSpec tabDaypage;

    public static TimeRangePickerDialog newInstance(OnTimeRangeSelectedListener callback, boolean is24HourMode) {
        TimeRangePickerDialog ret = new TimeRangePickerDialog();
        ret.initialize(callback, is24HourMode);
        return ret;
    }

    public void initialize(OnTimeRangeSelectedListener callback,
                           boolean is24HourMode) {
        onTimeRangeSelectedListener = callback;
        this.is24HourMode = is24HourMode;
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

    }

    public interface OnTimeRangeSelectedListener {
        void onTimeRangeSelected(int day,int startHour, int startMin, int endHour, int endMin);
    }

    public void setOnTimeRangeSetListener(OnTimeRangeSelectedListener callback) {
        onTimeRangeSelectedListener = callback;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.timerange_picker_dialog, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        tabs = (TabHost) root.findViewById(R.id.tabHost);
        setTimeRange = (Button) root.findViewById(R.id.bSetTimeRange);
        startTimePicker = (TimePicker) root.findViewById(R.id.startTimePicker);
        endTimePicker = (TimePicker) root.findViewById(R.id.endTimePicker);
        dayPicker = (NumberPicker) root.findViewById(R.id.dayPicker);
        setTimeRange.setOnClickListener(this);
        setTimeRange.setText("Next");
        tabs.findViewById(R.id.tabHost);
        tabs.setup();
        setupDayPicker();

        startTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                endTimePicker.setCurrentHour(hourOfDay);
                endTimePicker.setCurrentMinute(minute);
            }
        });

        tabDaypage = tabs.newTabSpec("one");
        tabDaypage.setContent(R.id.dayGroup);
        tabDaypage.setIndicator("Choose Day");

        TabHost.TabSpec tabpage1 = tabs.newTabSpec("two");
        tabpage1.setContent(R.id.startTimeGroup);
        tabpage1.setIndicator("Start Time");

        TabHost.TabSpec tabpage2 = tabs.newTabSpec("three");
        tabpage2.setContent(R.id.endTimeGroup);
        tabpage2.setIndicator("End Time");

        tabs.addTab(tabDaypage);
        tabs.addTab(tabpage1);
        tabs.addTab(tabpage2);

        tabs.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if(tabId.equals("three")){
                    setTimeRange.setText("DONE");
                }else{
                    tabDaypage.setIndicator(days[dayPicker.getValue()]);
                    setTimeRange.setText("NEXT");
                }
            }
        });


        return root;
    }

    public void setupDayPicker(){


        //disable keyboard editing
        dayPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        dayPicker.setMinValue(0);
        dayPicker.setMaxValue(days.length - 1);
        dayPicker.setDisplayedValues(days);

        NumberPicker.OnValueChangeListener myValChangedListener = new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //txtPickerOutput.setText("Value: " + genders[newVal]);
                tabDaypage.setIndicator(days[newVal]);
            }
        };


        dayPicker.setOnValueChangedListener(this);
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onStart() {
        super.onStart();

        // safety check
        if (getDialog() == null)
            return;
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bSetTimeRange) {
            switch (tabs.getCurrentTab()){
                case 0:
                    tabs.setCurrentTab(1);
                    break;
                case 1:
                    tabs.setCurrentTab(2);
                    break;
                default:
                    dismiss();
                    int startHour = startTimePicker.getCurrentHour();
                    int startMin = startTimePicker.getCurrentMinute();
                    int endHour = endTimePicker.getCurrentHour();
                    int endMin = endTimePicker.getCurrentMinute();
                    //This hardcoded one is so the days correspond to the constants
                    //in the Calendar class used to represent days.
                    int day = dayPicker.getValue() + 1;
                    onTimeRangeSelectedListener.onTimeRangeSelected(day ,startHour, startMin, endHour, endMin);
            }
        }
    }
}
