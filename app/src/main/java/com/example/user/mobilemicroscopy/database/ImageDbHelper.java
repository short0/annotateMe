package com.example.user.mobilemicroscopy.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// import Contract class
import com.example.user.mobilemicroscopy.Image;
import com.example.user.mobilemicroscopy.database.ImageContract.ImageEntry;

import java.util.ArrayList;

public class ImageDbHelper extends SQLiteOpenHelper {

    /**
     * Statement to drop table
     */
    private static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + ImageEntry.TABLE_NAME;

    /**
     * Statement to delete all rows
     */
    private static final String SQL_DELETE_ALL = "DELETE FROM " + ImageEntry.TABLE_NAME;

    /**
     * Statement to select all rows
     */
    private static final String SQL_SELECT_ALL = "SELECT * FROM " + ImageEntry.TABLE_NAME + " ORDER BY _ID DESC";

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
                + ImageEntry.COLUMN_NAME_DATE + " TEXT, "
                + ImageEntry.COLUMN_NAME_TIME + " TEXT, "
                + ImageEntry.COLUMN_NAME_SPECIMEN_TYPE + " TEXT, "
                + ImageEntry.COLUMN_NAME_ORIGINAL_FILE_NAME + " TEXT, "
                + ImageEntry.COLUMN_NAME_ANNOTATED_FILE_NAME + " TEXT, "
                + ImageEntry.COLUMN_NAME_GPS_POSITION + " TEXT, "
                + ImageEntry.COLUMN_NAME_MAGNIFICATION + " TEXT, "
                + ImageEntry.COLUMN_NAME_ORIGINAL_IMAGE_LINK + " TEXT, "
                + ImageEntry.COLUMN_NAME_ANNOTATED_IMAGE_LINK + " TEXT, "
                + ImageEntry.COLUMN_NAME_STUDENT_COMMENT + " TEXT, "
                + ImageEntry.COLUMN_NAME_TEACHER_COMMENT + " TEXT, "
                + ImageEntry.COLUMN_NAME_USERNAME + " TEXT); ";

