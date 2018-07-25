package it.mfx.shopaholic.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.mfx.shopaholic.R;
import it.mfx.shopaholic.ShopApplication;
import it.mfx.shopaholic.customviews.TextInputAutoCompleteTextView;
import it.mfx.shopaholic.models.Item;
import it.mfx.shopaholic.viewmodels.ItemFormViewModel;

public class EditItemActivity extends AppCompatActivity {

    public final static String SUGGESTED_NAME_ARG = "suggested_name";
    public final static String ITEM_ID_ARG = "item_id";

    private interface SaveListener {
        void onSaved(final Item item);
        void onError(String err);
    }

    private SaveListener defaultListener = new SaveListener() {
        @Override
        public void onSaved(final Item item) {
            showMsg("Salvato");
        }

        @Override
        public void onError(String err) {

        }
    };

    private ItemFormViewModel viewModel;
    private TextInputAutoCompleteTextView shopAutocomplete;
    private ArrayAdapter<String> autoCompleteAdapter;

    protected ShopApplication app() {
        return (ShopApplication) this.getApplication();
    }


    private void save() {
        save(defaultListener);
    }

    private void save(@NonNull final SaveListener listener) {

        Item item = viewModel.getItem().getValue();

        // Retrieve item data from GUI
        retrieveDataFor(item);
        //
        final Item fitem = item;

        if( viewModel.isNewItem() ) {
            app().addItemAsync(item, new ShopApplication.Callback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    listener.onSaved(fitem);
                }

                @Override
                public void onError(Exception e) {
                    listener.onError(e.getMessage());
                }
            });
        }
        else {
            app().saveItemAsync(item, new ShopApplication.Callback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    listener.onSaved(fitem);
                }

                @Override
                public void onError(Exception e) {
                    listener.onError(e.getMessage());
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
        save(new SaveListener() {
            @Override
            public void onSaved(final Item item) {
                returnItem(item);
            }

            @Override
            public void onError(String err) {

            }
        });
    }

    private void showMsg(String msg) {
        //Toast
        Log.i("ITEMEDT", msg);
    }

    private void subscribeUI() {
        viewModel.getItem().observe(this, new Observer<Item>() {
            @Override
            public void onChanged(@Nullable Item item) {
                renderDataFor(item);
            }
        });

        viewModel.getShopNames().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> strings) {
                autoCompleteAdapter.clear();
                autoCompleteAdapter.addAll(strings);
                autoCompleteAdapter.notifyDataSetChanged();
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

        FloatingActionButton butSave = findViewById(R.id.but_item_save);
        butSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAndReturn();
            }
        });

        shopAutocomplete = findViewById(R.id.txt_item_shop);
        autoCompleteAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        shopAutocomplete.setThreshold(1);
        shopAutocomplete.setAdapter(autoCompleteAdapter);

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
        return super.onOptionsItemSelected(item);
    }

    void retrieveDataFor(@NonNull Item item) {
        TextView txt;

        txt = findViewById(R.id.txt_item_name);
        item.name = txt.getText().toString();

        txt = findViewById(R.id.txt_item_description);
        item.description = txt.getText().toString();

        txt = findViewById(R.id.txt_item_shop);
        item.shopName = txt.getText().toString();

        txt = findViewById(R.id.txt_item_zone);
        item.zoneName = txt.getText().toString();

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
