package com.rajshekhar.mapapp;

import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;


public  class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mGoogleMap;
    GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (googleServiceAvialable()) {

            Toast.makeText(MainActivity.this, "Perfect", Toast.LENGTH_SHORT).show();
            setContentView(R.layout.activity_main);
            initMap();
        }
    }

    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }

    public boolean googleServiceAvialable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvialable = api.isGooglePlayServicesAvailable(this);
        if (isAvialable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvialable)) {
            Dialog dialog = api.getErrorDialog(this, isAvialable, 0);
            dialog.show();
        } else {
            Toast.makeText(MainActivity.this, "Can't connect to play services", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

   // @Override

    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        gotoLocactionZoom(12.913255,77.6259,15);


       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
        }
        mGoogleMap.setMyLocationEnabled(true);*/


        // mGoogleApiClient.connect();

    }

    private void gotoLocaction(double lat, double lng) {
        LatLng ll=new LatLng(lat,lng);
        CameraUpdate update=CameraUpdateFactory.newLatLng(ll);
        mGoogleMap.moveCamera(update);

    }

    private void gotoLocactionZoom(double lat, double lng,float zoom) {
        LatLng ll=new LatLng(lat,lng);
        CameraUpdate update=CameraUpdateFactory.newLatLngZoom(ll,zoom);
        mGoogleMap.moveCamera(update);

    }

    public void geoLocate(View view) throws IOException {
        EditText et = (EditText) findViewById(R.id.editText);
        String location = et.getText().toString();

        Geocoder gc = new Geocoder(this);
        List<android.location.Address> list = gc.getFromLocationName(location, 1);
        android.location.Address address = list.get(0);
        String localoty = address.getLocality();

        double lat = address.getLatitude();
        double lng = address.getLongitude();

        gotoLocactionZoom(lat, lng, 15);

    }


}