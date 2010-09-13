
package tice.twitterwalk.trace;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class DefaultExceptionHandler implements UncaughtExceptionHandler {

	private UncaughtExceptionHandler defaultExceptionHandler;
	
	public DefaultExceptionHandler(UncaughtExceptionHandler pDefaultExceptionHandler) {
		defaultExceptionHandler = pDefaultExceptionHandler;
	}
	
	public void uncaughtException(Thread t, Throwable e) {
		
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		e.printStackTrace(printWriter);

		String output = "";
		PackageManager pm = G.mContext.getPackageManager();
		
		try {
			PackageInfo pi;
			pi = pm.getPackageInfo(G.mContext.getPackageName(), 0);
			G.APP_VERSION = pi.versionName;
			G.APP_PACKAGE = pi.packageName;
			G.FILES_PATH = G.mContext.getFilesDir().getAbsolutePath();
			
			output  = "Board:" + android.os.Build.BOARD + "\n";
			output += "BRAND:" + android.os.Build.BRAND + "\n";
			output += "Device:" + android.os.Build.DEVICE + "\n";
			output += "Display:" + android.os.Build.DISPLAY + "\n";
			output += "FingerPrint:" + android.os.Build.FINGERPRINT + "\n";
			output += "Host:" + android.os.Build.HOST + "\n";
			output += "ID:" + android.os.Build.ID + "\n";
			output += "Model:" + android.os.Build.MODEL + "\n";
			output += "Product:" + android.os.Build.PRODUCT + "\n";
			output += "TAGS:" + android.os.Build.TAGS + "\n";
			output += "TYPE:" + android.os.Build.TYPE + "\n";
			output += "USER:" + android.os.Build.USER + "\n";
			output += "USER:" + android.os.Build.VERSION.RELEASE + "\n";
			
		} catch (NameNotFoundException ex) { }

		try{
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(G.URL);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps .add(new BasicNameValuePair("package_version", G.APP_VERSION));
			nvps.add(new BasicNameValuePair("package_name", output));
			nvps.add(new BasicNameValuePair("stacktrace", result.toString()));
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			httpClient.execute(httpPost);
		} catch (Exception exx) {}
       
		defaultExceptionHandler.uncaughtException(t, e);
	}

}