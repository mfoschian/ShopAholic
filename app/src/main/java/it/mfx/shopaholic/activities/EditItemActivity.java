package it.mfx.shopaholic.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import it.mfx.shopaholic.R;
import it.mfx.shopaholic.ShopApplication;
import it.mfx.shopaholic.models.Item;

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

    protected ShopApplication app() {
        return (ShopApplication) this.getApplication();
    }


    private void save() {
        save(defaultListener);
    }

    private void save(@NonNull final SaveListener listener) {

        Item item = new Item();

        // Retrieve item data from GUI
        retrieveDataFor(item);
        //
        final Item fitem = item;

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

    private void returnItem(final Item item) {
        Intent data = new Intent();
        String result = item.id;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        FloatingActionButton butSave = findViewById(R.id.but_item_save);
        butSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAndReturn();
            }
        });

        Intent intent = getIntent();
        String item_id = intent.getStringExtra(ITEM_ID_ARG);
        String sugg_name = intent.getStringExtra(SUGGESTED_NAME_ARG);
        if( item_id != null ) {
            app().getItemByIdAsync(item_id, new ShopApplication.Callback<Item>() {
                @Override
                public void onSuccess(Item result) {
                    renderDataFor(result);
                }

                @Override
                public void onError(Exception e) {

                }
            });
        }
        else if( sugg_name != null ) {
            Item item = new Item();
            item.name = sugg_name;
            renderDataFor(item);
        }

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
