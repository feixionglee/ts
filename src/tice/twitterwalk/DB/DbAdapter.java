/*
 * Copyright (C) 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package tice.twitterwalk.DB;

import tice.twitterwalk.Util.TwitterItem;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

public class DbAdapter {
    
    private DBTweetsHelper mDBTweetsHelper = null;;
    private DBImagesHelper mDBImagesHelper = null;;
    private DBPicsHelper mDBPicsHelper = null;
    
    private final Context mCtx;

    public DbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    synchronized public DbAdapter open() throws SQLException {
    	try{
    		mDBTweetsHelper = new DBTweetsHelper(mCtx);
    		mDBImagesHelper = new DBImagesHelper(mCtx);
    		mDBPicsHelper = new DBPicsHelper(mCtx);
    	}catch (Exception e){}
        return this;
    }
    
    synchronized public void close() {
        try{	
    		if(mDBTweetsHelper != null) mDBTweetsHelper.close();
        	if(mDBImagesHelper != null) mDBImagesHelper.close();
        	if(mDBPicsHelper != null) mDBPicsHelper.close();
		}catch (Exception e){}
    }

    public void ClearCache(){
    	try{
    		mDBImagesHelper.ClearCache();
    		mDBPicsHelper.ClearCache();
    	}catch (Exception e){}
    }

    public void CleanDB(){
    	try{
    		mDBImagesHelper.CleanDB();
    		mDBTweetsHelper.CleanDB();
    		mDBPicsHelper.CleanDB();
    	}catch (Exception e){}
    }
    
    public long InsertImage(String screenmane, byte[] data ){
    	try{
    		return mDBImagesHelper.InsertImage( screenmane, data );
    	}catch (Exception e){}
    	return -1;
    }
        
    public Cursor fetchImage(String screenmane){
        return mDBImagesHelper.fetchImage(screenmane);
    }
    
    public long InsertPic(Long status_id, byte[] data ){
    	try{
    		return mDBPicsHelper.InsertPic( status_id, data );
    	}catch (Exception e){}
    	return -1;
    }
    
    public Cursor fetchPics(Long status_id){
        return mDBPicsHelper.fetchPic(status_id);
    }
    
	public void beginTransaction(){
		mDBTweetsHelper.beginTransaction();
	}

	public void endTransaction(){
		mDBTweetsHelper.endTransaction();
	}
	
	public void setTransactionSuccessful(){
		mDBTweetsHelper.setTransactionSuccessful();
	}
    
    public long updatetweet(String account, int type, long id, ContentValues values) {
    	try{
    		return mDBTweetsHelper.updatetweet(account, type, id, values);
    	}catch (Exception e){}
    	return -1;
    }  
	
    public long createtweet(int type, TwitterItem item) {
    	try{
    		return mDBTweetsHelper.createtweet(type, item);
    	}catch (Exception e){}
    	return -1;
    }    

    public long updatetweet(int type, TwitterItem item) {
    	try{
    		return mDBTweetsHelper.updatetweet(type, item);
    	}catch (Exception e){}
    	return -1;
    }    
    
    public boolean deleteAll(String account, int type) {
    	try{
    		return  mDBTweetsHelper.deleteAll(account, type);
    	}catch (Exception e){}
    	return false;
    }

    public Cursor fetchAll(String account, int type, String order, String limit) {
    	return mDBTweetsHelper.fetchAll(account, type, order, limit);
    }
    
    public Cursor QueryTweet(String account, int type, String id){
    	return mDBTweetsHelper.QueryTweet(account, type, id);
    }

    public boolean FindTweet(String account, int type, long id){
    	return mDBTweetsHelper.FindTweet(account, type, id); 
    }
    
    public long FetchMInID(String account, int type){
    	return mDBTweetsHelper.FetchMInID(account, type);
    }
    
    public long FetchMaxID(String account, int type){
    	return mDBTweetsHelper.FetchMaxID(account, type);
    }
    
    public Cursor QueryAccounts(String user){
    	return mDBTweetsHelper.QueryAccounts(user);
    }

    public long deleteAccount(String user) {
    	try{
    		return mDBTweetsHelper.deleteAccount(user);
    	}catch (Exception e){}
    	return -1;
    }
    
    public long updateAccounts(String user, ContentValues initialValues) {
    	try{
    		return mDBTweetsHelper.updateAccounts(user, initialValues);
    	}catch (Exception e){}
    	return -1;
    }  
}