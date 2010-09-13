package tice.twitterwalk.Activities;

import tice.twitterwalk.R;
import tice.twitterwalk.HttpClient.TwitterClient;
import tice.twitterwalk.List.TweetsListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class MentionActivity extends TweetsListActivity {
	
	private static int ACTOVITY_TYPE_ID = TwitterClient.HOME_MENTIONS;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	_Handler = mHandler;
    	_ActivityType = ACTOVITY_TYPE_ID;
    	_Context = MentionActivity.this;
    	
    	super.onCreate(savedInstanceState);
        
    	_list.setOnItemClickListener(OnItemClickListener);
    	
        InitButtons();
        SetWindowTitle(ACTOVITY_TYPE_ID);
        
        if(_Items.getCount() == 0){
        	setMyProgressBarVisibility(true);
        	if (_App._twitter != null) _App._twitter.Get_timeline(mHandler, ACTOVITY_TYPE_ID);
        }
    }
    
    @Override
    public void defaultonItemClick(int position, Bundle b){
    	super.defaultonItemClick(position,b);
    }

    OnItemClickListener OnItemClickListener = new OnItemClickListener(){

		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
			defaultonItemClick(position,null);
		}
    };
    
 	public void InitButtons(){
    	
    	View.inflate(this.getBaseContext(),R.layout.mention_toolbar, _Toolbarpanel);
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
    	
    	case TwitterClient.HTTP_MENTIONS_TIMELINE:
//    		_Page = 1;
    		_Refresh = false;
//    		_LastID = 0;
//    		_PrevID = Long.MAX_VALUE;
    		defaultUpdateListViewThread(msg.getData(),ADD_TYPE_INSERT, true );
    		break;
    	case TwitterClient.HTTP_MENTIONS_TIMELINE_MAXID:
//    		_Page = _Page + 1;
    		_Refresh = false;
    		_StartLoading = false;
    		defaultUpdateListViewThread(msg.getData(),ADD_TYPE_APPEND, true );
    		break;
    	case TwitterClient.HTTP_MENTIONS_TIMELINE_SINCEID:
    		_Refresh = false;
//    		_LastID = 0;
//    		_PrevID = Long.MAX_VALUE;
    		defaultUpdateListViewThread(msg.getData(),ADD_TYPE_INSERT, true );
    		break;
    	case TwitterClient.HTTP_STATUSES_UPDATE:
    		if (_App._Refreshafterpost == false) break;
    		setMyProgressBarVisibility(true);
    		if (_App._twitter != null) _App._twitter.Get_timeline(mHandler, ACTOVITY_TYPE_ID);
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
        	if(_App._twitter != null) _App._twitter.Get_timeline(mHandler, ACTOVITY_TYPE_ID);
			PanelAnimationOff(false, _Statuspanel);
			PanelAnimationOff(false, _Toolbarpanel);
        	break;
        }
    	
        return super.onMenuItemSelected(featureId, item);
    }
}