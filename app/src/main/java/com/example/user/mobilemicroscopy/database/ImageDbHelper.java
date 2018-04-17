package com.example.user.mobilemicroscopy.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// import Contract class
import com.example.user.mobilemicroscopy.database.ImageContract.ImageEntry;

public class ImageDbHelper extends SQLiteOpenHelper {

    /**
     * Statement to delete every entries
     */
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + ImageEntry.TABLE_NAME;

    /**
     * Declare database name
     */
    private final static String DATABASE_NAME = "annotateMe.db";

    /**
     * Declare database version
     */
    private final static int DATABASE_VERSION = 1;

    /**
     * Constructor method
     */
    public ImageDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        // Create TABLE
        String CREATE_IMAGE_TABLE = "CREATE TABLE " + ImageEntry.TABLE_NAME + "("
                + ImageEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ImageEntry.COLUMN_NAME_DATE + " TEXT NOT NULL, "
                + ImageEntry.COLUMN_NAME_TIME + " TEXT NOT NULL, "
                + ImageEntry.COLUMN_NAME_SPECIMEN_TYPE + " TEXT, "
                + ImageEntry.COLUMN_NAME_ORIGINAL_FILE_NAME + " TEXT, "
                + ImageEntry.COLUMN_NAME_ANNOTATED_FILE_NAME + " TEXT, "
                + ImageEntry.COLUMN_NAME_GPS_POSITION + " TEXT, "
                + ImageEntry.COLUMN_NAME_MAGNIFICATION + " TEXT, "
                + ImageEntry.COLUMN_NAME_ORIGINAL_IMAGE_LINK + " TEXT, "
                + ImageEntry.COLUMN_NAME_ANNOTATED_IMAGE_LINK + " TEXT, "
                + ImageEntry.COLUMN_NAME_COMMENT + " TEXT); ";

        // Execute the statement
        database.execSQL(CREATE_IMAGE_TABLE);
    }

    /**
     * Method called when the database is upgraded
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }

    /**
     * Method called when the database is downgraded
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
