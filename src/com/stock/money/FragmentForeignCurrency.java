package com.stock.money;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
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
import java.util.Timer;
import java.util.TimerTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.csvreader.CsvReader;
import com.stock.money.R;
import com.stock.money.FragmentSearch_Fundamentals.UserSchema;

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

public class FragmentForeignCurrency extends Fragment {
	private static final int CURRENCY = 1;
	private static final int UPDATE_PROGRESS = 2;
	private static final int FINISH = 8;
	private View v;
	private ListView listView;
    private SimpleAdapter simpleAdapter;
	private ProgressDialog progressBar;
    private String[] searchCmd = new String[1*2];
    private static final int TIMEOUTMILLIS = 5000; // 10 seconds
    List<Map<String, Object>> items = new ArrayList<Map<String,Object>>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		v = inflater.inflate(R.layout.fragmentforeigncurrency, container, false);
		listView = (ListView)v.findViewById(R.id.fragmentforeigncurrency_list);
		listView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				if(!(position==3&&id==3)) 
					return;		  	             
				Fragment newFragment = new FragmentCommodityPrice();
				FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
				ft.hide(getActivity().getSupportFragmentManager().findFragmentById(R.id.realtabcontent));
				ft.add(R.id.realtabcontent, newFragment);	    		
				ft.setTransition(FragmentTransaction.TRANSIT_NONE);
				ft.addToBackStack(null);
				ft.commit();		
			}
        });
		simpleAdapter = new SimpleAdapter(getActivity(), 
			items, R.layout.fragmentforeigncurrency_simpleadapter, 
			new String[]{	"textCurrencyType", "textCurrencyBuy",
							"textCurrencySell" },
			new int[]{	R.id.textCurrencyType, R.id.textCurrencyBuy,
						R.id.textCurrencySell });
		listView.setAdapter(simpleAdapter);
		progressBar = ProgressDialog.show(v.getContext(), null, "讀取中, 請稍後 ...");	
		searchCmd[0] = 	"currency";
        searchCmd[1] =	"https://www.bankchb.com/frontend/G0100.html";
  	
        /*
		Timer LoadDataTimer =new Timer();
		LoadDataTimer.schedule(new LoadDataTimerTask(searchCmd), 0,10000);
		*/
		
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
	
	class LoadDataTimerTask extends TimerTask {

		private final String[] serials;
		LoadDataTimerTask (String... urls)
		{
			this.serials = urls;
		}

		public void run() {
			//Do stuff
			try {
				loadData(serials);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void loadData(String... urls) throws Exception {
		int count = 0;
		Document doc = null;
		URL url;
		DecimalFormat df=new DecimalFormat("#.####");
		Message msg = null;
		Bundle data = null;
		String currencyType = null;
		int i = 0, j = 0, k = 0;	
		int td_size = 0;
		int index = 0;
		ArrayList<String> currencyAryList = new ArrayList<String>();
		Element table0 = null;
		for(i = 0; i < urls.length; i = i+2) {
			msg = new Message();
			data = new Bundle();
			if(urls[i].compareTo("currency") == 0) {
				System.out.println("Parsing currency ...");
				url = new URL(urls[i+1]);
			    doc = null;
			    try {
			    	doc = Jsoup.parse(url, TIMEOUTMILLIS);
				} catch (MalformedURLException e) {
					System.out.println(e.toString());		        
				} catch (IOException e) {
					System.out.println(e.toString());
				}	
				if((doc==null) || (doc.select("table[class=table table-data]").size() < 1)) {
			    	System.out.println("error : table size is less than 1");
			    	continue;
			    }	
				table0 = doc.select("table[class=table table-data]").get(0);
				td_size = table0.select("td").size();
		        if( td_size > 49) {
					for(j=0; j<3; j++) {
						if(j==0) {
							currencyType = "美金";
							index = 1;
						}
						else if(j==1) {
							currencyType = "澳幣";
							index = 10;
						}
						else if(j==2) {
							currencyType = "紐幣";
							index = 49;
						}
						currencyAryList.add(	
							"currency_type:" + currencyType +
							"currency_buy:" + table0.select("td").get(index).text() +				
							"currency_sell:" + table0.select("td").get(index+1).text() );
						System.out.println(currencyAryList.get(currencyAryList.size()-1));
					}
				}
				msg = new Message();
				data = new Bundle();
				msg.what = CURRENCY;
				data.putStringArrayList("currencyAryList", currencyAryList);
				msg.setData(data);
				mHandler.sendMessage(msg);
			} 
		}
		
		msg = new Message();
		msg.what = FINISH;
		mHandler.sendMessage(msg);
	}	
	//設定ProgressBar進度
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			Bundle bundle = msg.getData();  
			int total = 0;
			int current = 0;
			int count = 0;
			switch (msg.what) {
				case UPDATE_PROGRESS:
	//				progressbar1.setProgress(msg.arg1);
					break;				
				case CURRENCY:
					Map<String, Object> item = new HashMap<String, Object>();
					String itemStr = null;
					items.clear();
					item.put("textCurrencyType",	"幣別");
					item.put("textCurrencyBuy", 	"買入");				
					item.put("textCurrencySell", 	"賣出");
					items.add(item);
	                for (int i = 0; i < bundle.getStringArrayList("currencyAryList").size(); i++) {
						System.out.println("Load3");
	                	item = new HashMap<String, Object>();
	                	itemStr = bundle.getStringArrayList("currencyAryList").get(i);	
						item.put("textCurrencyType", itemStr.substring(itemStr.lastIndexOf("currency_type:") + 14, itemStr.lastIndexOf("currency_buy:")));
						item.put("textCurrencyBuy", itemStr.substring(itemStr.lastIndexOf("currency_buy:") + 13, itemStr.lastIndexOf("currency_sell:")));
						item.put("textCurrencySell", itemStr.substring(itemStr.lastIndexOf("currency_sell:") + 14));				
	                    items.add(item);
	                }              
					simpleAdapter.notifyDataSetChanged();
	                break;
				case FINISH:
					if (progressBar.isShowing())  
						progressBar.dismiss();  
	   //             progressbar1.setProgress(100);
	   //             progressbar1.setVisibility(View.INVISIBLE);
	                break;
			}
		}
	};
}
