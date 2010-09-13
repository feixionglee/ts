package tice.twitterwalk.Util;

import tice.twitterwalk.DB.DbAdapter;
import tice.twitterwalk.HttpClient.TwitterClient;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

public class Twitter extends Application {

	public boolean _InApp = false;
	public boolean _ReloadSettings = false;
	public DbAdapter _DbHelper;
	public TwitterClient _twitter;
	public Intent _NotiServices = null;
	public NotificationManager _NotificationMgr = null;

	public Activity _MainAtivity;
	public GoogleAnalyticsTracker _tracker = null;
	public Context _this;
	
	public String _Username = "";
	public String _Password = "";
	public String _Baseapi = "";
	public String _Searchapi = "";
	public String _Pictureapi = "";
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
}
