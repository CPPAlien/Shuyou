package com.shuyou.db;

import java.util.ArrayList;

import com.shuyou.net.BookDetail;
import com.shuyou.net.SearchLabel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DBService extends DaoHelper {

	private static DBService m_stInstance;

	public static DBService getInstance(Context context) {
		if (m_stInstance == null)
			m_stInstance = new DBService(context);
		return m_stInstance;
	}

	protected DBService(Context context) {
		super(context);
	}

	/**
	 * 数据库保存热词
	 * 
	 * @param list
	 * */
	public synchronized void saveHotWords(ArrayList<BookDetail> list) {
		Cursor c = null;
		SQLiteDatabase db = mDb.getWritableDatabase();
		db.beginTransaction();
		try {
			if (list != null && list.size() > 0) {
				for (BookDetail book : list) {
					ContentValues values = new ContentValues();
					values.put("isbn", book.getIsbn());
					values.put("bookName", book.getBook_name());
					long rows = db.update("hotWords", values, "isbn=?",
							new String[] { book.getIsbn() });
					if(rows <= 0){
						rows = db.insert("hotWords", null, values);
					}
				}
			}
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			closeCursor(c);
			closeDB(db);
		}
	}
	/**
	 * 数据库获取热词
	 * 
	 * @return 如果list为empty，则没有数据
	 * */
	public ArrayList<BookDetail> getHotWords(){
		ArrayList<BookDetail> list = new ArrayList<BookDetail>();
		SQLiteDatabase db = null;
		Cursor c = null;
		try{
			db = mDb.getWritableDatabase();
			c = db.rawQuery(SELECT_FROM + "hotWords", null);
			while(c.moveToNext()){
				BookDetail book = new BookDetail();
				book.setIsbn(c.getString(c.getColumnIndex("isbn")));
				book.setBook_name(c.getString(c.getColumnIndex("bookName")));
				list.add(book);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			closeCursorAndDB(c, db);
		}
		return list;
	}
	
	/**
	 * 数据库保存分类标签
	 * */
	public synchronized void saveSearchLabel(ArrayList<SearchLabel> list){
		Cursor c = null;
		SQLiteDatabase db = mDb.getWritableDatabase();
		db.beginTransaction();
		try {
			if (list != null && list.size() > 0) {
				for (SearchLabel label : list) {
					ContentValues values = new ContentValues();
					values.put("labelID", label.getId());
					values.put("labelType", label.getType());
					values.put("labelName", label.getName());
					long rows = db.update("searchLabel", values, "labelID=?",
							new String[] { label.getId() });
					if(rows <= 0){
						rows = db.insert("searchLabel", null, values);
					}
				}
			}
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			closeCursor(c);
			closeDB(db);
		}
	}
	
	/**
	 * 数据库获取分类标签
	 * 
	 * @return 如果list为empty，则没有数据
	 * */
	public ArrayList<SearchLabel> getSearchLabel(){
		ArrayList<SearchLabel> list = new ArrayList<SearchLabel>();
		SQLiteDatabase db = null;
		Cursor c = null;
		try{
			db = mDb.getWritableDatabase();
			c = db.rawQuery(SELECT_FROM + "searchLabel", null);
			while(c.moveToNext()){
				SearchLabel label = new SearchLabel();
				label.setId(c.getString(c.getColumnIndex("labelID")));
				label.setType(c.getString(c.getColumnIndex("labelType")));
				label.setName(c.getString(c.getColumnIndex("labelName")));
				list.add(label);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			closeCursorAndDB(c, db);
		}
		return list;
	}

}
