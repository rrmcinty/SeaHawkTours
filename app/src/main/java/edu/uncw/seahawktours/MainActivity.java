package edu.uncw.seahawktours;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

import io.objectbox.Box;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

/**
 * Main activity attaches toolbar, creates options menu, requests permission for location, contains onclick for nearest
 * building button, and location loop
 */
public class MainActivity extends AppCompatActivity {
    String TAG = "MAIN";
    //in order of use
    private static int permissionRequested = 0;
    CoordinatorLayout snackLocation;
    private LocationCallback mLocationCallback;
    double lastLatitude;
    double lastLongitude;
    final static int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    Box<Building> buildingBox = App.boxStore.boxFor(Building.class);
    private LocationRequest mLocationRequest;
    private static int REQUEST_CHECK_SETTINGS = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //request access before app starts
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && permissionRequested<1) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            permissionRequested++;
            snackLocation = (CoordinatorLayout) findViewById(R.id.snackbarlocation);
            Snackbar.make(snackLocation, R.string.location_off, Snackbar.LENGTH_INDEFINITE).show();
        } else {
            //access granted
        }


        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult != null) {
                    for (Location location : locationResult.getLocations()) {
                        Log.i(TAG, String.format("%.7f", location.getLatitude()));
                        Log.i(TAG, String.format("%.7f", location.getLongitude()));
                        lastLatitude = location.getLatitude();
                        lastLongitude = location.getLongitude();
                    }
                }
            }
        };

        if (savedInstanceState != null) {

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu started");
        //inflate the menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "onOptionsItemSelected start");
        switch (item.getItemId()) {
            case R.id.action_about:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onClickNearestBuilding(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            //create toast if user denies location request after clicking nearest building
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "I need permission to access location in order get nearest building", Toast.LENGTH_LONG).show();

            }
        }
        else {
            double closestDist = 0;
            Building closestBuilding = null;
            List<Building> buildingList = buildingBox.getAll();
            for (Building b : buildingList) {
                //
                double lats = lastLatitude - b.getLatitude();
                double lons = lastLongitude - b.getLongitude();
                double current = Math.sqrt((lats * lats) + (lons * lons));
                if ((current < closestDist || closestDist == 0)) {
                    closestDist = current;
                    closestBuilding = b;
                }
            }
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(DetailActivity.EXTRA_BUILDINGID, (int) closestBuilding.getId());
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                //shows snackbar after first permissions decision
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    snackLocation = (CoordinatorLayout) findViewById(R.id.snackbarlocation);
                    Snackbar.make(snackLocation, R.string.location_on, Snackbar.LENGTH_INDEFINITE).show();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    snackLocation = (CoordinatorLayout) findViewById(R.id.snackbarlocation);
                    Snackbar.make(snackLocation, R.string.location_off, Snackbar.LENGTH_INDEFINITE).show();
                }
                return;
            }
        }
    }

    //
    //=========================================================================================================================
    //


    /**
     * This method is called to kick off the chain of events that requests continuous location updates.
     */
    protected void createLocationRequest() {
        if (mLocationRequest == null) {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
                startLocationUpdates();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHECK_SETTINGS) {
            // Regardless of whether the user fixed the issue or not, try again.
            // This could be really annoying for the user...
            // In reality, you should check the resultCode for success. If not successful, you
            // should degrade the functionality.
            createLocationRequest();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        createLocationRequest();
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(this, "I need permission to access location in order get nearest building", Toast.LENGTH_SHORT).show();
        } else {
            getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    Looper.myLooper());

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//                 mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//                 outState.putCharSequence(LATITUDE_KEY, latText.getText());
//                 outState.putCharSequence(LONGITUDE_KEY, lonText.getText());
//                 outState.putCharSequence(ACCURACY_KEY, accuracyText.getText());
        super.onSaveInstanceState(outState);
    }
}