        // Execute the statement
        database.execSQL(CREATE_IMAGE_TABLE);
    }

    /**
     * Add an image
     */
    public void addImage(Image image) {
        // get writable database
        SQLiteDatabase database = this.getWritableDatabase();

        // create a content value
        ContentValues values = new ContentValues();

        values.put(ImageEntry.COLUMN_NAME_DATE, image.getDate());
        values.put(ImageEntry.COLUMN_NAME_TIME, image.getTime());
        values.put(ImageEntry.COLUMN_NAME_SPECIMEN_TYPE, image.getSpecimenType());
        values.put(ImageEntry.COLUMN_NAME_ORIGINAL_FILE_NAME, image.getOriginalFileName());
        values.put(ImageEntry.COLUMN_NAME_ANNOTATED_FILE_NAME, image.getAnnotatedFileName());
        values.put(ImageEntry.COLUMN_NAME_GPS_POSITION, image.getGpsPosition());
        values.put(ImageEntry.COLUMN_NAME_MAGNIFICATION, image.getMagnification());
        values.put(ImageEntry.COLUMN_NAME_ORIGINAL_IMAGE_LINK, image.getOriginalImageLink());
        values.put(ImageEntry.COLUMN_NAME_ANNOTATED_IMAGE_LINK, image.getAnnotatedImageLink());
        values.put(ImageEntry.COLUMN_NAME_STUDENT_COMMENT, image.getStudentComment());
        values.put(ImageEntry.COLUMN_NAME_TEACHER_COMMENT, image.getTeacherComment());
        values.put(ImageEntry.COLUMN_NAME_USERNAME, image.getUsername());

        // insert to database
        long newRowId = database.insert(ImageEntry.TABLE_NAME, null, values);

//        // close the database connection
//        database.close();
    }

    /**
     * get an image
     */
    public Image getImage(int id) {
        // get readable database
        SQLiteDatabase database = this.getReadableDatabase();

        // projection
        String[] projection = {
                ImageEntry._ID,
                ImageEntry.COLUMN_NAME_DATE,
                ImageEntry.COLUMN_NAME_TIME,
                ImageEntry.COLUMN_NAME_SPECIMEN_TYPE,
                ImageEntry.COLUMN_NAME_ORIGINAL_FILE_NAME,
                ImageEntry.COLUMN_NAME_ANNOTATED_FILE_NAME,
                ImageEntry.COLUMN_NAME_GPS_POSITION,
                ImageEntry.COLUMN_NAME_MAGNIFICATION,
                ImageEntry.COLUMN_NAME_ORIGINAL_IMAGE_LINK,
                ImageEntry.COLUMN_NAME_ANNOTATED_IMAGE_LINK,
                ImageEntry.COLUMN_NAME_STUDENT_COMMENT,
                ImageEntry.COLUMN_NAME_TEACHER_COMMENT,
                ImageEntry.COLUMN_NAME_USERNAME
        };

        // selection
        String selection = ImageEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        // order by
        String sortOrder = ImageEntry._ID + " DESC";

        // retrieve information to a cursor
        Cursor cursor = database.query(
                ImageEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            // Select columns to display
            int idColumnIndex = cursor.getColumnIndex(ImageEntry._ID);
            int dateColumnIndex = cursor.getColumnIndex(ImageEntry.COLUMN_NAME_DATE);
            int timeColumnIndex = cursor.getColumnIndex(ImageEntry.COLUMN_NAME_TIME);
            int specimenTypeColumnIndex = cursor.getColumnIndex(ImageEntry.COLUMN_NAME_SPECIMEN_TYPE);
            int originalImageFileNameColumnIndex = cursor.getColumnIndex(ImageEntry.COLUMN_NAME_ORIGINAL_FILE_NAME);
            int annotatedImageFileNameColumnIndex = cursor.getColumnIndex(ImageEntry.COLUMN_NAME_ANNOTATED_FILE_NAME);
            int gpsPositionColumnIndex = cursor.getColumnIndex(ImageEntry.COLUMN_NAME_GPS_POSITION);
            int magnificationColumnIndex = cursor.getColumnIndex(ImageEntry.COLUMN_NAME_MAGNIFICATION);
            int originalImageLinkColumnIndex = cursor.getColumnIndex(ImageEntry.COLUMN_NAME_ORIGINAL_IMAGE_LINK);
            int annotatedImageLinkColumnIndex = cursor.getColumnIndex(ImageEntry.COLUMN_NAME_ANNOTATED_IMAGE_LINK);
            int studentCommentColumnIndex = cursor.getColumnIndex(ImageEntry.COLUMN_NAME_STUDENT_COMMENT);
            int teacherCommentColumnIndex = cursor.getColumnIndex(ImageEntry.COLUMN_NAME_TEACHER_COMMENT);
            int usernameColumnIndex = cursor.getColumnIndex(ImageEntry.COLUMN_NAME_USERNAME);

            // create an image object
            Image image = new Image(
                    cursor.getInt(idColumnIndex),
                    cursor.getString(dateColumnIndex),
                    cursor.getString(timeColumnIndex),
                    cursor.getString(specimenTypeColumnIndex),
                    cursor.getString(originalImageFileNameColumnIndex),
                    cursor.getString(annotatedImageFileNameColumnIndex),
                    cursor.getString(gpsPositionColumnIndex),
                    cursor.getString(magnificationColumnIndex),
                    cursor.getString(originalImageLinkColumnIndex),
                    cursor.getString(annotatedImageLinkColumnIndex),
                    cursor.getString(studentCommentColumnIndex),
                    cursor.getString(teacherCommentColumnIndex),
                    cursor.getString(usernameColumnIndex)
            );
            return image;
        }
        return null;
    }

    /**
     * Get all images
     */
    public ArrayList<Image> getAllImages() {
        ArrayList<Image> list = new ArrayList<Image>();

        // get readable database
        SQLiteDatabase database = this.getReadableDatabase();

        // get the result to the cursor
        Cursor cursor = database.rawQuery(SQL_SELECT_ALL, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Image image = new Image();

                int idColumnIndex = cursor.getColumnIndex(ImageEntry._ID);
                int dateColumnIndex = cursor.getColumnIndex(ImageEntry.COLUMN_NAME_DATE);
                int timeColumnIndex = cursor.getColumnIndex(ImageEntry.COLUMN_NAME_TIME);
                int specimenTypeColumnIndex = cursor.getColumnIndex(ImageEntry.COLUMN_NAME_SPECIMEN_TYPE);
                int originalImageFileNameColumnIndex = cursor.getColumnIndex(ImageEntry.COLUMN_NAME_ORIGINAL_FILE_NAME);
                int annotatedImageFileNameColumnIndex = cursor.getColumnIndex(ImageEntry.COLUMN_NAME_ANNOTATED_FILE_NAME);
                int gpsPositionColumnIndex = cursor.getColumnIndex(ImageEntry.COLUMN_NAME_GPS_POSITION);
                int magnificationColumnIndex = cursor.getColumnIndex(ImageEntry.COLUMN_NAME_MAGNIFICATION);
                int originalImageLinkColumnIndex = cursor.getColumnIndex(ImageEntry.COLUMN_NAME_ORIGINAL_IMAGE_LINK);
                int annotatedImageLinkColumnIndex = cursor.getColumnIndex(ImageEntry.COLUMN_NAME_ANNOTATED_IMAGE_LINK);
                int studentCommentColumnIndex = cursor.getColumnIndex(ImageEntry.COLUMN_NAME_STUDENT_COMMENT);
                int teacherCommentColumnIndex = cursor.getColumnIndex(ImageEntry.COLUMN_NAME_TEACHER_COMMENT);
                int usernameColumnIndex = cursor.getColumnIndex(ImageEntry.COLUMN_NAME_USERNAME);

                image.setId(cursor.getInt(idColumnIndex));
                image.setDate(cursor.getString(dateColumnIndex));
                image.setTime(cursor.getString(timeColumnIndex));
                image.setSpecimenType(cursor.getString(specimenTypeColumnIndex));
                image.setOriginalFileName(cursor.getString(originalImageFileNameColumnIndex));
                image.setAnnotatedFileName(cursor.getString(annotatedImageFileNameColumnIndex));
                image.setGpsPosition(cursor.getString(gpsPositionColumnIndex));
                image.setMagnification(cursor.getString(magnificationColumnIndex));
                image.setOriginalImageLink(cursor.getString(originalImageLinkColumnIndex));
                image.setAnnotatedImageLink(cursor.getString(annotatedImageLinkColumnIndex));
                image.setStudentComment(cursor.getString(studentCommentColumnIndex));
                image.setTeacherComment(cursor.getString(teacherCommentColumnIndex));
                image.setUsername(cursor.getString(usernameColumnIndex));

                // Adding image to list
                list.add(image);
            } while (cursor.moveToNext());
        }

        return list;
    }

    /**
     * update an image
     */
    public int updateImage(Image image) {
        // get writable database
        SQLiteDatabase database = this.getWritableDatabase();

        // create a content value
        ContentValues values = new ContentValues();

        values.put(ImageEntry.COLUMN_NAME_DATE, image.getDate());
        values.put(ImageEntry.COLUMN_NAME_TIME, image.getTime());
        values.put(ImageEntry.COLUMN_NAME_SPECIMEN_TYPE, image.getSpecimenType());
        values.put(ImageEntry.COLUMN_NAME_ORIGINAL_FILE_NAME, image.getOriginalFileName());
        values.put(ImageEntry.COLUMN_NAME_ANNOTATED_FILE_NAME, image.getAnnotatedFileName());
        values.put(ImageEntry.COLUMN_NAME_GPS_POSITION, image.getGpsPosition());
        values.put(ImageEntry.COLUMN_NAME_MAGNIFICATION, image.getMagnification());
        values.put(ImageEntry.COLUMN_NAME_ORIGINAL_IMAGE_LINK, image.getOriginalImageLink());
        values.put(ImageEntry.COLUMN_NAME_ANNOTATED_IMAGE_LINK, image.getAnnotatedImageLink());
        values.put(ImageEntry.COLUMN_NAME_STUDENT_COMMENT, image.getStudentComment());
        values.put(ImageEntry.COLUMN_NAME_TEACHER_COMMENT, image.getTeacherComment());
        values.put(ImageEntry.COLUMN_NAME_USERNAME, image.getUsername());

        // selection
        String selection = ImageEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(image.getId())};

        return database.update(
                ImageEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    /**
     * delete an image
     */
    public int deleteImage(Image image) {
        // get writable database
        SQLiteDatabase database = this.getWritableDatabase();

        // selection
        String selection = ImageEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(image.getId())};

        return database.delete(ImageEntry.TABLE_NAME, selection, selectionArgs);
    }

    public void deleteAll() {
        // get writable database
        SQLiteDatabase database = this.getWritableDatabase();

        database.execSQL(SQL_DELETE_ALL);
    }

    /**
     * Method called when the database is upgraded
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL(SQL_DROP_TABLE);
        onCreate(database);
    }

    /**
     * Method called when the database is downgraded
     */
    @Override
    public void onDowngrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        onUpgrade(database, oldVersion, newVersion);
    }
}
