package it.mfx.shopaholic.database.migrations;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.room.migration.Migration;
import androidx.annotation.NonNull;

public class Migration_09_to_10 extends Migration {
    public Migration_09_to_10() {
        super(9, 10);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {

        String[] statements = {
                "ALTER TABLE shopitems ADD COLUMN status INTEGER DEFAULT 0",
                "ALTER TABLE shopitems ADD COLUMN job_id TEXT DEFAULT null"
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
