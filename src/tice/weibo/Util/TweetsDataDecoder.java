package tice.weibo.Util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Iterator;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import tice.weibo.HttpClient.TwitterClient;
import tice.weibo.XmlParse.XmlDirectsHandler;
import tice.weibo.XmlParse.XmlFavoritesHandler;
import tice.weibo.XmlParse.XmlFriendsHandler;
import tice.weibo.XmlParse.XmlHomeHandler;
import tice.weibo.XmlParse.XmlMentionsHandler;
import tice.weibo.XmlParse.XmlStatusesHandler;
import tice.weibo.XmlParse.XmlUserHandler;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;

public class TweetsDataDecoder{

	public static String ReplaceStr_1 ="</iframe></noscript></object></layer></span></div></table></body></html><!-- adsok -->";
	public static String ReplaceStr_2 ="<script language='javascript' src='https://a12.alphagodaddy.com/hosting_ads/gd01.js'></script>";
	public static String ReplaceStr_All ="</iframe>.*</script>";

 	static public String inputStreamToString(HttpEntity entity ) throws IOException{

 		byte[] bytes = EntityUtils.toByteArray(entity);
 		return new String(bytes);
 	}

	public static String convertStreamToString(InputStream is) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line + "");
		}
		is.close();
		return sb.toString();
	}

	private InputStream RemoveAD(boolean remove, InputStream in){
		if(remove == false) return in;
		InputStream out = null;
		try{
			String str = convertStreamToString(in);
			//str = str.replaceAll(ReplaceStr_1, "");
			//str = str.replaceAll(ReplaceStr_2, "");
			str = str.replaceAll(ReplaceStr_All, "");
			out = new ByteArrayInputStream(str.getBytes("UTF-8"));
		} catch (Exception e){}
		return out;
	}

	private String RemoveAD(boolean remove, String in){
		if(remove == false) return in;
		String str = in;
		str = str.replaceAll(ReplaceStr_1, "");
		str = str.replaceAll(ReplaceStr_2, "");
		return str;
	}

	public TweetsData Decoder(int format, Handler handler, int type, HttpEntity data, boolean removead){
		try{
			if (format == TwitterClient.REQUEST_TYPE_JSON){
				return DecoderJSON(handler, type, RemoveAD(removead, inputStreamToString(data)));
			}else if (format == TwitterClient.REQUEST_TYPE_XML){
				return DecoderXML(handler, type, RemoveAD(removead, data.getContent()));
			}
		} catch (Exception e) {
			Bundle err = new Bundle();
			String strerr = String.format("%s", e.getLocalizedMessage());
			err.putString(TwitterClient.KEY, strerr);
			TwitterClient.SendMessage(handler, TwitterClient.HTTP_ERROR, err);
		}

		return null;
	}

	private TweetsData DecoderJSON(Handler handler, int type, String data){

		TweetsData value = null;
		if(data == null) return null;

		try {

			switch (type){
			case TwitterClient.HTTP_SEARCH:
			case TwitterClient.HTTP_SEARCH_NEXT:
				{
					JSONObject result = new JSONObject(data);
					JSONArray lines = result.getJSONArray("results");
		 			int count = lines.length();
		 			if (count <= 0) return null;

		 			JSONObject aline;
		 	 		String source;

		 	 		value = new TweetsData();
		 	 		TwitterItem item = null;

		 			for (int i = 0; i < count; i++){
		 				item = new TwitterItem();
		 				aline = lines.getJSONObject(i);
		 				item.mText =  aline.getString("text");
		 				item.mScreenname = aline.getString("from_user");
		 				item.mTitle = aline.getString("from_user");
		 				item.mTime = Date.parse(aline.getString("created_at"));
		 				item.mID = aline.getLong("id");
		 				item.mImageurl = aline.getString("profile_image_url");
		 				try{
		 					item.mPicurl = aline.getString("bmiddle_pic");
		 				}catch(Exception e) {}
		 				item.mReplyID = "null";
		 				source = aline.getString("source");
		 				source = source.replaceAll("&lt;", "<");
		 				source = source.replaceAll("&gt;", ">");
		 				source = source.replaceAll("&quot;", "\"");
		 				item.mSource = String.format("%s", Html.fromHtml(source));
		 				value.items.add(item);
		 			}
					break;
				}
	    	case TwitterClient.HTTP_TRENDS_CURRENT:
	    	case TwitterClient.HTTP_TRENDS_DAILY:
	    	case TwitterClient.HTTP_TRENDS_WEEKLY:
	    		{
	    			JSONObject result = new JSONObject(data);
	     			JSONObject trends = result.getJSONObject("trends");
	     			Iterator<?> it = trends.keys();

	     			while(it.hasNext()){
	     				String key = (String)it.next();
	     				JSONArray datas = trends.getJSONArray(key);

	     				int count = datas.length();
	     				if (count <= 0) return null;

	     				value = new TweetsData();
	     				TwitterItem item = null;

	     				for(int i=0;i<count;i++){
		     				item = new TwitterItem();
	     					JSONObject aline = datas.getJSONObject(i);
	     					item.mReplyID = aline.getString("query");
	     					item.mText = aline.getString("name");
	     					item.mScreenname = "";
	     					value.items.add(item);
	     				}
	     			}

	    			break;
				}
	    	case TwitterClient.HTTP_FRIENDSHIPS_SHOW:
	    		{
	    			value = new TweetsData(1);
	    			value.mJSONObject = new JSONObject(data);
	    			break;
	    		}
	    	case TwitterClient.HTTP_CHECK_VERSION:
	    		{
	    			value = new TweetsData(1);
	    			value.mData = data;
	    			break;
	    		}
			case TwitterClient.HTTP_RATE_LIMIT:
				{
	    			value = new TweetsData(1);
	    			value.mJSONObject = new JSONObject(data);
	    			break;
				}
			}

		} catch (JSONException e) {
			Bundle err = new Bundle();
			String strerr = String.format("%s", data);
			err.putString(TwitterClient.KEY, strerr);
			TwitterClient.SendMessage(handler, TwitterClient.HTTP_ERROR, err);
		}

		return value;
	}

	private TweetsData DecoderXML(Handler handler, int type, InputStream data){
		TweetsData value = null;
		if(data == null) return null;

		try{

			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();

			switch (type){
			case TwitterClient.HTTP_HOME_TIMELINE:
			case TwitterClient.HTTP_HOME_TIMELINE_MAXID:
			case TwitterClient.HTTP_HOME_TIMELINE_SINCEID:{
				XmlHomeHandler home = new XmlHomeHandler(0);
				xr.setContentHandler(home);
				xr.parse(new InputSource(data));
				value = home.GetParsedData();
				break;
			}
			case TwitterClient.HTTP_MENTIONS_TIMELINE:
			case TwitterClient.HTTP_MENTIONS_TIMELINE_MAXID:
			case TwitterClient.HTTP_MENTIONS_TIMELINE_SINCEID:{
				XmlMentionsHandler mention = new XmlMentionsHandler(0);
				xr.setContentHandler(mention);
				xr.parse( new InputSource(data));
				value = mention.GetParsedData();
				break;
			}
			case TwitterClient.HTTP_FAVORITES_TIMELINE:
			case TwitterClient.HTTP_FAVORITES_TIMELINE_MAXID:
			case TwitterClient.HTTP_FAVORITES_TIMELINE_SINCEID:{
				XmlFavoritesHandler favorites = new XmlFavoritesHandler(0);
				xr.setContentHandler(favorites);
				xr.parse( new InputSource(data));
				value = favorites.GetParsedData();
				break;
			}
			case TwitterClient.HTTP_USER_TIMELINE:
			case TwitterClient.HTTP_USER_TIMELINE_MAXID:
			case TwitterClient.HTTP_USER_TIMELINE_SINCEID:{
				XmlUserHandler user = new XmlUserHandler(0);
				xr.setContentHandler(user);
				xr.parse( new InputSource(data));
				value = user.GetParsedData();
				break;
			}
			case TwitterClient.HTTP_DIRECT_TIMELINE:
			case TwitterClient.HTTP_DIRECT_TIMELINE_MAXID:
			case TwitterClient.HTTP_DIRECT_TIMELINE_SINCEID:
			case TwitterClient.HTTP_DIRECT_TIMELINE_SENT:
			case TwitterClient.HTTP_DIRECT_TIMELINE_SENT_MAXID:
			case TwitterClient.HTTP_DIRECT_TIMELINE_SENT_SINCEID:{
					XmlDirectsHandler directs = new XmlDirectsHandler(0);
					xr.setContentHandler(directs);
					xr.parse( new InputSource(data));
					value = directs.GetParsedData();
					break;
		 		}
			case TwitterClient.HTTP_STATUSES_SHOW:
				{
					XmlStatusesHandler statuses = new XmlStatusesHandler(0);
					xr.setContentHandler(statuses);
					xr.parse( new InputSource(data));
					value = statuses.GetParsedData();
					break;
				}
			case TwitterClient.HTTP_FRIENDS_TIMELINE:
			case TwitterClient.HTTP_FRIENDS_TIMELINE_MAXID:
			case TwitterClient.HTTP_FRIENDS_TIMELINE_SINCEID:
			case TwitterClient.HTTP_FOLLOWERS_TIMELINE:
			case TwitterClient.HTTP_FOLLOWERS_TIMELINE_MAXID:
			case TwitterClient.HTTP_FOLLOWERS_TIMELINE_SINCEID:{
					XmlFriendsHandler friends = new XmlFriendsHandler(0);
					xr.setContentHandler(friends);
					xr.parse( new InputSource(data));
					value = friends.GetParsedData();
					break;
				}
			}

		} catch (Exception e) {
			Bundle err = new Bundle();
			String strerr = String.format("%s", e.getLocalizedMessage());
			err.putString(TwitterClient.KEY, strerr);
			TwitterClient.SendMessage(handler, TwitterClient.HTTP_ERROR, err);
		}

		return value;
	}
}

