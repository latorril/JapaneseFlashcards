package com.latorril.japanese_flashcards;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

 
public class FlashcardDb {
	public static final String KEY_QUESTION = "question";
	public static final String KEY_ANSWER = "answer";
	public static final String KEY_ROWID = "_id";
	
	private static final String TAG = "NotesDbAdapter";
	private SQLiteDatabase mDb;

    private static final String DATABASE_CREATE =
        "create table notes (_id integer primary key autoincrement, "
        + "question text not null, answer text not null);";

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "flashcards";
    private static final int DATABASE_VERSION = 2;
    private final Context mCtx;
	private DatabaseHelper mDbHelper;
    
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS notes");
            onCreate(db);
        }
    }
    
    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public FlashcardDb(Context ctx) {
        this.mCtx = ctx;
    }
    
    public FlashcardDb open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }
    
    public long createFlashcard(String question, String answer) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_QUESTION, question);
        initialValues.put(KEY_ANSWER, answer);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }
    
    //offset
    
    public boolean deleteAllFlashcards() {

        return mDb.delete(DATABASE_TABLE, null, null)>0;
    }
    
    public Cursor fetchFlashcard(long rowId) throws SQLException {

        Cursor mCursor =
            mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                    KEY_QUESTION, KEY_ANSWER}, KEY_ROWID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
   /*
   public FlashcardDb(Context context) {
      this.context = context;
      OpenHelper openHelper = new OpenHelper(this.context);
      this.db = openHelper.getWritableDatabase();
      this.insertStmt = this.db.compileStatement(INSERT);
   }
 
   public long insert(String name) {
      this.insertStmt.bindString(1, name);
      return this.insertStmt.executeInsert();
   }
 
   public void deleteAll() {
      this.db.delete(TABLE_NAME, null, null);
   }
 
   public List<String> selectAll() {
      List<String> list = new ArrayList<String>();
      Cursor cursor = this.db.query(TABLE_NAME, new String[] { "name" }, 
        null, null, null, null, "name desc");
      if (cursor.moveToFirst()) {
         do {
            list.add(cursor.getString(0)); 
         } while (cursor.moveToNext());
      }
      if (cursor != null && !cursor.isClosed()) {
         cursor.close();
      }
      return list;
   }
 
   private static class OpenHelper extends SQLiteOpenHelper {
 
      OpenHelper(Context context) {
         super(context, DATABASE_NAME, null, DATABASE_VERSION);
      }
 
      @Override
      public void onCreate(SQLiteDatabase db) {
         db.execSQL("CREATE TABLE " + TABLE_NAME + 
          "(id INTEGER PRIMARY KEY, name TEXT)");
      }
 
      @Override
      public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
         Log.w("Example", "Upgrading database, this will drop tables and recreate.");
         db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
         onCreate(db);
      }
   }
   */
}
