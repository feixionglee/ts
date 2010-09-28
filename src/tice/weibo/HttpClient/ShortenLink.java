package tice.weibo.HttpClient;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.net.Uri;

import tice.weibo.App;
import tice.weibo.List.ItemAdapter;
import tice.weibo.Util.TweetsDataDecoder;

public class ShortenLink {

	static private String _shortenLink(App app, String link, String text){
		
		String retstr = text;
		String newlink, result;
		String requesturl;
		
		Matcher m1 = ItemAdapter.p1.matcher(text);
		
		HttpGet request;
		HttpResponse response;
		HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpConnectionParams.setSoTimeout(params, 10 * 1000);
		
		try{
			Uri uri = Uri.parse(app._Shortenlinkapi);	
			requesturl = String.format("%s=%s", app._Shortenlinkapi, Uri.encode(link));
			request = new HttpGet(requesturl);
			DefaultHttpClient httpClient = new DefaultHttpClient(params);
			response = httpClient.execute(request);
	 		int status = response.getStatusLine().getStatusCode();  
	 		if (status == HttpStatus.SC_OK) {
	 			result = TweetsDataDecoder.inputStreamToString(response.getEntity());
	 			String host = uri.getHost();
	 			host =	host.replace("http://api.", "http://");
	 			Pattern shortlink = Pattern.compile(String.format("http://%s/[\\w-]+", host),Pattern.CASE_INSENSITIVE);
	 			m1 = shortlink.matcher(result);
	 			if(m1.find()){
	 				newlink = m1.group();
	 				retstr = retstr.replace(link, newlink);
	 			}
	 		}

		}catch (Exception e){ };
		
		return retstr;
	}
	
	static public String ShortenLinkFromText(App app, String text)
	{
		String retstr = text;
		
		Matcher m1 = ItemAdapter.p1.matcher(text);
		ArrayList<String> _links = new ArrayList<String>(0);
		String link;
		
		while(m1.find()){  
			 link = m1.group();
			 if (_links.indexOf(link) == -1){
				 if(link.length() >= 30){
					 _links.add(link);
					 retstr = _shortenLink(app, link, retstr);
				 }
			 }
		}  

		return retstr;
	}
}