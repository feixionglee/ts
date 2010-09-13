package tice.twitterwalk.Activities;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import tice.twitterwalk.R;
import tice.twitterwalk.HttpClient.TwitterClient;
import tice.twitterwalk.List.TweetsListActivity;
import tice.twitterwalk.Util.TweetsData;
import tice.twitterwalk.Util.UserData;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;


public class UserInfoActivity extends TweetsListActivity {
	
	private static int ACTOVITY_TYPE_ID = TwitterClient.HOME_USERINFO;
	
	String mUser = "";
	boolean mFollow = false;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	_Handler = mHandler;
    	_ActivityType = ACTOVITY_TYPE_ID;
    	_Context = UserInfoActivity.this;

        super.onCreate(savedInstanceState);
        
    	_list.setOnItemClickListener(OnItemClickListener);
    	
        InitButtons();
        SetWindowTitle(ACTOVITY_TYPE_ID); 
        
        mUser = this.getIntent().getStringExtra("user");
        
        setMyProgressBarVisibility(true);
        if (_App._twitter != null) _App._twitter.Get_user_timeline(mHandler, mUser);
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
   
    private void UpdateUserInforFollowButton(Bundle bundle){
    	
 		TweetsData data = (TweetsData) bundle.getSerializable(TwitterClient.KEY);
 		JSONObject json = data.mJSONObject;

    	try {
			JSONObject relationship = json.getJSONObject("relationship");
			JSONObject source = relationship.getJSONObject("source");
			//JSONObject target = relationship.getJSONObject("target");
			mFollow = source.getBoolean("followed_by");

			Button follow = (Button)findViewById(R.id.userinfo_btnfollow);
	    	follow.setEnabled(true);
//	    	follow.setVisibility(View.VISIBLE);
	    	
			if(mFollow == true){
		    	follow.setText("Unfollow");
			}else{
		    	follow.setText("Follow");
			}
			
		} catch (JSONException e) {
			Bundle err = new Bundle();
			err.putString(TwitterClient.KEY, e.getLocalizedMessage());
			TwitterClient.SendMessage(mHandler, TwitterClient.HTTP_ERROR, err);
			return;
		}
    	
    }

 	private void UpdateUserInforView(Bundle data){
 		
 		if(data == null) return;
		UserData user = (UserData) data.getSerializable(TwitterClient.KEY);
 		if (user == null) return;
		
 		ImageView icon = (ImageView)findViewById(R.id.userinfo_icon);
 		if (_App._twitter != null) icon.setImageBitmap(_App._twitter.LoadIcon(mHandler, user.mScreenname, user.mProfile_image_url));
	 		
 		String name,follow,info;
 		name = String.format("%s", user.mTitle);
 		follow = String.format("Statuses: %s | %s\n", user.mStatuses_count, TwitterClient.GetSmartTimeString(Date.parse(user.mCreated_at)));
 		follow += String.format("Following: %s Follower: %s", user.mFriends_count,user.mFollowers_count);
 		info = String.format("%s", user.mDescription);
 		//info += String.format("%s\n", location);
 		//info += String.format("%s\n", url);
 		//info += String.format("%s", time_zone);
	 		
 		TextView nameview = (TextView)findViewById(R.id.userinfo_name);
 		TextView followview = (TextView)findViewById(R.id.userinfo_follow);
 		TextView infoview = (TextView)findViewById(R.id.userinfo_info);

 		nameview.setText(name);
 		followview.setText(follow);
	 		
 		//infoview.setAutoLinkMask(Linkify.WEB_URLS | Linkify.MAP_ADDRESSES | Linkify.EMAIL_ADDRESSES);
 		//infoview.setLinksClickable(true);
 		infoview.setText(info);
 	}
 	
