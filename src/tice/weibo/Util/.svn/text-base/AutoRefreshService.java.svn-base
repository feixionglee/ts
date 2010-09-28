package tice.twitterwalk.Util;

import java.net.URI;
import java.net.URISyntaxException;

import tice.twitterwalk.App;
import tice.twitterwalk.R;
import tice.twitterwalk.Activities.HomeActivity;
import tice.twitterwalk.DB.DbAdapter;
import tice.twitterwalk.HttpClient.TwitterClient;
import tice.twitterwalk.trace.ExceptionHandler;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;

public class AutoRefreshService  extends Service {

	public App _App = null;
	
	boolean mbStartNotification = true;

	public static final String ACTION_REFRESH_TWIGEE_ALARM = "tice.twitterwalk.ACTION_REFRESH_TWIGEE_ALARM";
	
	public static PendingIntent _Sender = null;
	public static AlarmManager _AlarmMgr = null;
	public static NotificationManager _NotificationMgr = null;
	
	int RetryCount = 0;
	private TwitterClient _twitter;
	private DbAdapter _DbHelper;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

    @Override  
    public void onCreate() { 
		
    	_App = (App)getApplicationContext();
    	
    	ExceptionHandler.register(this, "http://catchreport.appspot.com/catch");

    	_DbHelper = new DbAdapter(this);
        _DbHelper.open();
    	
		if (_NotificationMgr == null){
			_NotificationMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		}
		_NotificationMgr.cancelAll();
		
		if (_AlarmMgr == null){
			Intent intent = new Intent(ACTION_REFRESH_TWIGEE_ALARM);
			_Sender = PendingIntent.getBroadcast(this, 0, intent, 0);
			_AlarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
		}
		
		registerReceiver(mReceiver, new IntentFilter(ACTION_REFRESH_TWIGEE_ALARM));  
    }  
  
