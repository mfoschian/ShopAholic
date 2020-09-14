package it.mfx.shopaholic.ui.activities;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import it.mfx.shopaholic.R;
import it.mfx.shopaholic.ShopApplication;
import it.mfx.shopaholic.ui.fragments.ShopRunListFragment;
import it.mfx.shopaholic.viewmodels.ShopRunViewModel;

public class ShopRunActivity extends AppCompatActivity implements ShopRunListFragment.Listener {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private ShopRunViewModel viewModel;


    static final public String RESULT_STOP = "STOP";
    static final public String RESULT_PAUSE = "PAUSE";

    private void subscribeUI() {
        viewModel.getShopNames().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> shopNames) {
                setupPages(shopNames);
            }
        });
    }

    private void setupPages(List<String> shopNames) {

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.clearOnTabSelectedListeners();
        tabLayout.removeAllTabs();

        for( String shopName:shopNames ) {
            TabLayout.Tab tab = tabLayout.newTab();
            tab.setText(shopName);
            tabLayout.addTab(tab);
        }


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), shopNames);

        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

    }

    private void refreshUI() {
        viewModel.loadShopItems();
    }

    @Override
    public void onItemSelected(String shopitem_id) {
        viewModel.revertItemStatus(shopitem_id, new ShopApplication.Callback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                refreshUI();
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_run);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        if( bar != null ) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        mViewPager = findViewById(R.id.container);

        /*
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        viewModel = ViewModelProviders.of(this).get(ShopRunViewModel.class);

        subscribeUI();

        viewModel.loadShopNames();
        refreshUI();

    }


    private void setShowItemsDoneIcon(boolean isShowing, MenuItem item) {

        item.setChecked( !isShowing );
        item.setIcon( isShowing ?
                R.drawable.ic_visibility_24dp
                : R.drawable.ic_visibility_off_24dp);

    }
    private void setItemOrderIcon(ShopRunViewModel.ItemsOrder order, MenuItem item) {
        switch( order ) {
            case BY_NAME:
                item.setIcon( R.drawable.ic_sort_by_name_24dp);
                item.setChecked( true );
                break;
            case BY_ZONE: item.setIcon( R.drawable.ic_sort_by_zone_24dp );
                item.setIcon( R.drawable.ic_sort_by_zone_24dp);
                item.setChecked( false );
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shop_run, menu);
        MenuItem item = menu.findItem(R.id.menu_show_items_done);
        boolean isShowing = viewModel != null ? viewModel.isShowingItemsDone() : false;
        setShowItemsDoneIcon( isShowing, item);

        item = menu.findItem(R.id.menu_show_items_order);
        setItemOrderIcon( viewModel != null ? viewModel.getItemsOrder() : ShopRunViewModel.ItemsOrder.BY_ZONE, item );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_show_items_done) {
            boolean isShowing = ! viewModel.isShowingItemsDone();
            //item.setChecked( !isShowing );
            //item.setIcon( isShowing ? R.drawable.ic_visibility_24dp : R.drawable.ic_visibility_off_24dp);
            setShowItemsDoneIcon(isShowing, item);
            viewModel.setShowItemsDone( isShowing );
            //viewModel.loadShopItems();
            refreshUI();
            return true;
        }
        else if(id == R.id.menu_show_items_order) {
            ShopRunViewModel.ItemsOrder order = viewModel.toggleItemOrder();
            setItemOrderIcon(order, item);
            //viewModel.loadShopItems();
            refreshUI();
            return true;
        }
        else if (id == android.R.id.home) {
            //navigateUpTo(new Intent(this, ComposeListActivity.class));
            Intent data = new Intent();
            String result = RESULT_PAUSE;

            data.setData(Uri.parse(result));
            setResult(RESULT_OK, data);

            finish();

            return true;
        }
        else if (id == R.id.menu_shop_stop) {
            Intent data = new Intent();
            String result = RESULT_STOP;

            data.setData(Uri.parse(result));
            setResult(RESULT_OK, data);

            finish();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        List<String> shopNames;

        public SectionsPagerAdapter(FragmentManager fm, List<String> shopNames) {
            super(fm);
            this.shopNames = shopNames;
        }

        @Override
        public Fragment getItem(int position) {
            String shopName = shopNames.get(position);
            return ShopRunListFragment.newInstance(shopName);
        }

        @Override
        public int getCount() {
            return shopNames.size();
        }
    }
}
