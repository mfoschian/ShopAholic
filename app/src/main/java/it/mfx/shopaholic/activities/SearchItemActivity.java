package it.mfx.shopaholic.activities;

import android.app.Fragment;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import it.mfx.shopaholic.R;
import it.mfx.shopaholic.ShopApplication;
import it.mfx.shopaholic.fragments.SearchItemFragment;
import it.mfx.shopaholic.models.Item;
import it.mfx.shopaholic.viewmodels.ShopListViewModel;


public class SearchItemActivity extends AppCompatActivity implements SearchItemFragment.Listener {

    private ShopListViewModel modelView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        modelView = ViewModelProviders.of(this).get(ShopListViewModel.class);

        View fragment = findViewById(R.id.search_fragment);

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
}
