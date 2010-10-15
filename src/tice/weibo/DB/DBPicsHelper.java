package tice.weibo.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBPicsHelper extends SQLiteOpenHelper {
	public static final String KEY__ID = "_id";
//    public static final String KEY_SCREENNAME = "screenname";
    public static final String KEY_STATUS_ID = "status_id";
//    public static final String KEY_TIME = "time";
    public static final String KEY_DATA = "data";    

	public static final int COL__ID = 0;
    public static final int COL_STATUS_ID = 1;
    public static final int COL_DATA = 2;    
    
    private static final int DATABASE_VERSION = 82;
    private static final String DATABASE_NAME = "pics";
    private static final String TABLE_NAME = "pics";
    private static final String DATABASE_CREATE =
        "create table " + TABLE_NAME + " (_id integer primary key autoincrement, " +
                "status_id NUMERIC not null, " +
                "data text not null)";
    
    private SQLiteDatabase mDB = null;
	
    DBPicsHelper(Context context ) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mDB = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    synchronized public void CleanDB(){
    	if(mDB == null) return;

    	Cursor c = null;
    	try{
    		c = mDB.query(true, TABLE_NAME, null, null , null, null, null, "time DESC" , null);
	        if (c != null) {
	        	int count = c.getCount();
	        	if(count >= 1000){
	        		c.moveToFirst();
	        		c.moveToPosition(count/3*2);
	        		mDB.delete(TABLE_NAME, "time <" + c.getLong(2), null);
	        	}
	        }
    	}catch(Exception e){
    		mDB.delete(TABLE_NAME, "1", null);
    	}finally{
    		c.close();
    	}
    }

    synchronized public void ClearCache(){
    	if(mDB == null) return;
    	try {
    		mDB.beginTransaction();
    		mDB.delete(TABLE_NAME, "1", null);
    		mDB.setTransactionSuccessful();
    	}finally {
    		mDB.endTransaction();
        }
    }
    
    synchronized public long InsertPic(Long status_id, byte[] data ){
        if(mDB == null) return -1;
        long ret;
    	ContentValues initialValues = new ContentValues();    	
    	initialValues.put(KEY_STATUS_ID, status_id);
    	initialValues.put(KEY_DATA, data);
    	
        Cursor mCursor = mDB.query(true, TABLE_NAME, null, KEY_STATUS_ID + "=" + "\"" + status_id + "\"", null, null, null, null, null);
        int count = mCursor.getCount();
        mCursor.close();

    	mDB.beginTransaction();
        try{
	        if (count == 0){
	    		ret = mDB.insert(TABLE_NAME, null, initialValues);
	    	}else{
	    		ret = mDB.update(TABLE_NAME,initialValues,KEY_STATUS_ID + "=" + "\"" + status_id + "\"" ,null);
	    	}
	        mDB.setTransactionSuccessful();
        }finally{
        	mDB.endTransaction();
        }
        
        return ret;
    }
    
    public Cursor fetchPic(Long status_id){
    	if(mDB == null) return null;
    	Cursor mCursor = mDB.query(TABLE_NAME, null, KEY_STATUS_ID + "=" + "\"" + status_id + "\"", null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        
        return mCursor;
    }

}
