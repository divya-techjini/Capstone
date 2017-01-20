package udacity.com.capstone.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DbHelper extends SQLiteOpenHelper {


    static final String NAME = "Recall.db";
    private static final int VERSION = 1;


    public DbHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String builder = "CREATE TABLE " + Contract.Record.TABLE_NAME + " (" +
                Contract.Record._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Contract.Record.COLUMN_NAME + " TEXT NOT NULL, " +
                Contract.Record.COLUMN_PATH + " REAL NOT NULL )";

        db.execSQL(builder);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(" DROP TABLE IF EXISTS " + Contract.Record.TABLE_NAME);

        onCreate(db);
    }
}
