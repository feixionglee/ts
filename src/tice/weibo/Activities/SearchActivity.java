package tice.weibo.Activities;

import tice.weibo.R;
import tice.weibo.HttpClient.TwitterClient;
import tice.weibo.List.TweetsListActivity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.AdapterView.OnItemClickListener;

public class SearchActivity extends TweetsListActivity {
	
	private static int ACTOVITY_TYPE_ID = TwitterClient.HOME_SEARCH;
	String mQuery = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	_Handler = mHandler;
    	_ActivityType = ACTOVITY_TYPE_ID;
    	_Context = SearchActivity.this;

        super.onCreate(savedInstanceState);
        
    	_list.setOnItemClickListener(OnItemClickListener);
    	
        InitButtons();
        SetWindowTitle(ACTOVITY_TYPE_ID);
        
        mQuery = getIntent().getStringExtra("search");
        if(mQuery != null && mQuery.length() != 0){
        	setMyProgressBarVisibility(true);
        	if (_App._twitter != null) _App._twitter.Get_search(_Handler, mQuery);
        }else{
        	PanelAnimationOn(false, _Statuspanel);
        }
    }
    
    @Override
    public void defaultonItemClick(int position, Bundle b){
    	super.defaultonItemClick(position, b);
    	
		Button viewbtn = (Button)findViewById(R.id.thread);
		viewbtn.setEnabled(true);
		
		Button newsearch = (Button)findViewById(R.id.newsearch);
    	if(newsearch != null){
    		newsearch.setOnClickListener(new OnClickListener(){
    			public void onClick(View v) {
    				PanelAnimationOn(false, _Statuspanel);
    				PanelAnimationOff(false, _Toolbarpanel);

    			}
        	});
    	}
    }

    OnItemClickListener OnItemClickListener = new OnItemClickListener(){

		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
	    	Bundle b = new Bundle();
	    	b.putString("search", mQuery);
			defaultonItemClick(position,b);
		}
    };
    
 	public void InitButtons(){
    	
    	View.inflate(this.getBaseContext(),R.layout.search_toolbar, _Toolbarpanel);
    	View.inflate(this.getBaseContext(),R.layout.search_statusbar, _Statuspanel);
    	
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
    	case TwitterClient.HTTP_SEARCH:
    		EditText edit = (EditText)findViewById(R.id.EditText);
    		Button send = (Button)findViewById(R.id.Send);
    		edit.setEnabled(true);
    		send.setEnabled(true);
    		setMyProgressBarVisibility(false);
//    		_LastID = 0;
//    		_PrevID = Long.MAX_VALUE;
    		defaultUpdateListViewThread(msg.getData(),ADD_TYPE_REPLACE, true );
    		break;
    	case TwitterClient.HTTP_SEARCH_NEXT:
    		_StartLoading = false;
    		setMyProgressBarVisibility(false);
    		defaultUpdateListViewThread(msg.getData(),ADD_TYPE_APPEND, true );
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
			EditText edit = (EditText)findViewById(R.id.EditText);
			String text = edit.getText().toString();
			if(text.length() != 0){
 				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService( Context.INPUT_METHOD_SERVICE );
 				inputMethodManager.hideSoftInputFromWindow(edit.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS );
 				setMyProgressBarVisibility(true);
 				if (_App._twitter != null) _App._twitter.Get_search(_Handler, text);
			}else if (mQuery != null && mQuery.length() != 0){
				setMyProgressBarVisibility(true);
				if (_App._twitter != null) _App._twitter.Get_search(_Handler, mQuery);
			}
        	break;
        }
    	
        return super.onMenuItemSelected(featureId, item);
    }
}