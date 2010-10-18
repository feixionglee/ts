package tice.weibo.DB;

import tice.weibo.Util.TwitterItem;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

public class DBTweetsHelper extends SQLiteOpenHelper {
	

 	final public static int COL__ID = 0;
	final public static int COL_SCREENNAME = 1;
	final public static int COL_TITLE = 2;
	final public static int COL_TEXT = 3;
	final public static int COL_TIME = 4;
	final public static int COL_ID = 5;
	final public static int COL_SOURCE = 6;
	final public static int COL_REPLYID = 7;
	final public static int COL_FAVORITE = 8;
	final public static int COL_FOLLOWING = 9;
	final public static int COL_ICONURL = 10;
	final public static int COL_TYPE = 11;
	final public static int COL_READ = 12;
	final public static int COL_ACCOUNT = 13;
	
	final public static int COL_RETWEETED_SCREENNAME = 14;
	final public static int COL_RETWEETED_TEXT = 15;
	final public static int COL_PICURL = 16;

	final public static int COL_USERNAME = 1;
	final public static int COL_PASSWORD = 2;
	final public static int COL_TOKEN = 3;
	final public static int COL_TOKENSECRET = 4;
	
	public static final String KEY__ID = "_id";
    public static final String KEY_SCREENNAME = "screenname";
    public static final String KEY_TITLE = "title";
    public static final String KEY_TEXT = "text";
    public static final String KEY_TIME = "time";
    public static final String KEY_ID = "id";
    public static final String KEY_SOURCE = "source";
    public static final String KEY_REPLYID = "replyid";
    public static final String KEY_FAVORITE = "favorite";
    public static final String KEY_FOLLOWING = "following";
    public static final String KEY_ICONURI = "iconuri";
    public static final String KEY_TYPE = "type";
    public static final String KEY_READ = "read";
    public static final String KEY_ACCOUNT = "account";
    public static final String KEY_RETWEETED_SCREENNAME = "retweeted_screenname";
    public static final String KEY_RETWEETED_TEXT = "retweeted_text";
    public static final String KEY_PICURI = "picuri";
    
    
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_TOKENSECRET = "tokensecret";
	
	private static final int DATABASE_VERSION = 82;
    private static final String DATABASE_TWEETS = "data";
    public  static final String DATABASE_TABLE = "tweets";
    
    private static final String DATABASE_CREATE =
            "create table tweets" + " (_id integer primary key autoincrement, " +
                    "screenname text not null, " +
                    "title text not null, " +
                    "text text not null, " +
                    "time NUMERIC not null, " +
                    "id NUMERIC not null, " +
                    "source text not null, " +
                    "replyid text not null, " +
                    "favorite INTEGER not null, " +
                    "following INTEGER not null, " +
                    "iconuri text not null," + 
                    "type INTEGER not null," +
                    "read INTEGER not null," +
                    "account text not null," +
                    "retweeted_screenname text not null," +
                    "retweeted_text text not null," +
                    "picuri text not null)";

    private static final String ACCOUNTS_DATABASE_CREATE =
        "create table Accounts" + " (_id integer primary key autoincrement, " +
                "username text not null, " +
                "password text not null," +
                "token text," +
                "tokensecret text)";
	
	private SQLiteDatabase mDB = null;
	
