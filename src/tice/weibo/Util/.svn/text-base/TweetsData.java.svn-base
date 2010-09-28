package tice.twitterwalk.Util;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONObject;

public class TweetsData implements Serializable{
	
	public static final long serialVersionUID = 1L;
	public ArrayList<TwitterItem> items = null;
	public UserData user = null;
	public JSONObject mJSONObject;
	public String mData = "";
	public String mError = null;
	
	public TweetsData(int count){
		items = new ArrayList<TwitterItem>(count);
	}
	
	public TweetsData(){
		items = new ArrayList<TwitterItem>();
	}
	
	public TwitterItem Get(int i){
		if (i >= items.size() || i < 0 ) return null;
		return items.get(i);
	}
}