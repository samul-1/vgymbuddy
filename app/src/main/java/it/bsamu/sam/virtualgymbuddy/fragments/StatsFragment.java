package it.bsamu.sam.virtualgymbuddy.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

import it.bsamu.sam.virtualgymbuddy.R;

public class StatsFragment extends Fragment {

    private BarChart chart;
    private SeekBar seekBarX, seekBarY;
    private BarDataSet gymTimeDataSet, actualTrainTimeDataSet;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stats_fragment, container,false);

        chart = view.findViewById(R.id.gym_time_chart);
        chart.setPinchZoom(false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        asyncFetchDataSet();
    }

    void asyncFetchDataSet() {
        ArrayList<BarEntry> gymTimeValues = new ArrayList<>();
        ArrayList<BarEntry> actualTrainTimeValues = new ArrayList<>();

        for(int i = 0; i < 7; i++) {
            gymTimeValues.add(new BarEntry(i, i));
            actualTrainTimeValues.add(new BarEntry(i, 6-i));
        }
        gymTimeDataSet = new BarDataSet(gymTimeValues, "Tempo in palestra");
        gymTimeDataSet.setColor(Color.rgb(104, 241, 175));

        actualTrainTimeDataSet = new BarDataSet(actualTrainTimeValues, "Durata allenamento");
        actualTrainTimeDataSet.setColor(Color.rgb(164, 228, 251));

        BarData data = new BarData(gymTimeDataSet, actualTrainTimeDataSet);
        chart.setData(data);

        chart.getBarData().setBarWidth(0.3f);
        chart.getXAxis().setAxisMinimum(0.0f);
        chart.getXAxis().setAxisMaximum(
                0 + chart.getBarData().getGroupWidth(0.18f, 0.0f) *
                        7); // number of groups
        chart.getXAxis().setGranularity(1f);
        chart.groupBars(0, 0.18f, 0.0f);
        // TODO invalidate?
    }
}
