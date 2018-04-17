package com.example.user.mobilemicroscopy.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class ImageContract {

    // Declare Content Authority
    public static final String CONTENT_AUTHORITY = "com.example.user.mobilemicroscopy";

    // Create Base Content URI
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Declare possible paths
    public static final String PATH_IMAGES = "images";

    /**
     * ImageContract class should not be instantiated
     */
    private ImageContract() {
    }

    public static final class ImageEntry implements BaseColumns {
        /**
         * MIME type for a list of images
         */
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_IMAGES;

        /**
         * MIME type for an single image
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_IMAGES;

        /**
         * Content URI
         */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.withAppendedPath(BASE_CONTENT_URI, PATH_IMAGES);

        public static final String TABLE_NAME = "images";

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
