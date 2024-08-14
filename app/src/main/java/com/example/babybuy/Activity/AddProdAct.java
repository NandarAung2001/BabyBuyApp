package com.example.babybuy.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.tasks.Task;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddProdAct extends AppCompatActivity implements OnMapReadyCallback {

    SupportMapFragment smf;
    FusedLocationProviderClient client;
    GoogleMap mgoogleMap;
    NetworkInfo networkInfo;
    ConnectivityManager manager;
    List<Address> address;
    String selectedaddress;
    Geocoder geocoder;
    TextView productaddcamera, productaddgallery;
    Button productaddbtn;
    ImageView productaddimage, backicon;
    EditText productaddname, productadddes, productaddquantity, productaddprice;
    final int CAMERA_CODE = 100;
    final int GALLERY_CODE = 200;
    Double lat, lng;
    ProductDataModel productDataModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        //category id value from Intent data pass
        int procatidlist = getIntent().getExtras().getInt("pcid");

        //Database connection
        Database db = new Database(this);

        //id connected
        productaddbtn = findViewById(R.id.productaddbtnid);
        productaddname = findViewById(R.id.productaddtitleid);
        productaddquantity = findViewById(R.id.productaddquantityid);
        productaddprice = findViewById(R.id.productaddpriceid);
        productadddes = findViewById(R.id.productadddesid);
        productaddimage = findViewById(R.id.productaddimageid);
        productaddgallery = findViewById(R.id.productaddfromgallery);
        productaddcamera = findViewById(R.id.productaddfromcamera);
        backicon = findViewById(R.id.backimgf);


        //add product image from gallery
        productaddgallery.setOnClickListener(v -> {
            Intent igallery = new Intent(Intent.ACTION_PICK);
            igallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(igallery, GALLERY_CODE);
        });

        //add product image from camera
        productaddcamera.setOnClickListener(v -> {
            Intent icamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(icamera, CAMERA_CODE);
        });

        smf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        client = LocationServices.getFusedLocationProviderClient(this);

        smf.getMapAsync(this);
        getmylocation();

        //add product data to product table of database
        productaddbtn.setOnClickListener(v -> {

            Integer prdstatus = -1;
            productDataModel = new ProductDataModel();
            productDataModel.setProductimage(convertImageViewToByteArray(productaddimage));
            productDataModel.setProductname(productaddname.getText().toString());
            productDataModel.setProductquantity(Integer.parseInt(productaddquantity.getText().toString()));
            productDataModel.setProductprice(Double.parseDouble(productaddprice.getText().toString()));
            productDataModel.setProductdescription(productadddes.getText().toString());
            productDataModel.setProductstatus(prdstatus);
            productDataModel.setProductcategoryid(procatidlist);
            productDataModel.setProductlat(lat);
            productDataModel.setProductlong(lng);

            long result = db.productadd(productDataModel);
            if (result == -1) {
                Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Succcess", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AddProdAct.this, ProdListAct.class);
                intent.putExtra("pcid", procatidlist);
                startActivity(intent);
            }
        });

        //back to productlistactivity
        backicon.setOnClickListener(v -> {
            Intent intent = new Intent(AddProdAct.this, ProdListAct.class);
            intent.putExtra("pcid", procatidlist);
            startActivity(intent);
        });

        //Load Map

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_CODE) {
                //for camera
                Bitmap img = (Bitmap) (data.getExtras().get("data"));
                productaddimage.setImageBitmap(img);
            } else if (requestCode == GALLERY_CODE) {
                //for gallery
                productaddimage.setImageURI(data.getData());
            }
        }
    }

    byte[] convertImageViewToByteArray(ImageView imageView) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

    }

    public void getmylocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(location -> smf.getMapAsync(googleMap -> {

            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("You are here...!!");

            googleMap.addMarker(markerOptions);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
            mgoogleMap = googleMap;
            mgoogleMap.setOnMapClickListener(lating -> {
                mgoogleMap.clear();

                checkConnection();
                if (networkInfo.isConnected() && networkInfo.isAvailable()) {

                    lat = lating.latitude;
                    lng = lating.longitude;
                    try {
                        GetAddress(lat, lng);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "Please Check Network", Toast.LENGTH_SHORT).show();
                }
            });
        }));
    }

    //check internet connectivity
    public void checkConnection() {
        manager = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        networkInfo = manager.getActiveNetworkInfo();
    }


    //get address
    private void GetAddress(double mlat, double mlng) throws IOException {
        geocoder = new Geocoder(this, Locale.getDefault());

        if (lat != 0) {

            address = geocoder.getFromLocation(lat, lng, 1);
            if (address != null) {
                String mAddress = address.get(0).getAddressLine(0);
                String city = address.get(0).getLocality();
                String state = address.get(0).getAdminArea();
                String Country = address.get(0).getCountryName();
                String postalCode = address.get(0).getPostalCode();
                String knownName = address.get(0).getFeatureName();

                selectedaddress = mAddress;

                if (mAddress != null) {
                    MarkerOptions markerOptions = new MarkerOptions();
                    LatLng lating = new LatLng(lat, lng);
                    markerOptions.position(lating).title(mAddress);
                    mgoogleMap.addMarker(markerOptions).showInfoWindow();

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
}