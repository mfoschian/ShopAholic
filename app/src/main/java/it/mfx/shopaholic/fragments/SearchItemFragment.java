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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.mfx.shopaholic.R;
import it.mfx.shopaholic.models.Item;
import it.mfx.shopaholic.viewmodels.ShopListViewModel;


interface ItemViewListener {
    void onItemSelected(Item item);
}

public class SearchItemFragment extends Fragment implements ItemViewListener {

    private int mColumnCount = 1;
    private ShopListViewModel viewModel;
    private Adapter adapter;

    public interface Listener {
        void onItemSelected(Item item);
    }

    private Listener mListener = null;


    public SearchItemFragment() {
        // Required empty public constructor
    }

    public void setListener(Listener l) {
        mListener = l;
    }

    void subscribeUI() {
        viewModel.getItems().observe(this, new Observer<List<Item>>() {
            @Override
            public void onChanged(@Nullable List<Item> items) {
                adapter.setItems(items);
            }
        });
    }


    private TextWatcher filterListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String filter = s.toString();
            viewModel.filterItems( filter );
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.item_search, container);

        viewModel = ViewModelProviders.of(this.getActivity()).get(ShopListViewModel.class);

        EditText filterText = view.findViewById((R.id.editText));
        if( filterText != null ) {
            filterText.addTextChangedListener(filterListener);
        }

        RecyclerView r = view.findViewById(R.id.searchresults);
        if (r != null ) {
            Context context = r.getContext();
            if (mColumnCount <= 1) {
                r.setLayoutManager(new LinearLayoutManager(context));
            } else {
                r.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            adapter = new Adapter(this);
            r.setAdapter(adapter);

            subscribeUI();

            //refreshData();
        }


        return view;
    }


    @Override
    public void onItemSelected(Item item) {
        Log.i("MFX","Selected item " + item.name);
        if(mListener != null)
            mListener.onItemSelected(item);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Listener) {
            mListener = (Listener) context;
        }
        else
            mListener = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
                    .inflate(R.layout.item_search_item, parent, false);

            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ItemViewHolder holder, int position) {
            Item item = mValues.get(position);
            holder.mItem = item;
            holder.nameView.setText(item.name);
            holder.descriptionView.setText(item.description);

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
