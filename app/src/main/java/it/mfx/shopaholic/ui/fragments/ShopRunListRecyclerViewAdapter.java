package it.mfx.shopaholic.ui.fragments;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.mfx.shopaholic.R;
import it.mfx.shopaholic.models.ShopItem;

public class ShopRunListRecyclerViewAdapter extends RecyclerView.Adapter<ShopRunListRecyclerViewAdapter.ViewHolder> {

    private final List<ShopItem> mValues;
    private final String shopName;
    //private final OnListFragmentInteractionListener mListener;

    public interface ShopItemRowListener {
        void onItemSelected( ShopItem item );
    }
    private ShopRunListRecyclerViewAdapter.ShopItemRowListener mListener = null;

    public ShopRunListRecyclerViewAdapter(@NonNull String shopName, ShopRunListRecyclerViewAdapter.ShopItemRowListener listener) {
        mValues = new ArrayList<>();
        mListener = listener;
        this.shopName = shopName;
    }

    public void setItems(List<ShopItem> items, boolean show_done) {
        mValues.clear();
        for (ShopItem shopItem : items) {

            if( shopItem.status == ShopItem.Status.DONE && show_done == false )
                continue;

            if (shopItem.qty > 0 && shopName.equals(shopItem.item.shopName))
                mValues.add(shopItem);
        }

        notifyDataSetChanged();
    }

    @Override
    @NonNull
    public ShopRunListRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_shop_run_item, parent, false);
        return new ShopRunListRecyclerViewAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ShopRunListRecyclerViewAdapter.ViewHolder holder, int position) {
        final ShopItem item = mValues.get(position);
        holder.mItem = item;
        holder.qtyView.setText(""+ item.qty);
        holder.nameView.setText(item.item.name);
        holder.descriptionView.setText(item.item.description);

        if( item.status == ShopItem.Status.DONE ) {
            holder.imgCheck.setVisibility(View.VISIBLE);
        }
        else {
            holder.imgCheck.setVisibility(View.INVISIBLE);
        }

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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView qtyView;
        public final TextView nameView;
        public final TextView descriptionView;
        public final ImageView imgCheck;

        public ShopItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            qtyView = view.findViewById(R.id.item_qty);
            nameView = view.findViewById(R.id.item_name);
            descriptionView = view.findViewById(R.id.item_description);
            imgCheck = view.findViewById(R.id.imgCheckboxDone);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + nameView.getText() + "'";
        }
    }
}


