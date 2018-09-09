package com.dmns.nanoInventoryApp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.dmns.nanoInventoryApp.AnythingShopContract.AnythingShopEntry;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    // Identifier for the loader
    private static final int STOCK_LOADER = 0;

    // initializing data holder to detailed intent
    Uri currentUri;

    // Cursor Adapter
    AnythingShopCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView itemListView = (ListView) findViewById(R.id.list_view);

        // Set an EmptyView on the List to display when the list is empty
        View emptyView = findViewById(R.id.empty_view);
        itemListView.setEmptyView(emptyView);

        // Setup a CursorAdapter to display the data in the cursor
        // Set to null since there is no data yet

        mCursorAdapter = new AnythingShopCursorAdapter(this, null);

        itemListView.setAdapter(mCursorAdapter);
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //passing item Uri to new intent
                Intent intent = new Intent(MainActivity.this, DetailedActivity.class);
                // bundle the id image url to next intent
                if (currentUri != null) {
                    Bundle bundle = new Bundle();
                    Uri imageUri = ContentUris.withAppendedId(AnythingShopEntry.CONTENT_URI,
                            ContentUris.parseId(currentUri));
                    String pass = imageUri.toString();
                    bundle.putString("imageUri", pass);
                    intent.putExtras(bundle);
                }

                currentUri = ContentUris.withAppendedId(AnythingShopEntry.CONTENT_URI, id);
                intent.setData(currentUri);
                startActivity(intent);
            }
        });

        getSupportLoaderManager().initLoader(STOCK_LOADER, null, this);
    }

    // OnClick fab
    public void fab(View v) {
        Intent intent = new Intent(MainActivity.this, DetailedActivity.class);
        startActivity(intent);
    }

    /**
     * Helper method for inserting test data
     */

    private void insertItem() {
        // Build a ContentValues object for an stock item
        ContentValues values = new ContentValues();
        values.put(AnythingShopEntry.ITEM_NAME, "Anything Item");
        values.put(AnythingShopEntry.ITEM_PRICE, 7.89);
        values.put(AnythingShopEntry.ITEM_QUANTITY, 56);
        values.put(AnythingShopEntry.ITEM_SUPPLY, "Anything Tech .GMBH");
        values.put(AnythingShopEntry.ITEM_PHONE, 01234567);
        values.put(AnythingShopEntry.ITEM_EMAIL, "anything@anythingShop.com");
        values.put(AnythingShopEntry.ITEM_IMAGE, "no image");

        Uri newUri = getContentResolver().insert(AnythingShopEntry.CONTENT_URI, values);
        if (newUri == null) {
            // If no rows were affected, then there was an error with the update.
            Toast.makeText(this, "failed",
                    Toast.LENGTH_SHORT).show();
        } else {

            Toast.makeText(this, "added", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Method to delete all Items in the table
     */
    private void deleteAllItems() {
        int rowsDeleted = getContentResolver().delete(AnythingShopEntry.CONTENT_URI, null, null);
        Log.v("InventoryCatalog", rowsDeleted + " rows deleted from inventory database");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to click on Insert Test Data
            case R.id.insert_noobie_data:
                insertItem();
                return true;
            // Respond to click on Delete All Data
            case R.id.delete_all_data:
                showDeleteConfirmationDialogBox();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from the res menu_catalog.xml file
        getMenuInflater().inflate(R.menu.drop, menu);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns we want
        String[] projection = {
                AnythingShopEntry._ID,
                AnythingShopEntry.ITEM_NAME,
                AnythingShopEntry.ITEM_PRICE,
                AnythingShopEntry.ITEM_QUANTITY,
                AnythingShopEntry.ITEM_IMAGE};

        // Return the CursorLoader
        return new CursorLoader(this,       // Parent Activity
                AnythingShopEntry.CONTENT_URI, // Content Uri to query
                projection,                 // Projection
                null,                       // No selection clause
                null,                       // No selectionArgs
                null);                      // Default sortOrder
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update the AnythingShopCursorAdapter with the new Cursor
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Called when data is to be deleted
        mCursorAdapter.swapCursor(null);
    }

    private void showDeleteConfirmationDialogBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.deleteConfirmationMessage);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteAllItems();
                Toast.makeText(MainActivity.this, "Data erased.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.cancel, null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
