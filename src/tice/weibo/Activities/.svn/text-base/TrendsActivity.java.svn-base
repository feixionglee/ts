package tice.twitterwalk.Activities;

import tice.twitterwalk.R;
import tice.twitterwalk.HttpClient.TwitterClient;
import tice.twitterwalk.List.TweetsListActivity;
import tice.twitterwalk.Util.TwitterItem;
import tice.twitterwalk.Util.TweetsData;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.AdapterView.OnItemClickListener;

public class TrendsActivity extends TweetsListActivity {
	
	private static int ACTOVITY_TYPE_ID = TwitterClient.HOME_TRENDS;
	
	int mTrendsType;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	_Handler = mHandler;
    	_ActivityType = ACTOVITY_TYPE_ID;
    	_Context = TrendsActivity.this;

        super.onCreate(savedInstanceState);
        
    	_list.setOnItemClickListener(OnItemClickListener);
    	
        InitButtons();
        SetWindowTitle(ACTOVITY_TYPE_ID); 
        
        mTrendsType = getIntent().getIntExtra("trends_type", TwitterClient.TRENDS_CURRENT);
        setMyProgressBarVisibility(true);
        if (_App._twitter != null) _App._twitter.Get_trends(mHandler,mTrendsType);
    }
    
    @Override
    public void defaultonItemClick(int position, Bundle b){
    	super.defaultonItemClick(position,b);
    	
		Button thread = (Button)findViewById(R.id.thread);
    	if(thread != null){
    		thread.setOnClickListener(new OnClickListener(){
    			public void onClick(View v) {
    				String query = "";
     				TwitterItem item = _Items.Get(_CurrentThread);
     				if(item != null) {
	    				PanelAnimationOff(false, _Toolbarpanel);
	    	        	Intent i = new Intent(TrendsActivity.this, SearchActivity.class);
	    	        	query = String.format("\"%s\"", item.mReplyID);
	    	        	i.putExtra("search",query);
	    	        	TrendsActivity.this.startActivityForResult(i, APP_CHAINCLOSE);
     				}
    			}
        	});
    	}
    }

    OnItemClickListener OnItemClickListener = new OnItemClickListener(){

		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
			defaultonItemClick(position,null);
		}
    };

 	private void UpdateListView(Bundle bundle,boolean append, boolean order){
 		
 		TweetsData data = (TweetsData) bundle.getSerializable(TwitterClient.KEY);

 		if(data == null || data.items == null) return;
 		int count = ( (data.items == null) ? 0 : data.items.size() );
 				
 		TwitterItem item;
 		for(int i=0;i<count;i++){
 			item = data.Get(i);
 			_Items.addThread(READ_STATE_READ, " ", "", item.mText, 0 , "", i, item.mReplyID, false, false, "", true);
 		}

 		
 		_Items.notifyDataSetChanged();
 		
		PanelAnimationOff(false, _Toolbarpanel);
		SaveTweetItemsToDB();
		setMyProgressBarVisibility(false);
 	}

 	public void InitButtons(){
    	
    	View.inflate(this.getBaseContext(),R.layout.trends_toolbar, _Toolbarpanel);
    	View.inflate(this.getBaseContext(),R.layout.home_statusbar, _Statuspanel);
    	
    	super.InitButtons();
    }
    
    private final Handler mHandler = new Handler() {
        @Override
         public void handleMessage(final Message msg) {
        	defaulthandleMessage(msg);
        	processMessage(msg);
        }
    }; 
    
    public void processMessage(Message msg) {
    	switch (msg.what){
    	case TwitterClient.HTTP_TRENDS_CURRENT:
    	case TwitterClient.HTTP_TRENDS_DAILY:
    	case TwitterClient.HTTP_TRENDS_WEEKLY:
    		_Items.Clear();
    		UpdateListView(msg.getData(),true, true );
    		break;
    	}

    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

    	switch(item.getItemId()) {
        case R.id.refresh:
        	if(_Refresh == true) break;
        	_Refresh = true;
        	setMyProgressBarVisibility(true);
        	if (_App._twitter != null) _App._twitter.Get_trends(_Handler, mTrendsType);
			PanelAnimationOff(false, _Statuspanel);
			PanelAnimationOff(false, _Toolbarpanel);
        	break;
        }
    	
        return super.onMenuItemSelected(featureId, item);
    }
}