	DBTweetsHelper(Context context) {
        super(context, DATABASE_TWEETS, null, DATABASE_VERSION);
        mDB = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
       	db.execSQL(DATABASE_CREATE);
        db.execSQL(ACCOUNTS_DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tweets");
        db.execSQL("DROP TABLE IF EXISTS Accounts");
        onCreate(db);
    }
    
    synchronized public void CleanDB(){
    	if(mDB == null) return;
    	
    	Cursor c = null;
    	
    	try{
	    	c = mDB.query(true, DATABASE_TABLE, null, null , null, null, null, "time DESC" , null);
	        if (c != null) {
	        	int count = c.getCount();
	        	if(count >= 5000){
	        		c.moveToFirst();
	        		c.moveToPosition(count/3*2);
	        		mDB.delete(DATABASE_TABLE, "time <" + c.getLong(2), null);
	        	}
	            c.close();
	        }
    	}catch (Exception e){
    		mDB.delete(DATABASE_TABLE, "1", null);
    	}finally{
    		c.close();
    	}
    }
    
	public ContentValues decodeContent(TwitterItem item){
		ContentValues values = new ContentValues();
		
		values.put(KEY_SCREENNAME, item.mScreenname);
		values.put(KEY_TITLE, item.mTitle);
		values.put(KEY_TEXT, item.mText);
		values.put(KEY_TIME, item.mTime);
		values.put(KEY_ID, item.mID);
        values.put(KEY_SOURCE, item.mSource);
        values.put(KEY_REPLYID, item.mReplyID);
        values.put(KEY_FAVORITE, item.mFavorite);
        values.put(KEY_FOLLOWING, item.mFollowing);
        values.put(KEY_ICONURI, item.mImageurl);
        values.put(KEY_TYPE, item.mType);
        values.put(KEY_READ, item.mRead);
        values.put(KEY_ACCOUNT, item.mAccount);
        values.put(KEY_RETWEETED_SCREENNAME, item.mRetweeted_Screenname);
        values.put(KEY_RETWEETED_TEXT, item.mRetweeted_Text);
        values.put(KEY_PICURI, item.mPicurl);
        
        return values;
	}
   
	synchronized public void beginTransaction(){
		if(mDB == null) return;
		mDB.beginTransaction();
	}

	synchronized public void endTransaction(){
		if(mDB == null) return;
		mDB.endTransaction();
	}
	
	synchronized public void setTransactionSuccessful(){
		if(mDB == null) return;
		mDB.setTransactionSuccessful();
	}
	
    synchronized public long updatetweet(String account, int type, long id, ContentValues values) {
        if(mDB == null) return -1;
        return mDB.update(DATABASE_TABLE, 
        		values, 
        		KEY_ID + "=\"" + id + "\" AND " +
        		KEY_ACCOUNT + "=\"" + account + "\"", 
        		null);
    }  
	
    synchronized public long createtweet(int type, TwitterItem item) {
        if(mDB == null) return -1;
        return mDB.insert(DATABASE_TABLE, null, decodeContent(item));
    }    

    synchronized public long updatetweet(int type, TwitterItem item) {
        if(mDB == null) return -1;
        
        long ret = -1;
        
        Cursor c =  mDB.query(
		    			DATABASE_TABLE, 
		    			null, 
		    			KEY_ID + "=\"" + item.mID + "\"", 
		    			null, 
		    			null, 
		    			null,
		    			null,
		    			null);
        
        if(c != null){
        	if (c.getCount() == 0){
        		ret = createtweet(type, item);
        	}else{
        		ret = mDB.update(DATABASE_TABLE, 
        				decodeContent(item), 
        				KEY_ID + "=\"" + item.mID + "\"",
        				null);
        	}
        	c.close();
        }
        
        return ret;
    }
    
    synchronized public boolean deleteAll(String account, int type) {
    	if (mDB == null) return false;
        return mDB.delete(DATABASE_TABLE, 
        		KEY_TYPE + "=\"" + type + "\" AND " +
        		KEY_ACCOUNT + "=\"" + account + "\"",
        		null) > 0;
    }

    public Cursor fetchAll(String account, int type, String order, String limit) {
    	if (mDB == null) return null;
    	return mDB.query(
    			DATABASE_TABLE, 
    			null, 
    			KEY_ACCOUNT + "=\"" + account + "\" AND " +
    			KEY_TYPE + "=\"" + type + "\"", 
    			null, 
    			null, 
    			null, 
    			order,
    			limit);
    }
    
    public Cursor QueryTweet(String account, int type, String id){
    	if(mDB == null) return null;

    	if(type >= 0){
	    	return mDB.query(
	    			true, 
	    			DATABASE_TABLE, 
	    			null, 
	    			KEY_ACCOUNT + "=\"" + account + "\" AND " +
	    			KEY_ID + "=" + "\"" + id + "\" AND " +
	    			KEY_TYPE + "=" + "\"" + type + "\"" ,
	    			null, 
	    			null, 
	    			null, 
	    			null, 
	    			null);	
    	}else{
	    	return mDB.query(
	    			true, 
	    			DATABASE_TABLE, 
	    			null, 
	    			KEY_ACCOUNT + "=\"" + account + "\" AND " +
	    			KEY_ID + "=" + "\"" + id + "\"", 
	    			null, 
	    			null, 
	    			null, 
	    			null, 
	    			null);	
    	}
    }
    
    public boolean FindTweet(String account, int type, long id){
    	if(mDB == null) return false;
    	
    	Cursor mCursor = QueryTweet(account, type, String.valueOf(id));
        
    	if(mCursor == null) return false;
    	if (mCursor.getCount() != 0) {
        	mCursor.close();
        	return true;
        }
    	mCursor.close();
        return false;
    }
    
    public long FetchMInID(String account, int type){
    	long retid = 0;
    	Cursor cursor = fetchAll(account, type, " id ASEC", null);
    	if (cursor.getCount() != 0) {
    		cursor.moveToFirst();
   			retid = cursor.getLong(COL_ID);
    	}
    	cursor.close();
    	return retid;
    }
    
    public long FetchMaxID(String account, int type){
    	long retid = 0;
    	Cursor cursor = fetchAll(account, type, " id DESC", null);
    	if (cursor.getCount() != 0) {
    		cursor.moveToFirst();
   			retid = cursor.getLong(COL_ID);
    	}
    	cursor.close();
    	return retid;
    }
    
    public Cursor QueryAccounts(String user){
    	if(mDB == null) return null;

    	if(TextUtils.isEmpty(user) == false){
	    	return mDB.query(
	    			true, 
	    			"Accounts", 
	    			null, 
	    			KEY_USERNAME + "=" + "\"" + user + "\"" ,
	    			null, 
	    			null, 
	    			null, 
	    			null, 
	    			null);	
    	}else{
	    	return mDB.query(
	    			true, 
	    			"Accounts", 
	    			null, 
	    			null, 
	    			null, 
	    			null, 
	    			null, 
	    			null, 
	    			null);	
    	}
    }

    synchronized public long deleteAccount(String user) {
        if(mDB == null) return -1;
        return mDB.delete("Accounts", KEY_USERNAME + "=" + "\"" + user + "\"", null);
    }
    
    synchronized public long updateAccounts(String user, ContentValues initialValues) {
        if(mDB == null) return -1;
        
        long ret = 0;
        Cursor c = QueryAccounts(user);
        
        if(c != null){
        	if(c.getCount() == 0){
        		ret = mDB.insert("Accounts", null, initialValues);
        	}else{
        		ret = mDB.update("Accounts", initialValues, KEY_USERNAME + "=\"" + user + "\"" , null);
        	}
        	c.close();
        }
        
        return ret;
    } 
}