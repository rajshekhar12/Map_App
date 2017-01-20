package com.rajshekhar.mapapp;

import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.graphics.Color;
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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

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
     // Check Google sevice is avialable in device or not
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
        //gotoLocactionZoom(12.913255, 77.6259, 15);

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
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();


    }

    Circle circle;

    //Go to desire locaion with zoom
    private void gotoLocactionZoom(double lat, double lng, float zoom) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mGoogleMap.moveCamera(update);

        if(circle!=null){
            circle.remove();
        }
        circle=drawCircle(new LatLng(lat,lng));

    }
    private Circle drawCircle(LatLng latlng){

        CircleOptions option=new CircleOptions()
                .center(latlng)
                .radius(5000)
                .fillColor(0x330000FF)
                .strokeColor(Color.BLUE)
                .strokeWidth(3);
        return mGoogleMap.addCircle(option);
    }
    // code for button(search) click
    public void geoLocate(View view) throws IOException {
        EditText et = (EditText) findViewById(R.id.editText);
        String location = et.getText().toString();

        Geocoder gc = new Geocoder(this);
        List<android.location.Address> list = gc.getFromLocationName(location, 1);
        android.location.Address address = list.get(0);//it sows only first data in array list
        String localoty = address.getLocality();

        double lat = address.getLatitude();
        double lng = address.getLongitude();

        gotoLocactionZoom(lat, lng, 15);

    }
    //call menu layout
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    //implement optional context menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mapTypeNone:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;
            case R.id.mapTypeNormal:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.mapTypeTerrain:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.mapTypeSatellite:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.mapTypeHybride:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    LocationRequest mLocationReques;

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //circle.remove();
        mLocationReques = LocationRequest.create();
        mLocationReques.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationReques.setInterval(100000);

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
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationReques, this);
        mGoogleMap.setMyLocationEnabled(true);

    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if(location==null){
            Toast.makeText(MainActivity.this,"Can't find your location",Toast.LENGTH_LONG).show();

        }else{

            LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());
            CameraUpdate update=CameraUpdateFactory.newLatLngZoom(ll,15);
            mGoogleMap.moveCamera(update);
            // call circle method
            if(circle!=null){
                circle.remove();
            }
            circle=drawCircle(new LatLng(location.getLatitude(),location.getLongitude()));

            //circle.remove();


        }

    }
}