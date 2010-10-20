package tice.weibo.Util;

public class Comment {
    public String mScreenname;
    public String mText;
    public long mTime;
    public long mID;
    
    public Comment(){
    	mScreenname = "";
		mText = "";
		mTime = 0l;
		mID = 0l;
    }
    
	public Comment(Comment obj){
		mScreenname = obj.mScreenname;
		mText = obj.mText;
		mTime = obj.mTime;
		mID = obj.mID;	
	}
	
	public Comment(Long id, String screenname, String text, long time){
		mScreenname = screenname;
		mText = text;
		mTime = time;
		mID = id;
	}
}
