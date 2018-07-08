package it.mfx.shopaholic.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.mfx.shopaholic.R;
//import it.mfx.shopaholic.fragments.ShopItemListFragment.OnListFragmentInteractionListener;
import it.mfx.shopaholic.models.ShopItem;

import java.util.ArrayList;
import java.util.List;


public class ComposeShopItemRecyclerViewAdapter extends RecyclerView.Adapter<ComposeShopItemRecyclerViewAdapter.ViewHolder> {

    private final List<ShopItem> mValues;
    //private final OnListFragmentInteractionListener mListener;

    public ComposeShopItemRecyclerViewAdapter() {
        mValues = new ArrayList<ShopItem>();
        //mListener = listener;
    }

    public void setItems(List<ShopItem> items) {
        mValues.clear();
        mValues.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_compose_shopitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getName());
        holder.mContentView.setText(mValues.get(position).getDescription());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
                */
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public ShopItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.item_number);
            mContentView = view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
