package com.dmns.nanoInventoryApp;

import android.Manifest;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.dmns.nanoInventoryApp.AnythingShopContract.AnythingShopEntry;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Allows user to create a item or edit an existing one.
 */
public class DetailedActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    // Identifier for the stock data loader
    private static final int ITEM_LOADER = 1;
    // support retrieving an image from camera
    private static final int IMAGE_REQUEST = 0;
    //Constant to be used when asking for storage read
    private final static int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 111;
    // Content URI for the existing item
    public Uri mCurrentItemUri;
    @BindView(R.id.nameET)
    EditText itemNameInput;
    @BindView(R.id.supplierPhoneET)
    EditText supplierPhoneInput;
    @BindView(R.id.supplierEmailET)
    EditText supplierEmailInput;
    @BindView(R.id.decreaseQuantity)
    ImageView decreaseQuantity;
    @BindView(R.id.increaseQuantity)
    ImageView increaseQuantity;
    @BindView(R.id.quantityET)
    EditText quantityInput;
    @BindView(R.id.priceET)
    EditText priceInput;
    @BindView(R.id.supplierNameET)
    EditText supplierNameInput;
    @BindView(R.id.select_image)
    Button imageButton;
    // Boolean to check whether or not the register has changed
    private boolean mProductHasChanged = false;

    //any change on view will be notice in variable
    View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mProductHasChanged = true;
            return false;
        }
    };
    // member values for parsing the image data
    private Uri mImageUri;
    private ImageView mItemImage;
    private String currentImageUri = "no image";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_activity);

        ButterKnife.bind(this);
        setOnTouchListener();
        mItemImage = (ImageView) findViewById(R.id.image_view);

        Bundle bundle = getIntent().getExtras();
        //retrieving data from previous intent
        if (bundle != null) {
            String string = bundle.toString();
            mImageUri = Uri.parse(string);
        }

        mCurrentItemUri = getIntent().getData();
        // validate data
        if (mCurrentItemUri == null) {
            setTitle(getString(R.string.ifNewItemBarName));
        } else {
            setTitle(getString(R.string.ifEditItemBarName));
            getLoaderManager().initLoader(ITEM_LOADER, null, this);
        }

    }

    private void setOnTouchListener() {
        imageButton.setOnTouchListener(mTouchListener);
        supplierNameInput.setOnTouchListener(mTouchListener);
        priceInput.setOnTouchListener(mTouchListener);
        quantityInput.setOnTouchListener(mTouchListener);
        increaseQuantity.setOnTouchListener(mTouchListener);
        decreaseQuantity.setOnTouchListener(mTouchListener);
        supplierEmailInput.setOnTouchListener(mTouchListener);
        supplierPhoneInput.setOnTouchListener(mTouchListener);
        itemNameInput.setOnTouchListener(mTouchListener);
    }

    public void selectImage(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                String[] permisionRequest = {Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permisionRequest, PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                return;
            } else {
                // If permission granted, create a new intent and prompt
                // user to pick image from Gallery
                getImage();
            }
        }

    }

    private void getImage() {

        Intent getIntent = new Intent(Intent.ACTION_PICK);
        File imageFlie = Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_PICTURES);
        String imagePath = imageFlie.getPath();
        Uri imageUri = Uri.parse(imagePath);
        getIntent.setDataAndType(imageUri, "image/*");
        startActivityForResult(getIntent, IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED) {
            getImage();
        } else {
            Toast.makeText(this, "No authorization", Toast.LENGTH_LONG).show();
        }
    }

    public void minusQuantity(View v) {
        String previousValueString = quantityInput.getText().toString();
        int previousValue;
        if (previousValueString.isEmpty()) {
            return;
        } else if (previousValueString.equals("0")) {
            return;
        } else {
            previousValue = Integer.parseInt(previousValueString);
            quantityInput.setText(String.valueOf(previousValue - 1));
        }

    }

    public void addQuantity(View v) {
        String previousValueString = quantityInput.getText().toString();
        int previousValue;
        if (previousValueString.isEmpty()) {
            previousValue = 0;
        } else {
            previousValue = Integer.parseInt(previousValueString);
        }
        quantityInput.setText(String.valueOf(previousValue + 1));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from the res menu_catalog.xml file
        getMenuInflater().inflate(R.menu.drop_detailed, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentItemUri == null) {
            MenuItem deleteOneItemMenuItem = menu.findItem(R.id.delete_item);
            MenuItem orderMenuItem = menu.findItem(R.id.action_order);
            deleteOneItemMenuItem.setVisible(false);
            orderMenuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to click on Insert Test Data
            case R.id.action_save:
                if (!mProductHasChanged) {
                    Toast.makeText(this, "You haven't change any data", Toast.LENGTH_SHORT).show();
                } else {
                    if (mCurrentItemUri == null) {
                        addNewItem();
                    } else {
                        saveItem();
                    }
                }
                return true;
            case R.id.action_order:
                // dialog with phone and email
                showOrderConfirmationDialog();
                return true;
            case R.id.delete_item:
                showDeleteConfirmationDialogBox();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Get user input from editor and save item into database.
     */

    private void saveItem() {
        // Read from input fields
        String name = itemNameInput.getText().toString().trim();
        String phone = supplierPhoneInput.getText().toString().trim();
        String email = supplierEmailInput.getText().toString().trim();
        String quantity = quantityInput.getText().toString().trim();
        String price = priceInput.getText().toString().trim();
        String supplierName = supplierNameInput.getText().toString().trim();

        // check if all the fields in the editor are blank
        if (mCurrentItemUri == null || name.isEmpty() || price.isEmpty() ||
                email.isEmpty() || quantity.isEmpty() || phone.isEmpty() ||
                supplierName.isEmpty()) {
            Toast.makeText(this, R.string.validate, Toast.LENGTH_SHORT).show();
        } else {
            // Create a ContentValues object where column names are the keys,
            // and inserted item attributes are the values.
            ContentValues values = new ContentValues();
            values.put(AnythingShopEntry.ITEM_NAME, name);
            values.put(AnythingShopEntry.ITEM_PRICE, price);
            values.put(AnythingShopEntry.ITEM_QUANTITY, quantity);
            values.put(AnythingShopEntry.ITEM_SUPPLY, supplierName);
            values.put(AnythingShopEntry.ITEM_PHONE, phone);
            values.put(AnythingShopEntry.ITEM_EMAIL, email);
            values.put(AnythingShopEntry.ITEM_IMAGE, currentImageUri);
            // update the item with content URI
            // and pass in the new ContentValues.
            int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);
            //print affection result
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, "failed",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "modyfied", Toast.LENGTH_SHORT).show();
                finish();
            }

        }

    }

    /**
     * Get user input from editor and save new item into database.
     */

    private void addNewItem() {
        // Read from input fields
        String name = itemNameInput.getText().toString().trim();
        String phone = supplierPhoneInput.getText().toString().trim();
        String email = supplierEmailInput.getText().toString().trim();
        String quantity = quantityInput.getText().toString().trim();
        String price = priceInput.getText().toString().trim();
        String supplierName = supplierNameInput.getText().toString().trim();

        // check if all the fields in the editor are blank
        if (name.isEmpty() || phone.isEmpty() || mImageUri == null ||
                email.isEmpty() || quantity.isEmpty() || price.isEmpty() ||
                supplierName.isEmpty()) {
            Toast.makeText(this, R.string.validate, Toast.LENGTH_SHORT).show();
        } else {
            // Create a ContentValues object where column names are the keys,
            // and inserted item attributes are the values.
            ContentValues values = new ContentValues();
            values.put(AnythingShopEntry.ITEM_NAME, name);
            values.put(AnythingShopEntry.ITEM_PRICE, price);
            values.put(AnythingShopEntry.ITEM_QUANTITY, quantity);
            values.put(AnythingShopEntry.ITEM_SUPPLY, supplierName);
            values.put(AnythingShopEntry.ITEM_PHONE, phone);
            values.put(AnythingShopEntry.ITEM_EMAIL, email);
            values.put(AnythingShopEntry.ITEM_IMAGE, currentImageUri);
            // add new item with new URI
            // and pass in the new ContentValues.
            Uri newUri = getContentResolver().insert(AnythingShopEntry.CONTENT_URI, values);
            if (newUri == null) {
                // If no rows were affected, then there was an error with the insertion.
                Toast.makeText(this, "failed",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "added", Toast.LENGTH_SHORT).show();
                finish();
            }

        }

    }

    /**
     * Perform the deletion of the pet in the database.
     */

    private void deleteItem() {
        // Only perform the delete if this is an existing item.
        if (mCurrentItemUri != null) {

            // Call the ContentResolver to delete the item at the given content URI.
            int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, "nie udalo sie", Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, "usunelo", Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

    /**
     * Prompt the user to confirm that they want to order more items.
     */

    private void showOrderConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message,
        // and click listeners for order more button confirmation box
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.orderConfirmationMessage);
        builder.setPositiveButton(R.string.phone, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // intent to phone
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + supplierPhoneInput.getText().toString().trim()));
                startActivity(intent);
            }
        });
        builder.setNegativeButton(R.string.email, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // intent to email
                Intent intent = new Intent(android.content.Intent.ACTION_SENDTO);
                intent.setType("text/plain");
                intent.setData(Uri.parse("mailto:" + supplierEmailInput.getText().toString().trim()));
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "New order");
                String bodyMessage = "I would like to order more of this item" +
                        itemNameInput.getText().toString().trim() +
                        "!!!";
                intent.putExtra(android.content.Intent.EXTRA_TEXT, bodyMessage);
                startActivity(intent);
            }
        });
        builder.setNeutralButton(R.string.cancel, null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this item.
     */

    private void showDeleteConfirmationDialogBox() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.deleteConfirmationMessage);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // on yes delete item
                deleteItem();
                Toast.makeText(DetailedActivity.this, "Data erased.", Toast.LENGTH_SHORT).show();
                Intent newIntent = new Intent(DetailedActivity.this, MainActivity.class);
                startActivity(newIntent);
            }
        });
        builder.setNegativeButton(R.string.cancel, null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns we want
        String[] projection = {
                AnythingShopEntry._ID,
                AnythingShopEntry.ITEM_NAME,
                AnythingShopEntry.ITEM_PHONE,
                AnythingShopEntry.ITEM_EMAIL,
                AnythingShopEntry.ITEM_SUPPLY,
                AnythingShopEntry.ITEM_PRICE,
                AnythingShopEntry.ITEM_QUANTITY,
                AnythingShopEntry.ITEM_IMAGE};
        // This loader will execute the ContentProvider's query method on a background thread
        return new android.content.CursorLoader(this, mCurrentItemUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        // Proceed with moving to the first row of the cursor and reading data from it
        if (cursor.moveToFirst()) {

            // Extract out the value from the Cursor for the given column index
            // Update the views on the screen with the values from the database
            itemNameInput.setText(cursor.getString(cursor.getColumnIndex(AnythingShopEntry.ITEM_NAME)));
            supplierPhoneInput.setText(cursor.getString(cursor.getColumnIndex(AnythingShopEntry.ITEM_PHONE)));
            supplierEmailInput.setText(cursor.getString(cursor.getColumnIndex(AnythingShopEntry.ITEM_EMAIL)));
            quantityInput.setText(cursor.getString(cursor.getColumnIndex(AnythingShopEntry.ITEM_QUANTITY)));
            priceInput.setText(cursor.getString(cursor.getColumnIndex(AnythingShopEntry.ITEM_PRICE)));
            supplierNameInput.setText(cursor.getString(cursor.getColumnIndex(AnythingShopEntry.ITEM_SUPPLY)));

            int imageColumnIndex = cursor.getColumnIndex(AnythingShopEntry.ITEM_IMAGE);
            currentImageUri = cursor.getString(imageColumnIndex);

            Picasso.with(this).load(currentImageUri)
                    .error(R.drawable.if_no_data)
                    .fit()
                    .into(mItemImage);
        }

    }

    // retrieving image data
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code READ_REQUEST_CODE.
        // If the request code seen here doesn't match, it's the response to some other intent,
        // and the below code shouldn't run at all.

        if (requestCode == IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.  Pull that uri using "resultData.getData()"

            if (resultData != null) {
                mImageUri = resultData.getData();
                currentImageUri = mImageUri.toString();

                Picasso.with(this).load(mImageUri)
                        .error(R.drawable.if_no_data)
                        .fit()
                        .into(mItemImage);
            }

        }

    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        itemNameInput.getText().clear();
        supplierPhoneInput.getText().clear();
        supplierEmailInput.getText().clear();
        quantityInput.getText().clear();
        priceInput.getText().clear();
        supplierNameInput.getText().clear();

    }

}