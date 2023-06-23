package com.stock.money;

import com.stock.money.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

public class FragmentWarrant_Simulation extends Fragment {
	
	private Bundle data;
	private WebView webview;
	private View v;
	
	private int[] image = {
            R.drawable.cat, R.drawable.flower, R.drawable.hippo,
            R.drawable.monkey, R.drawable.mushroom, R.drawable.panda,
            R.drawable.rabbit, R.drawable.raccoon
    };
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
		if(data == null || data.getStringArray("warrantNum") == null) {
			return v;
		}
		
		String warrantNum = data.getStringArray("warrantNum")[0];
		
		/*
		ImageView img = (ImageView)v.findViewById(R.id.image_view);
		img.setImageResource(image[getArguments().getInt("position")]);
		
		
		System.out.println("warrantNum : " + warrantNum);
		*/
		
		webview = (WebView)v.findViewById(R.id.webViewTechnicals);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebChromeClient(new WebChromeClient() {   
			public void onProgressChanged(WebView view, int progress) {     
				// Activities and WebViews measure progress with different scales.     
				// The progress meter will automatically disappear when we reach 100%     
				System.out.println("progress : " + String.valueOf(progress * 1000));   
			} 
		}); 
		webview.setWebViewClient(new WebViewClient() {   
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {     
				System.out.println("onReceivedError");   
			} 
		}); 
		
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setUseWideViewPort(true);
        webview.getSettings().setSupportZoom(true);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.loadUrl("http://warrantinfo.jihsun.com.tw/want/wSimulation.aspx?wcode=" + warrantNum);	                
        
		return v;
	}

}
