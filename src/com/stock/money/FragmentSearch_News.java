package com.stock.money;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.csvreader.CsvReader;


public class FragmentSearch_News extends Fragment {
	private static final int NEWS = 1;
	private static final int FINISH = 11;
	private static final int TIMEOUTMILLIS = 5000; // 10 seconds
	private View v;
	private Bundle data;
	private ListView listView;
    private SimpleAdapter simpleAdapter;
	private String[] searchCmd = new String[1*2];
	private ProgressDialog progressBar;
	private ArrayList<String> m_linkAryList;
	
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
		v = inflater.inflate(R.layout.fragmentsearch_news, container, false);	
		if(data == null || data.getStringArray("stockNum") == null) {
			return v;
		}
		
		progressBar = ProgressDialog.show(v.getContext(), null, "讀取中, 請稍候 ...");
		listView = (ListView)v.findViewById(R.id.fragmentsearch_news_list);
		listView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				System.out.println("position : " + position + ", link : " + m_linkAryList.get(position));
				Bundle args = new Bundle();
    	        args.putStringArray("link", new String[]{m_linkAryList.get(position)});     	               
    	        Fragment newFragment = new FragmentSearch_News_Details();
	        	FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
	        	newFragment.setArguments(args);	        	
	        	ft.hide(getActivity().getSupportFragmentManager().findFragmentById(R.id.realtabcontents));
	        	ft.add(R.id.realtabcontents, newFragment);	    		
	       // 	ft.replace(R.id.realtabcontents, newFragment);
	            ft.setTransition(FragmentTransaction.TRANSIT_NONE);
	            ft.addToBackStack(null);
	            ft.commit();
			}
        });
		
		final String stockNum = data.getStringArray("stockNum")[0];
        searchCmd[0] = "news";
        searchCmd[1] =	stockNum;
        
        progressBar.setProgress(30);
        Thread thread = new Thread(new Runnable() {
		 public void run() {
		      try {
		    	  m_linkAryList = loadData(searchCmd);
		      } catch (Exception e) {
		           // TODO Auto-generated catch block
		           e.printStackTrace();
		      }
		
		 }
        });
        thread.start();    
            
		return v;
	}
	
	private ArrayList<String> loadData(String... urls) throws Exception {
		int i = 0, j = 0, k = 0;
		int td_size = 0;
		int link_index = 0;
		URL url = null;
		Message msg = null;
		Bundle data = null;
		Document doc = null;
		ArrayList<String> newsAryList = new ArrayList<String>();
		ArrayList<String> linkAryList = new ArrayList<String>();
		for(i = 0; i < urls.length; i = i+2) {
			if(urls[i].compareTo("news") == 0) { 
				System.out.println("Parsing news ...");				
				for(j=1; j<=3;j++) {
					url = new URL("https://tw.stock.yahoo.com/q/h?s=" + urls[i+1] + "&pg=" + j);
					System.out.println("https://tw.stock.yahoo.com/q/h?s=" + urls[i+1] + "&pg=" + j);		
				    doc = null;
				    try {
				    	doc = Jsoup.parse(url, TIMEOUTMILLIS);
					} catch (MalformedURLException e) {
						System.out.println(e.toString());		        
					} catch (IOException e) {
						System.out.println(e.toString());
					}	
				    if((doc==null) || (doc.select("table[border=0]").size() < 5))
				    	continue;
				    link_index = 0;
				    Element table0 = doc.select("table[border=0]").get(4);
				    td_size = table0.select("td").size();			    
			        for(k=4; k<td_size-1; k=k+3) {
						newsAryList.add(table0.select("td").get(k).text() + "\n" + table0.select("td").get(k+1).text() + "\n");	
						linkAryList.add(table0.select("a[href]").get(link_index).attr("abs:href"));
						link_index++;
			        }       		        
				}
				msg = new Message();
				data = new Bundle();		
		        msg.what = NEWS;
		        data.putStringArrayList("newsAryList", newsAryList);
				msg.setData(data);
				mHandler.sendMessage(msg);
			}
		} 
		msg = new Message();
		msg.what = FINISH;
		mHandler.sendMessage(msg);
		System.out.println("Finish");
		return linkAryList;
	}

	//設定ProgressBar進度
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			Bundle bundle = msg.getData();  
			int i = 0;
			String itemStr = null;
			switch (msg.what) {
				case NEWS:
	                List<Map<String, Object>> items = new ArrayList<Map<String,Object>>();
					Map<String, Object> item = null;
	                for (i = 0; i < bundle.getStringArrayList("newsAryList").size(); i++) {
	                	item = new HashMap<String, Object>();
	                	itemStr = bundle.getStringArrayList("newsAryList").get(i);	
						item.put("textNews", itemStr);
	                    items.add(item);
	                }              
					simpleAdapter = new SimpleAdapter(getActivity(), 
	    				items, R.layout.fragmentsearch_news_simpleadapter, 
	    				new String[]{	"textNews" 	},
						new int[]{	R.id.textNews });
	                listView.setAdapter(simpleAdapter);             
	                break;
				case FINISH:
					if (progressBar.isShowing())  
						progressBar.dismiss();  
	                break;
			}
		}
	};
}