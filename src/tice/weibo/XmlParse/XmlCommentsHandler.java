package tice.weibo.XmlParse;

import java.util.Date;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import tice.weibo.Util.CommentItem;
import tice.weibo.Util.CommentsData;
import android.text.Html;

public class XmlCommentsHandler extends DefaultHandler {
	private boolean in_comment = false;
    private boolean in_status = false; 
    private boolean in_user = false; 

    private boolean in_error = false;
    
    private StringBuilder builder;
    private CommentItem mItem = null;
	private CommentsData mCommentsData = null;
    
	public CommentsData GetParsedData() { 
		return mCommentsData; 
	} 
    
//	public XmlCommentsHandler(){
//		
//	}
	
    @Override 
    public void startDocument() throws SAXException { 
         mCommentsData = new CommentsData(); 
    } 

    @Override 
    public void endDocument() throws SAXException { 
         // Nothing to do 
    } 
    
    @Override 
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException { 
    	if (localName.equals("comment")){
        	in_comment = true;
        	mItem = new CommentItem();
        	builder = new StringBuilder();
        }else if (localName.equals("status")){
        	in_status = true;
        }else if (localName.equals("user")){
        	in_user = true;
        }else if (localName.equals("error")){
        	in_error = true;
        	builder = new StringBuilder();
        }
    } 
     
    @Override 
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException { 
        
    	if (localName.equals("comment")){
        	in_comment = false;
        	mCommentsData.items.add(mItem);
        } else if (localName.equals("status")){
        	in_status = false;
        } else if (localName.equals("user")){
        	in_user = false;
        } else if (localName.equals("error")){
        	in_error = false;
        	mCommentsData.mError = builder.toString();
        	builder.setLength(0);
        }
    	
    	if (in_comment){
    		String body = builder.toString().trim();
    		if(in_user == false && in_status == false){
	    		if (localName.equals("created_at")){
		        	mItem.mTime = Date.parse(body);
		        } else if (localName.equals("id") ){
		        	mItem.mID = Long.valueOf(body);
		        } else if (localName.equals("text")){
		        	mItem.mText = String.format("%s", Html.fromHtml(body));
		        } 
    		}
    		builder.setLength(0);
    	} 
    } 
     
    @Override 
	public void characters(char ch[], int start, int length) { 
    	if (in_comment || in_error == true){
    		if(ch[start] == '\r' || ch[start] == '\n') return;
    		builder.append(ch, start, length);
    	}
    } 
}
