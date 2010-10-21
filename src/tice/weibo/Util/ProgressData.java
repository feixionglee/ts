package tice.weibo.Util;

import java.util.ArrayList;

public class ProgressData{
	public int addtype;
	public ArrayList<TwitterItem> items;
	public ArrayList<CommentItem> citems;
	
	public ProgressData(){
		items = new ArrayList<TwitterItem>();
		citems = new ArrayList<CommentItem>();
	}
	
}