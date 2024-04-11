package com.poseintelligence.cssdm1.core.SQLiteDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FeedReaderDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "CSSD_M1.db";

    public FeedReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create_configuration =
                "CREATE TABLE configuration (" +
                        "RowID INTEGER PRIMARY KEY," +
                        "SR_IsCheckItemInMachine INTEGER," +
                        "SS_IsMatchBasketAndType INTEGER)";
        db.execSQL(create_configuration);

        String create_itemstock =
                "CREATE TABLE itemstock (" +
                        "RowID INTEGER PRIMARY KEY," +
                        "ItemCode TEXT," +
                        "UsageCode TEXT," +
                        "IsStatus INTEGER," +
                        "IsCancel INTEGER," +
                        "IsPay INTEGER)";
        db.execSQL(create_itemstock);

        String create_washdetail =
                "CREATE TABLE washdetail (" +
                        "RowID INTEGER PRIMARY KEY," +
                        "WashDocNo TEXT," +
                        "ItemStockID TEXT," +
                        "ModifyDate TEXT," +
                        "IsStatus INTEGER," +
                        "PrintCount INTEGER)";
        db.execSQL(create_washdetail);

        String create_steriletype_item =
                "CREATE TABLE steriletype_item (" +
                        "RowID INTEGER PRIMARY KEY," +
                        "ItemCode TEXT," +
                        "SterileTypeID INTEGER)";
        db.execSQL(create_steriletype_item);

        String create_sterilemachine_item =
                "CREATE TABLE sterilemachine_item (" +
                        "RowID INTEGER PRIMARY KEY," +
                        "ItemCode TEXT," +
                        "SterileMachineID INTEGER)";
        db.execSQL(create_sterilemachine_item);

        String create_itemstockdetailbasket =
                "CREATE TABLE itemstockdetailbasket (" +
                        "RowID INTEGER PRIMARY KEY," +
                        "BasketID INTEGER," +
                        "ItemStockID INTEGER," +
                        "WashDetailID INTEGER," +
                        "PairTime TEXT," +
                        "IsActive INTEGER)";
        db.execSQL(create_itemstockdetailbasket);

        String create_steriledetail =
                "CREATE TABLE steriledetail (" +
                        "RowID INTEGER PRIMARY KEY," +
                        "DocNo TEXT," +
                        "ItemStockID INTEGER," +
                        "IsStatus INTEGER)";
        db.execSQL(create_steriledetail);
    }
}