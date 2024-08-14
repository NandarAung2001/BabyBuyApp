package com.example.babybuy.Fragments;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.denzcoskun.imageslider.ImageSlider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.babybuy.Adapter.CatoAdapter;
import com.example.babybuy.Database.Database;
import com.example.babybuy.Model.CatDataModel;
import com.example.babybuy.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    //Variable for Recyclerview and Adapter
    RecyclerView recycler_category;
    CatoAdapter catadapter;
    ArrayList<CatDataModel> allcatdata;
    final int CAMERA_CODE = 100;
    final int GALLERY_CODE = 200;
    TextView cataddcamera, cataddgallery;
    ImageView cataddimage;
    ImageSlider catmainimage;
    FloatingActionButton  addnewcat;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        //Insert category button
        addnewcat = view.findViewById(R.id.cataddbtn);



        //Add new cateogry
        addnewcat.setOnClickListener(v -> {
            //call insert method
            addnewcategorydailoge();
        });



        //recycler view for category
        recycler_category = view.findViewById(R.id.recy_view_cat);
        recycler_category.setHasFixedSize(false);
        GridLayoutManager grid = new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false);

        //set layout manager for recycler view
        recycler_category.setLayoutManager(grid);

        //CategoryListAdapter object
        catadapter = new CatoAdapter(getContext(), catdata());
        //set Recyclerview to Adapter
        recycler_category.setAdapter(catadapter);

        return view;
    }

    //method to get category item from database
    private ArrayList<CatDataModel> catdata() {
        Database catdb = new Database(getActivity());
        allcatdata = catdb.categoryfetchdata();
        return allcatdata;
    }

    //method to add new category
    private void addnewcategorydailoge() {
        AlertDialog.Builder addcat = new AlertDialog.Builder(getActivity(), R.style.YourThemeName);

        View viewalert = LayoutInflater.from(getActivity()).inflate(R.layout.alertdialoge_category, null);
        cataddgallery = viewalert.findViewById(R.id.cataddfromgallery);
        cataddcamera = viewalert.findViewById(R.id.cataddfromcamera);
        cataddimage = viewalert.findViewById(R.id.cataddimageid);
        addcat.setCancelable(true);
        addcat.setView(viewalert);
        EditText newcategoryname = viewalert.findViewById(R.id.edittextcatname);

        //add product image from gallery
        cataddgallery.setOnClickListener(v -> {
            Intent igallery = new Intent(Intent.ACTION_PICK);
            igallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(igallery, GALLERY_CODE);
        });

        //add product image from camera
        cataddcamera.setOnClickListener(v -> {
            Intent icamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(icamera, CAMERA_CODE);
        });

        //new category add button
        addcat.setPositiveButton("add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Database db_cat = new Database(getActivity());
                long insertCat = db_cat.categoryadd(newcategoryname.getText().toString(), convertImageViewToByteArray(cataddimage));
                if (insertCat == -1) {
                    Toast.makeText(getActivity(), "Failed to Update", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Successfully Updated", Toast.LENGTH_SHORT).show();
                    // Collections.reverse(allcatdata);
                    CatoAdapter adapter = new CatoAdapter(getContext(), catdata());
                    recycler_category.setAdapter(adapter);
                }
            }
        });
        //cancel button
        addcat.setNegativeButton("Cancel", null);
        addcat.create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_CODE) {
                //for camera
                Bitmap img = (Bitmap) (data.getExtras().get("data"));
                cataddimage.setImageBitmap(img);
            } else if (requestCode == GALLERY_CODE) {
                //for gallery
                cataddimage.setImageURI(data.getData());
            }
        }
    }


    private byte[] convertImageViewToByteArray(ImageView imageView) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

}