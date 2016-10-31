package mx.com.chilitech.appwebbrowser;

import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public abstract class WebViewC extends WebViewClient {
	
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		if(Uri.parse(url).getHost().equals("www.google.com")) {
			return false;
		}
		launchExternalBrowser(url);
		return true;
	}
	
	public abstract void launchExternalBrowser(String url);
}
