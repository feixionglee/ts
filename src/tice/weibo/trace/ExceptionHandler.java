
package tice.weibo.trace;

import android.content.Context;

public class ExceptionHandler {
	
	private static void register() {
		new Thread() {
			@Override
			public void run() {
				UncaughtExceptionHandler currentHandler = Thread.getDefaultUncaughtExceptionHandler();
				// don't register again if already registered
				if (!(currentHandler instanceof DefaultExceptionHandler)) {
					// Register default exceptions handler
					Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(currentHandler));
				}
			}
		}.start();
	}	
	
	public static void register(Context c, String url) {
		G.mContext = c; 
		G.URL = url;
		register();
	}
}