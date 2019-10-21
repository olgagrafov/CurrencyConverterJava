package com.olgag.currencyconverter.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by olgag on 26/12/2017.
 */

public class DbProvider extends ContentProvider {
    private DBCoinNameHelper helper;
    private static final String AUTHORIZATION = "com.example.olgag.currencyconverter.db";
    public static final Uri CONTENT_TABLE_URI = Uri.parse("content://" + AUTHORIZATION + "/" + DBCoinNameHelper.TABLE_COINSNAME);

    public DbProvider() {
    }
    @Override
    public boolean onCreate() {
        helper=new DBCoinNameHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] columns, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = helper.getReadableDatabase();
        // getLastPathSegment will return the end of the Uri (in our case the name of the table!)
        Cursor c = db.query(uri.getLastPathSegment(), columns, selection, selectionArgs, null, null, sortOrder);
        return c;
    }


    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = helper.getWritableDatabase();
        long rowNum = db.insert(uri.getLastPathSegment(), null, values);
        db.close();
        return Uri.withAppendedPath(uri, rowNum+"");
        // add "/" at the end and then add rowNum
        //return Uri.parse(uri.toString() + "/" + rowNum);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int count = db.delete(uri.getLastPathSegment(), selection, selectionArgs);
        db.close();
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int count = db.update(uri.getLastPathSegment(), values, selection, selectionArgs);
        db.close();
        return count;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
