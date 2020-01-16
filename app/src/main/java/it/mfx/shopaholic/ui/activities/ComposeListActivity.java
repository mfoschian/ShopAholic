package it.mfx.shopaholic.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;

import java.io.File;

import it.mfx.shopaholic.R;
import it.mfx.shopaholic.ShopApplication;
import it.mfx.shopaholic.ui.fragments.SearchItemFragment;
import it.mfx.shopaholic.ui.fragments.ShopItemListFragment;
import it.mfx.shopaholic.models.Item;
import it.mfx.shopaholic.models.ShareableData;
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
            //Manifest.permission.CAMERA
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

    private void onItemUpdated( Item item ) {
        refreshData();
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
        app().getShopRunDataAsync(new ShopApplication.Callback<ShareableData>() {
            @Override
            public void onSuccess(ShareableData data) {
                File jsonFile = ShareUtils.saveDataToSharableFile(data, ctx);
                if (jsonFile != null)
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
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    private void exportData() {

        app().exportDataAsync(new ShopApplication.Callback<File>() {
            @Override
            public void onSuccess(File result) {
                showMsg(getString(R.string.export_data_succeeded, result.getPath()));
            }

            @Override
            public void onError(Exception e) {
                showMsg(getString(R.string.export_data_failed, e.getCause()));
            }
        });
    }


    public void performFileSearch() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        //intent.setType("image/*");
        //intent.setType("text/xml");   //XML file only
        intent.setType("*/*");      //all files
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(intent, ShopApplication.IntentRequests.CHOOSE_IMPORT_FILE_REQUEST );
        }
        catch( ActivityNotFoundException e) {
            Log.d("OPENFILE", "No activity to handle intent ACTION_OPEN_DOCUMENT");
            showMsg(getString(R.string.action_get_content_not_handled));
        }
    }

    private boolean tryImportData(Intent intent) {

        boolean can_import_intent = app().importDataAsync(intent, new ShopApplication.CallbackSimple() {
            @Override
            public void onSuccess() {
                showMsg(getString(R.string.import_data_succeeded));
                refreshData();
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                showMsg(getString(R.string.import_data_failed, e.getMessage()));
            }
        });

        return can_import_intent;
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

        Intent intent = getIntent();
        boolean can_import_intent = tryImportData(intent);

        if( !can_import_intent )
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
        else if( id == R.id.menu_export_data ) {
            exportData();
        }
        else if( id == R.id.menu_import_data ) {
            performFileSearch();
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
        else if( requestCode == ShopApplication.IntentRequests.CHOOSE_IMPORT_FILE_REQUEST
                && resultCode == Activity.RESULT_OK) {

            boolean can_import = tryImportData(data);
            if( !can_import )
                showMsg(getString(R.string.import_data_failed, "File not supported"));
        }
        else if( requestCode == ShopApplication.IntentRequests.EDIT_ITEM_REQUEST
                && resultCode == Activity.RESULT_OK) {

            String item_id = data.getData().toString();

            app().getItemByIdAsync(item_id, new ShopApplication.Callback<Item>() {
                @Override
                public void onSuccess(Item item) {
                    onItemUpdated(item);
                }

                @Override
                public void onError(Exception e) {

                }
            });
        }
    }

    private void showMsg(final String msg) {
        //Toast
        Log.i("SHOPAHOLIC", msg);

        boolean isUiThread = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                            ? Looper.getMainLooper().isCurrentThread()
                            : Thread.currentThread() == Looper.getMainLooper().getThread();

        //And, if you wish to run something on the UI thread, you can use this:

        if( isUiThread ) {
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }
        else {

            final Context ctx = this;
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    //this runs on the UI thread
                    Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
                }
            });
        }

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
