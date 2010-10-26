package tice.weibo.Activities;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import tice.weibo.App;
import tice.weibo.R;
import tice.weibo.HttpClient.TwitterClient;
import tice.weibo.List.CommentAdapter;
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
	
	private long status_id;
	private int _Count = 100;
	CommentAdapter _adapter;
	protected boolean _Refresh = false;
	
	static final String ACTION = "tice.weibo.Activities.CommentActivity";
	private static final String EXTRA_ITEM = "tice.weibo.Util.TwittterItem";
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		_App = (App)getApplicationContext();
//		mCtx = (TweetsListActivity)this;
//		_Handler = mHandler;
		
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		
		setContentView(R.layout.commentblog);
		
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_1);
		
		setupView();
		
		status_id = getIntent().getExtras().getLong(EXTRA_ITEM);	
		ListView _list = (ListView)findViewById(R.id.lvCmt);
		
		_adapter = new CommentAdapter(this);
		
		_list.setAdapter(_adapter);
		setMyProgressBarVisibility(true);
		loadComments(status_id);
	
	}
	
	public void setupView(){
		Button combtn = (Button)findViewById(R.id.btCmtSend);

		if(combtn != null){
			combtn.setOnClickListener(new OnClickListener(){
				public void onClick(View v){
					EditText edit = (EditText)findViewById(R.id.etCmtReason);
					String text = edit.getText().toString();
					
					CheckBox state = (CheckBox)findViewById(R.id.rb_forward);
					
					if(text.length() > 0){
						if (state.isChecked()){
							_App._twitter.Post_retweeted_statuses(mHandler,status_id,text);
						}
						_App._twitter.Post_comment(mHandler,status_id,text);
						
					}
				}
			});
		}
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
        	processMessage(msg);
        }
    };
    
    public void processMessage(Message msg) {
    	switch (msg.what){
    	case TwitterClient.HTTP_COMMENTS_TIMELINE:
    		defaultUpdateListViewThread(msg.getData(),ADD_TYPE_INSERT, true );
    		break;
    	case TwitterClient.HTTP_POST_COMMENT:
    		finish();
    		break;
    	case TwitterClient.UI_REFRESHVIEWS:
       		_adapter.notifyDataSetChanged();
       		break; 
    	}
	}
    
	public void defaultUpdateListViewThread(Bundle bundle,int addtype, boolean order){
		bundle.putInt("append", addtype);
		new defaultUpdateListAsyncTask().execute(bundle);
    }

	public void defaultUpdateListViewProcessFrontEnd(ProgressData tweets){

		ArrayList<CommentItem> items = tweets.citems;

 		if(items.size() == 0) {
 			TwitterClient.SendMessage(mHandler, TwitterClient.UI_REFRESHVIEWS, null);
 			return;
 		}

 		int index;
 		int count;

 		int addtype = tweets.addtype;
 		count = items.size();
 		CommentItem t;

 		if(addtype == ADD_TYPE_INSERT){
	 		for (index = 0; index < count; index++){
	 			t = items.get(index);
	 			if(t == null) continue;
	 			_adapter.addThread(index, t, addtype, 1);
	 		}
 		}

 		TwitterClient.SendMessage(mHandler, TwitterClient.UI_REFRESHVIEWS, null);
	}
	
	public void defaultUpdateListViewProcessBackEnd(ProgressData tweets, Bundle bundle){

		ArrayList<CommentItem> items = tweets.citems;
		if (_App._twitter != null) _App._twitter.Get_rate_limit_status(mHandler);

		CommentsData data = (CommentsData) bundle.getSerializable(TwitterClient.KEY);
		System.out.println("======="+data.items.size());

 		if(data == null || data.items == null) return;

 		int index = 0;
 		int count = ( (data.items == null) ? 0 : data.items.size() );
 		CommentItem insertitem;

		for (index = 0; index < count; index++){
			insertitem = data.Get(index);
			if(insertitem != null){
				items.add(insertitem);
			}
		}
	}
	
	private class defaultUpdateListAsyncTask extends AsyncTask <Bundle, ProgressData, Long> {

		@Override
		protected void onPreExecute(){
			_Refresh = true;
			setMyProgressBarVisibility(false);
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
        	_Refresh = false;
        }

	}
	
	public void setMyProgressBarVisibility(boolean visibilty){
		ProgressBar progressbar = (ProgressBar) findViewById(R.id.titleProgressBar);
		progressbar.setVisibility(visibilty == true? View.VISIBLE: View.INVISIBLE);
	}
	
	private void loadComments(long status_id){
		_App._twitter.Get_comments_timeline(mHandler, status_id, _Count);
	}
	
}
