package tice.weibo.Activities;

import java.util.ArrayList;

import tice.weibo.App;
import tice.weibo.R;
import tice.weibo.DB.DBTweetsHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class AccountsActivity extends Activity {

	public App _App = null;
	ArrayAdapter<String> _Accounts;
	private  ListView listview;
	private ListAdapter listadapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        _App = (App)getApplicationContext();
        
        setContentView(R.layout.accounts);
        
        listadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice);
        
        listview = (ListView)findViewById(android.R.id.list);
        listview.setAdapter(listadapter);
        listview.setEmptyView(findViewById(android.R.id.empty));
        listview.setItemsCanFocus(false);
        listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listview.setOnItemClickListener(new OnItemClickListener(){

        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		String user = (String)listadapter.getItem(position);
        		String pass = GetPasswordFromUsername(user);
        		if(TextUtils.isEmpty(pass) == false){
	   		        SaveUsernameAndPassword(user,pass);
        		}
			}
        });
        
        TextView add = (TextView)findViewById(R.id.addaccount);
        TextView edit = (TextView)findViewById(R.id.editaccount);
        TextView del = (TextView)findViewById(R.id.delaccount);
        

        
        
        final LayoutInflater factory = LayoutInflater.from(this);
        add.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
                final View textEntryView = factory.inflate(R.layout.userpass, null);
	            new AlertDialog.Builder(AccountsActivity.this)
                .setTitle("Add account")
                .setView(textEntryView)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        EditText user = (EditText) textEntryView.findViewById(R.id.username_edit);
                        EditText pass = (EditText) textEntryView.findViewById(R.id.password_edit);
                    	String username = user.getText().toString();
                    	String password = pass.getText().toString();
                    	if(TextUtils.isEmpty(username) == false && TextUtils.isEmpty(password) == false){
                	    	ContentValues initialValues = new ContentValues();    	
                	    	initialValues.put(DBTweetsHelper.KEY_USERNAME, username);
                	    	initialValues.put(DBTweetsHelper.KEY_PASSWORD, password);
                    		_App._DbHelper.updateAccounts(username, initialValues);
                    		SaveUsernameAndPassword(username, password);
                    		LoadAccountsFromDB();
                    	}
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).show();
        	}
        });
        
        edit.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
                final View textEntryView = factory.inflate(R.layout.userpass, null);
                final EditText user = (EditText) textEntryView.findViewById(R.id.username_edit);
                final EditText pass = (EditText) textEntryView.findViewById(R.id.password_edit);
                user.setText(_App._Username);
                pass.setText(_App._Password);
	            new AlertDialog.Builder(AccountsActivity.this)
                .setTitle("Edit account")
                .setView(textEntryView)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	String username = user.getText().toString();
                    	String password = pass.getText().toString();
                    	if(TextUtils.isEmpty(username) == false && TextUtils.isEmpty(password) == false){
                	    	ContentValues initialValues = new ContentValues();    	
                	    	initialValues.put(DBTweetsHelper.KEY_USERNAME, username);
                	    	initialValues.put(DBTweetsHelper.KEY_PASSWORD, password);
                    		_App._DbHelper.updateAccounts(username, initialValues);
                    		SaveUsernameAndPassword(username, password);
                    		LoadAccountsFromDB();
                    	}
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).show();
        	}
        });
        
        del.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		if(GetCkeckItem() == -1) return;
	            new AlertDialog.Builder(AccountsActivity.this)
                .setTitle("Delete account")
                .setMessage("Are you sure want to delete it?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                	int position = GetCkeckItem();
                	public void onClick(DialogInterface dialog, int whichButton) {
                		String password;
                		String username = (String)listadapter.getItem(position);
                    	_App._DbHelper.deleteAccount(username);
                    	LoadAccountsFromDB();
                    	username = ""; password="";
                    	int count = listadapter.getCount() - 1;
                    	if(count >=0 ){
                    		username = (String)listadapter.getItem(0);
                    		password = GetPasswordFromUsername(username);
                    	}
                    	SaveUsernameAndPassword(username,password);
                    	LoadAccountsFromDB();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).show();	
        	}
        });

    }
	
    @Override
	protected void onResume() {
        super.onResume();
        LoadAccountsFromDB();
	}

