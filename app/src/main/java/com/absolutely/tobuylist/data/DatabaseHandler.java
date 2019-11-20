package com.absolutely.tobuylist.data;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.absolutely.tobuylist.model.Item;
import com.absolutely.tobuylist.util.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private final Context context;

    public DatabaseHandler(@Nullable Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_ITEM_TABLE = "CREATE TABLE " + Constants.TABLE_NAME + "("
                + Constants.KEY_ID + " INTEGER PRIMARY KEY, "
                + Constants.KEY_ITEM_NAME + " TEXT, "
                + Constants.KEY_COLOR + " TEXT, "
                + Constants.KEY_QUANTITY + " INTEGER, "
                + Constants.KEY_SIZE + " INTEGER, "
                + Constants.KEY_CREATED_AT + " LONG);";

        sqLiteDatabase.execSQL(CREATE_ITEM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    //CRUD Operation
    public void addItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Constants.KEY_ITEM_NAME, item.getItemName());
        values.put(Constants.KEY_QUANTITY, item.getItemQuantity());
        values.put(Constants.KEY_COLOR, item.getItemColor());
        values.put(Constants.KEY_SIZE, item.getItemSize());
        values.put(Constants.KEY_CREATED_AT, java.lang.System.currentTimeMillis()); //timestamp

        db.insert(Constants.TABLE_NAME, null, values);
//        Log.d("DBHandler", "addItem: ");
    }

    public Item getItem(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Constants.TABLE_NAME,
                new String[]{ Constants.KEY_ID,
                Constants.KEY_ITEM_NAME,
                Constants.KEY_QUANTITY,
                Constants.KEY_COLOR,
                Constants.KEY_SIZE,
                Constants.KEY_CREATED_AT },
                Constants.KEY_ID + "=?",
                new String[]{ String.valueOf(id) }, null, null, null, null);

        if(cursor != null)
            cursor.moveToFirst();

        Item item = new Item();
        if (cursor != null) {
            item.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Constants.KEY_ID))));
            item.setItemName(cursor.getString(cursor.getColumnIndex(Constants.KEY_ITEM_NAME)));
            item.setItemQuantity(cursor.getInt(cursor.getColumnIndex(Constants.KEY_QUANTITY)));
            item.setItemSize(cursor.getInt(cursor.getColumnIndex(Constants.KEY_SIZE)));
            item.setItemColor(cursor.getString(cursor.getColumnIndex(Constants.KEY_COLOR)));

            //convert timestamp to something readable
//            DateFormat dateFormat = DateFormat.getDateInstance();
            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh.mm a");
            String formattedDate = dateFormat.format(new Date(cursor.getLong(cursor.getColumnIndex(Constants.KEY_CREATED_AT))).getTime());
            item.setDateItemAdded(formattedDate);
//            Log.d("DBHandler", "getItem: " + formattedDate);
        }

        return item;
    }

    public List<Item> getAllItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Item> list = new ArrayList<>();

        Cursor cursor = db.query(Constants.TABLE_NAME,
                new String[]{ Constants.KEY_ID,
                        Constants.KEY_ITEM_NAME,
                        Constants.KEY_QUANTITY,
                        Constants.KEY_COLOR,
                        Constants.KEY_SIZE,
                        Constants.KEY_CREATED_AT },
                null, null, null, null, Constants.KEY_CREATED_AT + " DESC");

        if(cursor.moveToFirst()) {
            do {
                Item item = new Item();
                item.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Constants.KEY_ID))));
                item.setItemName(cursor.getString(cursor.getColumnIndex(Constants.KEY_ITEM_NAME)));
                item.setItemQuantity(cursor.getInt(cursor.getColumnIndex(Constants.KEY_QUANTITY)));
                item.setItemSize(cursor.getInt(cursor.getColumnIndex(Constants.KEY_SIZE)));
                item.setItemColor(cursor.getString(cursor.getColumnIndex(Constants.KEY_COLOR)));
                //convert timestamp to something readable
                @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh.mm a");
                String formattedDate = dateFormat.format(new Date(cursor.getLong(cursor.getColumnIndex(Constants.KEY_CREATED_AT))).getTime());
                item.setDateItemAdded(formattedDate);

                //add to arrayList
                list.add(item);
            } while(cursor.moveToNext());
        }
        return list;
    }

    public int updateItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Constants.KEY_ITEM_NAME, item.getItemName());
        values.put(Constants.KEY_QUANTITY, item.getItemQuantity());
        values.put(Constants.KEY_COLOR, item.getItemColor());
        values.put(Constants.KEY_SIZE, item.getItemSize());
        values.put(Constants.KEY_CREATED_AT, java.lang.System.currentTimeMillis()); //timestamp

        return db.update(Constants.TABLE_NAME, values, Constants.KEY_ID + "=?", new String[]{ String.valueOf(item.getId())});
    }

    public void deleteItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Constants.TABLE_NAME, Constants.KEY_ID + "=?", new String[]{ String.valueOf(id) });

        db.close();
    }

    public int getItemCount() {
        String count = "SELECT * FROM " + Constants.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(count, null);

        return cursor.getCount();
    }
}
