package it.mfx.shopaholic.activities;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TintableImageSourceView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import it.mfx.shopaholic.R;
import it.mfx.shopaholic.ShopApplication;
import it.mfx.shopaholic.fragments.SearchItemFragment;
import it.mfx.shopaholic.fragments.ShopItemListFragment;
import it.mfx.shopaholic.models.ShopItem;
import it.mfx.shopaholic.utils.ShareUtils;
import it.mfx.shopaholic.viewmodels.ShopListViewModel;



public class ComposeListActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private SearchItemFragment searchItemFragment = null;
    private View mItemList = null;

    private ViewPager mViewPager;
    private ShopListViewModel modelView;

    private static final String[] permissions = new String[] {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };



    private ShopApplication app() {
        ShopApplication app = (ShopApplication)getApplication();
        return app;
    }


    private boolean requestPermissions( String[] permissions ) {

        Activity thisActivity = this;

        boolean hasAllPermissions = true;
        for( int i=0; i<permissions.length; i++ ) {
            String permission = permissions[i];

            if (ContextCompat.checkSelfPermission( thisActivity, permission )
                    != PackageManager.PERMISSION_GRANTED) {

                hasAllPermissions = false;
                break;
            }
        }

        if(!hasAllPermissions) {

            ActivityCompat.requestPermissions(thisActivity,
                    permissions,
                    ShopApplication.IntentRequests.PERMISSIONS_REQUEST);

            return false;
        } else {
            // Permission has already been granted
            return true;
        }
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
    }

    private void runShop() {
        Context ctx = this;
        Intent intent = new Intent(ctx, ShopRunActivity.class);
        //intent.putExtra(DocumentDetailFragment.ARG_ITEM_ID, doc_id);

        final Intent fintent = intent;

        // First save changes
        modelView.saveChanges(new ShopApplication.Callback<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                // Then open select GUI for item
                //startActivity(fintent,null);
                startActivityForResult(fintent, ShopApplication.IntentRequests.SHOP_RUN_REQUEST);
            }

            @Override
            public void onError(Exception e) {

            }
        });

    }


    private void shareShopList() {
        final Context ctx = this.getApplicationContext();
        final String title = getString(R.string.share_list);
        final String mimeType = getString(R.string.share_mime_type);

        // First save changes
        ShareUtils.getSharableData(app(), new ShopApplication.Callback<String>() {
                    @Override
                    public void onSuccess(String json) {
                        /**/
                        File jsonFile = ShareUtils.saveDataToSharableFile(json,ctx);
                        if( jsonFile != null )
                            ShareUtils.shareFile(jsonFile, mimeType, ComposeListActivity.this);
                        /**/
                        //ShareUtils.shareTextData(json, mimeType, ComposeListActivity.this);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
    }

    private void saveAndShareShopList() {
        // First save changes
        modelView.saveChanges(new ShopApplication.Callback<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                shareShopList();
                //startActivity(fintent,null);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    public boolean isTypeAccepted(String type) {
        if( type == null )
            return true;

        final String[] acceptedTypes = new String[] {
                getString(R.string.share_mime_type),
                "text/html",
                "application/json"
        };

        boolean type_accepted = false;
        for( String acceptedType: acceptedTypes ) {
            if( acceptedType.equals(type)) {
                type_accepted = true;
                break;
            }
        }

        return type_accepted;
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

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewItem();
            }
        });

        modelView = ViewModelProviders.of(this).get(ShopListViewModel.class);
        subscribeUI();

        boolean permissions_ok = requestPermissions(permissions);
        //if( !permissions_ok )

        //Check shared data
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        //final String mimeType = getString(R.string.share_mime_type);
        if(Intent.ACTION_SEND.equals(action)
                || Intent.ACTION_VIEW.equals(action)) {

            boolean type_accepted = isTypeAccepted(type);

            if( type_accepted ) {
                // Load data shared from outside
                String json = intent.getStringExtra(Intent.EXTRA_TEXT);
                if( json == null ) {
                    // maybe a file was passed
                    Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                    if( uri == null )
                        uri = intent.getData();

                    if( uri != null ) {
                        //String scheme = uri.getScheme();
                        //String path = uri.getEncodedPath();
                        //json = ShareUtils.getDataFromSharedFile(path, this);
                        json = ShareUtils.getDataFromSharedFile(uri, this);

                        /*
                        String json2 = ShareUtils.getDataFromHtmlBody(json);
                        if( json2 != null )
                            json = json2;
                        */
                    }
                }

                if( json != null ) {
                    ShareUtils.putSharableData(json, app(), new ShopApplication.CallbackSimple() {
                        @Override
                        public void onSuccess() {
                            refreshData();
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
                    return;
                }
            }
        }

        refreshData();
    }

    private void subscribeUI() {
/*
        modelView.getShopItems().observe(this, new Observer<List<ShopItem>>() {
                    @Override
                    public void onChanged(@Nullable List<ShopItem> shopItems) {


                    }
                }
        );
*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_compose_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if( id == R.id.menu_start_shop_run ) {
            runShop();
        }
        else if( id == R.id.menu_share_list ) {
            saveAndShareShopList();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ShopApplication.IntentRequests.CHOOSE_ITEM_REQUEST) {
            if (resultCode == RESULT_OK) {
                String item_id = data.getData().toString();
                ShopItem sit = modelView.findByItemId(item_id);
                if( sit != null ) {
                    if( sit.qty > 0 )
                        return;

                    // Qty == 0 : increment by one
                    sit.qty = 1;
                    modelView.saveChanges(new ShopApplication.Callback<Integer>() {
                        @Override
                        public void onSuccess(Integer result) {
                            modelView.loadShopItems();
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
                    return;
                }

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
        else if( requestCode == ShopApplication.IntentRequests.SHOP_RUN_REQUEST ) {
            if( resultCode == RESULT_OK ) {
                String action = data.getData().toString();
                if( ShopRunActivity.RESULT_STOP.equals(action)) {
                    //TODO: storicize shopitems
                    // Child activity may have modified the data, so reload them
                    modelView.archiveDoneShopeItems(new ShopApplication.CallbackSimple() {
                        @Override
                        public void onSuccess() {
                            refreshData();
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
                    return;
                }
            }
            refreshData();
        }
    }

    private void showMsg(String msg) {
        //Toast
        Log.i("SHOPAHOLIC", msg);
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] allowedPermissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, allowedPermissions, grantResults);

        if( requestCode == ShopApplication.IntentRequests.PERMISSIONS_REQUEST ) {
            int allowedCount = 0;
            for( int i = 0; i < permissions.length; i++) {
                String neededPermission = permissions[i];
                for( int j = 0; j < allowedPermissions.length; j++ ) {
                    String allowedPermission = allowedPermissions[j];
                    if (neededPermission.equals(allowedPermission) && grantResults[i] == 0) {
                        allowedCount++;
                        break;
                    }
                }
            }

            if( allowedCount != permissions.length ) {
                showMsg("Without permissions the app cannot works, sorry");
                //finish();
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
            // TODO: copy what done for ShopRun and swipe between Shops
            return 1;
        }
    }
}