//	@Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.accounts,menu);
//
//        return true;
//    }
 	
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	super.onMenuItemSelected(featureId, item);

        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.userpass, null);

    	switch(item.getItemId()) {
	        case R.id.addaccount:
	            new AlertDialog.Builder(AccountsActivity.this)
	                .setTitle("Add account")
	                .setView(textEntryView)
	                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int whichButton) {
	                        EditText user = (EditText) textEntryView.findViewById(R.id.username_edit);
	                        EditText pass = (EditText) textEntryView.findViewById(R.id.password_edit);
	                    	String username = user.getText().toString();
	                    	String password = pass.getText().toString();
	                    	if(TextUtils.isEmpty(username) == false && TextUtils.isEmpty(password) == false){
	                	    	ContentValues initialValues = new ContentValues();    	
	                	    	initialValues.put(DBTweetsHelper.KEY_USERNAME, username);
	                	    	initialValues.put(DBTweetsHelper.KEY_PASSWORD, password);
	                    		_App._DbHelper.updateAccounts(username, initialValues);
	                    		SaveUsernameAndPassword(username, password);
	                    		LoadAccountsFromDB();
	                    	}
	                    }
	                })
	                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int whichButton) {
	                    	
	                    }
	                }).show();
	            break;
	        case R.id.delaccount:
	        	if(GetCkeckItem() == -1) return true;
	            new AlertDialog.Builder(AccountsActivity.this)
                .setTitle("Delete account")
                .setMessage("Are you sure want to delete it?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                	int position = GetCkeckItem();
                	public void onClick(DialogInterface dialog, int whichButton) {
                		String password;
                		String username = (String)listadapter.getItem(position);
                    	_App._DbHelper.deleteAccount(username);
                    	LoadAccountsFromDB();
                    	username = ""; password="";
                    	int count = listadapter.getCount() - 1;
                    	if(count >=0 ){
                    		username = (String)listadapter.getItem(0);
                    		password = GetPasswordFromUsername(username);
                    	}
                    	SaveUsernameAndPassword(username,password);
                    	LoadAccountsFromDB();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).show();	        	
	        	break;
    	} 
	    
    	return true;
    }
    
    private void SaveUsernameAndPassword(String user, String pass){
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(AccountsActivity.this); 
		SharedPreferences.Editor editor = settings.edit(); 
		editor.putString("username", user);
		editor.putString("password", pass);
		_App._Username = user;
		_App._Password = pass;
		editor.commit();
    }
    
    private String GetPasswordFromUsername(String user){
		String pass = "";
    	Cursor c = _App._DbHelper.QueryAccounts(user);
		if(c != null){
			if(c.getCount() != 0){
				c.moveToFirst();
				pass = c.getString(DBTweetsHelper.COL_PASSWORD);
			}
			c.close();
		}
		
		return pass;
    }
    
    private void LoadAccountsFromDB(){
    	Cursor c = _App._DbHelper.QueryAccounts(null);
    	ArrayList<String> accounts = new ArrayList<String>(0);
    	String user;
        
    	if(c != null){
    		if(c.getCount() != 0){
    			c.moveToFirst();
    			do{
    				user = c.getString(DBTweetsHelper.COL_USERNAME);
    				accounts.add(user);
    			}while (c.moveToNext());
    		}
    		c.close();
    	}
    	listadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, accounts);
        listview.setAdapter(listadapter);
        
        CheckAllItem();
    }
    
    private void CheckAllItem(){
    	
   		String user;
   		int checked = -1;
   		
    	for (int i =0;i<listadapter.getCount();i++){
    		user = (String)listadapter.getItem(i);
			if(user.equalsIgnoreCase(_App._Username) == true){
				checked = i;
			}
    	}
    	
   		listview.setItemChecked(checked, true);
    }
    
    private int GetCkeckItem(){
    	for (int i =0;i<listadapter.getCount();i++){
    		if(listview.isItemChecked(i) == true){
    			return i;
    		}
    	}    	
    	
    	return -1;
    }
}
