package com.example.user.mobilemicroscopy.database;

import android.provider.BaseColumns;

public final class ImageContract {

    /**
     * ImageContract class should not be instantiated
     */
    private ImageContract() {
    };

    public static final class ImageEntry implements BaseColumns {

        public final static String TABLE_NAME = "Image";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_NAME_DATE = "date";
        public final static String COLUMN_NAME_TIME = "time";
        public final static String COLUMN_NAME_SPECIMEN_TYPE = "specimenType";
        public final static String COLUMN_NAME_ORIGINAL_FILE_NAME = "originalFileName";
        public final static String COLUMN_NAME_ANNOTATED_FILE_NAME = "annotatedFileName";
        public final static String COLUMN_NAME_GPS_POSITION = "GPSposition";
        public final static String COLUMN_NAME_MAGNIFICATION = "magnification";
        public final static String COLUMN_NAME_ORIGINAL_IMAGE_LINK = "originalImageLink";
        public final static String COLUMN_NAME_ANNOTATED_IMAGE_LINK = "annotatedImageLink";
        public final static String COLUMN_NAME_COMMENT = "comment";
    }
}
