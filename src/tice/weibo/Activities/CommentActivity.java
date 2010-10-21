package tice.weibo.Activities;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ListAdapter;
import android.widget.ListView;
import tice.weibo.App;
import tice.weibo.R;
import tice.weibo.HttpClient.TwitterClient;
import tice.weibo.List.CommentAdapter;
import tice.weibo.List.TweetsListActivity;
import tice.weibo.Util.CommentItem;
import tice.weibo.Util.CommentsData;
import tice.weibo.Util.ProgressData;
import tice.weibo.Util.TwitterItem;

public class CommentActivity extends Activity {

	public App _App = null;
	
	public final static int ADD_TYPE_APPEND = 0;
	public final static int ADD_TYPE_INSERT = 1;
	public final static int ADD_TYPE_REPLACE = 2;
	protected int mAddType = ADD_TYPE_INSERT;
	
	private TwitterItem tItem;
	private CommentItem cItem;
	CommentAdapter _adapter;
//	TweetsListActivity mCtx;
	
	static final String ACTION = "tice.weibo.Activities.CommentActivity";
	private static final String EXTRA_ITEM = "tice.weibo.Util.TwittterItem";
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		_App = (App)getApplicationContext();
//		mCtx = (TweetsListActivity)this;
//		_Handler = mHandler;
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.commentblog);
		
				
		ListView _list = (ListView)findViewById(R.id.lvCmt);
		
		_adapter = new CommentAdapter(this);
		
		_list.setAdapter(_adapter);
	
	}
	
	static void show(Context context, TwitterItem item){
		final Intent intent = new Intent(ACTION);
        intent.putExtra(EXTRA_ITEM, item.mID);
        context.startActivity(intent);
	}
	
	
	public void loadCommentsFromWeibo(long status_id){
		
	}
	
	private final Handler mHandler = new Handler() {
        @Override
         public void handleMessage(final Message msg) {
//        	defaulthandleMessage(msg);
        	processMessage(msg);
        }
    };
    
    public void processMessage(Message msg) {
    	switch (msg.what){

    	case TwitterClient.HTTP_COMMENTS_TIMELINE:
//    		_Page = 1;
//    		_Refresh = false;
//    		_LastID = 0;
//    		_PrevID = Long.MAX_VALUE;
    		defaultUpdateListViewThread(msg.getData(),mAddType, true );
    		break;
    	}
	}
    
	public void defaultUpdateListViewProcessFrontEnd(ProgressData tweets){

		ArrayList<CommentItem> items = tweets.citems;

 		if(items.size() == 0) {
 			_adapter.SetLoadingItem();
 			TwitterClient.SendMessage(mHandler, TwitterClient.UI_REFRESHVIEWS, null);
 			return;
 		}

 		int index;
 		int count;

 		int addtype = tweets.addtype;
 		int mincount = Math.min(items.size(), _adapter.getCount());
 		count = items.size();
 		CommentItem t;

 		if(addtype == ADD_TYPE_INSERT){
	 		for (index = 0; index < count; index++){
	 			t = items.get(index);
	 			if(t == null) continue;
	 			_adapter.addThread(index, t, addtype, 1);
	 		}

	 		if(_adapter.getCount() >= TwitterClient.MAX_TWEETS_COUNT){
	 			for (index = _adapter.getCount() - 1; index >= _App._Tweetscount; index = _adapter.getCount() - 1){
	 				_adapter.Remove(index);
	 			}
	 		}
 		}else if (addtype == ADD_TYPE_REPLACE){
 			mAddType = ADD_TYPE_INSERT;
 	 		for (index = 0; index < mincount; index++){
	 			t = items.get(index);
	 			if(t == null) continue;
 	 			_adapter.ReplaceThread(index, t, 1);
  			}

  			for (; index < count; index++){
	 			t = items.get(index);
	 			if(t == null) continue;
				_adapter.addThread(index, t, addtype, 1);
 	 		}

 	 		for (index = _adapter.getCount() - 1; index >= count;index = _adapter.getCount() - 1){
 	 			_adapter.Remove(index);
 	 		}
 		} else if (addtype == ADD_TYPE_APPEND){
 			if(count !=0 && _adapter.getCount() != 0){
 	 			_adapter.Remove(_adapter.getCount() - 1);
 	 		}

 			for (index = 0; index < count; index++){
	 			t = items.get(index);
	 			if(t == null) continue;
 				_adapter.addThread(index, t, addtype, 1);
 			}
 		}

 		//_adapter.notifyDataSetChanged();

 		if (items.size() != 0 && _adapter.getCount() <= TwitterClient.MAX_TWEETS_COUNT){
// 			_adapter.addThread(READ_STATE_READ, "", "", "", 0, "", 0, "null", false, false, "", true, "");
 			_adapter.SetLoadingItem();
 		}

// 		new Thread(new Runnable(){
//				public void run() {
//					SaveTweetItemsToDB();
//				}
//		}).start();

 		TwitterClient.SendMessage(mHandler, TwitterClient.UI_REFRESHVIEWS, null);
	}
	
    
	public void defaultUpdateListViewThread(Bundle bundle,int addtype, boolean order){
		bundle.putInt("append", addtype);
		new defaultUpdateListAsyncTask().execute(bundle);
    }
	
	public void defaultUpdateListViewProcessBackEnd(ProgressData tweets, Bundle bundle){

		ArrayList<CommentItem> items = tweets.citems;

		if (_App._twitter != null) _App._twitter.Get_rate_limit_status(mHandler);

		CommentsData data = (CommentsData) bundle.getSerializable(TwitterClient.KEY);

 		if(data == null || data.items == null) return;

 		int index = 0;
 		boolean ret = false;
 		Bundle userdata = new Bundle();
 		int count = ( (data.items == null) ? 0 : data.items.size() );
 		CommentItem insertitem;
 		int readstate;

		for (index = 0; index < count; index++){
			insertitem = data.Get(index);
			if(insertitem != null){
//				readstate = _App._DbHelper.FindTweet(_App._Username, _ActivityType, insertitem.mID) == false ? READ_STATE_UNKNOW : READ_STATE_READ;
				if( mAddType != ADD_TYPE_REPLACE ){
					items.add(null);
					continue;
				}
//				insertitem.mRead = readstate;
//				ret = defaultDecodeJSON(items, insertitem);
				items.add(insertitem);
			}
		}

		count = _adapter.getCount();
		TwitterItem item;
//		for(index=0;index<count;index++){
//			item = _adapter.Get(index);
//			if(item != null){
//				item.mTimeSource = CreateTimeSource(item.mTime, item.mSource);;
//			}
//		}
//
//	 	if(_ActivityType == TwitterClient.HOME_USERINFO && data.user != null){
//
//	 		userdata.putBoolean("ret", ret);
//	 		userdata.putSerializable(TwitterClient.KEY, data.user);
//	 		TwitterClient.SendMessage(_Handler, TwitterClient.UI_REFRESHVIEWS_PRE, userdata);
//	 	}
	}
	
	private class defaultUpdateListAsyncTask extends AsyncTask <Bundle, ProgressData, Long> {

		@Override
		protected void onPreExecute(){
//			_Refresh = true;
//			setMyProgressBarVisibility(true);
		}

		@Override
		protected Long doInBackground(Bundle... bundle) {
	 		try{
	 			ProgressData tweets = new ProgressData();
		 		int addtype = bundle[0].getInt("append");
		 		tweets.addtype = addtype;
				defaultUpdateListViewProcessBackEnd(tweets, bundle[0]);
				publishProgress(tweets);
	 		} catch (Exception e){}
			return null;
		}

        protected void onProgressUpdate(ProgressData ... tweets) {
			defaultUpdateListViewProcessFrontEnd(tweets[0]);
		}

        @Override
        protected void onPostExecute(Long Result){
//        	_Refresh = false;
        }

	}
	
}
