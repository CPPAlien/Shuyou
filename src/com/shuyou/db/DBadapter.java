package com.shuyou.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DBadapter extends MySqliteOpenHelper {

	protected DBadapter(Context context) {
		super(context);
	}

	private SQLiteDatabase db;

    public DBadapter openWriteable() throws SQLException {
        db = this.getWritableDatabase();
        return this;
    }

    public DBadapter openReadable() throws SQLException {
        db = this.getReadableDatabase();
        return this;
    }

    public void close() {
        if (db != null) {
            db.close();
        }
    }

    public Cursor rawQuery(String sql, String[] selectionArgs) {
        return db.rawQuery(sql, selectionArgs);
    }

    public boolean delete(String table, String whereClause, String[] whereArgs) {
        return db.delete(table, whereClause, whereArgs) > 0;
    }

    public long insert(String table, String nullColumnHack, ContentValues cv) {
        return db.insert(table, nullColumnHack, cv);
    }

    public int update(String table, ContentValues cv, String whereClause, String[] whereArgs) {
        return db.update(table, cv, whereClause, whereArgs);
    }
}
