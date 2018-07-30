package it.mfx.shopaholic.database.migrations;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;

public class Migration_10_to_11 extends Migration {
    public Migration_10_to_11() {
        super(10, 11);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {

        String[] statements = {
                "CREATE INDEX index_items_name ON  items(name)",
                "CREATE INDEX index_items_shopName ON  items(shopName)",
                "CREATE INDEX index_items_zoneName ON  items(zoneName)",

                "CREATE INDEX index_shopitems_item_id ON  shopitems(item_id)",
                "CREATE INDEX index_shopitems_status ON  shopitems(status)",
                "CREATE INDEX index_shopitems_job_id ON  shopitems(job_id)"
        };

        try {
            for (String sql : statements) {
                database.execSQL(sql);
            }
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
    }
}