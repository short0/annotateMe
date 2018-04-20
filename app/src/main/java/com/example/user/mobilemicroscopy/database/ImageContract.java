package com.example.user.mobilemicroscopy.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Class that contains the "contracts" with database
 */
public final class ImageContract {

    /**
     * private constructor to ensure ImageContract class should not be instantiated
     */
    private ImageContract() {
    }

    /**
     * Inner class that contains constants for database
     */
    public static final class ImageEntry implements BaseColumns {

        // Table name
        public static final String TABLE_NAME = "images";

        // Column names
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_TIME = "time";
        public static final String COLUMN_NAME_SPECIMEN_TYPE = "specimenType";
        public static final String COLUMN_NAME_ORIGINAL_FILE_NAME = "originalFileName";
        public static final String COLUMN_NAME_ANNOTATED_FILE_NAME = "annotatedFileName";
        public static final String COLUMN_NAME_GPS_POSITION = "gpsPosition";
        public static final String COLUMN_NAME_MAGNIFICATION = "magnification";
        public static final String COLUMN_NAME_ORIGINAL_IMAGE_LINK = "originalImageLink";
        public static final String COLUMN_NAME_ANNOTATED_IMAGE_LINK = "annotatedImageLink";
        public static final String COLUMN_NAME_COMMENT = "comment";
    }
}
