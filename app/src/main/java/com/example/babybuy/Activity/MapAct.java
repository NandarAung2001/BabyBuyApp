package com.example.babybuy.Activity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.babybuy.Database.Database;
import com.example.babybuy.Model.ProductDataModel;
import com.example.babybuy.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapAct extends AppCompatActivity implements OnMapReadyCallback {

    SupportMapFragment smf;
    FusedLocationProviderClient client;
    GoogleMap mgoogleMap;
    Geocoder geocoder;
    Double lat, lng, productlat, productlng;
    NetworkInfo networkInfo;
    ConnectivityManager manager;
    List<Address> address;
    String selectedaddress, newitem = "";
    Database db;
    int procatid;
    ArrayList<ProductDataModel> productDataModels;
    ImageView backimg;
    String  product;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //change notification color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.setStatusBarColor(this.getResources().getColor(R.color.greencolor));
        }


        procatid = getIntent().getExtras().getInt("productid");
        product = getIntent().getExtras().getString("productname");


        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        smf = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
        client = LocationServices
                .getFusedLocationProviderClient(this);



        smf.getMapAsync(this);


        backimg = findViewById(R.id.mbackimgf);
        backimg.setOnClickListener(view -> {
            startActivity(new Intent(MapAct.this, MainAct.class));
        });

    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mgoogleMap = googleMap;
        db = new Database(MapAct.this);
        productDataModels = db.productfetchdataformapload(procatid);
        productlat = productDataModels.get(0).getProductlat();
        productlng = productDataModels.get(0).getProductlong();
        if (productlng != 0.0) {
            try {
                GetAddress(productlat, productlng);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void checkConnection() {
        manager = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        networkInfo = manager.getActiveNetworkInfo();
    }


    private void GetAddress(double mlat, double mlng) throws IOException {
        geocoder = new Geocoder(MapAct.this, Locale.getDefault());
        if (mlat != 0) {
            address = geocoder.getFromLocation(mlat, mlng, 1);
            if (address != null) {
                String mAddress = address.get(0).getAddressLine(0);
                String city = address.get(0).getLocality();
                String state = address.get(0).getAdminArea();
                String Country = address.get(0).getCountryName();
                String postalCode = address.get(0).getPostalCode();
                String knownName = address.get(0).getFeatureName();

                String productaddress = newitem + " " + mAddress;

                selectedaddress = mAddress;

                if (mAddress != null) {
                    MarkerOptions markerOptions = new MarkerOptions();
                    LatLng lating = new LatLng(mlat, mlng);
                    markerOptions.position(lating).title(productaddress);
                    mgoogleMap.addMarker(markerOptions).showInfoWindow();
                    mgoogleMap.animateCamera(CameraUpdateFactory
                            .newLatLngZoom(lating, 17));

                    procatid = -1;

                } else {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }




    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MapAct.this, MainAct.class);
        startActivity(intent);
    }


}