package tice.twitterwalk.Setting;

import tice.twitterwalk.App;
import tice.twitterwalk.R;
import tice.twitterwalk.Activities.AccountsActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

import android.content.pm.PackageInfo;

public class Setting extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	public App _App = null;

    private String getVersionName(){
        try {
            PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return pinfo.versionName;
        } catch (android.content.pm.PackageManager.NameNotFoundException e) {
            return null;
        }
    }
	
	@Override
    protected void onCreate(Bundle savedInstanceState){
		_App = (App)getApplicationContext();
		super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting);
    }
	

    @Override
    protected void onPause() {
        super.onPause();
        _App._ReloadSettings = true;
    }

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

		String key = preference.getKey();
		
		if (key != null) {
	        if (preference.getKey().equals("clearcache")) {
	        	try{
		            new AlertDialog.Builder(this)
		            .setTitle("Delete cache confirmation")
		            .setMessage("Are you sure want to delete cache?")
		            .setNegativeButton("No", new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int whichButton) {
		                }
		            })
		            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int whichButton) {
		                	_App._DbHelper.ClearCache();
		                }
		            })
		            .show();
	        	}catch (Exception err){}
	        }else if (preference.getKey().equals("about")){
	        	new AlertDialog.Builder(this)
	        	.setIcon(R.drawable.icon)
	        	.setTitle(getText(R.string.app_name))
                .setMessage(String.format("version: %s\nauthor: %s\nthanks: %s",
                        getVersionName(),
                        getText(R.string.author),
                        getText(R.string.thanks)))
	        	.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface dialog, int whichButton) {
	        			}
	               })
	        	.show();
	        }else if (preference.getKey().equals("accounts")){
	        	Intent t = new Intent(this, AccountsActivity.class);
	        	startActivityForResult(t, 0);
	        }
		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
	}

}
