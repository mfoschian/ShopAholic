package it.mfx.shopaholic.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import it.mfx.shopaholic.R;
import it.mfx.shopaholic.ShopApplication;
import it.mfx.shopaholic.fragments.ShopRunListFragment;
import it.mfx.shopaholic.viewmodels.ShopRunViewModel;

public class ShopRunActivity extends AppCompatActivity implements ShopRunListFragment.Listener {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private ShopRunViewModel viewModel;



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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shop_run, menu);
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
            boolean isShowing = viewModel.isShowingItemsDone();
            item.setChecked( !isShowing );
            viewModel.setShowItemsDone( !isShowing );
            viewModel.loadShopItems();
            return true;
        }
        else if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, ComposeListActivity.class));

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
