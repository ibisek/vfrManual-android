package com.ibisek.outlanded.navigation;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Provides orientation sensor data .. azimuth, roll, pitch.
 * 
 * @author ibisek
 * @version 2013-11-14
 */
public class OrientationSensorSource implements SensorEventListener {
	private final static String TAG = OrientationSensorSource.class.getSimpleName();

	/**
	 * Max interval for updating the listeners .. (it generates too many events).
	 */
	public static final int MAX_EVENT_INVERVAL = 250; // ms

	private static OrientationSensorSource instance;

	private SensorManager sensorManager;
	private Sensor orientationSensor;

	private long timeOfLastFilteredEvent = 0;

	private List<SensorEventListener> listeners = new ArrayList<SensorEventListener>();

	private OrientationSensorSource(Context context) {
		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
	}

	/**
	 * @param context
	 * @return
	 */
	public static OrientationSensorSource getInstance(Context context) {
		if (instance == null)
			instance = new OrientationSensorSource(context);
		return instance;
	}

	public void resume() {
		Log.d(TAG, "Resuming orientation sensor");
		sensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_NORMAL);
	}

	public void pause() {
		Log.d(TAG, "Pausing orientation sensor");
		sensorManager.unregisterListener(this);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// not needed
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (listeners.size() > 0) { // is anyone even interested?
			long time = System.currentTimeMillis();
			if (time - timeOfLastFilteredEvent > MAX_EVENT_INVERVAL) {
				timeOfLastFilteredEvent = time;

				for (SensorEventListener listener : listeners) {
					listener.onSensorChanged(event);
				}
			}
		}
	}

	public void addListener(SensorEventListener listener) {
		listeners.add(listener);
	}

	public void removeListener(SensorEventListener listener) {
		listeners.remove(listener);
	}
}
