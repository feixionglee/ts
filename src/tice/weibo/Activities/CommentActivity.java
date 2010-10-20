package tice.weibo.Activities;

import android.os.Bundle;
import tice.weibo.R;
import tice.weibo.List.TweetsListActivity;
import tice.weibo.Util.TwitterItem;

public class CommentActivity extends TweetsListActivity {
	private TwitterItem mItem;
	TweetsListActivity mCtx;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		mCtx = (TweetsListActivity)this;
//		_Handler = mHandler;
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.commentblog);
		
		setupViews();
	}
	
	private void setupViews(){
		
	} 
	
}
