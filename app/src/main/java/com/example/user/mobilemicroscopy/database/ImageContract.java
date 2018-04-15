package com.example.user.mobilemicroscopy.database;

import android.net.Uri;
import android.provider.BaseColumns;

public final class ImageContract {

    // Declare Content Authority
    public static final String CONTENT_AUTHORITY = "com.example.user.mobilemicroscopy";

    // Create Base Content URI
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Declare possible paths
    public static final String PATH_IMAGES = "Image";

    /**
     * ImageContract class should not be instantiated
     */
    private ImageContract() {
    }

    ;

    public static final class ImageEntry implements BaseColumns {

        public final static Uri CONTENT_URI = BASE_CONTENT_URI.withAppendedPath(BASE_CONTENT_URI, PATH_IMAGES);

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
