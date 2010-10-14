package tice.weibo.Activities;

import tice.weibo.App;
import tice.weibo.R;
import tice.weibo.List.TweetsListActivity;
import tice.weibo.Util.TwitterItem;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DetailWeiboActivity extends TweetsListActivity implements View.OnClickListener{
	private TwitterItem mItem;

	
	public class ViewHolder {
		TextView title;
		TextView text;
		TextView retweeted_text;
		TextView timesource;
		ImageView icon;
		//IconImageView icon_right;
		LinearLayout itemline;
		LinearLayout imagelayout;
		//LinearLayout imagelayout_right;
		ImageView unread;
		ProgressBar progressbar;
		ProgressBar retweeted_progressbar;
		ImageView favorite;
		ImageView conversation;
		ImageView pic;
	}
	
    static final String ACTION = "tice.weibo.Activities.DetailWeiboActivity";
    private static final String EXTRA_ITEM = "tice.weibo.Util.TwittterItem";
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		mItem = getTweet();
		
		setContentView(R.layout.detailweibo);
		setupViews();
	}
	
	public static void show(Context context, TwitterItem item) {
		final Intent intent = new Intent(ACTION);
        intent.putExtra(EXTRA_ITEM, item);
        context.startActivity(intent);
	}
	
	private void setupViews() {
		ViewHolder holder= new ViewHolder();
		
		holder.text = (TextView) findViewById(R.id.tweet_message);
		holder.text.setText(mItem.mText);
		holder.text.setVisibility(View.VISIBLE);
		
		holder.icon = (ImageView) findViewById(R.id.tweet_profile_preview);
		
       	if(mItem.mPicurl.length() > 0) {
       		holder.pic = (ImageView) findViewById(R.id.tweet_upload_pic);
       		Bitmap picture = _App._twitter.LoadPic(_Handler, mItem.mID, mItem.mPicurl);
       		holder.pic.setImageBitmap(picture);
       		if (picture != null) holder.pic.setVisibility(View.VISIBLE);
       	} 
       	
       	holder.retweeted_text = (TextView) findViewById(R.id.tweet_oriTxt);
       	holder.retweeted_text.setText(mItem.mRetweeted_Text);
       	if (mItem.mRetweeted_Text.length() > 0) findViewById(R.id.src_text_block).setVisibility(View.VISIBLE);
	}
	
	public void onClick(View v){
		
	}
	
	public TwitterItem getTweet(){
        final Intent intent = getIntent();
        final Bundle extras = intent.getExtras();
        
		TwitterItem item = null;
        if (extras != null) {
        	item = extras.getParcelable(EXTRA_ITEM);
        }

        return item;
	}
	
	protected void LoadTweetItemsFromDB(){
		String screenname,title,text,source,replyid,iconuri,picurl,reteeted_screenname,reteeted_text;
		long time,id;
		boolean favorited,following;
		int read;
	}

}
