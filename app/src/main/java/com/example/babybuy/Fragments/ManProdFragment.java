package com.example.babybuy.Fragments;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.babybuy.Adapter.ManProdAdapter;
import com.example.babybuy.Adapter.ProdListAdapter;
import com.example.babybuy.Database.Database;
import com.example.babybuy.Model.ProductDataModel;
import com.example.babybuy.R;

import java.util.ArrayList;
import java.util.Objects;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;


public class ManProdFragment extends Fragment {

    public ManProdFragment() {
        // Required empty public constructor
    }

    int productsts;
    Database db;
    ArrayList<ProductDataModel> alldatapurchased;
    ArrayList<ProductDataModel> alldata;
    RecyclerView product_recy;
    ManProdAdapter adapter;
    ManProdAdapter aadapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product, container, false);
        Button purchasedbtn = view.findViewById(R.id.purchasedprodfrag);
        Button tobuybtn = view.findViewById(R.id.tobuyprod);
        db = new Database(getActivity());
        product_recy = view.findViewById(R.id.pruductmanagerecy);
        product_recy.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Load purchased product when purchased button is clicked
        alldata = db.productfetchdataforpurchased(1);
        aadapter = new ManProdAdapter(getActivity(), alldata);
        product_recy.setAdapter(aadapter);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(product_recy);

        purchasedbtn.setOnClickListener(v -> {
            alldata = db.productfetchdataforpurchased(1);
            aadapter = new ManProdAdapter(getActivity(), alldata);
            product_recy.setAdapter(aadapter);
            new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(product_recy);
        });


        //Load tobuy product when tobuy is clicked
        tobuybtn.setOnClickListener(v -> {
            alldata = db.productfetchdataforpurchased(-1);
            adapter = new ManProdAdapter(getActivity(), alldata);
            product_recy.setAdapter(adapter);
            new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(product_recy);
        });
        return view;
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }


        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            Database db = new Database(getActivity());
            ArrayList<ProductDataModel> alldataswipe = db.productfetchdataformap();
            int productIdswipe = alldataswipe.get(position).getProductid();
            int productstsswipe = alldataswipe.get(position).getProductstatus();

            switch (direction) {
                case ItemTouchHelper.LEFT:
                    db.deleteproduct(productIdswipe);
                    alldataswipe.remove(position);
                    product_recy.getAdapter().notifyItemRemoved(position);
                    ArrayList<ProductDataModel> rrecentdata = db.productfetchdataforpurchased(1);
                    ProdListAdapter aadapter = new ProdListAdapter(getActivity(), rrecentdata);
                    product_recy.setAdapter(aadapter);
                    Toast.makeText(getActivity(), "Successfully deleted", Toast.LENGTH_SHORT).show();
                    break;

                case ItemTouchHelper.RIGHT:
                    if (productstsswipe == -1) {
                        db.productpurchased(1, productIdswipe);
                        alldata = db.productfetchdataforpurchased(1);
                        ManProdAdapter aadapter2 = new ManProdAdapter(getActivity(), alldata);
                        product_recy.setAdapter(aadapter2);
                        //Toast.makeText(ProductListActivity.this, "Item Purchased", Toast.LENGTH_SHORT).show();
                        Objects.requireNonNull(product_recy.getAdapter()).notifyDataSetChanged();

                        alldata.get(position).setProductstatus(1);


                    } else if (productstsswipe == 1) {
                        db.productpurchased(-1, productIdswipe);
                        alldata = db.productfetchdataforpurchased(-1);
                        ManProdAdapter aadapter2 = new ManProdAdapter(getActivity(), alldata);
                        product_recy.setAdapter(aadapter2);
                        // Toast.makeText(ProductListActivity.this, "Item unmarked", Toast.LENGTH_SHORT).show();
                        Objects.requireNonNull(product_recy.getAdapter()).notifyDataSetChanged();

                        alldata.get(position).setProductstatus(-1);


                    }
                    break;

            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftLabel("Delete")
                    .setSwipeLeftLabelColor(getResources().getColor(R.color.white))
                    .addSwipeLeftActionIcon(R.drawable.ic_delete)
                    .setSwipeLeftActionIconTint(getResources().getColor(R.color.white))
                    .addSwipeLeftBackgroundColor(getResources().getColor(R.color.colorRed))
                    .addSwipeRightLabel("purchased or tobuy")
                    .setSwipeRightLabelColor(getResources().getColor(R.color.white))
                    .addSwipeRightActionIcon(R.drawable.ic_purchase)
                    .setSwipeRightActionIconTint(getResources().getColor(R.color.white))
                    .addSwipeRightBackgroundColor(getResources().getColor(R.color.greencolor))
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };
}