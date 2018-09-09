package com.dmns.nanoInventoryApp;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for this app.
 */

public class AnythingShopContract {

    // Content Authority for the app.
    public static final String CONTENT_AUTHORITY = "com.dmns.nanoInventoryApp";

    // Base Uri for queries
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible path for querying the Inventory table.
    public static final String PATH_STOCK = "stock";

    private AnythingShopContract() {
        //An empty private constructor makes sure that the class is not going to be initialised.
    }

    /**
     * Inner class that defines constants for the stock database table
     * Each entry in the table represents a single stock item
     */

    public static final class AnythingShopEntry implements BaseColumns {

        // The Content Uri to access Stock data in the provider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_STOCK);

        // The MIME type the content Uri for a list of Stock items
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STOCK;

        // Mime type for a single Stock item
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STOCK;

        // Name of Database table
        public static final String TABLE_NAME = "stock";

        // Item name
        public static final String ITEM_NAME = "name";

        // Item price
        public static final String ITEM_PRICE = "price";

        // Quantity in stock
        public static final String ITEM_QUANTITY = "quantity";

        // Item Image
        public static final String ITEM_IMAGE = "image";

        // Supplier name
        public static final String ITEM_SUPPLY = "supplier_name";

        // Supplier phone
        public static final String ITEM_PHONE = "supplier_phone";

        // Supplier email
        public static final String ITEM_EMAIL = "supplier_email";

    }

}
