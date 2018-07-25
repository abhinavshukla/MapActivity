package test.com.maptest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.koushikdutta.ion.Ion;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import dmax.dialog.SpotsDialog;
import test.com.maptest.model.LocationData;
import test.com.maptest.model.LocationDataItem;
import test.com.maptest.model.ServerResponse;
import test.com.maptest.utils.DisplayUtils;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private boolean mLocationPermissionGranted = false;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 101;
    SupportMapFragment mapFragment;
    Location mLastKnownLocation, mDefaultLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    List<LocationDataItem> locationDataArrayList;
    SpotsDialog dialog;
    ProgressDialog progressDialog;
    private int nearestIndex = -1;
    double nearestDistance = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //dialog = new SpotsDialog(this, R.style.Custom);
        progressDialog = new ProgressDialog(MapsActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("please wait..");
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        updateLocationUI();
        getDeviceLocation();
    }

    public void getLocationPermision() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            updateLocationUI();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            updateLocationUI();
        }
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermision();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();

    }

    private void getDeviceLocation() {

        try {
            if (mLocationPermissionGranted) {
                Task locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location) task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), 15));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getServerData();
                                }
                            });

                        } else {

                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mDefaultLocation.getLatitude(),
                                    mDefaultLocation.getLongitude()), 15));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getServerData() {

        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://myscrap.com/android/msdiscover", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("getData", response);
                locationDataArrayList = new ArrayList<>();
                ServerResponse serverResponse = new Gson().fromJson(response, ServerResponse.class);
                locationDataArrayList = serverResponse.getLocationData();
                createMarker();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Sorry Server Data Not Present", Toast.LENGTH_SHORT).show();
                Log.e("getStatusDriver", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("apiKey", "123456test");
                return params;
            }
        };

        stringRequest.setShouldCache(false);
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }

    private void createMarker() {
        //Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background);
        //Bitmap smallMarker = Bitmap.createScaledBitmap(bm, (int) DisplayUtils.getPixelsFromDP(24, BookNewRideActivity.this), (int) DisplayUtils.getPixelsFromDP(42, BookNewRideActivity.this), false);
        int len = locationDataArrayList.size();
        Bitmap bmImg = BitmapFactory.decodeResource(getResources(), R.drawable.img_pick_marker);
        Bitmap smallMarker = Bitmap.createScaledBitmap(bmImg, (int) DisplayUtils.getPixelsFromDP(24, MapsActivity.this), (int) DisplayUtils.getPixelsFromDP(24, MapsActivity.this), false);
        for (int i = 0; i < len; i++) {
           /* Bitmap bmImg;
            try {
                bmImg = Ion.with(getApplicationContext())
                        .load(locationDataArrayList.get(i).getImage()).asBitmap().get();
            } catch (InterruptedException e) {
                bmImg=null;
                e.printStackTrace();
            } catch (ExecutionException e) {
                bmImg=null;
                e.printStackTrace();
            }*/
            LocationDataItem locationDataItem = locationDataArrayList.get(i);


            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(locationDataItem.getLatitude(), locationDataItem.getLongitude()))
                    .anchor(0.5f, 0.5f)
                    .title(locationDataItem.getName())
                    .snippet(locationDataItem.getAddressOne())
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        }
        progressDialog.dismiss();


    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_filter) {
            int len = locationDataArrayList.size();
            for (int i = 0; i < len; i++) {
                LocationDataItem locationDataItem = locationDataArrayList.get(i);
                double distance = distance(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(), locationDataItem.getLatitude(), locationDataItem.getLongitude());
                if (distance < nearestDistance || nearestDistance == -1) {
                    nearestDistance = distance;
                    nearestIndex = i;
                }
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locationDataArrayList.get(nearestIndex).getLatitude(),
                    locationDataArrayList.get(nearestIndex).getLongitude()), 18));

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Share Nearest Location https://www.google.com/maps/dir/'"+mLastKnownLocation.getLatitude()+","+mLastKnownLocation.getLongitude()+"'/'"+locationDataArrayList.get(nearestIndex).getLatitude()+","+locationDataArrayList.get(nearestIndex).getLongitude()+"'");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
