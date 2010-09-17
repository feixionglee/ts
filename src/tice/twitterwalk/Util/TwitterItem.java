package tice.twitterwalk.Util;

import tice.twitterwalk.HttpClient.TwitterClient;
import tice.twitterwalk.List.TweetsListActivity;
import android.graphics.Bitmap;

public class TwitterItem{
    public String mScreenname;
    public String mTitle;
    public String mText;
    public long mTime;
    public String mSource;
    public String mTimeSource;
    public long mID;
    public String mReplyID;
    public String mImageurl;
    public boolean mFavorite;
    public boolean mFollowing;
    public boolean mLoading = false;
    public int mRead = 0;
    public String mAccount;
    public Bitmap mImage;
    public String mPicurl;
    public Bitmap mPic;
    public int mType = TwitterClient.HOME_HOME;
    //public Bitmap mImage;

    public TwitterItem(int read, String screenname, String title, String text, long time, String source, long id, String replyid, boolean fav, boolean following, String iconuri, int type, String account, String picurl){
        try {
	    	mScreenname = screenname;
	        mTitle = title;
	        mTime = time;
	        mSource = source;
			mText = text;
	        mID = id;
	        mReplyID = replyid;
	        mFavorite = fav;
	        mFollowing = following;
	        mImageurl = iconuri;
	        mImage = null;
	        mRead = read;
	        mType = type;
	        mAccount = account;
	        mPicurl = picurl;
	        mPic = null;

	    	mTimeSource = TweetsListActivity.CreateTimeSource(mTime, mSource);

		} catch (Exception e) {}
    }


    public TwitterItem(TwitterItem obj){
        try {
	    	mScreenname = obj.mScreenname;
	        mTitle = obj.mTitle;
	        mTime = obj.mTime;
	        mSource = obj.mSource;
			mText = obj.mText;
	        mID = obj.mID;
	        mReplyID = obj.mReplyID;
	        mFavorite = obj.mFavorite;
	        mFollowing = obj.mFollowing;
	        mImageurl = obj.mImageurl;
	        mImage = null;
	        mRead = obj.mRead;
	        mTimeSource = obj.mTimeSource;
	        mType = obj.mType;
	        mAccount = obj.mAccount;
	        mPicurl = obj.mPicurl;
	        mPic = null;
		} catch (Exception e) {}
    }

    public TwitterItem(){
        try {
	    	mScreenname = "";
	        mTitle = "";
	        mTime = 0;
	        mSource = "";
			mText = "";
	        mID = 0;
	        mReplyID = "";
	        mFavorite = false;
	        mFollowing = false;
	        mImageurl = "";
	        mImage = null;
	        mRead = 0;
	        mTimeSource = "";
	        mType = TwitterClient.HOME_HOME;
	        mAccount = "";
	        mPicurl = "";
	        mPic = null;
		} catch (Exception e) {}
    }
}