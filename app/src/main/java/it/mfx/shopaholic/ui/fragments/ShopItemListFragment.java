package it.mfx.shopaholic.ui.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.mfx.shopaholic.R;
import it.mfx.shopaholic.ShopApplication;
import it.mfx.shopaholic.ui.activities.EditItemActivity;
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

    private void deleteShopItem(ShopItem shopItem) {
        if( shopItem == null )
            return;

        modelView.deleteShopItemAsync(shopItem, new ShopApplication.Callback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if( result == true ) {
                    // TODO: adapter.notifyItemRemoved(xxx);
                    modelView.loadShopItems();
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });

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
        EditItemActivity.openGUI(item.item.id, this.getActivity());
    }

    @Override
    public void onItemQtyChanged(ShopItem item) {
        if( item == null )
            return;
        if( item.qty == 0 ) {
            final ShopItem fitem = item;
            // Ask if the shop item has to be deleted
            new AlertDialog.Builder(this.getActivity())
                    .setTitle(getString(R.string.confirm_shopitem_del_titile))
                    .setMessage(getString(R.string.confirm_shopitem_del_message))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            deleteShopItem(fitem);
                        }})
                    .setNegativeButton(android.R.string.no, null).show();

        }
        else {
            // Save item to db
            modelView.saveShopItem(item, new ShopApplication.Callback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {

                }

                @Override
                public void onError(Exception e) {

                }
            });
        }
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
