package it.mfx.shopaholic.ui.fragments;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import it.mfx.shopaholic.R;
import it.mfx.shopaholic.models.ShopItem;
import it.mfx.shopaholic.viewmodels.ShopRunViewModel;


public class ShopRunListFragment extends Fragment implements ShopRunListRecyclerViewAdapter.ShopItemRowListener {

    public interface Listener {
        void onItemSelected(String shopitem_id);
    }


    private static final String ARG_SHOP_NAME = "shop_name";
    private String mShopName;

    private Listener mListener;

    private ShopRunViewModel viewModel;
    private ShopRunListRecyclerViewAdapter adapter;


    public ShopRunListFragment() {
        // Required empty public constructor
    }

    public static ShopRunListFragment newInstance(String shopName) {
        ShopRunListFragment fragment = new ShopRunListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SHOP_NAME, shopName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mShopName = getArguments().getString(ARG_SHOP_NAME);
        }

        viewModel = ViewModelProviders.of(this.getActivity()).get(ShopRunViewModel.class);
    }


    private void subscribeUI() {
        viewModel.getShopItems().observe(this, new Observer<List<ShopItem>>() {
                    @Override
                    public void onChanged(@Nullable List<ShopItem> shopItems) {
                        boolean showItemsDone = viewModel.isShowingItemsDone();
                        adapter.setItems(shopItems, showItemsDone);
                    }
                }
        );

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shop_run, container, false);

        //TextView textView = rootView.findViewById(R.id.section_label);
        //textView.setText( mShopName );

        RecyclerView recyclerView = rootView.findViewById(R.id.shop_run_list);
        Context context = rootView.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new ShopRunListRecyclerViewAdapter(mShopName,this);
        recyclerView.setAdapter(adapter);

        subscribeUI();

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Listener) {
            mListener = (Listener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemSelected(ShopItem item) {
        if(mListener != null)
            mListener.onItemSelected(item.sid);
    }
}
