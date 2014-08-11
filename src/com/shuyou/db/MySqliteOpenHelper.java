package com.shuyou.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MySqliteOpenHelper extends SQLiteOpenHelper {

	private Context context;

	public MySqliteOpenHelper(Context context)
	{
		super(context, "shuyou.db", null, Preferences.CURRENT_VERSION_CODE);
		this.context = context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		onCreateAllTables(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(oldVersion > newVersion){
			dropAllTables(db);
			onCreateAllTables(db);
		}else{
			switch (oldVersion) {
			case 1://
				
				break;

			default:
				dropAllTables(db);
				onCreateAllTables(db);
				break;
			}
		}
	}
	
	private void onCreateAllTables(SQLiteDatabase db){
		try{
			if(db != null){
				db.beginTransaction();
				//热词表
				db.execSQL("CREATE TABLE IF NOT EXISTS hotWords ("
						+ "_id integer primary key autoincrement, "//id
						+ "isbn varchar, "//isbn
						+ "bookName varchar)");//book_name
				//标签表
				db.execSQL("CREATE TABLE IF NOT EXISTS searchLabel ("
						+ "_id integer primary key autoincrement, "//id
						+ "labelID varchar, "//标签id
						+ "labelType varchar, "//标签类型
						+ "labelName varchar)");//标签名
				
				db.setTransactionSuccessful();
			}
		}finally{
			if(db != null)
			{
				db.endTransaction();
			}
		}
	}
	private void dropAllTables(SQLiteDatabase db)
	{
		try {
            if (db != null) {
                db.beginTransaction();
                db.execSQL("DROP TABLE IF EXISTS hotWords");
                db.execSQL("DROP TABLE IF EXISTS searchLabel");

                db.setTransactionSuccessful();
            }
        } finally {
            if (db != null) {
                db.endTransaction();
            }
        }
	}

}
