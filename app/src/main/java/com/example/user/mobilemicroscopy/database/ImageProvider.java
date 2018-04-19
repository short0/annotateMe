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

public class ImageProvider {}//extends ContentProvider {

//    /**
//     * URI matcher code for images table
//     */
//    private static final int IMAGES = 10;
//
//    /**
//     * URI matcher code for an image
//     */
//    private static final int IMAGE_ID = 11;
//
//    private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
//
//    static {
//        // Map URI "content://com.example.user.mobilemicroscopy/Image with int code IMAGES
//        mUriMatcher.addURI(ImageContract.CONTENT_AUTHORITY, ImageContract.PATH_IMAGES, IMAGES);
//
//        // Map URI "content://com.example.user.mobilemicroscopy/Image/# with int code IMAGE_ID
//        mUriMatcher.addURI(ImageContract.CONTENT_AUTHORITY, ImageContract.PATH_IMAGES + "/#", IMAGE_ID);
//    }
//
//    private ImageDbHelper mDbHelper;
//
//    /**
//     * Initialize the provider and the database helper object.
//     */
//    @Override
//    public boolean onCreate() {
//        mDbHelper = new ImageDbHelper(getContext());
//        return true;
//    }
//
//    /**
//     * Do the query
//     */
//    @Override
//    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
//                        String sortOrder) {
//        // Get readable database
//        SQLiteDatabase database = mDbHelper.getReadableDatabase();
//
//        // Declare cursor to get the result
//        Cursor cursor;
//
//        // match URI
//        int match = mUriMatcher.match(uri);
//
//        switch (match) {
//            case IMAGES:
//                cursor = database.query(ImageEntry.TABLE_NAME, projection, selection, selectionArgs,
//                        null, null, sortOrder);
//                break;
//            case IMAGE_ID:
//                selection = ImageEntry._ID + "=?";
//                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
//                cursor = database.query(ImageEntry.TABLE_NAME, projection, selection, selectionArgs,
//                        null, null, sortOrder);
//                break;
//            default:
//                throw new IllegalArgumentException("Invalid URI");
//        }
//
//        // set notification URI on the cursor
//        cursor.setNotificationUri(getContext().getContentResolver(), uri);
//
//        return cursor;
//    }
//
//    /**
//     * Insert data
//     */
//    @Override
//    public Uri insert(Uri uri, ContentValues contentValues) {
//        final int match = mUriMatcher.match(uri);
//        switch (match) {
//            case IMAGES:
//                return insertImage(uri, contentValues);
//            default:
//                throw new IllegalArgumentException("In valid Insertion");
//        }
//    }
//
//    /**
//     * Helper method to insert image
//     */
//    private Uri insertImage(Uri uri, ContentValues values) {
//        // Check that the date is not null
//        String date = values.getAsString(ImageEntry.COLUMN_NAME_DATE);
//        if (date == null) {
//            throw new IllegalArgumentException("Date is required");
//        }
//
//        // TODO check other attributes
//
//        // Get writable database
//        SQLiteDatabase database = mDbHelper.getWritableDatabase();
//
//        // Insert the new image with the given values
//        long id = database.insert(ImageEntry.TABLE_NAME, null, values);
//        // If id is -1, do nothing
//        if (id == -1)
//        {
//            return null;
//        }
//
//        // Notify data has changed
//        getContext().getContentResolver().notifyChange(uri, null);
//
//        // Return the new URI
//        return ContentUris.withAppendedId(uri, id);
//    }
//
//    /**
//     * Updates data
//     */
//    @Override
//    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
//        final int match = mUriMatcher.match(uri);
//        switch (match) {
//            case IMAGES:
//                return updateImage(uri, contentValues, selection, selectionArgs);
//            case IMAGE_ID:
//                selection = ImageEntry._ID + "=?";
//                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
//                return updateImage(uri, contentValues, selection, selectionArgs);
//            default:
//                throw new IllegalArgumentException("Invalid Update");
//        }
//    }
//
//    /**
//     * Helper method to update image
//     */
//    private int updateImage(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
//        // check if date is in the update query, if yes, check if it's null
//        if (values.containsKey(ImageEntry.COLUMN_NAME_DATE)) {
//            String date = values.getAsString(ImageEntry.COLUMN_NAME_DATE);
//            if (date == null) {
//                throw new IllegalArgumentException("Date is required");
//            }
//        }
//
//        // TODO check other attributes
//
//        // If no new values, do nothing
//        if (values.size() == 0) {
//            return 0;
//        }
//
//        // get writable database
//        SQLiteDatabase database = mDbHelper.getWritableDatabase();
//
//        // get number of rows updated
//        int updatedRows = database.update(ImageEntry.TABLE_NAME, values, selection, selectionArgs);
//
//        // if any rows updated, notify data has changed
//        if (updatedRows != 0) {
//            getContext().getContentResolver().notifyChange(uri, null);
//        }
//
//        // return number of rows updated
//        return updatedRows;
//    }
//
//    /**
//     * Delete data
//     */
//    @Override
//    public int delete(Uri uri, String selection, String[] selectionArgs) {
//        // Get writable database
//        SQLiteDatabase database = mDbHelper.getWritableDatabase();
//
//        // Keep track the number of rows deleted
//        int deletedRows;
//
//        final int match = mUriMatcher.match(uri);
//        switch (match) {
//            case IMAGES:
//                // Delete rows based on selection and selection args
//                deletedRows = database.delete(ImageEntry.TABLE_NAME, selection, selectionArgs);
//                break;
//            case IMAGE_ID:
//                // Delete the row with id in the URI
//                selection = ImageEntry._ID + "=?";
//                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
//                deletedRows = database.delete(ImageEntry.TABLE_NAME, selection, selectionArgs);
//                break;
//            default:
//                throw new IllegalArgumentException("Invalid deletion");
//        }
//
//        // if any rows deleted, notify data has changed
//        if (deletedRows != 0) {
//            getContext().getContentResolver().notifyChange(uri, null);
//        }
//
//        // return number of rows deleted
//        return deletedRows;
//    }
//
//    /**
//     * Returns MIME type
//     */
//    @Override
//    public String getType(Uri uri) {
//        final int match = mUriMatcher.match(uri);
//        switch (match) {
//            case IMAGES:
//                return ImageEntry.CONTENT_LIST_TYPE;
//            case IMAGE_ID:
//                return ImageEntry.CONTENT_ITEM_TYPE;
//            default:
//                throw new IllegalStateException("Unknown URI");
//        }
//    }
//}
