package it.mfx.shopaholic.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.mfx.shopaholic.R;
import it.mfx.shopaholic.models.ShopItem;
import it.mfx.shopaholic.viewmodels.ShopListViewModel;

import java.util.List;

public class ShopItemListFragment extends Fragment implements ShopItemListRecyclerViewAdapter.ShopItemRowListener {

    private static final String ARG_SHOP_NAME = "shopname";
    private String mShopName;
    private int mColumnCount = 1;
    //private OnListFragmentInteractionListener mListener;
    private ShopListViewModel modelView;
    private ShopItemListRecyclerViewAdapter adapter;

    public ShopItemListFragment() {
    }

    @SuppressWarnings("unused")
    public static ShopItemListFragment newInstance(String shopName) {
        ShopItemListFragment fragment = new ShopItemListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SHOP_NAME, shopName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mShopName = getArguments().getString(ARG_SHOP_NAME, null);
        }
    }

    private void subscribeUI() {
        modelView.getShopItems().observe(this, new Observer<List<ShopItem>>() {
                    @Override
                    public void onChanged(@Nullable List<ShopItem> shopItems) {
                        adapter.setItems(shopItems);
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_compose_shopitem_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            modelView = ViewModelProviders.of(this.getActivity()).get(ShopListViewModel.class);
            subscribeUI();

            adapter = new ShopItemListRecyclerViewAdapter(this);
            recyclerView.setAdapter(adapter);

        }
        return view;
    }

    @Override
    public void onItemSelected(ShopItem item) {
        Log.i("MFX", "Clicked item " + item.item.name);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //if (context instanceof OnListFragmentInteractionListener) {
        //    mListener = (OnListFragmentInteractionListener) context;
        //}
        //else {
        //    throw new RuntimeException(context.toString()
        //            + " must implement OnListFragmentInteractionListener");
        //}
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }
}
