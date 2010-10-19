package tice.weibo.Activities;

import tice.weibo.R;
import tice.weibo.DB.DBPicsHelper;
import tice.weibo.List.TweetsListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ZoomControls;

public class ImageViewerActivity extends TweetsListActivity {
	TweetsListActivity mCtx;
	Bitmap image;
	long status_id;
	
    static final String ACTION = "tice.weibo.Activities.ImageViewerActivity";
    private static final String EXTRA_ITEM = "tice.weibo.image";

    private final Handler mHandler = new Handler();
    
    @Override
	public void onCreate(Bundle savedInstanceState){
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.imageviewer);
		setupViews();
	}
	
	static void show(Context context, long status_id){
		final Intent intent = new Intent(ACTION);
        intent.putExtra(EXTRA_ITEM, status_id);
        context.startActivity(intent);
	}
	
	public void setupViews(){
		mCtx = (TweetsListActivity)this;
		_Handler = mHandler;
		
		final Intent intent = getIntent();
        final Bundle extras = intent.getExtras(); 
        
        status_id = extras.getLong(EXTRA_ITEM);
		image = _App._twitter.LoadPic(_Handler, status_id, "");
		
		ImageView shit = (ImageView) findViewById(R.id.ivImage);
		shit.setImageBitmap(image);
		
	}
	
	public void setZoomControls(){
		ZoomControls zm = (ZoomControls) findViewById(R.id.zcZooms);
		if (zm != null){
			zm.setOnZoomInClickListener(new OnClickListener(){
				public void onClick(View v){
					System.out.println("in");
				}
			});
			zm.setOnZoomOutClickListener(new OnClickListener(){
				public void onClick(View v){
					System.out.println("out");
				}
			});	
			
		}
	}
}
