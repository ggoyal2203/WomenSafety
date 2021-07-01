package com.example.womensafety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
public class MainActivity extends AppCompatActivity implements LocationListener {

    EditText num;
    Button store;
    SharedPreferences pref;

    LocationManager manager;
    String lati, longi;
    String link;
    SensorManager sensorManager;
    SmsManager smsManager;
    float acelVal,acelLast,shake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        num=findViewById(R.id.num);


        SensorEventListener sensorListener =new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                acelLast=acelVal;
                acelVal=(float) Math.sqrt((double) x*x + y*y + z*z);
                float delta = acelVal-acelLast;
                shake = shake * 0.9f +delta;

                if (shake>12){
                    pref=getSharedPreferences("data", Context.MODE_PRIVATE);

                    String number = num.getText().toString();
                    link = "https://www.google.co.in/maps/dir///@"+lati+","+longi+",4z";
                    String message = "Hello i am in trouble at this location \n"+link;
                    smsManager.sendTextMessage(number,"",message,null,null);
                    Toast.makeText(MainActivity.this, "sent", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };





        smsManager=SmsManager.getDefault();
        sensorManager =(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(sensorListener,sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
        acelVal=SensorManager.GRAVITY_EARTH;
        acelLast=SensorManager.GRAVITY_EARTH;
        shake =0.0f;


        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) !=
        PackageManager.PERMISSION_GRANTED){
            return;
        }
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                0, this);


        store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //String n = num.getText().toString();
                Integer i = Integer.parseInt(num.getText().toString());

                SharedPreferences.Editor edit = pref.edit();
                edit.putInt("num",i);
                edit.commit();

                Toast.makeText(MainActivity.this, "Data stored", Toast.LENGTH_SHORT).show();

            }
        });





    }


    @Override
    public void onLocationChanged(Location location) {

        lati=""+location.getLatitude();
        longi=""+location.getLongitude();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
