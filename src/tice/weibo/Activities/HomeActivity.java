package tice.weibo.Activities;

import tice.weibo.R;
import tice.weibo.HttpClient.TwitterClient;
import tice.weibo.List.TweetsListActivity;
import tice.weibo.Setting.Setting;
import tice.weibo.Util.TweetsData;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.AdapterView.OnItemClickListener;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;


public class HomeActivity extends TweetsListActivity {

	private static int ACTOVITY_TYPE_ID = TwitterClient.HOME_HOME;
	private static boolean FirstBoot = false;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
                
    	_Handler = mHandler;
    	_ActivityType = ACTOVITY_TYPE_ID;
    	_Context = HomeActivity.this;
   	
    	super.onCreate(savedInstanceState);

    	if(_App._tracker == null){
    		_App._tracker = GoogleAnalyticsTracker.getInstance();
    		_App._tracker.start("UA-11425880-2", 30, this);
    	}
    	
    	_App._MainAtivity = HomeActivity.this;        
    	
    	if(_App._Username.length() == 0 || _App._Password.length() == 0){
        	Intent i = new Intent(this, Setting.class);
            startActivityForResult(i, APP_CHAINCLOSE);
        }

        //_App.InitTwitterClient(_Handler);

    	_App._this = this;
    	_list.setOnItemClickListener(OnItemClickListener);
    	
        InitButtons();
        SetWindowTitle(ACTOVITY_TYPE_ID);
        
        String text = "";
        Intent intent = getIntent();
        int type = intent.getIntExtra("nextlaunch", TwitterClient.HOME_HOME);
        Bundle bundle = intent.getExtras();
        if(bundle != null) text = bundle.getString(Intent.EXTRA_TEXT);
        
        if(type == TwitterClient.HOME_MENTIONS){
        	Intent i = new Intent(this, MentionActivity.class);
        	startActivityForResult(i, APP_CHAINCLOSE);
            //finish();
        }else if (type == TwitterClient.HOME_DIRECT){
        	Intent i = new Intent(this, DirectActivity.class);
        	startActivityForResult(i, APP_CHAINCLOSE);
            //finish();
    	}else{
	        if(_Items.getCount() == 0 || _App._Refreshonlaunch == true){
	            if(_App._Username.length() != 0 && _App._Password.length() != 0){
	            	setMyProgressBarVisibility(true);
	            	if (_App._twitter != null) _App._twitter.Get_timeline(mHandler, ACTOVITY_TYPE_ID);
	            }
	        }
        }
        
        if(text != null && text.length() != 0){
        	PanelAnimationOn(false, _Statuspanel);
        	EditText edit = (EditText)findViewById(R.id.EditText);
        	edit.setText(text);
        }
        
    	if(FirstBoot == false){
    		if (_App._twitter != null) _App._twitter.Get_version(mHandler);
    		FirstBoot = true;
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

    	View.inflate(this.getBaseContext(),R.layout.home_toolbar, _Toolbarpanel);
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

    	case TwitterClient.HTTP_HOME_TIMELINE:
//    		_Page = 1;
    		_Refresh = false;
//    		_LastID = 0;
//    		_PrevID = Long.MAX_VALUE;
    		defaultUpdateListViewThread(msg.getData(),mAddType, true );
    		break;
    	case TwitterClient.HTTP_HOME_TIMELINE_MAXID:
//    		_Page = _Page + 1;
    		_Refresh = false;
    		_StartLoading = false;
    		defaultUpdateListViewThread(msg.getData(),ADD_TYPE_APPEND, true );
    		break;
    	case TwitterClient.HTTP_HOME_TIMELINE_SINCEID:
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
    	case TwitterClient.HTTP_STATUS_DESTORY:
    		setMyProgressBarVisibility(false);
    		int i = findPositionByIDFromStatus(msg);
    		if(i >=0) _Items.Remove(i);
    		_Items.notifyDataSetChanged();
    		break;
    	case TwitterClient.HTTP_CHECK_VERSION:
    		try{
	    		TweetsData data = (TweetsData) msg.getData().getSerializable(TwitterClient.KEY);
	    		String version = data.mData;
	    		String current;
	    		PackageInfo pi;
	    		PackageManager pm = this.getPackageManager();
				pi = pm.getPackageInfo(this.getPackageName(), 0);
				current = pi.versionName + "\n";
//				if (version.equalsIgnoreCase(current) == false){
//			        new AlertDialog.Builder(this)
//		            .setTitle("Version Check")
//			        .setMessage("New version is on the market!")
//			        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
//			            public void onClick(DialogInterface dialog, int whichButton) {
//			            }
//			        })
//			        .show();
//				}
    		}catch (Exception e){}    	
    	case TwitterClient.UI_REFRESHVIEWS:
       		_Items.notifyDataSetChanged();
       		PanelAnimationOff(false, _Toolbarpanel);
       		//PanelAnimationOff(false, _Statuspanel);
       		setMyProgressBarVisibility(false);
     		break;
    	}
    }
 	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        if(_App._Username.length() == 0 || _App._Password.length() == 0){
        	Intent i = new Intent(this, Setting.class);
        	startActivityForResult(i, APP_CHAINCLOSE);
            return false;
        }
        
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

    	switch(item.getItemId()) {
        case R.id.refresh:
        	if(_Refresh == true) break;
        	_Refresh = true;
        	setMyProgressBarVisibility(true);
        	if (_App._twitter != null) _App._twitter.Get_timeline(mHandler, ACTOVITY_TYPE_ID);
			PanelAnimationOff(false, _Statuspanel);
			PanelAnimationOff(false, _Toolbarpanel);
        	break;
        }
       
        return super.onMenuItemSelected(featureId, item);
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		_App._tracker.stop();
	}
	
	public void onStart(){
		super.onStart();
		home.setChecked(true);
	}
}