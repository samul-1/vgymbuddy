package util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class RepCounter {
    private RepCounter() {}

    private static RepCounter instance;

    private long lastEventTimestamp = 0L;
    private boolean lastReadingWasRep = false;

    private double previousMagnitude = 0d;
    private SensorEventListener repListener;
    private SensorManager sm;
    private Sensor sensor;

    private RepCounterListener listener;

    private final long ONE_SECOND = 1000000000;
    private final double THRESHOLD = 5.5;

    private RepCounter(Context context) {
        sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public interface RepCounterListener {
        void onRep();
    }

    public void stopCounting() {
        if(repListener != null) {
            sm.unregisterListener(repListener);
            repListener = null;
            lastReadingWasRep = false;
            lastEventTimestamp = 0L;
            previousMagnitude = 0d;
        }
    }

    public void startCounting() {
        repListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if(sensorEvent != null && isRep(sensorEvent) && listener != null) {
                    listener.onRep();
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        sm.registerListener(repListener, sensor, SensorManager.SENSOR_DELAY_UI);
    }

    public RepCounter setListener(RepCounterListener listener) {
        this.listener = listener;
        return this;
    }

    private boolean isRep(SensorEvent sensorEvent) {
        if(
            // discard the very first reading
            lastEventTimestamp == 0L || previousMagnitude == 0d ||
            // discard new events if they are too close to the last recorded
            sensorEvent.timestamp - lastEventTimestamp < ONE_SECOND/2 ||
            // discard new event if the last valid reading was a rep and not enough time has
            // passed to avoid counting positive + negative portions of a rep as 2 distinct reps
            lastReadingWasRep && sensorEvent.timestamp - lastEventTimestamp < ONE_SECOND/1.2
        ) {
            if(lastEventTimestamp == 0L) {
                previousMagnitude = getMagnitude(sensorEvent);
                lastEventTimestamp = sensorEvent.timestamp;
            }
            return false;
        }

        lastEventTimestamp = sensorEvent.timestamp;

        double magnitude = getMagnitude(sensorEvent);
        double delta = magnitude - previousMagnitude;

        previousMagnitude = magnitude;

        boolean isRep = delta > THRESHOLD;
        lastReadingWasRep = isRep;

        if(isRep) {
            System.out.println(delta + ", p " + previousMagnitude + ", c " + magnitude);
        }

        return isRep;
    }

    private double getMagnitude(SensorEvent sensorEvent) {
        float accX = sensorEvent.values[0];
        float accY = sensorEvent.values[1];
        float accZ = sensorEvent.values[2];

        double magnitude = Math.sqrt(
                accX*accX + accY*accY + accZ*accZ
        );
        return magnitude;
    }

    public static RepCounter getInstance(Context context) {
        if(instance != null) {
            return instance;
        }
        synchronized (RepCounter.class) {
            if(instance == null) {
                instance = new RepCounter(context);
            }
            return instance;
        }
    }

}
