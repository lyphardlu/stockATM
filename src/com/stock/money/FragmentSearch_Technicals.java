package com.stock.money;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.stock.money.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentSearch_Technicals extends Fragment {
	private WebView webview;
	private View v;
	private Bundle data;
	private ProgressDialog progressBar;
	
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
		if(data == null || data.getStringArray("stockNum") == null) {
			return v;
		}
			
		String stockNum = data.getStringArray("stockNum")[0]; 
		webview = (WebView)v.findViewById(R.id.webViewTechnicals);
        webview.getSettings().setJavaScriptEnabled(true);
        progressBar = ProgressDialog.show(v.getContext(), null, "讀取中, 請稍候 ..."); 
        webview.setWebChromeClient(new WebChromeClient() {   
			public void onProgressChanged(WebView view, int progress) {     
				// Activities and WebViews measure progress with different scales.     
				// The progress meter will automatically disappear when we reach 100%     
				System.out.println("progress : " + progress);
			} 
		});
		webview.setWebViewClient(new WebViewClient() {   
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {     
				System.out.println("onReceivedError");   
			} 
			public void onPageFinished(WebView view, String url) {  
		        if (progressBar.isShowing())  
			       progressBar.dismiss();  
			}
		}); 
		
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setUseWideViewPort(true);
        webview.getSettings().setSupportZoom(true);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.loadUrl("http://so.cnyes.com/JavascriptGraphic/chartstudy.aspx?country=tw&market=tw&code=" + stockNum + "&divwidth=990&divheight=400");	                
        
		return v;
	}
}