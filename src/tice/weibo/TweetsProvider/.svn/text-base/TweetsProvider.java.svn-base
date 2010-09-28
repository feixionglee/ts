package tice.twitterwalk.TweetsProvider;

import java.util.Date;

import tice.twitterwalk.Util.TwitterItem;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class TweetsProvider extends ContentProvider {

    public static final String PROVIDER_NAME =  "tice.twigee.provider.tweets";
     
	public static final Uri CONTENT_URI =  Uri.parse("content://"+ PROVIDER_NAME + "/tweets");
	public static final Uri CONTENT_IMAGE_URI =  Uri.parse("content://"+ PROVIDER_NAME + "/images");
	
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
	final public static int COL_READ = 11;
	final public static int COL_TYPE = 12; 
	final public static int COL_TIMESOURCE = 13;
     
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
    public static final String KEY_READ = "read";
    public static final String KEY_TYPE = "type";
    public static final String KEY_TIMESOURCE = "timesource";
    
    public static final String KEY_DATA = "data";
    public static final String KEY_ROWID = "_rowid";

    private static final int QUERY_TWEETS = 1;
    private static final int QUERY_IMAGES = 2;
       
    private static final UriMatcher uriMatcher;
    static{
       uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
       uriMatcher.addURI(PROVIDER_NAME, "tweets", QUERY_TWEETS);
       uriMatcher.addURI(PROVIDER_NAME, "images", QUERY_IMAGES);
    }
    
    private DatabaseHelper mDbHelper = null;;
    private SQLiteDatabase mDb = null;
    
    private static final String DATABASE_NAME = "data";
    public  static final String DATABASE_TABLE = "tweets";
    private static final int DATABASE_VERSION = 70;
    
    
    private static final String DATABASE_CREATE =
            "create table " + "%s" + " (_id integer primary key autoincrement, " +
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
                    "read INTEGER not null," +
                    "type INTEGER not null," +
                    "timesource text not null)";

    private static final String IMAGE_DATABASE_CREATE =
        	"create table Images" + " (_id integer primary key autoincrement, " +
        			"screenname text not null, " +
        			"time NUMERIC not null, " +
        			"data text not null)";

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        	String sql;
            sql = String.format(DATABASE_CREATE, DATABASE_TABLE);
            db.execSQL(sql);
            db.execSQL(IMAGE_DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS tweets");
            db.execSQL("DROP TABLE IF EXISTS Images");
            onCreate(db);
        }
    }

	static public ContentValues decodeContent(TwitterItem item){
		ContentValues values = new ContentValues();
		
		values.put(TweetsProvider.KEY_SCREENNAME, item.mScreenname);
		values.put(TweetsProvider.KEY_TITLE, item.mTitle);
		values.put(TweetsProvider.KEY_TEXT, item.mText);
		values.put(TweetsProvider.KEY_TIME, item.mTime);
		values.put(TweetsProvider.KEY_ID, item.mID);
        values.put(TweetsProvider.KEY_SOURCE, item.mSource);
        values.put(TweetsProvider.KEY_REPLYID, item.mReplyID);
        values.put(TweetsProvider.KEY_FAVORITE, item.mFavorite);
        values.put(TweetsProvider.KEY_FOLLOWING, item.mFollowing);
        values.put(TweetsProvider.KEY_ICONURI, item.mImageurl);
        values.put(TweetsProvider.KEY_READ, item.mRead);
        values.put(TweetsProvider.KEY_TYPE, item.mType);
        values.put(TweetsProvider.KEY_TIMESOURCE, item.mTimeSource);
        
        return values;
	}
    
	@Override
	public boolean onCreate() {

        mDbHelper = new DatabaseHelper(getContext());
        mDb = mDbHelper.getWritableDatabase();

		return (mDb == null) ? false : true;
	}

	@Override
	public String getType(Uri arg0) {
		
	      switch (uriMatcher.match(arg0)){
	         //---get all books---
	      case QUERY_TWEETS:
	    	  return "vnd.android.cursor.dir/tice.twigee.provider.tweets ";
	      case QUERY_IMAGES:
	    	  return "vnd.android.cursor.dir/tice.twigee.provider.images ";
	      default:
	          throw new IllegalArgumentException("Unsupported URI: " + arg0);        
	      }  
	}
    
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		
        if(mDb == null) return -1;
        
		int count=0;
		switch (uriMatcher.match(uri)){
			case QUERY_TWEETS:
				count = mDb.delete(
	               DATABASE_TABLE,
	               selection, 
	               selectionArgs);
	            break;
			 case QUERY_IMAGES:
				count = mDb.delete(
		              "Images",
		              selection, 
		              selectionArgs);
		        break;
	         default: throw new IllegalArgumentException("Unknown URI " + uri);    
	      }   
		
	      getContext().getContentResolver().notifyChange(uri, null);
	      return count;   
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		
        if(mDb == null) return null;
		
		String tablename ="";
		switch (uriMatcher.match(uri)){
			case QUERY_TWEETS:
	        	tablename = DATABASE_TABLE;
	        	break;
	        case QUERY_IMAGES:
		    	tablename = "Images";
		        break;
	        default: throw new IllegalArgumentException("Unknown URI " + uri);    
		}
		
		@SuppressWarnings("unused")
		long rowID = mDb.insert(tablename, "", values);
		
		getContext().getContentResolver().notifyChange(uri, null);	    	           
		
		return uri;                
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        
		if(mDb == null) return null;
        
		String tablename ="";
		switch (uriMatcher.match(uri)){
			case QUERY_TWEETS:
			{
	        	tablename = DATABASE_TABLE;
	    		if (sortOrder == null || sortOrder == ""){
	    			sortOrder = KEY_ID + " DESC";
	    		}
	    		SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder(); 
	    		sqlBuilder.setTables(tablename);
	    		Cursor c = sqlBuilder.query(mDb, projection, selection, selectionArgs, null, null, sortOrder);
	    		c.setNotificationUri(getContext().getContentResolver(), uri);
	    		return c;
			}
	        case QUERY_IMAGES:
	        {
		    	tablename = "Images";
		    	return fetchImage(selection);
	        }
	        default: throw new IllegalArgumentException("Unknown URI " + uri);    
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        
		if(mDb == null) return -1; 
		
		int count = 0;
	     switch (uriMatcher.match(uri)){
	     	case QUERY_TWEETS:
	            count = mDb.update(
	               DATABASE_TABLE, 
	               values,
	               selection, 
	               selectionArgs);
	            break;
	     	case QUERY_IMAGES:
	            count = mDb.update(
	               "Images", 
	               values,
	               selection, 
	               selectionArgs);
	            break;
	         default: throw new IllegalArgumentException(
	            "Unknown URI " + uri);    
	      }       
	     
	     getContext().getContentResolver().notifyChange(uri, null);
	     return count;
	}

    synchronized public long InsertImage(String screenmane, byte[] data ){
        if(mDb == null) return -1;
        long ret;
        Date now = new Date();
    	ContentValues initialValues = new ContentValues();    	
    	initialValues.put(KEY_SCREENNAME, screenmane);
    	initialValues.put(KEY_TIME, now.getTime());
    	initialValues.put(KEY_DATA, data);
    	
        Cursor mCursor = mDb.query(true, "Images", null, KEY_SCREENNAME + "=" + "\"" + screenmane + "\"", null, null, null, null, null);
        int count = mCursor.getCount();
        mCursor.close();

        if (count == 0){
    		ret = mDb.insert("Images", null, initialValues);
    	}else{
    		ret = mDb.update("Images",initialValues,KEY_SCREENNAME + "=" + "\"" + screenmane + "\"" ,null);
    	}
        
        return ret;
    }
    
    public Cursor fetchImage(String screenmane) throws SQLException {
    	if(mDb == null) return null;
    	Cursor mCursor = mDb.query(true, "Images", null, KEY_SCREENNAME + "=" + "\"" + screenmane + "\"", null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
}
