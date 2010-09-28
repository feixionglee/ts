package tice.weibo.Activities;

import tice.weibo.R;
import tice.weibo.HttpClient.TwitterClient;
import tice.weibo.List.TweetsListActivity;
import tice.weibo.Util.TwitterItem;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.AdapterView.OnItemClickListener;

public class UserStatusesActivity extends TweetsListActivity {
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	_Handler = mHandler;
    	_Context = UserStatusesActivity.this;
    	_ActivityType = getIntent().getIntExtra("type", TwitterClient.HOME_FRIENDS);
    	
        super.onCreate(savedInstanceState);
        
    	_list.setOnItemClickListener(OnItemClickListener);
    	
        InitButtons();
        SetWindowTitle(_ActivityType);
        
        if(_Items.getCount() == 0){
        	setMyProgressBarVisibility(true);
        	if (_App._twitter != null) _App._twitter.Get_friendsfollowers_timeline(mHandler, _ActivityType);
        }
    }
    
    @Override
    public void defaultonItemClick(int position, Bundle b){
    	super.defaultonItemClick(position,b);
    	
    	TwitterItem item = _Items.Get(_CurrentThread);
    	
		Button btnfollow =  (Button)findViewById(R.id.userfollow);
		Button btnunfollow =  (Button)findViewById(R.id.userunfollow);
		
		boolean follow = false;
		if(item != null) follow = item.mFollowing;
			
		if(follow == true){
			btnfollow.setVisibility(View.GONE);
			btnunfollow.setVisibility(View.VISIBLE);
		}else{
			btnfollow.setVisibility(View.VISIBLE);
			btnunfollow.setVisibility(View.GONE);
		}
    }

    OnItemClickListener OnItemClickListener = new OnItemClickListener(){

		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
			defaultonItemClick(position,null);
		}
    };

 	public void InitButtons(){
    	
    	View.inflate(this.getBaseContext(),R.layout.userstatuses_toolbar, _Toolbarpanel);
    	View.inflate(this.getBaseContext(),R.layout.home_statusbar, _Statuspanel);

    	super.InitButtons();
    	
		Button btnfollow =  (Button)findViewById(R.id.userfollow);
		Button btnunfollow =  (Button)findViewById(R.id.userunfollow);
		
    	if(btnfollow != null){
    		btnfollow.setOnClickListener(new OnClickListener(){
    			public void onClick(View v) {
    				TwitterItem item = _Items.Get(_CurrentThread);
    				setMyProgressBarVisibility(true);
    				if (item != null){
    					if (_App._twitter != null) _App._twitter.Get_friendships_create(_Handler, item.mScreenname);
    					PanelAnimationOff(false, _Statuspanel);
    					PanelAnimationOff(false, _Toolbarpanel);
    					v.setEnabled(false);
    				}
    			}
        	});
    	}
    	
    	if(btnunfollow != null){
    		btnunfollow.setOnClickListener(new OnClickListener(){
    			public void onClick(View v) {
    				TwitterItem item = _Items.Get(_CurrentThread);
    				if(item != null){
	    				setMyProgressBarVisibility(true);
	    				if (_App._twitter != null) _App._twitter.Get_friendships_destory(_Handler,item.mScreenname);
	    	    		PanelAnimationOff(false, _Statuspanel);
	    	    		PanelAnimationOff(false, _Toolbarpanel);
	    				v.setEnabled(false);
    				}
    			}
        	});
    	}
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
    	case TwitterClient.HTTP_FRIENDS_TIMELINE:
    	case TwitterClient.HTTP_FOLLOWERS_TIMELINE:
//    		_Page = 1;
    		_Refresh = false;
//    		_LastID = 0;
//    		_PrevID = Long.MAX_VALUE;
    		defaultUpdateListViewThread(msg.getData(),ADD_TYPE_INSERT, true );
    		break;
    	case TwitterClient.HTTP_FRIENDS_TIMELINE_MAXID:
    	case TwitterClient.HTTP_FOLLOWERS_TIMELINE_MAXID:
//    		_Page = _Page + 1;
    		_Refresh = false;
    		_StartLoading = false;
    		defaultUpdateListViewThread(msg.getData(),ADD_TYPE_APPEND, true );
    		break;
    	case TwitterClient.HTTP_FRIENDS_TIMELINE_SINCEID:
    	case TwitterClient.HTTP_FOLLOWERS_TIMELINE_SINCEID:
    		_Refresh = false;
//    		_LastID = 0;
//    		_PrevID = Long.MAX_VALUE;
    		defaultUpdateListViewThread(msg.getData(),ADD_TYPE_INSERT, true );
    		break;
    	case TwitterClient.HTTP_STATUSES_UPDATE:
    		if (_App._Refreshafterpost == false) break;
    		setMyProgressBarVisibility(true);
    		if (_App._twitter != null) _App._twitter.Get_friendsfollowers_timeline(mHandler, _ActivityType);
    		break;
    	case TwitterClient.HTTP_FRIENDSHIPS_DESTORY:
	    	{
	    		int i = findPositionByIDFromUser(msg);
	    		if(i >=0 && i < _Items.getCount()){
	    			_Items.Get(i).mFollowing = false;
	    			if(_ActivityType == TwitterClient.HOME_FRIENDS) _Items.Remove(i);
	    			_Items.notifyDataSetChanged();
	    		}
	    		setMyProgressBarVisibility(false);
	    		Button btnfollow =  (Button)findViewById(R.id.userfollow);
	    		Button btnunfollow =  (Button)findViewById(R.id.userunfollow);
	    		btnfollow.setEnabled(true);
	    		btnunfollow.setEnabled(true);
	    		break;
	    	}
    	case TwitterClient.HTTP_FRIENDSHIPS_CREATE:
	    	{
	    		int i = findPositionByIDFromUser(msg);
	    		if(i >=0 && i < _Items.getCount()) _Items.Get(i).mFollowing = true;
	    		setMyProgressBarVisibility(false);
	    		Button btnfollow =  (Button)findViewById(R.id.userfollow);
	    		Button btnunfollow =  (Button)findViewById(R.id.userunfollow);
	    		btnfollow.setEnabled(true);
	    		btnunfollow.setEnabled(true);
	    	}	
        	break;    		

		case TwitterClient.UI_REFRESHVIEWS:
	   		_Items.notifyDataSetChanged();
	   		PanelAnimationOff(false, _Toolbarpanel);
	   		//PanelAnimationOff(false, _Statuspanel);
	   		setMyProgressBarVisibility(false);
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
        	if(_App._twitter != null) _App._twitter.Get_friendsfollowers_timeline(mHandler, _ActivityType);
			PanelAnimationOff(false, _Statuspanel);
			PanelAnimationOff(false, _Toolbarpanel);
        	break;
        }
    	
        return super.onMenuItemSelected(featureId, item);
    }
}