package tice.weibo.XmlParse;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import tice.weibo.Util.UserData;

public class XmlUserHandler extends XmlMentionsHandler {

    public XmlUserHandler(int type) {
		super(type);
	}

	@Override 
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException { 
    	
    	super.startElement(namespaceURI, localName, qName, atts);
    	
    	if (localName.equals("user")){
        	in_user = true;
        	mTweetsData.user = new UserData();
        }
    } 
	
    @Override 
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException { 

    	if(in_user){

    		String body = builder.toString().trim();

    		if (localName.equals("created_at")){
    			mTweetsData.user.mCreated_at = body;
	        } else if (localName.equals("description")){
	        	mTweetsData.user.mDescription = body;
	        } else if (localName.equals("followers_count")){
	        	mTweetsData.user.mFollowers_count = body;
	        } else if (localName.equals("friends_count")){
	        	mTweetsData.user.mFriends_count = body;
	        } else if (localName.equals("id")){
	        	mTweetsData.user.mID = body;
	        } else if (localName.equals("location")){
	        	mTweetsData.user.mLocation = body;
	        } else if (localName.equals("location")){
	        	mTweetsData.user.mLocation = body;
	        } else if (localName.equals("profile_image_url")){
	        	mTweetsData.user.mProfile_image_url = body;
	        } else if (localName.equals("screen_name")){
	        	mTweetsData.user.mScreenname = body;
	        } else if (localName.equals("statuses_count")){
	        	mTweetsData.user.mStatuses_count = body;
	        } else if (localName.equals("time_zone")){
	        	mTweetsData.user.mTime_zone = body;
	        } else if (localName.equals("name")){
	        	mTweetsData.user.mTitle = body;
	        } else if (localName.equals("url")){
	        	mTweetsData.user.mUrl = body;
	        }
	    }
    	
    	super.endElement(namespaceURI, localName, qName);
    } 
}
