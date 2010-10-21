package tice.weibo.Util;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONObject;

public class CommentsData implements Serializable{
	
	public static final long serialVersionUID = 1L;
	public ArrayList<CommentItem> items = null;
	public UserData user = null;
	public JSONObject mJSONObject;
	public String mData = "";
	public String mError = null;
	
	public CommentsData(int count){
		items = new ArrayList<CommentItem>(count);
	}
	
	public CommentsData(){
		items = new ArrayList<CommentItem>();
	}
	
	public CommentItem Get(int i){
		if (i >= items.size() || i < 0 ) return null;
		return items.get(i);
	}
}
