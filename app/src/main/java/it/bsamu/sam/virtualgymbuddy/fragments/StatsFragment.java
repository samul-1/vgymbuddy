package it.bsamu.sam.virtualgymbuddy.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import it.bsamu.sam.virtualgymbuddy.R;
import relational.AppDb;
import relational.dao.TrainingSessionDao;
import relational.entities.GymTransition;

public class StatsFragment extends Fragment {

    public static final int GYM_TIME_BAR_COLOR = Color.rgb(104, 241, 175);
    public static final int TRAIN_TIME_BAR_COLOR = Color.rgb(164, 228, 251);
    public static final int CHART_LABEL_COUNT = 14;
    public static final float CHART_TEXT_SIZE = 7f;
    private BarChart chart;
    private BarDataSet gymTimeDataSet, actualTrainTimeDataSet;
    ArrayList<BarEntry> gymTimeValues = new ArrayList<>();
    ArrayList<BarEntry> actualTrainTimeValues = new ArrayList<>();

    List<TrainingSessionDao.TrainingSessionDurationData> trainingSessionDurationData;
    double[] gymTimeDurationData;
    double totalGymTime = 0, totalTrainTime = 0;

    TextView gymTimeText, trainTimeText, tipCardText;
    View tipCard;

    AppDb db;
    public static final float CHART_GROUP_SPACE = 0.1f;
    public static final float CHART_BAR_SPACE = 0.0f;
    public static final float CHART_BAR_WIDTH = 0.42f;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = AppDb.getInstance(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stats_fragment, container,false);

        chart = view.findViewById(R.id.gym_time_chart);
        chart.clear();
        chart.setPinchZoom(false);

        // don't display negative axis values
        chart.getAxisLeft().setAxisMinimum(0.0f);
        chart.getXAxis().setAxisMinimum(0.0f);

        XAxis xAxis= chart.getXAxis();
        xAxis.setDrawGridLines(false);

