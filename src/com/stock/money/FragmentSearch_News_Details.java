package com.stock.money;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.stock.money.R;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class FragmentSearch_News_Details extends Fragment {
	private static final int NEWS = 1;
	private static final int FINISH = 11;	
	private static final int TIMEOUTMILLIS = 5000; // 10 seconds
	private WebView webview;
	private TextView textViewContent; 
	private View v;
	private Bundle data;
	private ProgressDialog progressBar;
	private String[] searchCmd = new String[1*2];
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		data = getArguments();
		v = inflater.inflate(R.layout.fragmentsearch_technicals, container, false);
		webview = (WebView)v.findViewById(R.id.webViewTechnicals);
		webview.setVisibility(View.INVISIBLE);         
		if(data == null || data.getStringArray("link") == null) {
			return v;
		}
		
		textViewContent = (TextView)v.findViewById(R.id.textViewContent); 
		progressBar = ProgressDialog.show(v.getContext(), null, "讀取中, 請稍候 ...");	   
		searchCmd[0] = "link";
        searchCmd[1] =	data.getStringArray("link")[0];
        
		Thread thread = new Thread(new Runnable() {
		public void run() {
		      try {
		    	  loadData(searchCmd);
		      } catch (Exception e) {
		           // TODO Auto-generated catch block
		           e.printStackTrace();
		      }
		
		 }
	    });
	    thread.start();    
	    
	    return v;  
	}
	        
	 private void loadData(String... urls) throws Exception {
		URL url = null;
		int i = 0;
	    Document doc = null;
	    Message msg = null;
		Bundle data = null;
		String strDetails = null;
	    for(i = 0; i < urls.length; i = i+2) {
			if(urls[i].compareTo("link") == 0) { 
				System.out.println("Parsing link ...");
				url = new URL(urls[i+1]);
			    try {
			    	doc = Jsoup.parse(url, TIMEOUTMILLIS);
				} catch (MalformedURLException e) {
					System.out.println(e.toString());		        
				} catch (IOException e) {
					System.out.println(e.toString());
				}	
			    if((doc==null) || (doc.select("div").size() < 26))
			    	continue;
			    Element div0 = doc.select("div").get(24);
			    Element div1 = doc.select("div").get(25);
			    msg = new Message();
				data = new Bundle();		
		        msg.what = NEWS;
		        strDetails = div0.text() + div1.text();
		        if(strDetails.lastIndexOf("TOP 關閉") != -1)
		        	strDetails = strDetails.substring(0, strDetails.lastIndexOf("TOP 關閉"));
		        data.putStringArray("str", new String[]{strDetails});          
				msg.setData(data);
				mHandler.sendMessage(msg);
			}
	 	}
	    msg = new Message();
		msg.what = FINISH;
		mHandler.sendMessage(msg);
		System.out.println("Finish");
	}
	 
	//設定ProgressBar進度
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			Bundle bundle = msg.getData();  
			int i = 0;
			String itemStr = null;
			switch (msg.what) {
				case NEWS:
					textViewContent.setText(bundle.getStringArray("str")[0]);
					System.out.println(bundle.getStringArray("str")[0]);
	                break;
				case FINISH:
					if (progressBar.isShowing())  
						progressBar.dismiss();  
	                break;
			}
		}
	};
}
