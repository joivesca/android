package com.example.chapter2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import java.net.URL;

import javax.xml.parsers.SAXParser;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;

import org.xml.sax.InputSource;

import org.xml.sax.SAXException;

import org.xml.sax.SAXParseException;

import org.xml.sax.XMLReader;

import org.xml.sax.helpers.DefaultHandler;

import android.graphics.Bitmap;

import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.util.Log;

public class IotdHandler extends DefaultHandler {
	private String url = "http://www.nasa.gov/rss/image_of_the_day.rss";
	private boolean inUrl = false;
	private boolean inTitle = false;
	private boolean inDescription = false;
	private boolean inItem = false;
	private boolean inDate = false;
	private Bitmap image = null;
	private String imageURL = null;
	private String title = null;
	private StringBuffer description = new StringBuffer();
	private String date = null;
	
	private FileCache fileCache;
	public void processFeed() {
		try {
			//This part is added to allow the network connection on a main GUI thread...    
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy); 
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			XMLReader reader = parser.getXMLReader();
			reader.setContentHandler(this);
			InputStream inputStream = new URL(url).openStream();
			reader.parse(new InputSource(inputStream));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(new String("Got Exception General"));
		}
	}
	
	public Bitmap getBitmap(String url) {
		
			File file = fileCache.getFile(url);
	        Bitmap bm = decodeFile(file);
	        if(bm!=null) 
	            return bm;
	        try {
	            Bitmap bitmap=null;
	            URL ImageUrl = new URL(url);
	            HttpURLConnection conn = (HttpURLConnection)ImageUrl.openConnection();
	            conn.setConnectTimeout(50000);
	            conn.setReadTimeout(50000);
	            conn.setInstanceFollowRedirects(true);
	            InputStream is = conn.getInputStream();
	            OutputStream os = new FileOutputStream(file);
	            Utils.CopyStream(is, os);
	            os.close();
	            bitmap = decodeFile(file);
	            return bitmap;
	        } catch (Exception ex){
	           ex.printStackTrace();
	           return null;
	        }
	       
		
	}
	
	private Bitmap decodeFile(File file){
        try {
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(file),null,opt);
            final int REQUIRED_SIZE=70;
            int width_tmp=opt.outWidth, height_tmp=opt.outHeight;
            int scale=1;
            while(true){
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale*=2;
            }
            BitmapFactory.Options opte = new BitmapFactory.Options();
            opte.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(file), null, opte);
        } catch (FileNotFoundException e) {}
        return null;
    }
	
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
	
		if(localName.equals("enclosure")) {
			inUrl = true;
			imageURL = attributes.getValue("url");
		} else {
			inUrl = false;
		}

		if(localName.startsWith("item")) {
			inItem = true;
		} else if(inItem) {
			if(localName.equals("title")) {
				inTitle = true;
			} else {
				inTitle = false;
			}

			if(localName.equals("description")) {
				inDescription = true;
			} else {
				inDescription = false;
			}
			
			if(localName.equals("pubDate")) {
				inDate = true;
			} else {
				inDate = false;
			}
		}
	}
	
	public void characters(char ch[], int start, int length) {

		String chars = new String(ch).substring(start, start + length);

		if(inUrl) {
			Log.d("imageURL", "-  " + imageURL);
			image = getBitmap(imageURL);
			//imageURL = chars;
		}
	
		if(inTitle && title == null) {
			title = chars;
		}
	
		if(inDescription) {
			description.append(chars);
		}
	
		if(inDate && date == null) {
			date = chars;
		}

	}

	
	
	public Bitmap getImage() { return image; }
	public String getTitle() { return title; }
	public StringBuffer getDescription() { return description; }
	public String getDate() { return date; }
	public String getImageURL(){return imageURL;}
	
}