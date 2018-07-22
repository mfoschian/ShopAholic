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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.mfx.shopaholic.R;
import it.mfx.shopaholic.models.Item;
import it.mfx.shopaholic.models.ShopItem;
import it.mfx.shopaholic.viewmodels.ShopListViewModel;


interface ItemViewListener {
    void onItemSelected(Item item);
}

public class SearchItemFragment extends Fragment implements ItemViewListener {

    private int mColumnCount = 1;
    private ShopListViewModel viewModel;
    private Adapter adapter;

    public interface Listener {
        void onClose();
    }

    private Listener mListener = null;


    public SearchItemFragment() {
        // Required empty public constructor
    }

    public void setListener(Listener l) {

    }

    void subscribeUI() {
        viewModel.getItems().observe(this, new Observer<List<Item>>() {
            @Override
            public void onChanged(@Nullable List<Item> items) {
                adapter.setItems(items);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_search, container);

        viewModel = ViewModelProviders.of(this.getActivity()).get(ShopListViewModel.class);


        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            adapter = new Adapter(this);
            recyclerView.setAdapter(adapter);

            subscribeUI();

            //refreshData();
        }


        return view;
    }


    @Override
    public void onItemSelected(Item item) {
        Log.i("MFX","Selected item " + item.name);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView nameView;
        public final TextView descriptionView;
        public Item mItem;

        public ItemViewHolder(View view) {
            super(view);
            mView = view;
            nameView = view.findViewById(R.id.item_name);
            descriptionView = view.findViewById(R.id.item_description);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + nameView.getText() + "'";
        }
    }


    public class Adapter extends RecyclerView.Adapter<ItemViewHolder> {

        private final List<Item> mValues;
        private final ItemViewListener mListener;

        public Adapter(ItemViewListener listener) {
            mListener = listener;
            mValues = new ArrayList<>();
        }

        public void setItems(List<Item> items) {
            mValues.clear();
            mValues.addAll(items);
            notifyDataSetChanged();
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_search_item, parent, false);

            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ItemViewHolder holder, int position) {
            Item item = mValues.get(position);
            holder.mItem = item;

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onItemSelected(holder.mItem);
                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

    }

}
