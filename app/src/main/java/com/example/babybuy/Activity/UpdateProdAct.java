package com.example.babybuy.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.babybuy.Database.Database;
import com.example.babybuy.Model.ProductDataModel;
import com.example.babybuy.R;
import com.google.android.gms.location.FusedLocationProviderClient;
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

public class UpdateProdAct extends AppCompatActivity implements OnMapReadyCallback {

    SupportMapFragment smf;
    FusedLocationProviderClient client;
    GoogleMap mgoogleMap;
    NetworkInfo networkInfo;
    ConnectivityManager manager;
    List<Address> address;
    ArrayList<ProductDataModel> pdm;
    Database db;
    Geocoder geocoder;
    TextView productaddcamera, productaddgallery;
    Button productupdatebtn;
    ImageView productaddimage, backicon;
    EditText productaddname, productadddes, productaddquantity, productaddprice;
    final int CAMERA_CODE = 100;
    final int GALLERY_CODE = 200;
    Double lat, lng;
    ProductDataModel productDataModel;
    Integer id, catid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product);
        id = getIntent().getExtras().getInt("productid");
        catid = getIntent().getExtras().getInt("pcid");
        db = new Database(this);

        //change notification color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.setStatusBarColor(this.getResources().getColor(R.color.greencolor));
        }

        //id connected
        productupdatebtn = findViewById(R.id.productupdatebtnid);
        productaddname = findViewById(R.id.productupdatetitleid);
        productaddquantity = findViewById(R.id.productupdatequantityid);
        productadddes = findViewById(R.id.productuodatedesid);
        productaddprice = findViewById(R.id.productupdatepriceid);
        productaddimage = findViewById(R.id.productupdateimageid);
        productaddgallery = findViewById(R.id.productupdatefromgallery);
        productaddcamera = findViewById(R.id.productupdatefromcamera);
        backicon = findViewById(R.id.ubackimgf);


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
        smf.getMapAsync(this);

        productupdatebtn.setOnClickListener(v -> {
            AddProdAct adp = new AddProdAct();
            productDataModel = new ProductDataModel();
            productDataModel.setProductimage(adp.convertImageViewToByteArray(productaddimage));
            productDataModel.setProductname(productaddname.getText().toString());
            productDataModel.setProductquantity(Integer.parseInt(productaddquantity.getText().toString()));
            productDataModel.setProductprice(Double.parseDouble(productaddprice.getText().toString()));
            productDataModel.setProductdescription(productadddes.getText().toString());
            productDataModel.setProductstatus(pdm.get(0).getProductstatus());
            productDataModel.setProductcategoryid(pdm.get(0).getProductcategoryid());
            productDataModel.setProductlat(lat);
            productDataModel.setProductlong(lng);

            int result = db.updateproduct(productDataModel, pdm.get(0).getProductid());
            if (result == -1) {
                Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Succcess", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UpdateProdAct.this, ProdListAct.class);
                intent.putExtra("pcid", pdm.get(0).getProductcategoryid());
                startActivity(intent);
            }
        });

        //back to productlistactivity
        backicon.setOnClickListener(v -> {
            Intent intent = new Intent(UpdateProdAct.this, ProdListAct.class);
            intent.putExtra("pcid", pdm.get(0).getProductcategoryid());
            startActivity(intent);
        });
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

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mgoogleMap = googleMap;
        pdm = db.productfetchdataformapload(id);

        productaddname.setText(pdm.get(0).getProductname());
        productaddquantity.setText(String.valueOf(pdm.get(0).getProductquantity()));
        productaddprice.setText(String.valueOf(pdm.get(0).getProductprice()));
        productadddes.setText(pdm.get(0).getProductdescription());
        Bitmap ImageDataInBitmap = BitmapFactory.decodeByteArray(pdm.get(0).getProductimage(), 0, pdm.get(0).getProductimage().length);
        productaddimage.setImageBitmap(ImageDataInBitmap);

        checkConnection();
        if (networkInfo.isConnected() && networkInfo.isAvailable()) {
            lat = pdm.get(0).getProductlat();
            lng = pdm.get(0).getProductlong();
            try {
                getitemlocation(lat, lng);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Please Check Network", Toast.LENGTH_SHORT).show();
        }

        mgoogleMap.setOnMapClickListener(lating -> {
            mgoogleMap.clear();

            checkConnection();
            if (networkInfo.isConnected() && networkInfo.isAvailable()) {

                lat = lating.latitude;
                lng = lating.longitude;
                try {
                    getitemlocation(lat, lng);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Please Check Network", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //check internet connectivity
    public void checkConnection() {
        manager = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        networkInfo = manager.getActiveNetworkInfo();
    }


    public void getitemlocation(Double mlat, Double mlng) throws IOException {
        geocoder = new Geocoder(UpdateProdAct.this, Locale.getDefault());
        if (mlat != 0) {
            address = geocoder.getFromLocation(mlat, mlng, 1);
            if (address != null) {
                String mAddress = address.get(0).getAddressLine(0);
                String city = address.get(0).getLocality();
                String state = address.get(0).getAdminArea();
                String Country = address.get(0).getCountryName();
                String postalCode = address.get(0).getPostalCode();
                String knownName = address.get(0).getFeatureName();

                String productaddress = pdm.get(0).getProductname() + " " + mAddress;


                if (mAddress != null) {
                    MarkerOptions mmarkerOptions = new MarkerOptions();
                    LatLng lating = new LatLng(mlat, mlng);
                    mmarkerOptions.position(lating).title(productaddress);

                    mgoogleMap.addMarker(mmarkerOptions).showInfoWindow();
                    mgoogleMap.animateCamera(CameraUpdateFactory
                            .newLatLngZoom(lating, 17));


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
        Intent intent = new Intent(UpdateProdAct.this, ProdListAct.class);
        intent.putExtra("pcid", pdm.get(0).getProductcategoryid());
        startActivity(intent);
    }
}