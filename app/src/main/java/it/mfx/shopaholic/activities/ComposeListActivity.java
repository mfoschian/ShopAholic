package it.mfx.shopaholic.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import it.mfx.shopaholic.R;
import it.mfx.shopaholic.ShopApplication;
import it.mfx.shopaholic.fragments.SearchItemFragment;
import it.mfx.shopaholic.fragments.ShopItemListFragment;
import it.mfx.shopaholic.models.ShopItem;
import it.mfx.shopaholic.viewmodels.ShopListViewModel;

public class ComposeListActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private SearchItemFragment searchItemFragment = null;
    private View mItemList = null;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private ShopListViewModel modelView;

    private ShopApplication app() {
        ShopApplication app = (ShopApplication)getApplication();
        return app;
    }

    private void refreshData() {
        modelView.loadShopItems();
        modelView.loadItems();
    }


    private void openItemSelectGUI() {
        Context ctx = this;
        Intent intent = new Intent(ctx, SearchItemActivity.class);
        //intent.putExtra(DocumentDetailFragment.ARG_ITEM_ID, doc_id);
        startActivityForResult(intent,ShopApplication.IntentRequests.CHOOSE_ITEM_REQUEST);
    }

    private void addNewItem() {

        // First save changes
        modelView.saveChanges(new ShopApplication.Callback<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                // Then open select GUI for item
                openItemSelectGUI();
            }

            @Override
            public void onError(Exception e) {

            }
        });


        //mItemList.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //mItemList = findViewById(R.id.item_list_fragment);
        //mItemList.setVisibility(View.INVISIBLE);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewItem();
            }
        });

        modelView = ViewModelProviders.of(this).get(ShopListViewModel.class);
        subscribeUI();

        refreshData();
    }

    private void subscribeUI() {

        modelView.getShopItems().observe(this, new Observer<List<ShopItem>>() {
                    @Override
                    public void onChanged(@Nullable List<ShopItem> shopItems) {


                    }
                }
        );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_compose_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ShopApplication.IntentRequests.CHOOSE_ITEM_REQUEST) {
            if (resultCode == RESULT_OK) {
                String item_id = data.getData().toString();


                ShopItem shopItem = new ShopItem();
                shopItem.item_id = item_id;
                shopItem.qty = 1;

                modelView.addShopItem(shopItem, new ShopApplication.Callback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        modelView.loadShopItems();
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
            }
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return ShopItemListFragment.newInstance(null);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }
}
