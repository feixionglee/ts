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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.AdapterView.OnItemClickListener;


public class DirectActivity extends TweetsListActivity {
	
	private static int ACTOVITY_TYPE_ID = TwitterClient.HOME_DIRECT;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	_Handler = mHandler;
    	_ActivityType = ACTOVITY_TYPE_ID;
    	_Context = DirectActivity.this;

        super.onCreate(savedInstanceState);

        _list.setOnItemClickListener(OnItemClickListener);
        
        InitButtons();
        SetWindowTitle(ACTOVITY_TYPE_ID);
        
        if(_Items.getCount() == 0){
        	setMyProgressBarVisibility(true);
        	if (_App._twitter != null) _App._twitter.Get_timeline(mHandler, ACTOVITY_TYPE_ID);
        }
        
        String receiver = getIntent().getStringExtra("receiver");
        if(receiver != null){
	    	_InputType = INPUT_DIRECT;
        	EditText rece = (EditText)findViewById(R.id.Receive);
        	rece.setText(receiver);
        	PanelAnimationOn(false, _Statuspanel);
        }
    }
    
    @Override
    public void defaultonItemClick(int position, Bundle b){
    	super.defaultonItemClick(position,b);

    	TwitterItem item = _Items.Get(_CurrentThread);
    	
    	EditText rece = (EditText)findViewById(R.id.Receive);
    	if(item != null) rece.setText(item.mScreenname);
    	
    	EditText edit = (EditText)findViewById(R.id.EditText);
    	edit.setText("");

    }
 	
    OnItemClickListener OnItemClickListener = new OnItemClickListener(){

		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
			defaultonItemClick(position,null);
		}
    };
 
    public void InitButtons(){
    	
    	View.inflate(this.getBaseContext(),R.layout.direct_toolbar, _Toolbarpanel);
    	View.inflate(this.getBaseContext(),R.layout.direct_statusbar, _Statuspanel);
    	
    	super.InitButtons();
    	
    	EditText rece = (EditText)findViewById(R.id.Receive);
    	rece.setEnabled(true);
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
    	case TwitterClient.HTTP_DIRECT_TIMELINE:
    	case TwitterClient.HTTP_DIRECT_TIMELINE_SENT:
//    		_Page = 1;
    		_Refresh = false;
//    		_LastID = 0;
//    		_PrevID = Long.MAX_VALUE;
    		defaultUpdateListViewThread(msg.getData(),ADD_TYPE_INSERT, true );
    		break;
    	case TwitterClient.HTTP_DIRECT_TIMELINE_MAXID:
    	case TwitterClient.HTTP_DIRECT_TIMELINE_SENT_MAXID:
//    		_Page = _Page + 1;
    		_Refresh = false;
    		_StartLoading = false;
    		defaultUpdateListViewThread(msg.getData(),ADD_TYPE_APPEND, true );
    		break;
    	case TwitterClient.HTTP_DIRECT_TIMELINE_SINCEID:
    	case TwitterClient.HTTP_DIRECT_TIMELINE_SENT_SINCEID:
    		_Refresh = false;
//    		_LastID = 0;
//    		_PrevID = Long.MAX_VALUE;
    		defaultUpdateListViewThread(msg.getData(),ADD_TYPE_INSERT, true );
    		break;
    	case TwitterClient.HTTP_DIRECT_MESSAGES_NEW:
    		EditText edit = (EditText)findViewById(R.id.EditText);
    		Button send = (Button)findViewById(R.id.Send);
    		edit.setText("");
    		send.setEnabled(true);
    		edit.setEnabled(true);
    		PanelAnimationOff(false,_Toolbarpanel);
    		PanelAnimationOff(false,_Statuspanel);
    		setMyProgressBarVisibility(false);
    		if (_App._Refreshafterpost == false) break;
    		setMyProgressBarVisibility(true);
    		if (_App._twitter != null) _App._twitter.Get_timeline(mHandler, _ActivityType);
    		break;
    	case TwitterClient.HTTP_DIRECT_DESTORY:
    	case TwitterClient.HTTP_DIRECT_DESTORY_SENT:
    		setMyProgressBarVisibility(false);
    		int i = findPositionByIDFromStatus(msg);
    		if(i >=0) _Items.Remove(i);
    		_Items.notifyDataSetChanged();
    		break;
    	case TwitterClient.UI_REFRESHVIEWS:
       		_Items.notifyDataSetChanged();
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
        	if(_App._twitter != null) _App._twitter.Get_timeline(mHandler, _ActivityType);
			PanelAnimationOff(false, _Statuspanel);
			PanelAnimationOff(false, _Toolbarpanel);
        	break;
        }

        return super.onMenuItemSelected(featureId, item);
    }
}