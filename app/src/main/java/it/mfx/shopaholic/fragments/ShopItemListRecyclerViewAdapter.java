package it.mfx.shopaholic.fragments;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import it.mfx.shopaholic.R;
//import it.mfx.shopaholic.fragments.ShopItemListFragment.OnListFragmentInteractionListener;
import it.mfx.shopaholic.models.ShopItem;

import java.util.ArrayList;
import java.util.List;


public class ShopItemListRecyclerViewAdapter extends RecyclerView.Adapter<ShopItemListRecyclerViewAdapter.ViewHolder> {

    private final List<ShopItem> mValues;
    //private final OnListFragmentInteractionListener mListener;

    public interface ShopItemRowListener {
        void onItemSelected( ShopItem item );
        void onItemQtyChanged( ShopItem item );
    }
    private ShopItemRowListener mListener = null;

    public ShopItemListRecyclerViewAdapter(ShopItemRowListener listener) {
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
        final ShopItem item = mValues.get(position);
        holder.mItem = item;
        holder.qtyView.setText(""+ item.qty);
        holder.nameView.setText(item.item.name);
        holder.descriptionView.setText(item.item.description);
        if( item.status == ShopItem.Status.DONE) {
            if( holder.btnPlus.hasOnClickListeners() )
                holder.btnPlus.setOnClickListener(null);

            if( holder.btnMinus.hasOnClickListeners() )
                holder.btnMinus.setOnClickListener(null);

            //holder.mView.setAlpha(0.5f);
            holder.imgCheck.setVisibility(View.VISIBLE);
            //holder.imgCheck.setAlpha(1f);
        }
        else {
            //holder.mView.setAlpha(1f);
            holder.imgCheck.setVisibility(View.INVISIBLE);
            holder.btnPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.qty++;
                    holder.qtyView.setText("" + item.qty);
                    if( mListener != null )
                        mListener.onItemQtyChanged(item);
                }
            });

            holder.btnMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.qty > 0) {
                        item.qty--;
                        holder.qtyView.setText("" + item.qty);
                    }
                    if( mListener != null )
                        mListener.onItemQtyChanged(item);
                }
            });

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
        public final ImageButton btnPlus;
        public final ImageButton btnMinus;
        public final ImageView imgCheck;
        public ShopItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            qtyView = view.findViewById(R.id.item_qty);
            nameView = view.findViewById(R.id.item_name);
            descriptionView = view.findViewById(R.id.item_description);
            btnPlus = view.findViewById(R.id.buttonPlus);
            btnMinus = view.findViewById(R.id.buttonMinus);
            imgCheck = view.findViewById(R.id.imgCheckboxDone);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + nameView.getText() + "'";
        }
    }
}
