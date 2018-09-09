package com.dmns.nanoInventoryApp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.dmns.nanoInventoryApp.AnythingShopContract.AnythingShopEntry;
import com.squareup.picasso.Picasso;

/**
 * Java Code to implement a custom cursor adapter to handle displaying new cursor objects in a List.
 */

public class AnythingShopCursorAdapter extends CursorAdapter {

    @BindView(R.id.nameTv)
    TextView nameView;
    @BindView(R.id.quantityTv)
    TextView quantityView;
    @BindView(R.id.priceTv)
    TextView priceView;
    @BindView(R.id._idSaleButton)
    Button saleId;
    @BindView(R.id.imageTv)
    ImageView imageView;

    // CursorAdapter Constructor
    public AnythingShopCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    // Create the new view, without adding any data
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    // Binds the data to the list, sets the views
    public void bindView(View view, final Context context, final Cursor cursor) {

        ButterKnife.bind(this, view);

        // Get the columns of the item attributes to display
        int quantityColumnIndex = cursor.getColumnIndex(AnythingShopEntry.ITEM_QUANTITY);
        String itemQuantity = cursor.getString(quantityColumnIndex);
        int imageColumnIndex = cursor.getColumnIndex(AnythingShopEntry.ITEM_IMAGE);
        Uri image = Uri.parse(cursor.getString(imageColumnIndex));

        nameView.setText(cursor.getString(cursor.getColumnIndex(AnythingShopEntry.ITEM_NAME)));
        quantityView.setText(itemQuantity);
        priceView.setText((cursor.getString(cursor.getColumnIndex(AnythingShopEntry.ITEM_PRICE)) + " $"));

        final int rowQuantity;
        final int rowID = cursor.getInt(cursor.getColumnIndex(AnythingShopEntry._ID));

        if (itemQuantity != "") {
            rowQuantity = Integer.parseInt(itemQuantity);
        } else {
            rowQuantity = Integer.parseInt("0");
        }

        Picasso.with(context).load(image)
                .error(R.drawable.if_no_data)
                .fit()
                .into(imageView);
        // sale button action
        saleId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (rowQuantity > 0) {
                    int newQuantity = rowQuantity - 1;

                    ContentValues sale = new ContentValues();
                    Uri updateUri = ContentUris.withAppendedId(AnythingShopEntry.CONTENT_URI, rowID);
                    sale.put(AnythingShopEntry.ITEM_QUANTITY, newQuantity);
                    context.getContentResolver().update(updateUri,
                            sale, null, null);

                    quantityView.setText(Integer.toString(newQuantity));
                }

            }

        });

    }

}
