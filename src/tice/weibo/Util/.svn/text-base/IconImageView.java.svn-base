package tice.twitterwalk.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.widget.ImageView;

public class IconImageView extends ImageView {
	
	private ShapeDrawable mRoundShape = null;
	private Shader mShader = null;
	
	public IconImageView(Context context) {
		super(context);
		
        if(mRoundShape == null){
        	mRoundShape = new ShapeDrawable(new RoundRectShape(new float[] { 5, 5, 5, 5, 5, 5, 5, 5 }, null, null));
        	mRoundShape.setBounds(0, 0, 48, 48);
        }
       
	}

	public void setImageBitmap(Bitmap bm){
		if(bm != null){
			mRoundShape.getPaint().setShader(null);
			mShader = new BitmapShader(bm, Shader.TileMode.REPEAT ,Shader.TileMode.REPEAT);
			mRoundShape.getPaint().setShader(mShader);
		}else{
			mShader = null;
			super.setImageBitmap(bm);
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		if(mShader != null){
			mRoundShape.draw(canvas);
		}
		
		super.onDraw(canvas);
	}
	
}