 	public void InitButtons(){
    	
    	View.inflate(this.getBaseContext(),R.layout.userinfo_toolbar, _Toolbarpanel);
    	View.inflate(this.getBaseContext(),R.layout.home_statusbar, _Statuspanel);
    	
    	LinearLayout parent = (LinearLayout)findViewById(R.id.InfoLayout);
    	View.inflate(this.getBaseContext(),R.layout.userinfo, parent);
    	
    	Button follow = (Button)findViewById(R.id.userinfo_btnfollow);
    	Button block = (Button)findViewById(R.id.userinfo_btnblock);
    	
    	//parent.setVisibility(View.GONE);
    	follow.setVisibility(View.GONE);
    	block.setVisibility(View.GONE);
    	
    	if(follow != null){
    		follow.setOnClickListener(new OnClickListener(){
    			public void onClick(View v) {
    				setMyProgressBarVisibility(true);
    				if(mFollow == true){
    					if (_App._twitter != null) _App._twitter.Get_friendships_destory(_Handler, mUser);
    				}else{
    					if (_App._twitter != null) _App._twitter.Get_friendships_create(_Handler, mUser);
    				}
    				v.setEnabled(false);
    			}
        	});
    	}
    	
    	if(block != null){
    		block.setOnClickListener(new OnClickListener(){
    			public void onClick(View v) {


    			}
        	});
    	}
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
    	case TwitterClient.HTTP_USER_TIMELINE:
//    		_Page = 1;
//    		_LastID = 0;
//    		_PrevID = Long.MAX_VALUE;
    		defaultUpdateListViewThread(msg.getData(),ADD_TYPE_REPLACE, true );
    		break;
    	case TwitterClient.HTTP_USER_TIMELINE_MAXID:
//    		_Page = _Page + 1;
    		_StartLoading = false;
    		defaultUpdateListViewThread(msg.getData(),ADD_TYPE_APPEND, true );
    		break;
    	case TwitterClient.HTTP_USER_TIMELINE_SINCEID:
//    		_LastID = 0;
//    		_PrevID = Long.MAX_VALUE;
    		defaultUpdateListViewThread(msg.getData(),ADD_TYPE_INSERT, true );
    		break;
    	case TwitterClient.HTTP_FETCH_IMAGE:
    		ImageView icon = (ImageView)findViewById(R.id.userinfo_icon);
    		if (_App._twitter != null) icon.setImageBitmap(_App._twitter.LoadIcon(mHandler, mUser,""));
    		break;
    	case TwitterClient.HTTP_FRIENDSHIPS_SHOW:
    		UpdateUserInforFollowButton(msg.getData());
    		break;
    	case TwitterClient.HTTP_FRIENDSHIPS_CREATE:
    	case TwitterClient.HTTP_FRIENDSHIPS_DESTORY:
    		setMyProgressBarVisibility(false);
    		if (_App._twitter != null) _App._twitter.Get_friendships_show(mHandler, mUser, _App._Username);
        	break;
    	case TwitterClient.UI_REFRESHVIEWS_PRE:
 	 		boolean ret = msg.getData().getBoolean("ret");
 	 		if(ret == true){
				UpdateUserInforView(msg.getData());
 	 		}
     		break;
    	case TwitterClient.UI_REFRESHVIEWS:
       		_Items.notifyDataSetChanged();
 			Button follow = (Button)findViewById(R.id.userinfo_btnfollow);
 	    	follow.setVisibility(View.VISIBLE);
 			PanelAnimationOff(false, _Toolbarpanel);
 			//PanelAnimationOff(false, _Statuspanel);
 			setMyProgressBarVisibility(false);
 			if (_App._twitter != null) _App._twitter.Get_friendships_show(mHandler, mUser, _App._Username);
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
        	if (_App._twitter != null) _App._twitter.Get_user_timeline(mHandler, mUser);
			PanelAnimationOff(false, _Statuspanel);
			PanelAnimationOff(false, _Toolbarpanel);
        	break;
        }
    	
        return super.onMenuItemSelected(featureId, item);
    }
}