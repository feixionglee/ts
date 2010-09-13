package tice.twitterwalk.DB;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBImagesHelper extends SQLiteOpenHelper {
   	
	public static final String KEY__ID = "_id";
    public static final String KEY_SCREENNAME = "screenname";
    public static final String KEY_TIME = "time";
    public static final String KEY_DATA = "data";    

	public static final int COL__ID = 0;
    public static final int COL_SCREENNAME = 1;
    public static final int COL_TIME = 2;
    public static final int COL_DATA = 3;    
    
    private static final int DATABASE_VERSION = 82;
    private static final String DATABASE_IMAGES = "images";
    private static final String IMAGE_DATABASE_CREATE =
        "create table Images" + " (_id integer primary key autoincrement, " +
                "screenname text not null, " +
                "time NUMERIC not null, " +
                "data text not null)";
    
    private SQLiteDatabase mDB = null;
	
	DBImagesHelper(Context context ) {
        super(context, DATABASE_IMAGES, null, DATABASE_VERSION);
        mDB = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(IMAGE_DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Images");
        onCreate(db);
    }

    synchronized public void CleanDB(){
    	if(mDB == null) return;

    	Cursor c = null;
    	try{
    		c = mDB.query(true, "Images", null, null , null, null, null, "time DESC" , null);
	        if (c != null) {
	        	int count = c.getCount();
	        	if(count >= 1000){
	        		c.moveToFirst();
	        		c.moveToPosition(count/3*2);
	        		mDB.delete("Images", "time <" + c.getLong(2), null);
	        	}
	        }
    	}catch(Exception e){
    		mDB.delete("Images", "1", null);
    	}finally{
    		c.close();
    	}
    }

    synchronized public void ClearCache(){
    	if(mDB == null) return;
    	try {
    		mDB.beginTransaction();
    		mDB.delete("Images", "1", null);
    		mDB.setTransactionSuccessful();
    	}finally {
    		mDB.endTransaction();
        }
    }
    
    synchronized public long InsertImage(String screenmane, byte[] data ){
        if(mDB == null) return -1;
        long ret;
        Date now = new Date();
    	ContentValues initialValues = new ContentValues();    	
    	initialValues.put(KEY_SCREENNAME, screenmane);
    	initialValues.put(KEY_TIME, now.getTime());
    	initialValues.put(KEY_DATA, data);
    	
        Cursor mCursor = mDB.query(true, "Images", null, KEY_SCREENNAME + "=" + "\"" + screenmane + "\"", null, null, null, null, null);
        int count = mCursor.getCount();
        mCursor.close();

    	mDB.beginTransaction();
        try{
	        if (count == 0){
	    		ret = mDB.insert("Images", null, initialValues);
	    	}else{
	    		ret = mDB.update("Images",initialValues,KEY_SCREENNAME + "=" + "\"" + screenmane + "\"" ,null);
	    	}
	        mDB.setTransactionSuccessful();
        }finally{
        	mDB.endTransaction();
        }
        
        return ret;
    }
    
    public Cursor fetchImage(String screenmane){
    	if(mDB == null) return null;
    	Cursor mCursor = mDB.query(true, "Images", null, KEY_SCREENNAME + "=" + "\"" + screenmane + "\"", null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        
        return mCursor;
    }
}
