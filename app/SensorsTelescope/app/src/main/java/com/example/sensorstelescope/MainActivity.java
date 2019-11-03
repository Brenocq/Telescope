package com.example.sensorstelescope;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager mSensorManager;

    LocationManager locationManager;
    LocationListener locationListener;

    // Individual light and proximity sensors.
    private Sensor mSensorProximity;
    private Sensor mSensorLight;
    private Sensor mSensorAccelerometer;
    private Sensor mSensorLinearAcceleration;
    private Sensor mSensorGyroscope;
    private Sensor mSensorRotation;
    private Sensor mSensorGravity;
    private Sensor mSensorBarometer;

    // TextViews to display current sensor values
    private TextView mTextSensorLight;
    private TextView mTextSensorProximity;
    private TextView mTextSensorAccelerometer;
    private TextView mTextSensorLinearAcceleration;
    private TextView mTextSensorGyroscope;
    private TextView mTextSensorRotation;
    private TextView mTextSensorGravity;
    private TextView mTextSensorGPS;
    private TextView mTextSensorBarometer;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            startListening();

        }

    }

    public void startListening() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        }

    }

    public void updateLocationInfo(Location location) {

        Log.i("LocationInfo", location.toString());

        mTextSensorGPS.setText(getResources().getString(R.string.label_gps,  location.getLatitude(),  location.getLongitude(),  location.getAltitude(), location.getAccuracy()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //---------- Sensors ----------//

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mTextSensorLight = (TextView) findViewById(R.id.label_light);
        mTextSensorProximity = (TextView) findViewById(R.id.label_proximity);
        mTextSensorAccelerometer = (TextView) findViewById(R.id.label_accelerometer);
        mTextSensorLinearAcceleration = (TextView) findViewById(R.id.label_linear_acceleration);
        mTextSensorGyroscope = (TextView) findViewById(R.id.label_gyroscope);
        mTextSensorRotation = (TextView) findViewById(R.id.label_rotation);
        mTextSensorGravity = (TextView) findViewById(R.id.label_gravity);
        mTextSensorGPS = (TextView) findViewById(R.id.label_gps);
        mTextSensorBarometer = (TextView) findViewById(R.id.label_barometer);

        mSensorProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mSensorLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mSensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorLinearAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mSensorGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorRotation = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mSensorGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mSensorBarometer = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

        String sensor_error = getResources().getString(R.string.error_no_sensor);

        if (mSensorLight == null) {
            mTextSensorLight.setText(sensor_error);
        }
        if (mSensorProximity == null) {
            mTextSensorProximity.setText(sensor_error);
        }
        if (mSensorAccelerometer == null) {
            mTextSensorAccelerometer.setText(sensor_error);
        }
        if (mSensorLinearAcceleration == null) {
            mTextSensorLinearAcceleration.setText(sensor_error);
        }
        if (mSensorGyroscope == null) {
            mTextSensorGyroscope.setText(sensor_error);
        }
        if (mSensorRotation == null) {
            mTextSensorRotation.setText(sensor_error);
        }
        if (mSensorGravity == null) {
            mTextSensorGravity.setText(sensor_error);
        }
        if (mSensorBarometer == null) {
            mTextSensorBarometer.setText(sensor_error);
        }

        //---------- GPS ----------//

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateLocationInfo(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
            }
        };

        if (Build.VERSION.SDK_INT < 23) {
            startListening();
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (location != null) {
                    updateLocationInfo(location);
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mSensorProximity != null) {
            mSensorManager.registerListener(this, mSensorProximity,
                    SensorManager.SENSOR_DELAY_NORMAL);// Can change to SENSOR_DELAY_GAME or SENSOR_DELAY_FASTEST
        }
        if (mSensorLight != null) {
            mSensorManager.registerListener(this, mSensorLight,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (mSensorAccelerometer != null) {
            mSensorManager.registerListener(this, mSensorAccelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (mSensorLinearAcceleration != null) {
            mSensorManager.registerListener(this, mSensorLinearAcceleration,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (mSensorGyroscope != null) {
            mSensorManager.registerListener(this, mSensorGyroscope,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (mSensorRotation != null) {
            mSensorManager.registerListener(this, mSensorRotation,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (mSensorGravity != null) {
            mSensorManager.registerListener(this, mSensorGravity,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (mSensorBarometer != null) {
            mSensorManager.registerListener(this, mSensorBarometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();

        switch (sensorType) {
            case Sensor.TYPE_LIGHT:
                mTextSensorLight.setText(getResources().getString(R.string.label_light,  event.values[0]));
                break;
            case Sensor.TYPE_PROXIMITY:
                mTextSensorProximity.setText(getResources().getString(R.string.label_proximity,  event.values[0]));
                break;
            case Sensor.TYPE_ACCELEROMETER:
                mTextSensorAccelerometer.setText(getResources().getString(R.string.label_accelerometer,  event.values[0],  event.values[1],  event.values[2]));
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                mTextSensorLinearAcceleration.setText(getResources().getString(R.string.label_linear_acceleration,  event.values[0],  event.values[1],  event.values[2]));
                break;
            case Sensor.TYPE_GYROSCOPE:
                mTextSensorGyroscope.setText(getResources().getString(R.string.label_gyroscope, event.values[0],  event.values[1],  event.values[2]));
                break;
            case Sensor.TYPE_ROTATION_VECTOR:
                mTextSensorRotation.setText(getResources().getString(R.string.label_rotation, event.values[0], event.values[1], event.values[2], event.values[3]));
                break;
            case Sensor.TYPE_GRAVITY:
                mTextSensorGravity.setText(getResources().getString(R.string.label_gravity, event.values[0], event.values[1], event.values[2]));
                break;
            case Sensor.TYPE_PRESSURE:
                mTextSensorBarometer.setText(getResources().getString(R.string.label_barometer, event.values[0]));
                break;
             default:
                // do nothing
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