        // set weekdays labels on x axis
        String[] weekDays = getResources().getStringArray(R.array.days_of_week);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return weekDays[(int)value];
            }
        });
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(7);
        xAxis.setGranularity(1f); // only intervals of 1 day

        chart.invalidate();

        gymTimeText = view.findViewById(R.id.gym_time_text);
        trainTimeText = view.findViewById(R.id.train_time_text);
        tipCard = view.findViewById(R.id.tip_card);
        tipCardText = view.findViewById(R.id.tip_card_text);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        asyncFetchDataSet();
    }

    void asyncFetchDataSet() {
        new AsyncTask<Void, Void, Void>() {
            @SuppressLint("StaticFieldLeak")
            @Override
            protected Void doInBackground(Void... voids) {
                LocalDateTime now = LocalDateTime.now(); // current date and time
                LocalDateTime midnight = now.toLocalDate().atStartOfDay();

                int todayIdx = now.getDayOfWeek().getValue();
                LocalDateTime beginningOfWeek = midnight.minusDays(todayIdx-1);
                LocalDateTime endOfWeek = beginningOfWeek.plusDays(7);
                Date midnightBeginningOfWeek = Date.from(beginningOfWeek.atZone(ZoneId.systemDefault()).toInstant());
                Date midnightEndOfWeek = Date.from(endOfWeek.atZone(ZoneId.systemDefault()).toInstant());

                trainingSessionDurationData = db.trainingSessionDao()
                        .getDurationDataForTimeInterval(midnightBeginningOfWeek, midnightEndOfWeek);


                gymTimeDurationData = getGymTimeDurationData(midnightBeginningOfWeek, midnightEndOfWeek);

                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                updateBarChartDataSets();
                setTotalTimeData();
                displayGymTime();
                displayTip();
            }
        }.execute();
    }

    private void setTotalTimeData() {
        totalGymTime = DoubleStream.of(gymTimeDurationData).sum();
        totalTrainTime = DoubleStream.of(
                trainingSessionDurationData
                        .stream()
                        .map(d->d.getDurationInMinutes())
                        .mapToDouble(x->x).toArray()
        ).sum();
    }

    @NonNull
    private double[] getGymTimeDurationData(Date beginTimestamp, Date endTimestamp) {
        List<GymTransition> weeklyGymTransitions = db
                .gymTransitionDao()
                .getTransitionsForTimePeriod(beginTimestamp, endTimestamp);

        double[] ret = new double[7];

        for(int i = 0; i < 7; i++) {
            int finalI = i;
            // get training session's duration data for the i-th day
            TrainingSessionDao.TrainingSessionDurationData durationData =
                    trainingSessionDurationData
                            .stream()
                            .filter(d->d.dayOfWeek == finalI + 1)
                            .findFirst().orElse(null);

            if(durationData == null) {
                // no training held that day, any transitions are irrelevant
                ret[i] = 0d;
                continue;
            }

            // get transitions happened on the i-th day of the week sorted increasingly by timestamp
            List<GymTransition> perDayTransitions = weeklyGymTransitions
                    .stream()
                    .filter(t->
                            LocalDateTime
                                    .ofInstant(
                                            t.timestamp.toInstant(),
                                            ZoneId.systemDefault()
                                    ).getDayOfWeek().getValue()-1 == finalI
                    ).sorted(Comparator.comparing(a -> a.timestamp))
                    .collect(Collectors.toList());

            Stream<GymTransition> enterTransitions = perDayTransitions
                    .stream().filter(t -> t.entering);
            Stream<GymTransition> leaveTransitions = perDayTransitions
                    .stream().filter(t -> !t.entering);


            // get the enter transition that's closest to the begin timestamp of the training
            // session held that day (to better approximate actual enter time in case of multiple
            // enter transitions for the day)
            Optional<GymTransition> enterTransition = enterTransitions.min(
                    Comparator.comparing(t ->
                            t.timestamp.getTime() -
                            durationData.beginTimestamp.getTime()
                    )
            );

            // same thing for leave transitions
            Optional<GymTransition> leaveTransition = leaveTransitions.min(
                    Comparator.comparing(t ->
                            t.timestamp.getTime() -
                                    durationData.endTimestamp.getTime()
                    )
            );


            // gps might've been turned on after user was already at the gym:
            // approximate enter time with training session's begin timestamp
            Date enterTransitionTimestamp = enterTransition.isPresent() ?
                    enterTransition.get().timestamp :
                    durationData.beginTimestamp;

            // gps might've been turned off while at the gym: use session's
            // end timestamp to approximate
            Date leaveTransitionTimestamp = leaveTransition.isPresent() ?
                    leaveTransition.get().timestamp :
                    durationData.endTimestamp;

            ret[i] =
                    TimeUnit.MINUTES.convert(
                            leaveTransitionTimestamp.getTime() - enterTransitionTimestamp.getTime(),
                            TimeUnit.MILLISECONDS
                    );
        }

        return ret;
    }

    private void updateBarChartDataSets() {
        gymTimeValues.clear();
        actualTrainTimeValues.clear();

        for(int i = 0; i < 7; i++) {
            // add time spent at the gym based on geofence transitions for i-th day of the week
            gymTimeValues.add(new BarEntry(i, (float) gymTimeDurationData[i]));

            // get duration of training session for i-th day of this week, if it exists
            int finalI = i;
            TrainingSessionDao.TrainingSessionDurationData sessionDuration =
                    trainingSessionDurationData
                            .stream()
                            .filter(d->d.dayOfWeek-1 == finalI)
                            .findFirst()
                            .orElse(null);

            actualTrainTimeValues.add(
                    new BarEntry(i,
                            sessionDuration != null ?
                                    sessionDuration.getDurationInMinutes() :
                                    0 // no session found for i-th day of the week
                    )
            );
        }

        gymTimeDataSet = new BarDataSet(gymTimeValues, getString(R.string.gym_time));
        gymTimeDataSet.setColor(GYM_TIME_BAR_COLOR);

        actualTrainTimeDataSet = new BarDataSet(actualTrainTimeValues, getString(R.string.train_time));
        actualTrainTimeDataSet.setColor(TRAIN_TIME_BAR_COLOR);

        BarData data = new BarData(gymTimeDataSet, actualTrainTimeDataSet);
        chart.setData(data);

        chart.getBarData().setBarWidth(CHART_BAR_WIDTH);

        chart.getXAxis().setLabelCount(CHART_LABEL_COUNT);
        chart.getXAxis().setTextSize(CHART_TEXT_SIZE);
        chart.groupBars(0, CHART_GROUP_SPACE, CHART_BAR_SPACE);

        chart.invalidate();
    }

    void displayGymTime() {
        gymTimeText.setText(String.valueOf(Math.round(totalGymTime/60 * 100) / 100) + "h");
        trainTimeText.setText(String.valueOf(Math.round(totalTrainTime/60 * 100) / 100) + "h");
    }

    void displayTip() {
        boolean displayCard = totalGymTime > 0 && totalTrainTime > 0;
        tipCard.setVisibility(displayCard ? View.VISIBLE : View.GONE);
        if(!displayCard) {
            return;
        }
        String cardMessage = "";
        double gymTimeToTrainTimeRatio = totalGymTime/totalTrainTime;
        if(gymTimeToTrainTimeRatio>=1.2) {
            cardMessage = getString(R.string.waste_less_gym_time_critical);
        } else if(gymTimeToTrainTimeRatio >= 1.1) {
            cardMessage = getString(R.string.waste_less_gym_time_normal);
        } else {
            cardMessage = getString(R.string.not_wasting_gym_time);
        }

        tipCardText.setText(cardMessage);
    }
}
