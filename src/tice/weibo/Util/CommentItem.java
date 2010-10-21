package tice.weibo.Util;

public class CommentItem {
    public String mScreenname;
    public String mText;
    public long mTime;
    public long mID;
    
    public CommentItem(){
    	mScreenname = "";
		mText = "";
		mTime = 0l;
		mID = 0l;
    }
    
	public CommentItem(CommentItem obj){
		mScreenname = obj.mScreenname;
		mText = obj.mText;
		mTime = obj.mTime;
		mID = obj.mID;	
	}
	
	public CommentItem(Long id, String screenname, String text, long time){
		mScreenname = screenname;
		mText = text;
		mTime = time;
		mID = id;
	}
}
