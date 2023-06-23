package com.stock.money;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class DBProvider extends ContentProvider {
	//SQLiteOpenHelper-�إ߸�ƮwPhoneContentDB�MTable:Users
	private static class DatabaseHelper extends SQLiteOpenHelper {
		private static final String DATABASE_NAME = "StockContentDB";
		private static final int DATABASE_VERSION = 1;
		//�إ�PhoneContentDB��Ʈw
		private DatabaseHelper(Context ctx) {
			super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
		}
		//�إ�Users���
		@Override
		public void onCreate(SQLiteDatabase db) {
			String sql = "CREATE TABLE " + UserSchema.TABLE_NAME + " (" 
			+ UserSchema.ID  + " INTEGER primary key autoincrement, " 
			+ UserSchema.STOCK_NO + " text not null " + ");";
			db.execSQL(sql);	
		}

		//��s�s����
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS test");
            onCreate(db);
		}
	}
	//�w�qDatabaseHelper���O�ܼ� databaseHelper
    static DatabaseHelper databaseHelper;
    //��@Content Providers��onCreate()
    @Override
    public boolean onCreate() {
    	databaseHelper = new DatabaseHelper(getContext());
        return true;
    }
    public interface UserSchema {
		String TABLE_NAME = "Users";           	//Table Name
		String ID = "_id";                    	//ID
		String STOCK_NO = "stock_no";       	//Game Name
	}
    
    //��@Content Providers��insert()
    @Override
    public Uri insert(Uri uri, ContentValues values) {
    	SQLiteDatabase db = databaseHelper.getWritableDatabase();
		db.insert(UserSchema.TABLE_NAME, null, values);
		db.close();
		return null;
	}
    //��@Content Providers��query()
    @Override
    public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
    	SQLiteDatabase db = databaseHelper.getWritableDatabase();
		db.update(UserSchema.TABLE_NAME, values, selection ,null);
		db.close();
		return 0;
	}
    //��@Content Providers��delete()
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
    	SQLiteDatabase db = databaseHelper.getWritableDatabase();
		db.delete(UserSchema.TABLE_NAME, selection ,null);
		db.close();
		return 0;
	}
    //��@Content Providers��update()
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(UserSchema.TABLE_NAME);
        Cursor c = qb.query(db, projection, selection, selectionArgs, null,
                null, null);
        return c;
    }
    //��@Content Providers��getType()
    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        return null;
    }
}
