package com.example.babybuy.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babybuy.Activity.MapAct;
import com.example.babybuy.Activity.UpdateProdAct;
import com.example.babybuy.Activity.SmsAct;
import com.example.babybuy.Model.ProductDataModel;
import com.example.babybuy.R;

import java.util.ArrayList;

public class ProdListAdapter extends RecyclerView.Adapter<ProdListAdapter.ViewHolder> {

    Context context;
    ArrayList<ProductDataModel> arrayList;
    ProductDataModel pdm;


    public ProdListAdapter(Context context, ArrayList<ProductDataModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView producttitle, productdes, productprice, productquantity;
        TextView productstatusbox;
        ImageView pedit, pdelete, productimage, pmap, psms;
        CardView productcardview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            producttitle = itemView.findViewById(R.id.product_title_id);
            productdes = itemView.findViewById(R.id.product_des_id);
            productprice = itemView.findViewById(R.id.product_price_id);
            productquantity = itemView.findViewById(R.id.product_quantity_id);
            productstatusbox = itemView.findViewById(R.id.productpurchasedstatus);
            productimage = itemView.findViewById(R.id.product_img_id);
            pedit = itemView.findViewById(R.id.productlistedit);
            productcardview = itemView.findViewById(R.id.cardview_id);
            pmap = itemView.findViewById(R.id.productlistmap);
            psms = itemView.findViewById(R.id.productlistsms);

        }
    }

    @NonNull
    @Override
    public ProdListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.design_product_recyclerview, parent, false);
        ProdListAdapter.ViewHolder viewHolder = new ProdListAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        pdm = arrayList.get(position);


        holder.producttitle.setText(pdm.getProductname());
        holder.productdes.setText(pdm.getProductdescription());
        holder.productprice.setText(String.valueOf(pdm.getProductprice()));
        holder.productquantity.setText(String.valueOf(pdm.getProductquantity()));
        Bitmap ImageDataInBitmap = BitmapFactory.decodeByteArray(pdm.getProductimage(), 0, pdm.getProductimage().length);
        holder.productimage.setImageBitmap(ImageDataInBitmap);

        if (pdm.getProductstatus() < 0) {
            holder.productstatusbox.setText("No");
        } else {
            holder.productstatusbox.setText("Yes");
        }

        holder.pmap.setOnClickListener(view -> {
            Intent imap = new Intent(context, MapAct.class);
            imap.putExtra("productid", pdm.getProductid());
            imap.putExtra("productname", pdm.getProductname());
            context.startActivity(imap);
        });

        holder.psms.setOnClickListener(view -> {
            Intent imap = new Intent(context, SmsAct.class);
            imap.putExtra("productid", pdm.getProductid());
            context.startActivity(imap);
        });


        holder.pedit.setOnClickListener(view -> {
            Intent iedit = new Intent(context, UpdateProdAct.class);
            iedit.putExtra("productid", pdm.getProductid());
            iedit.putExtra("pcid", pdm.getProductcategoryid());
            context.startActivity(iedit);
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


}