    @Override  
    public void onStart(Intent intent, int startId) {  
    	
    	_App.LoadSettings(this);
    	InitTwitterClient();
    	
    	_AlarmMgr.cancel(_Sender);
    	if(_App._Notification == true){
    		_AlarmMgr.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + _App._Notification_interval, _Sender);
    	}
    }  
  
    @Override  
    public void onDestroy() {  
    	_AlarmMgr.cancel(_Sender);
    	unregisterReceiver(mReceiver);  
    }  

    private BroadcastReceiver mReceiver = new BroadcastReceiver() { 
        public void onReceive(Context context, Intent intent) { 
	        Thread thr = new Thread(null, mTask, "TwitWalk_AutoRefresh_Service");
	    	thr.start();
        } 
    }; 
    
    private PendingIntent makeIntent(int type) {

    	Intent newintent = new Intent(this, HomeActivity.class);
    	newintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	newintent.putExtra("nextlaunch", type);
    	
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, newintent, PendingIntent.FLAG_UPDATE_CURRENT);
        return contentIntent;
    }
	
    private void StartNotification(int id, int type, String ticker, String title, String text){

    	if(mbStartNotification == true){

    		Notification notification = new Notification(R.drawable.stat_notify_chat, ticker, System.currentTimeMillis());
    		notification.setLatestEventInfo(this, title, text, makeIntent(type));
    		notification.defaults = 0;

    		if(_App._Notification_sound == true){
    			notification.sound = TextUtils.isEmpty(_App._Notification_ringtone) ? null : Uri.parse(_App._Notification_ringtone);
    		}
    		if(_App._Notification_vibrate == true){
    			notification.defaults |= Notification.DEFAULT_VIBRATE;
    		}
    		if(_App._Notification_led == true){
    			notification.flags |= Notification.FLAG_SHOW_LIGHTS;
    			notification.ledARGB = 0xff00ff00;
    			notification.ledOnMS = 500;
    			notification.ledOffMS = 2000;
    		}

    		_NotificationMgr.notify(id, notification);
    		
    		mbStartNotification = false;
    	}
    }

 	private void DecodeJSON(TwitterItem item, int i,int type){

 		int id = (int)R.id.home;
 		String ticker = "", preFix = "";

 		if (_DbHelper.FindTweet(_App._Username, -1, item.mID) == true) return;
 		
 		_twitter.FrechImg(mHandler, item.mImageurl,item.mScreenname);

 		switch (type){
 		case TwitterClient.HOME_HOME:
 			ticker = "You have new home tweets";
 			preFix = "Home - ";
 			id = (int)R.id.home;
 			break;
 		case TwitterClient.HOME_MENTIONS:
 			ticker = "You have new mention tweets";
 			preFix = "Mention - ";
 			id = (int)R.id.mentions;
 			break;
 		case TwitterClient.HOME_DIRECT:
 			ticker = "You have new direct messages";
 			preFix = "Direct - ";
 			id = (int)R.id.direct;
 			break;
 		}

 		item.mType = type;
 		item.mAccount = _App._Username;
 		_DbHelper.createtweet(type,item);

 		_App._HaveNotification = true;
 		
 		StartNotification(id, type,ticker, preFix + item.mScreenname, item.mText);
 	}
	
 	private void UpdateListView(Bundle bundle,int type){
 		
 		TweetsData data = (TweetsData) bundle.getSerializable(TwitterClient.KEY);

 		if(data == null) return;
 		int count = ( (data.items == null) ? 0 : data.items.size() );

 		for(int i = count - 1; i >= 0 ; i--){	
 			DecodeJSON(data.Get(i), 0,type);
 		}

 		RetryCount = 0;
 	}
	
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
        	
        	switch (msg.what){

        	case TwitterClient.HTTP_ERROR:
        		if(RetryCount >= 3) return;
                Thread thr = new Thread(null, mTask, "TwitWalk_AutoRefresh_Service");
                thr.start();
                RetryCount += 1;
        		break;
        	case TwitterClient.HTTP_HOME_TIMELINE_SINCEID:
        		UpdateListView(msg.getData(),TwitterClient.HOME_HOME);
        		break;
        	case TwitterClient.HTTP_MENTIONS_TIMELINE_SINCEID:
        		UpdateListView(msg.getData(),TwitterClient.HOME_MENTIONS);
        		break;
        	case TwitterClient.HTTP_DIRECT_TIMELINE_SINCEID:
        		UpdateListView(msg.getData(),TwitterClient.HOME_DIRECT);
        		break;
        	}
        }
    }; 
	
    Runnable mTask = new Runnable() {
        public void run() {

    		if(_App._InApp == true){

            	_App.LoadSettings(AutoRefreshService.this);
            	if(_App._Notification == true){
            		_AlarmMgr.cancel(_Sender);
            		_AlarmMgr.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 300000, _Sender);
            	}
    			
    			//Intent broadcastIntent = new Intent(); 
    			//broadcastIntent.setAction("tice.twitterwalk.INAPP_AUTO_REFRESH"); 
    			//sendBroadcast(broadcastIntent); 
    			//return;
    		}
    			
    		long id = 0;
    		
    		mbStartNotification = true;
    		
    		if(_App._Notification_home == true){
    			id = _DbHelper.FetchMaxID(_App._Username, TwitterClient.HOME_HOME);
    			if(id != 0){
    				_twitter.Get_timeline_since_single(mHandler, TwitterClient.HOME_HOME, id);
    			}else{
    				
    			}
    		}
    		
    		if(_App._Notification_mention == true){
    			id = _DbHelper.FetchMaxID(_App._Username, TwitterClient.HOME_MENTIONS);
    			if(id != 0){
    				_twitter.Get_timeline_since_single(mHandler, TwitterClient.HOME_MENTIONS, id);
    			}else{
    				
    			}

    		}
    		
    		if(_App._Notification_direct == true){
    			id = _DbHelper.FetchMaxID(_App._Username, TwitterClient.HOME_DIRECT);
    			if(id != 0){
    				_twitter.Get_timeline_since_single(mHandler, TwitterClient.HOME_DIRECT, id);
    			}else{
    				
    			}
    		}
    		
        	_App.LoadSettings(AutoRefreshService.this);
        	if(_App._Notification == true){
        		_AlarmMgr.cancel(_Sender);
        		_AlarmMgr.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + _App._Notification_interval, _Sender);
        	}
        }

    };

    private void InitTwitterClient(){

        int port;
        String url,searchurl;
        
        if(_App._Https == true){
        	url = String.format("https://%s", _App._Baseapi);
        	searchurl = String.format("https://%s", _App._Searchapi);
        	port = 443;
        }else{
        	url = String.format("http://%s", _App._Baseapi);
        	searchurl = String.format("http://%s", _App._Searchapi);
        	port = 80;
        }
        
        URI host = null;
		try {
			host = new URI(url);
	        
			_twitter = new TwitterClient(this, host.getHost(), port, url, searchurl, _App._Username, _App._Password);
	        
		} catch (URISyntaxException e) { } 
    }
}
