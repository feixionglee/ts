package tice.weibo.Activities;

import java.util.Date;

import tice.weibo.App;
import tice.weibo.R;
import tice.weibo.DB.DBTweetsHelper;
import tice.weibo.HttpClient.TwitterClient;
import tice.weibo.List.TweetsListActivity;
import tice.weibo.Util.TweetsData;
import tice.weibo.Util.TwitterItem;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DetailWeiboActivity extends TweetsListActivity {
	private TwitterItem mItem;
	TweetsListActivity mCtx;
	
	
	public class ViewHolder {
		TextView title;
		TextView text;
		TextView retweeted_text;
		TextView time;
		TextView source;
//		TextView timesource;
		ImageView icon;
		//IconImageView icon_right;
		LinearLayout itemline;
		LinearLayout imagelayout;
		//LinearLayout imagelayout_right;
		ImageView unread;
		ProgressBar progressbar;
		ProgressBar retweeted_progressbar;
		ImageView favorite;
		ImageView conversation;
		ImageView pic;
	}
	
    static final String ACTION = "tice.weibo.Activities.DetailWeiboActivity";
    private static final String EXTRA_ITEM = "tice.weibo.Util.TwittterItem";
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		mCtx = (TweetsListActivity)this;
		_Handler = mHandler;
		
		super.onCreate(savedInstanceState);
		
		mItem = getTweet();
		
		setContentView(R.layout.detailweibo);
		setupViews();
	}
	
    private final Handler mHandler = new Handler() {
        @Override
         public void handleMessage(final Message msg) {
        	defaulthandleMessage(msg);
//        	processMessage(msg);
        }
    }; 
	
	public static void show(Context context, TwitterItem item) {
		final Intent intent = new Intent(ACTION);
        intent.putExtra(EXTRA_ITEM, item.mID);
        context.startActivity(intent);
	}
	
	private void setupViews() {
		ViewHolder holder= new ViewHolder();
		
		InitButtons();
		
		holder.text = (TextView) findViewById(R.id.tweet_message);
		holder.text.setText(mItem.mText);
		holder.text.setVisibility(View.VISIBLE);
		
		holder.time = (TextView) findViewById(R.id.tweet_updated);
		holder.time.setText(new Date(mItem.mTime).toLocaleString());
		holder.time.setVisibility(View.VISIBLE);
		
		holder.source = (TextView) findViewById(R.id.tweet_via);
		holder.source.setText(mItem.mSource);
		holder.source.setVisibility(View.VISIBLE);
		
		
		holder.icon = (ImageView) findViewById(R.id.tweet_profile_preview);
		
       	if(mItem.mImage != null) {
       		holder.icon = (ImageView) findViewById(R.id.tweet_profile_preview);       		
       		holder.icon.setImageBitmap(mItem.mImage);
       		holder.icon.setVisibility(View.VISIBLE);
       	} 
       	
		if(mItem.mPicurl != null) {
			mItem.mPic = _App._twitter.LoadPic(_Handler, mItem.mID, mItem.mPicurl);	
       		
			if(mItem.mRetweeted_Text.length() > 0){
				holder.pic = (ImageView) findViewById(R.id.tweet_upload_pic2);
			}else{
				holder.pic = (ImageView) findViewById(R.id.tweet_upload_pic);
			}
       		holder.pic.setImageBitmap(mItem.mPic);
       		holder.pic.setVisibility(View.VISIBLE);
       	}
       	
       	holder.retweeted_text = (TextView) findViewById(R.id.tweet_oriTxt);
       	holder.retweeted_text.setText(mItem.mRetweeted_Text);
       	if (mItem.mRetweeted_Text.length() > 0) findViewById(R.id.src_text_block).setVisibility(View.VISIBLE);
	}
	
    public void InitButtons(){
    	_Statuspanel = (LinearLayout)findViewById(R.id.detailStatusLayout);
    	View.inflate(this.getBaseContext(),R.layout.home_statusbar, _Statuspanel);
    	
    	TextView back = (TextView)findViewById(R.id.tvReturn);
    	TextView retweet = (TextView)findViewById(R.id.tvForward);
    	TextView reply =  (TextView)findViewById(R.id.reply);
    	
    	TextView destory =  (TextView)findViewById(R.id.delete);
    	TextView favorite = (TextView)findViewById(R.id.favorite);
    	TextView directmsg = (TextView)findViewById(R.id.direcrmsg);
    	
    	if(back != null){
    		back.setOnClickListener(new OnClickListener(){
    			public void onClick(View v){
    				finish();	
    			}
    		});
    	}
    	
    	if(reply != null){
	    	reply.setOnClickListener(new OnClickListener(){
				public void onClick(View v) {
					String reply="";
					EditText edit = (EditText)findViewById(R.id.EditText);
					if(_ActivityType == TwitterClient.HOME_DIRECT){
						_InputType = INPUT_DIRECT;
						reply = "";
						edit.setText(reply);
						edit.setSelection(reply.length());
		 				PanelAnimationOn(false, _Statuspanel);
//						PanelAnimationOff(false, _Toolbarpanel);
					}else{
						doReply();
					}
				}
	    	});
    	}

    	if(retweet != null){
    		if (_Statuspanel == null) return;
	    	retweet.setOnClickListener(new OnClickListener(){
				public void onClick(View v) {
					String retweet="";
					EditText edit = (EditText)findViewById(R.id.EditText);
					TwitterItem item = _Items.Get(_CurrentThread);
					_InputType = INPUT_RETWEET;
					if (mItem != null) retweet = String.format("RT @%s: %s ", mItem.mScreenname, mItem.mText);
	 				edit.setText(retweet);
					PanelAnimationOn(false, _Statuspanel);
//					PanelAnimationOff(false, _Toolbarpanel);
				}
	    	});
    	}

    	if(destory != null){
        	destory.setOnClickListener(new OnClickListener(){
    			public void onClick(View v) {
    				if(_CurrentThread == -1) return;
    				try{
	    	            new AlertDialog.Builder(_Context)
	    	            .setTitle("Delete confirmation")
	    	            .setMessage("Are you sure want to delete it?")
	    	            .setNegativeButton("No", new DialogInterface.OnClickListener() {
	    	                public void onClick(DialogInterface dialog, int whichButton) {
	    	                }
	    	            })
	    	            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	    	                public void onClick(DialogInterface dialog, int whichButton) {
	    	                	TwitterItem item = _Items.Get(_CurrentThread);
	    	                	if(item != null){
		    	                	long id = item.mID;
		    	                	setMyProgressBarVisibility(true);
		    	    				PanelAnimationOff(false, _Statuspanel);
//		    	    				PanelAnimationOff(false, _Toolbarpanel);
		    	    				if (_App._twitter != null) _App._twitter.Post_destory(_Handler, _ActivityType, id);
	    	                	}
	    	                }
	    	            })
	    	            .show();
    				}catch (Exception err){}
    			}
        	});
    	}
    	
    	if(favorite != null){
    		favorite.setOnClickListener(new OnClickListener(){
    			public void onClick(View v) {
    				TwitterItem item = _Items.Get(_CurrentThread);
    				setMyProgressBarVisibility(true);
    	    		PanelAnimationOff(false, _Statuspanel);
//    	    		PanelAnimationOff(false, _Toolbarpanel);
    				if(item != null){
    					long id = item.mID;
	    	            if(item.mFavorite == false){
	    	            	if (_App._twitter != null) _App._twitter.Post_favorites_create(_Handler, id);
	    	            }else{
	    	            	try{
		        	            new AlertDialog.Builder(_Context)
		        	            .setTitle("Delete confirmation")
		        	            .setMessage("Are you sure want to unfavorite it?")
		        	            .setNegativeButton("No", new DialogInterface.OnClickListener() {
		        	                public void onClick(DialogInterface dialog, int whichButton) {
		        	                }
		        	            })
		        	            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		        	                public void onClick(DialogInterface dialog, int whichButton) {
		        	                	TwitterItem item = _Items.Get(_CurrentThread);
		        	                	if(item != null){
			        	                	long id = item.mID;
			        	                	if (_App._twitter != null) _App._twitter.Post_destory(_Handler, TwitterClient.HOME_FAVORITES, id);
		        	                	}
		        	                }
		        	            })
		        	            .show();
	    	            	}catch (Exception err){}
	    	            }
    				}
    			}
        	});
    	}

    	if(directmsg != null){
    		directmsg.setOnClickListener(new OnClickListener(){
    			public void onClick(View v) {
					TwitterItem item = _Items.Get(_CurrentThread);
					if(item != null){
	    				String receiver = item.mScreenname;
	    		    	Intent i = new Intent(_Context, DirectActivity.class);
	    		    	i.putExtra("receiver", receiver);
	    		    	startActivityForResult(i, APP_CHAINCLOSE);
					}
    			}
        	});
    	}
    	
    	super.InitButtons();
    }
	
	public TwitterItem getTweet(){
        final Intent intent = getIntent();
        final Bundle extras = intent.getExtras();
        
		TwitterItem item = null;
		long id ;
        if (extras != null) {
        	id = extras.getLong(EXTRA_ITEM);
        	item = ReadStatuses((new Long(id)).toString());
        }

        return item;
	}
		
    private TwitterItem ReadStatuses(String id){
    	
    	Cursor c = _App._DbHelper.QueryTweet(_App._Username, -1, id);
    	
    	if(c != null){
    		if(c.getCount() != 0){
    			c.moveToFirst();
    			
    			Bundle b = new Bundle();
    			TweetsData data = new TweetsData();
    			TwitterItem item = new TwitterItem();

				item.mScreenname = c.getString(DBTweetsHelper.COL_SCREENNAME);
				item.mTitle = c.getString(DBTweetsHelper.COL_TITLE);
				item.mText = c.getString(DBTweetsHelper.COL_TEXT);
				item.mTime = c.getLong(DBTweetsHelper.COL_TIME);
				item.mID = c.getLong(DBTweetsHelper.COL_ID);
				item.mSource = c.getString(DBTweetsHelper.COL_SOURCE);
				item.mReplyID = c.getString(DBTweetsHelper.COL_REPLYID);
				item.mFavorite = (c.getInt(DBTweetsHelper.COL_FAVORITE) == 1)? true : false;
				item.mFollowing = (c.getInt(DBTweetsHelper.COL_FOLLOWING) == 1)? true : false;
				item.mRead = c.getInt(DBTweetsHelper.COL_READ);
				item.mImageurl = c.getString(DBTweetsHelper.COL_ICONURL);
				
				item.mRetweeted_Screenname = c.getString(DBTweetsHelper.COL_RETWEETED_SCREENNAME);
				item.mRetweeted_Text = c.getString(DBTweetsHelper.COL_RETWEETED_TEXT);
				
				if(item.mScreenname.length() != 0 && _App._twitter != null){
	            	item.mImage = _App._twitter.LoadIcon(_Handler, item.mScreenname, item.mImageurl);
	           	}
				if(item.mScreenname.length() != 0 && item.mPicurl.length() != 0 && _App._twitter != null){
	        		item.mPic = _App._twitter.LoadPic(_Handler, item.mID, item.mPicurl);
	        	}
    			
    			data.items.add(item);
 				b.putSerializable(TwitterClient.KEY, data);
 				TwitterClient.SendMessage(mHandler, TwitterClient.HTTP_STATUSES_SHOW, b);
    			
 				c.close();
    			return item;
    		}
    		c.close();
    	}
    	
    	return null;
    	
//    	if (_App._twitter != null) _App._twitter.Get_statuses_show(mHandler, id);
    }
    

}
