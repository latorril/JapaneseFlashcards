package com.latorril.japanese_flashcards;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
 
public class FlashcardDb {
	public static final String KEY_QUESTION = "question";
	public static final String KEY_ANSWER = "answer";
	public static final String KEY_ROWID = "_id";
	private static final String TAG = "FlashcardDb";

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "flashcards";
    private static final int DATABASE_VERSION = 1;
    
    private static final String DATABASE_CREATE =
    	"create table flashcards (_id integer primary key autoincrement, "
    	+ "question text, answer text);";
    
    private final Context context; 
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;
    
    public FlashcardDb(Context ctx) 
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }
    
    private static class DatabaseHelper extends SQLiteOpenHelper {
    	
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        
        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            Log.w(TAG, "Upgrading database from version " + oldVersion 
            		+ " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS flashcards");
            onCreate(db);
        }
    }
    
    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    
    public FlashcardDb open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    public void close()
    {
        DBHelper.close();
    }
    
    public long createFlashcard(String question, String answer) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_QUESTION, question);
        initialValues.put(KEY_ANSWER, answer);

        return db.insert(DATABASE_TABLE, null, initialValues);
    }
    
    public boolean deleteFlashcard(long rowId) 
    {
        return db.delete(DATABASE_TABLE, KEY_ROWID + 
        		"=" + rowId, null) > 0;
    }
    
    public Cursor fetchAllFlashcards() 
    {
        return db.query(DATABASE_TABLE, new String[] {
        		KEY_ROWID, 
        		KEY_QUESTION,
        		KEY_ANSWER}, 
                null, 
                null, 
                null, 
                null, 
                null);
    }
    
    public Cursor fetchFlashcard(long rowId) throws SQLException {

        Cursor mCursor =
            db.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                    KEY_QUESTION, KEY_ANSWER}, KEY_ROWID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    
    public Cursor fetchNextFlashcard(long rowId) throws SQLException {

        Cursor mCursor =
            db.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                    KEY_QUESTION, KEY_ANSWER}, KEY_ROWID + ">" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
        	mCursor.moveToFirst();
        }
        return mCursor;
    }
   
    public Cursor fetchPreviousFlashcard(long rowId) throws SQLException {
    	
    	Cursor mCursor =
    		db.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
    				KEY_QUESTION, KEY_ANSWER}, KEY_ROWID + "<" + rowId, null,
    				null, null, null, null);
    	if (mCursor != null) {
    		mCursor.moveToLast();
    	}
    	return mCursor;
    }
    
    public Cursor fetchRandomFlashcard() throws SQLException {

        Cursor mCursor =
            db.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                    KEY_QUESTION, KEY_ANSWER}, null, null,
                    "RANDOM()", null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
}