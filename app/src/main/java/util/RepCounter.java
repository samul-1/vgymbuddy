package util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class RepCounter {
    private RepCounter() {}

    private static RepCounter instance;

    private long lastEventTimestamp;
    private double previousMagnitude;
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
        if(sensorEvent.timestamp - lastEventTimestamp < ONE_SECOND/1.8) {
            // discard new events if they are too close to the last recorded
            return false;
        }

        lastEventTimestamp = sensorEvent.timestamp;

        float accX = sensorEvent.values[0];
        float accY = sensorEvent.values[1];
        float accZ = sensorEvent.values[2];

        double magnitude = Math.sqrt(accX*accX + accY*accY + accZ*accZ);
        double delta = magnitude - previousMagnitude;
        previousMagnitude = magnitude;

        System.out.println(delta>THRESHOLD);
        return delta>THRESHOLD;
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
