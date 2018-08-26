package it.mfx.shopaholic.ui.activities;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.mfx.shopaholic.R;
import it.mfx.shopaholic.ShopApplication;
import it.mfx.shopaholic.ui.customviews.TextInputAutoCompleteTextView;
import it.mfx.shopaholic.ui.Utils;
import it.mfx.shopaholic.models.Item;
import it.mfx.shopaholic.viewmodels.ItemFormViewModel;

public class EditItemActivity extends AppCompatActivity {

    public final static String SUGGESTED_NAME_ARG = "suggested_name";
    public final static String ITEM_ID_ARG = "item_id";

    private ItemFormViewModel viewModel;
    private TextInputAutoCompleteTextView shopAutocomplete;
    private ArrayAdapter<String> shopAdapter;

    private TextInputAutoCompleteTextView zoneAutocomplete;
    private ArrayAdapter<String> zoneAdapter;

    protected ShopApplication app() {
        return (ShopApplication) this.getApplication();
    }


    public static void openGUI(String item_id, Activity parent ) {
        Intent intent = new Intent(parent, EditItemActivity.class);
        intent.putExtra(EditItemActivity.ITEM_ID_ARG, item_id);
        parent.startActivityForResult(intent, ShopApplication.IntentRequests.EDIT_ITEM_REQUEST);

    }

    private void save(@NonNull final ShopApplication.Callback<Item> listener) {

        Item item = viewModel.getItem().getValue();

        // Retrieve item data from GUI
        retrieveDataFor(item);
        //
        final Item fitem = item;

        if( viewModel.isNewItem() ) {
            app().addItemAsync(item, new ShopApplication.Callback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    listener.onSuccess(fitem);
                }

                @Override
                public void onError(Exception e) {
                    listener.onError(e);
                }
            });
        }
        else {
            app().saveItemAsync(item, new ShopApplication.Callback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    listener.onSuccess(fitem);
                }

                @Override
                public void onError(Exception e) {
                    listener.onError(e);
                }
            });
        }
    }

    private void returnItem(final Item item) {
        Intent data = new Intent();
        String result = item == null ? "" : item.id;

        data.setData(Uri.parse(result));
        setResult(RESULT_OK, data);

        finish();
    }

    private void saveAndReturn() {
        save(new ShopApplication.Callback<Item>() {
            @Override
            public void onSuccess(final Item item) {
                returnItem(item);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    void deleteItemAndReturn() {
        viewModel.deleteItemAsync(new ShopApplication.Callback<Boolean>() {
            @Override
            public void onSuccess(Boolean ok) {
                if( ok == true )
                    returnItem(null);
                else
                    showMsg(EditItemActivity.this.getString(R.string.delete_item_error));
            }

            @Override
            public void onError(Exception e) {
            }
        });
    }

    private void showMsg(String msg) {
        Log.i("ITEMEDT", msg);
        Utils.showMsg(this, msg);
    }

    private void subscribeUI() {
        viewModel.getItem().observe(this, new Observer<Item>() {
            @Override
            public void onChanged(@Nullable Item item) {
                if( item == null )
                    return;

                renderDataFor(item);
            }
        });

        viewModel.getShopNames().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> strings) {
                shopAdapter.clear();
                shopAdapter.addAll(strings);
                shopAdapter.notifyDataSetChanged();
            }
        });

        viewModel.getZoneNames().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> strings) {
                zoneAdapter.clear();
                zoneAdapter.addAll(strings);
                zoneAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar bar = getSupportActionBar();
        if( bar != null ) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        shopAutocomplete = findViewById(R.id.txt_item_shop);
        shopAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        shopAutocomplete.setThreshold(1);
        shopAutocomplete.setAdapter(shopAdapter);


        zoneAutocomplete = findViewById(R.id.txt_item_zone);
        zoneAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        zoneAutocomplete.setThreshold(1);
        zoneAutocomplete.setAdapter(zoneAdapter);




        viewModel = ViewModelProviders.of(this).get(ItemFormViewModel.class);
        final ItemFormViewModel vm = viewModel;

        subscribeUI();

        // Retrieve item to edit or to create ...
        Intent intent = getIntent();
        String item_id = intent.getStringExtra(ITEM_ID_ARG);
        String sugg_name = intent.getStringExtra(SUGGESTED_NAME_ARG);
        if( item_id != null ) {
            app().getItemByIdAsync(item_id, new ShopApplication.Callback<Item>() {
                @Override
                public void onSuccess(Item result) {
                    vm.setExistingItem(result);
                    vm.loadZoneNames(result.shopName);
                }

                @Override
                public void onError(Exception e) {

                }
            });
        }
        else if( sugg_name != null ) {
            Item item = new Item();
            item.name = sugg_name;
            //renderDataFor(item);
            viewModel.setNewItem(item);
        }

        viewModel.loadShopNames();

        // When leave focus of shop name control, reload zone names autocomplete
        shopAutocomplete.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    return;

                String text = shopAutocomplete.getText().toString();
                vm.loadZoneNames(text);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_item, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //

            //navigateUpTo(new Intent(this, ComposeListActivity.class));
            returnItem(null);

            return true;
        }
        else if( id == R.id.item_menu_save )
            saveAndReturn();

        else if( id == R.id.item_menu_delete ) {
            // Delete item, but first ask confirm
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.confirm_item_del_titile))
                    .setMessage(getString(R.string.confirm_item_del_message))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            deleteItemAndReturn();
                        }})
                    .setNegativeButton(android.R.string.no, null).show();
        }
        return super.onOptionsItemSelected(item);
    }

    void retrieveDataFor(@NonNull Item item) {
        TextView txt;

        txt = findViewById(R.id.txt_item_name);
        item.name = txt.getText().toString().trim();

        txt = findViewById(R.id.txt_item_description);
        item.description = txt.getText().toString();

        txt = findViewById(R.id.txt_item_shop);
        item.shopName = txt.getText().toString().trim();

        txt = findViewById(R.id.txt_item_zone);
        item.zoneName = txt.getText().toString().trim();

    }

    void renderDataFor(Item item) {
        if( item == null )
            return;

        TextView txt;

        txt = findViewById(R.id.txt_item_name);
        txt.setText(item.name);

        txt = findViewById(R.id.txt_item_description);
        txt.setText(item.description);

        txt = findViewById(R.id.txt_item_shop);
        txt.setText(item.shopName);

        txt = findViewById(R.id.txt_item_zone);
        txt.setText(item.zoneName);

    }


}
