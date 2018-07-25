package it.mfx.shopaholic.activities;

import android.app.Fragment;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import it.mfx.shopaholic.R;
import it.mfx.shopaholic.ShopApplication;
import it.mfx.shopaholic.fragments.SearchItemFragment;
import it.mfx.shopaholic.models.Item;
import it.mfx.shopaholic.models.ShopItem;
import it.mfx.shopaholic.viewmodels.ShopListViewModel;


public class SearchItemActivity extends AppCompatActivity implements SearchItemFragment.Listener {

    private ShopListViewModel modelView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar bar = getSupportActionBar();
        if( bar != null ) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        modelView = ViewModelProviders.of(this).get(ShopListViewModel.class);

        subscribeUI();

        refreshData();

    }

    private void subscribeUI() {
    }

    private ShopApplication app() {
        ShopApplication app = (ShopApplication)getApplication();
        return app;
    }

    private void refreshData() {
        modelView.loadItems();
    }

    @Override
    public void onItemSelected(Item item) {
        Intent data = new Intent();
        String result = item.id;

        data.setData(Uri.parse(result));
        setResult(RESULT_OK, data);

        finish();
    }

    @Override
    public void onNewItemRequest(String suggestedName) {
        Context ctx = this;
        Intent intent = new Intent(ctx, EditItemActivity.class);
        intent.putExtra(EditItemActivity.SUGGESTED_NAME_ARG, suggestedName);
        startActivityForResult(intent, ShopApplication.IntentRequests.EDIT_ITEM_REQUEST);

    }

    @Override
    public void onEditItemRequest(String suggestedName) {
        Context ctx = this;
        Intent intent = new Intent(ctx, EditItemActivity.class);
        intent.putExtra(EditItemActivity.ITEM_ID_ARG, suggestedName);
        startActivityForResult(intent, ShopApplication.IntentRequests.EDIT_ITEM_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ShopApplication.IntentRequests.EDIT_ITEM_REQUEST) {
            if (resultCode == RESULT_OK) {
                String item_id = data.getData().toString();

                app().getItemByIdAsync(item_id, new ShopApplication.Callback<Item>() {
                    @Override
                    public void onSuccess(Item item) {
                        onItemSelected(item);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

            }
        }
    }
}
