package tice.weibo.List;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

import tice.weibo.App;
import tice.weibo.R;
import tice.weibo.Activities.CommentActivity;
import tice.weibo.HttpClient.TwitterClient;
import tice.weibo.List.ItemAdapter.ViewHolder;
import tice.weibo.Util.CommentItem;
import tice.weibo.Util.CommentItem;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

public class CommentAdapter extends BaseAdapter {
	public App _App = null;

	public static Pattern p1 = Pattern.compile("https?://?((([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?)+)",Pattern.CASE_INSENSITIVE);
	public static Pattern p2 = Pattern.compile("@[a-zA-Z0-9_]+", Pattern.CASE_INSENSITIVE);
	public static Pattern p3 = Pattern.compile("#[a-zA-Z0-9_]+( |$)", Pattern.CASE_INSENSITIVE);

	private static int IMAGE_CACHE_SIZE = 12;

    public class ViewHolder {
    	TextView screenname;
    	TextView text;
    	TextView time;   	
    }

    boolean mInRefresh = false;
    Bitmap mFav, mUnFav, mBlank, mUnRead, mConversation;
    CommentActivity mCtx;
	private LayoutInflater mInflater;
	private ArrayList<CommentItem> mItem = new ArrayList<CommentItem>();
	private int mListbackground;
	private int mListbackgroundat;
	private int mListbackgroundmy;
	private int mListbackgroundselset;
	private int mtextviewcolor;
	private int mtextviewcolorselect;
	private int mScrollState;

    public CommentAdapter(Context context) {

    	_App = (App)context.getApplicationContext();

        mInflater = LayoutInflater.from(context);
        mCtx = (CommentActivity)context;

	    TypedArray atts = context.obtainStyledAttributes(new int []
	                                  {R.attr.ListViewBackground,	R.attr.ListViewBackgroundAt,
	                                   R.attr.ListViewBackgroundMy, R.attr.ListViewBackgroundSelect,
	                                   R.attr.TextViewColor, R.attr.TextViewColorSelect});

	    mtextviewcolor = atts.getColor(4, 0xff313031);
	    mtextviewcolorselect = atts.getColor(5, 0xff313031);

	    atts.recycle();
    }



    public int getCount() {
        return mItem.size();
    }

    public void Remove(int i) {
    	if (i >= mItem.size() && i < 0 ) return;
        mItem.remove(i);
        notifyDataSetChanged();
    }

    public void RemoveAll() {
        mItem.clear();
        notifyDataSetChanged();
    }

    public void Clear(){
    	mItem.clear();
    	notifyDataSetChanged();
    }

    public CommentItem Get(int i){
    	if (i >= mItem.size() || i < 0 ) return null;
    	return mItem.get(i);
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    synchronized public View getView(int position, View convertView, ViewGroup parent) {
    	if(position >= mItem.size()) return convertView;

        ViewHolder holder;
        CommentItem item = mItem.get(position);
                
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.cmtitemview, null);

            holder = new ViewHolder();

			
            holder.screenname = (TextView) convertView.findViewById(R.id.tvItemCmtNickname);
            holder.text = (TextView) convertView.findViewById(R.id.tvItemCmtContent);
            holder.time = (TextView) convertView.findViewById(R.id.tvItemCmtDate);

           	holder.screenname.setTextSize(_App._Fontsize);
           	holder.text.setTextSize(_App._Fontsize);
           	holder.time.setTextSize(_App._Fontsize - 4);

        	//holder.text.setLinksClickable(false);
        	//holder.text.setLinkTextColor(0xaa0000ff);

        	convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.screenname.setTextColor(mtextviewcolor);
      	holder.time.setTextColor(mtextviewcolor);
       	holder.text.setTextColor(mtextviewcolor);


    	SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
		String ret = String.format("%s", df2.format(new Date(item.mTime)));
		
       	holder.screenname.setText(item.mScreenname);
      	holder.time.setText(ret);
       	holder.text.setText(item.mText);
       	
       	if (item.mScreenname.length() == 0){
        	holder.screenname.setText("");
        	holder.time.setText("");
        	holder.text.setText(item.mText);
        }

        return convertView;
    }

    public void SetScrollState(int state){
    	mScrollState = state;
    }

    public void ReplaceThread(int index, CommentItem obj,int type){
    	if(index >= mItem.size()) return;

    	CommentItem item = mItem.get(index);

    	item.mScreenname = obj.mScreenname;
    	item.mTime = obj.mTime;
    	item.mText = obj.mText;
    	item.mID = obj.mID;

    }

    public void addThread(int index, CommentItem obj, int addtype, int type){

    	int count = mItem.size();
    	if(count >= TwitterClient.MAX_TWEETS_COUNT) return;

    	if(count != 0){
			if(mItem.get(count - 1).mID == obj.mID && obj.mID != 0){
				return;
			}
		}

    	if(addtype == TweetsListActivity.ADD_TYPE_APPEND){
    		mItem.add(new CommentItem(obj));
    	}else{
    		if(index >= mItem.size() ) index = mItem.size();
    		mItem.add(index, new CommentItem(obj));
    	}

    	notifyDataSetChanged();
    }
    
    
    public void addThread(long id, String screenname, String text, long time){

    	int count = mItem.size();
    	if(count >= TwitterClient.MAX_TWEETS_COUNT) return;

		mItem.add(new CommentItem(id, screenname, text, time));

    	notifyDataSetChanged();
    }

    public void SetLoadingItem(){
    	int count = mItem.size();
    	if(count == 0) return;
    	if(count >= TwitterClient.MAX_TWEETS_COUNT) return;
    	CommentItem item = mItem.get(count - 1);
		if (item.mScreenname.length() == 0){
			item.mText = "more";
//			item.mLoading = false;
		}
    }

    public void SetStartLoadingItem(){
    	int count = mItem.size();
    	if(count == 0) return;
    	if(count >= TwitterClient.MAX_TWEETS_COUNT) return;
    	CommentItem item = mItem.get(count - 1);
    	if (item.mScreenname.length() == 0){
			item.mText = "  Loading more tweets ...";
//			item.mLoading = true;
		}
    	notifyDataSetChanged();
    }
}
