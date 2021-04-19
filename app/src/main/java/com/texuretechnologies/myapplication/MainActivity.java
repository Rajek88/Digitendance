package com.texuretechnologies.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.Executors;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    private static final int FLAG_ONGOING_EVENT =2 ;
    private ImageView title;
    private TextView textAddress;
    private ProgressBar progressBar;
    private ResultReceiver resultReceiver;
    private Button inLogger;
    private Button outLogger;
    private Button btnReport;
    private Button btnLeave;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private static final String TAG = "MapsActivity";

    private GoogleMap mMap;
    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;
    private String GEOFENCE_ID = "SOME_GEOFENCE_ID";
    private int FINE_LOCATION_REQUEST_ACCESS_CODE = 10001;
    private int BACKGROUND_LOCATION_ACCESS_CODE = 10002;
    private float GEOFENCE_RADIUS = 25;
    private String id = "Ishan_Agrawal";
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
    String currentDateandTime = sdf.format(new Date());
    private static String id1="Ishan_Agrawal";
    static String url3 = "https://script.google.com/macros/s/AKfycbyW_Q5PxeVqTxkX9ajkSzpNWLJ-oBO_mR8Ij0_B0NBM7tAxTLMC/exec?action=getid&id="+id1;

    private String gcode = "https://script.google.com/macros/s/AKfycbyW_Q5PxeVqTxkX9ajkSzpNWLJ-oBO_mR8Ij0_B0NBM7tAxTLMC/exec?action=in&id=2";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textAddress = findViewById(R.id.textAddress);
        progressBar = findViewById(R.id.progressBar);
        title = findViewById(R.id.title);
        title.setScaleType(ImageView.ScaleType.FIT_XY);   // To scale the title image
        inLogger = (Button) findViewById(R.id.inLogger);
        outLogger = (Button) findViewById(R.id.outLogger);
        btnReport = (Button) findViewById(R.id.btnReport);
        btnLeave = (Button) findViewById(R.id.btnLeave);
        TextView empname = (TextView) findViewById(R.id.name);
        Typeface typeface1 = ResourcesCompat.getFont(this, R.font.amaranth_bold);
        empname.setTypeface(typeface1);
        TextView empno = (TextView) findViewById(R.id.rollno);
        Typeface typeface = ResourcesCompat.getFont(this, R.font.amaranth);
        empno.setTypeface(typeface);
        resultReceiver = new AddressResultReceiver(new Handler());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.storelocationmap);
        mapFragment.getMapAsync(this);

        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);
        findViewById(R.id.getLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });

        inLogger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentLocation();
                Toast.makeText(MainActivity.this, "Logging You In", Toast.LENGTH_SHORT).show();
                try {
                    inLogger();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
                }
            }

        });
        outLogger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentLocation();
                Toast.makeText(MainActivity.this, "Logging You Out", Toast.LENGTH_SHORT).show();
                try {
                    outLogger();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
                }
            }

        });
        btnLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Apply For Leave", Toast.LENGTH_SHORT).show();
                goToLeave();


            }

        });
        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Requesting Your Attendance Report", Toast.LENGTH_SHORT).show();
                getReport();


            }

        });

    }

    private void getReport() {
        Intent intent1= new Intent(this,report.class);
        startActivity(intent1);
    }

    private void goToLeave() {
        Intent intent1= new Intent(this,leave.class);
        startActivity(intent1);
    }

    @Override               // Need to put in Mainactivity Class to take effect..................
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
// Check for the integer request code originally supplied to startResolutionForResult().
            case LocationRequest.PRIORITY_HIGH_ACCURACY:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "onActivityResult: GPS Enabled by user");
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "onActivityResult: User rejected GPS request");
                        break;
                }
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getCurrentLocation();
        mMap.setOnMapLongClickListener(this);
    }

    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_REQUEST_ACCESS_CODE);
        }
    }


    private void getCurrentLocation() {
        progressBar.setVisibility(View.VISIBLE);
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(MainActivity.this).addApi(LocationServices.API).build();
        googleApiClient.connect();
        final LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder settingsBuilder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(this)
                .checkLocationSettings(settingsBuilder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    //getCurrentLocation();
                } catch (ApiException ex) {
                    switch (ex.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvableApiException =
                                        (ResolvableApiException) ex;
                                //startIntentSenderForResult(status.getResolution().getIntentSender(), REQUEST_CHECK_SETTINGS, null, 0, 0, 0, null);
                                resolvableApiException.startResolutionForResult(MainActivity.this, LocationRequest.PRIORITY_HIGH_ACCURACY);
                            } catch (IntentSender.SendIntentException e) {

                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                            break;
                    }
                }
            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION_PERMISSION);
            return;
        }
        LocationServices.getFusedLocationProviderClient(MainActivity.this).requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LocationServices.getFusedLocationProviderClient(MainActivity.this).removeLocationUpdates(this);
                if (locationResult != null && locationResult.getLocations().size() > 0) {
                    int latestLocationIndex = locationResult.getLocations().size() - 1;
                    double latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                    double longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                    Location location = new Location("");
                    location.setLatitude(latitude);
                    location.setLongitude(longitude);
                    fetchAddressFromLatLong(location);
                    // Add a marker in Sydney and move the camera
                    LatLng eiffel = new LatLng(latitude, longitude);
                    //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));//marker location or mark office
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eiffel, 19));
                    enableUserLocation();

                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        }, Looper.getMainLooper());
    }

    private void fetchAddressFromLatLong(Location location) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        startService(intent);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (Build.VERSION.SDK_INT >= 29) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                handleMapLongClick(latLng);
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_CODE);
            }

        } else {
            handleMapLongClick(latLng);
        }
    }

    public void handleMapLongClick(LatLng latLng) {
        mMap.clear();
        addMarker(latLng);
        addCircle(latLng, GEOFENCE_RADIUS);
        addGeoFence(latLng, GEOFENCE_RADIUS);
    }

    private void addMarker(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        mMap.addMarker(markerOptions);

    }

    private void addCircle(LatLng latLng, Float radius) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255, 255, 0, 0));
        circleOptions.fillColor(Color.argb(65, 255, 0, 0));
        circleOptions.strokeWidth(4);
        mMap.addCircle(circleOptions);

    }

    private void addGeoFence(LatLng latLng, float radius) {
        final Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_REQUEST_ACCESS_CODE);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_CODE);
            return;
        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Geofence Created");

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String errorMessage = geofenceHelper.getErrorString(e);
                Log.d(TAG, "onFailure" + errorMessage);

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_LOCATION_REQUEST_ACCESS_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_REQUEST_ACCESS_CODE);
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_CODE);
                    return;
                }
                mMap.setMyLocationEnabled(true);
            }else{

            }
        }
        if(requestCode==BACKGROUND_LOCATION_ACCESS_CODE){
            if(grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                mMap.setMyLocationEnabled(true);
            }else{

            }
        }
    }

   public void inLogger() throws NullPointerException, IOException {
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                String url1 = "https://script.google.com/macros/s/AKfycbyW_Q5PxeVqTxkX9ajkSzpNWLJ-oBO_mR8Ij0_B0NBM7tAxTLMC/exec?action=in&id=" + id;
                try {
                    String out = new Scanner(new URL(url1).openStream(), "UTF-8").useDelimiter("\\A").next();
                    System.out.println(out);
                    Notification.Builder noti1 = new Notification.Builder(MainActivity.this)
                            .setContentTitle("You are now logged into the office")
                            .setContentText("Your IN-Time is : "+currentDateandTime)
                            .setSmallIcon(R.drawable.ic_launcher_foreground)
                            .setOngoing(true);// Again, THIS is the important line;
                    NotificationManagerCompat notificationManager1 = NotificationManagerCompat.from(MainActivity.this);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    {
                        String channelId = "some_channel_id";
                        NotificationChannel channel = new NotificationChannel(
                                channelId,
                                "Running",
                                NotificationManager.IMPORTANCE_HIGH);
                        notificationManager1.createNotificationChannel(channel);
                        noti1.setChannelId(channelId);
                    }
                    // notificationId is a unique int for each notification that you must define
                    notificationManager1.notify(FLAG_ONGOING_EVENT, noti1.build());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
   }

   public void outLogger() throws NullPointerException, IOException {
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                String url1 = "https://script.google.com/macros/s/AKfycbyW_Q5PxeVqTxkX9ajkSzpNWLJ-oBO_mR8Ij0_B0NBM7tAxTLMC/exec?action=out&id=" + id;
                try {
                    String out = new Scanner(new URL(url1).openStream(), "UTF-8").useDelimiter("\\A").next();
                    System.out.println(out);
                    Notification.Builder noti2 = new Notification.Builder(MainActivity.this)
                            .setContentTitle("You are now logged out of office")
                            .setContentText("Your OUT-Time is : "+currentDateandTime)
                            .setSmallIcon(R.drawable.ic_launcher_foreground)
                            .setOngoing(true);// Again, THIS is the important line;
                    NotificationManagerCompat notificationManager2 = NotificationManagerCompat.from(MainActivity.this);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    {
                        String channelId = "some_channel_id";
                        NotificationChannel channel = new NotificationChannel(
                                channelId,
                                "Running",
                                NotificationManager.IMPORTANCE_HIGH);
                        notificationManager2.createNotificationChannel(channel);
                        noti2.setChannelId(channelId);
                    }
                    // notificationId is a unique int for each notification that you must define
                    notificationManager2.notify(FLAG_ONGOING_EVENT, noti2.build());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        public void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if(resultCode==Constants.SUCCESS_RESULT){
                textAddress.setText(resultData.getString(Constants.RESULT_DATA_KEY));
            }else{
                Toast.makeText(MainActivity.this, resultData.getString(Constants.RESULT_DATA_KEY), Toast.LENGTH_SHORT).show();
            }
            textAddress.setText(resultData.getString(Constants.RESULT_DATA_KEY));
            progressBar.setVisibility(View.GONE);
        }
    }
}