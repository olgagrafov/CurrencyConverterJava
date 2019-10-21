package com.olgag.currencyconverter.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by olgag on 26/12/2017.
 */

public class DBCoinNameHelper  extends SQLiteOpenHelper {
    public static final String TABLE_COINSNAME = "tbl_coins_name";
    public static final String COIN_ID_ROW = "id_row";
    public static final String COIN_ID = "coin_id";
    public static final String COIN_NAME = "coin_name";


    public DBCoinNameHelper(Context context) {
        super(context, "coinsName.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(String.format("CREATE TABLE %s ( %s  INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT )",
                TABLE_COINSNAME, COIN_ID_ROW, COIN_ID, COIN_NAME));
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
