package it.mfx.shopaholic.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.mfx.shopaholic.R;
import it.mfx.shopaholic.ShopApplication;
import it.mfx.shopaholic.models.ShopItem;
import it.mfx.shopaholic.viewmodels.ShopListViewModel;


interface SearchItemRowListener {
    void onItemSelected( ShopItem item );
}

public class SearchItemActivity extends AppCompatActivity implements SearchItemRowListener {

    private int mColumnCount = 1;
    private ShopListViewModel modelView;
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        View view = findViewById(R.id.searchresults);
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            modelView = ViewModelProviders.of(this).get(ShopListViewModel.class);

            adapter = new Adapter(this);
            recyclerView.setAdapter(adapter);

            subscribeUI();

            refreshData();
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

    private ShopApplication app() {
        ShopApplication app = (ShopApplication)getApplication();
        return app;
    }

    private void refreshData() {
        app().getAsyncShopItems(new ShopApplication.Callback<List<ShopItem>>() {
            @Override
            public void onSuccess(List<ShopItem> result) {
                modelView.setShopItems(result);
            }

            @Override
            public void onError(Exception e) {

            }
        });

    }



    @Override
    public void onItemSelected(ShopItem item) {
        Log.i("MFX","Selected item " + item.item.name);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView nameView;
        public final TextView descriptionView;
        public ShopItem mItem;

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

        private final List<ShopItem> mValues;
        //private final OnListFragmentInteractionListener mListener;

        private SearchItemRowListener mListener = null;

        public Adapter(SearchItemRowListener listener) {
            mValues = new ArrayList<ShopItem>();
            mListener = listener;
        }

        public void setItems(List<ShopItem> items) {
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
            ShopItem item = mValues.get(position);
            holder.mItem = item;
            holder.nameView.setText(item.item.name);
            holder.descriptionView.setText(item.item.description);

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
