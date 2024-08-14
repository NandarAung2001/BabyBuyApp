package com.example.babybuy.Fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.babybuy.Database.Database;
import com.example.babybuy.R;

import java.io.ByteArrayOutputStream;


public class UserFragment extends Fragment {


    public UserFragment() {
        // Required empty public constructor
    }

    final int GALLERY_CODE = 200;
    Database db;
    String value;
    TextView userfragFullname, userfragEmail, userfragFullnameforlogo;
    ImageView profile;
    byte[] imagebyte;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        userfragFullname = view.findViewById(R.id.userfragfullname);
        userfragEmail = view.findViewById(R.id.userfragEmail);
        userfragFullnameforlogo = view.findViewById(R.id.tv_name);
        profile = view.findViewById(R.id.imgUserr);

        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("Login", 0);
        value = sharedPreferences.getString("email", "null");
        db = new Database(getActivity());


        try {
            imagebyte = db.authfetchforimage(value);


            Bitmap ImageDataInBitmap = BitmapFactory.decodeByteArray(imagebyte, 0, imagebyte.length);
            profile.setImageBitmap(ImageDataInBitmap);
        }catch (Exception ex){

        }


        if (value != null) {
            String fullname = db.getfullname(value);
            userfragEmail.setText(value);
            userfragFullname.setText(fullname);
            userfragFullnameforlogo.setText(fullname);
        }

        profile.setOnClickListener(view1 -> {
            Intent igallery = new Intent(Intent.ACTION_PICK);
            igallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(igallery, GALLERY_CODE);
        });


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_CODE) {
                //for gallery
                SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("Login", 0);
                value = sharedPreferences.getString("email", "null");
                profile.setImageURI(data.getData());
                int result = db.updateimage(convertImageViewToByteArray(profile), value);

                if (result < 0) {
                    Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                }


            }
        }
    }

    public byte[] convertImageViewToByteArray(ImageView imageView) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }


}