package com.example.user.mobilemicroscopy.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.user.mobilemicroscopy.database.ImageContract.ImageEntry;

public class ImageProvider extends ContentProvider {

    /**
     * URI matcher code for the content URI for the pets table
     */
    private static final int IMAGES = 100;

    /**
     * URI matcher code for the content URI for a single pet in the pets table
     */
    private static final int IMAGE_ID = 101;

    private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // Map URI "content://com.example.user.mobilemicroscopy/Image with int code IMAGES
        mUriMatcher.addURI(ImageContract.CONTENT_AUTHORITY, ImageContract.PATH_IMAGES, IMAGES);

        // Map URI "content://com.example.user.mobilemicroscopy/Image/# with int code IMAGE_ID
        mUriMatcher.addURI(ImageContract.CONTENT_AUTHORITY, ImageContract.PATH_IMAGES + "/#", IMAGE_ID);
    }

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = ImageProvider.class.getSimpleName();

    private ImageDbHelper mDbHelper;

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        mDbHelper = new ImageDbHelper(getContext());
        return true;
    }

    /**
     * Do the query
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Try to match URI from parameters
        int match = mUriMatcher.match(uri);

        switch (match) {
            case IMAGES:
                cursor = database.query(ImageEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case IMAGE_ID:
                selection = ImageEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(ImageEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Invalid URI " + uri);
        }

        return cursor;
    }

    /**
     * Insert data
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case IMAGES:
                return insertImage(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertImage(Uri uri, ContentValues values) {
        // Check that the date is not null
        String date = values.getAsString(ImageEntry.COLUMN_NAME_DATE);
        if (date == null) {
            throw new IllegalArgumentException("Image requires date");
        }
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new pet with the given values
        long id = database.insert(ImageEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates data
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[]
            selectionArgs) {
        return 0;
    }

    /**
     * Delete data
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Returns MIME type
     */
    @Override
    public String getType(Uri uri) {
        return null;
    }
}
