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

    public interface ShopItemRowListener {
        void onItemSelected( ShopItem item );
    }
    private ShopItemRowListener mListener = null;

    public ComposeShopItemRecyclerViewAdapter(ShopItemRowListener listener) {
        mValues = new ArrayList<ShopItem>();
        mListener = listener;
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
        ShopItem item = mValues.get(position);
        holder.mItem = item;
        holder.qtyView.setText(""+ item.qty);
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView qtyView;
        public final TextView nameView;
        public final TextView descriptionView;
        public ShopItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            qtyView = view.findViewById(R.id.item_qty);
            nameView = view.findViewById(R.id.item_name);
            descriptionView = view.findViewById(R.id.item_description);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + nameView.getText() + "'";
        }
    }
}
