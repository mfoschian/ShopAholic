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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.mfx.shopaholic.R;
import it.mfx.shopaholic.models.ShopItem;
import it.mfx.shopaholic.models.ShopListViewModel;

import java.util.List;

/**
 * A fragment representing a list of Items..
 */
public class ShopItemListFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    //private OnListFragmentInteractionListener mListener;
    private ShopListViewModel modelView;
    private ComposeShopItemRecyclerViewAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ShopItemListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ShopItemListFragment newInstance(int columnCount) {
        ShopItemListFragment fragment = new ShopItemListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
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

            adapter = new ComposeShopItemRecyclerViewAdapter();
            recyclerView.setAdapter(adapter);
        }
        return view;
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
