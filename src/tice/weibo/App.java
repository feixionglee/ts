package tice.weibo;

import java.net.URI;
import java.net.URISyntaxException;

import tice.weibo.DB.DBTweetsHelper;
import tice.weibo.DB.DbAdapter;
import tice.weibo.HttpClient.TwitterClient;
import tice.weibo.HttpClient.TwitterClient.DownloadPool;
import tice.weibo.Util.AutoRefreshService;
import tice.weibo.trace.ExceptionHandler;
import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class App extends Application {
	
	public final static int PORTSSL = 443;
	public final static int PORT = 80;
	
	public boolean _InApp = false;
	public boolean _ReloadSettings = false;
	public DbAdapter _DbHelper;
	public TwitterClient _twitter;
	public Intent _NotiServices = null;
	public NotificationManager _NotificationMgr = null;
	protected SensorManager _SensorMgr = null;
	public GoogleAnalyticsTracker _tracker = null;
	public Activity _MainAtivity;
	public Context _this;
	public String _Username = "";
	public String _Password = "";
	public String _Baseapi = "";
	public String _Searchapi = "";
	public String _Pictureapi = "";
	public String _Shortenlinkapi = "";
	public boolean _Https = true;
	public int _Tweetscount = 25;
	public boolean _LongClick = true;
	public boolean _ShowStatusbar = true;
	public boolean _AutoHideToolbar = true;
	public boolean _Refreshafterpost = false;
	public boolean _Refreshonlaunch = false;
	public boolean _Displayicon = true;
	public int _Fontsize = 16;
	public boolean _Notification = false;
	public boolean _Notification_home = false;
	public boolean _Notification_mention = false;
	public boolean _Notification_direct = false;
	public boolean _Notification_sound = true;
	public String _Notification_ringtone = "content://settings/system/notification_sound";
	public boolean _Notification_vibrate = true;
	public boolean _Notification_led = true;
	public int _Notification_interval = 86400000;
	public int _PictureQuality = 1;
	public DownloadPool mDownloadPool = null;
	public int _Theme;
	public String _TitleString = "";
	public boolean _HaveNotification = false;
	public boolean _RemoveAD = false;
	
    private boolean isHttps(String url){
        if(url.toLowerCase().indexOf("https://") != -1){
            return true;
        }else if(url.toLowerCase().indexOf("http://") != -1){
            return false;
        }else{
            return _Https;
        }
    }

    private String normalizeURL(String url){
        String result;

        if(url.endsWith("/") == true){
            result = url.substring(0, url.length() - 1);
        }else{
            result = url;
        }
        
        result = result.trim();
        result = result.toLowerCase();
        if(result.indexOf("http://") != -1){
            result = result.substring(7);
        }
        if(result.indexOf("https://") != -1){
            result = result.substring(8);
        }

        return result;
    }

	public void LoadSettings(Context context){
		
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context); 
	
		_Username = settings.getString("username", "");
		_Password = settings.getString("password", "");
//		_Baseapi = settings.getString("baseapi", "twitter.com");
//		_Searchapi = settings.getString("searchapi", "search.twitter.com");
//		_Pictureapi = settings.getString("pictureapi", "http://twitpic.com/api/upload");
//		_Shortenlinkapi = settings.getString("shortenlinkapi", "http://tinyurl.com/api-create.php?url");
		_Baseapi = _Searchapi = _Pictureapi = _Shortenlinkapi = "api.t.sina.com.cn";
		
		_Https = settings.getBoolean("https", false);
		_Tweetscount = Integer.valueOf(settings.getString("tweetscount", "25"));
		_LongClick = settings.getBoolean("longclick", true);
		_ShowStatusbar = settings.getBoolean("statusdbar", true);
		_AutoHideToolbar = settings.getBoolean("autohidetoolbar", true);
		_Refreshafterpost = settings.getBoolean("refreshafterpost", false);
		_Refreshonlaunch = settings.getBoolean("refreonlaunch", false);
		_Displayicon = settings.getBoolean("displayicon", true);
		_Fontsize = Integer.valueOf(settings.getString("fontsize", "16"));
		_Notification = settings.getBoolean("notification", false);
		_Notification_home = settings.getBoolean("notification_home", false);
		_Notification_mention = settings.getBoolean("notification_mention", false);
		_Notification_direct = settings.getBoolean("notification_direct", false);
		_Notification_interval = Integer.valueOf(settings.getString("notification_interval","86400000"));
		_Notification_sound = settings.getBoolean("notification_sound", true);
		_Notification_ringtone = settings.getString("notification_ringtone", "content://settings/system/notification_sound");
		_Notification_vibrate = settings.getBoolean("notification_vibrate", true);
		_Notification_led = settings.getBoolean("notification_led", true);
//		_PictureQuality = Integer.valueOf(settings.getString("picturequality","1"));
		_Theme = Integer.valueOf(settings.getString("theme",String.valueOf(R.style.DefaultTheme)));
		_RemoveAD = settings.getBoolean("removead", false);

//        if(_Baseapi.length() !=0 ){
//        	_Https = isHttps(_Baseapi);
//            _Baseapi = normalizeURL(_Baseapi);
//        }else{
//        	_Baseapi = "twitter.com";
//        }
        
//        if(_Searchapi.length() !=0 ){
//        	_Searchapi = normalizeURL(_Searchapi);
//        }else{
//        	_Searchapi = "search.twitter.com";
//        }

        
        if(_Notification_ringtone.length() !=0 ) _Notification_ringtone.trim();
	}
    
    public void InitTwitterClient(){

        URI host = null;
        int port;
        String url,searchurl;
        
        if(_Https == true){
        	url = String.format("https://%s", _Baseapi);
        	searchurl = String.format("https://%s", _Searchapi);
        	port = PORTSSL;
        }else{
        	url = String.format("http://%s", _Baseapi);
        	searchurl = String.format("http://%s", _Searchapi);
        	port = PORT;
        }

        try{
        	host = new URI(url);
        }catch (Exception e){}
        
		_twitter = new TwitterClient(this, host.getHost(), port, url, searchurl, _Username, _Password);
    }
    
	@Override
	public void onCreate() {
		super.onCreate();
		
    	ExceptionHandler.register(this, "http://catchreport.appspot.com/catch");
    	
        try{
        	_DbHelper = new DbAdapter(this);
        	_DbHelper.open();
        	_DbHelper.CleanDB();
        }catch(Exception e){
        	e.printStackTrace();
        }
        
    	LoadSettings(this);

		if (_NotificationMgr == null){
			_NotificationMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		}
		_NotificationMgr.cancelAll();
		
        if (_NotiServices == null){
        	_NotiServices = new Intent(this,  AutoRefreshService.class);
        	startService(_NotiServices);
        }
        
        Cursor c = _DbHelper.QueryAccounts(null);
        if(c != null){
        	if(c.getCount() == 0){
        		if( TextUtils.isEmpty(_Username) == false && TextUtils.isEmpty(_Password) == false){
        			
        	    	ContentValues initialValues = new ContentValues();    	
        	    	initialValues.put(DBTweetsHelper.KEY_USERNAME, _Username);
        	    	initialValues.put(DBTweetsHelper.KEY_PASSWORD, _Password);
        			
        			_DbHelper.updateAccounts(_Username, initialValues);
        		}
        	}
        	c.close();
        }
        
        InitTwitterClient();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}
}
