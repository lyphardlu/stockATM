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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.csvreader.CsvReader;
import com.stock.money.R;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class FragmentWarrant_Sell extends Fragment {
	private static final int PARSE_CSV = 1;
	private static final int UPDATE_PROGRESS = 2;
	private static final int FINISH = 8;
	
	private View v;
	private ListView listView;
    private SimpleAdapter simpleAdapter;
    private ProgressBar progressbar1;
    private Bundle data;
    private String[] searchCmd = new String[2*2];
    
    private int[] image = {
            R.drawable.cat, R.drawable.flower, R.drawable.hippo,
            R.drawable.monkey, R.drawable.mushroom, R.drawable.panda,
            R.drawable.rabbit, R.drawable.raccoon
    };
    private String[] imgText = {
            "cat", "flower", "hippo", "monkey", "mushroom", "panda", "rabbit", "raccoon"
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
		v = inflater.inflate(R.layout.fragmentwarrant_sell, container, false);
		if(data == null || data.getStringArray("stockNum") == null) {
			return v;
		}
		
		listView = (ListView)v.findViewById(R.id.fragmentwarrant_list);
		progressbar1 = (ProgressBar) v.findViewById(R.id.progressBar1);
        listView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				if(position==0 || id==0)
					return;				
				TextView textViewWarrantNo = (TextView) view.findViewById(R.id.textWarrantNo);
	            String warrantNum = textViewWarrantNo.getText().toString(); 	           
				Bundle args = new Bundle();
    	        args.putStringArray("warrantNum", new String[]{warrantNum});     	               
    	        Fragment newFragment = new FragmentWarrant_Simulation();
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
			    
		String stockNum = data.getStringArray("stockNum")[0];
			
		searchCmd[0] = "parse_csv";
//		searchCmd[1] = "http://iwarrant.capital.com.tw/warrants/wScreenerPull_DLoad.aspx?ul=" + stockNum + "&histv=60";
		searchCmd[1] = "http://warrantchannel.sinotrade.com.tw/want/wSearchPull_DLoad.aspx?ul=" + stockNum;
		searchCmd[2] = "parse_warrant_condition";
		searchCmd[3] = "http://warrantinfo.jihsun.com.tw/want/wBasic.aspx?wcode=";
		     
		progressbar1.setVisibility(View.VISIBLE);
        progressbar1.setMax(100); // 設定最大值是100
        progressbar1.setProgress(0); // 設定目前進度0
        
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
		int count = 0;
		int progressCurrent = 0;
		int progressTotal = 0;
		DecimalFormat df=new DecimalFormat("#.####");
		Message msg = null;
		Bundle data = null;
		String warrant_no = null;
		String warrant_name = null;
		String buy_price = null;
		String sell_price = null;
		String real_bid_ask_spread = null;
		String reasonable_bid_ask_spread = null;
		String io_money = null;
		String days_left = null;
		String effective_lever = null;
		String siv = null;
		String theta = null;	
		String delta = null;
		String outstanding_percent  = null;
		double d_real_bid_ask_spread = 0;		
		double d_reasonable_bid_ask_spread = 0;
		double d_io_money = 0;
		double d_days_left = 0;
		double d_theta = 0;
		double d_outstanding_percent = 0;
		try {	
			int i = 0, j = 0, k = 0;
			URL url;
			ArrayList<String> matchWarrants = new ArrayList<String>();
			
			for(i = 0; i < urls.length; i = i+2) {
				if(urls[i].compareTo("parse_csv") == 0) {		
					try {
						URL warrantURL = new URL(urls[i+1]);						
						BufferedReader in = new BufferedReader(new InputStreamReader(warrantURL.openStream(), "Big5"));
						CsvReader warrantCsvReader = new CsvReader(in);					
						warrantCsvReader.readRecord();
						warrantCsvReader.readHeaders();
						progressCurrent = 0;
						while (warrantCsvReader.readRecord())
						{			
							progressCurrent++;
							msg = new Message();							
							msg.what = UPDATE_PROGRESS;
							msg.arg1 = progressCurrent % 30;
							mHandler.sendMessage(msg);		
							
							warrant_no = warrantCsvReader.get(warrantCsvReader.getHeader(0));
							warrant_no = warrant_no.substring(2, warrant_no.length() - 1);
							if(warrant_no.charAt(5) != 'P')
								continue;								
							warrant_name = warrantCsvReader.get(warrantCsvReader.getHeader(1));
							if( (warrant_name.contains("元大") || 
								warrant_name.contains("群益") ||
								warrant_name.contains("凱基") ||
								warrant_name.contains("統一")) == false )
								continue;								
							//buy_price = warrantCsvReader.get(warrantCsvReader.getHeader(3));
							buy_price = warrantCsvReader.get(warrantCsvReader.getHeader(2));
							if(buy_price.compareTo("-") == 0)
								continue;								
							//sell_price = warrantCsvReader.get(warrantCsvReader.getHeader(4));
							sell_price = warrantCsvReader.get(warrantCsvReader.getHeader(3));
							if(sell_price.compareTo("-") == 0)
								continue;
							//real_bid_ask_spread = warrantCsvReader.get(warrantCsvReader.getHeader(6));
							real_bid_ask_spread = warrantCsvReader.get(warrantCsvReader.getHeader(13));
							if(real_bid_ask_spread.compareTo("-") == 0)
								continue;			
							d_real_bid_ask_spread = Double.parseDouble(real_bid_ask_spread.substring(0, real_bid_ask_spread.lastIndexOf("%")));
							/*
							reasonable_bid_ask_spread = warrantCsvReader.get(warrantCsvReader.getHeader(7));
							if(reasonable_bid_ask_spread.compareTo("-") == 0)
								continue;		
							d_reasonable_bid_ask_spread = Double.parseDouble(reasonable_bid_ask_spread.substring(0, reasonable_bid_ask_spread.lastIndexOf("%")));
							if((d_real_bid_ask_spread >= d_reasonable_bid_ask_spread) || (d_real_bid_ask_spread >= 5))
								continue;
							*/
							d_reasonable_bid_ask_spread = 0;
							if(d_real_bid_ask_spread >= 5)
								continue;		
							//io_money = warrantCsvReader.get(warrantCsvReader.getHeader(12));
							io_money = warrantCsvReader.get(warrantCsvReader.getHeader(16));
							if(io_money.compareTo("-") == 0)
								continue;
							if(io_money.contains("價外")) {
								d_io_money = Math.abs(Double.parseDouble(io_money.substring(0, io_money.lastIndexOf("%"))));
								if(d_io_money > 25)
									continue;
							}
							//days_left = warrantCsvReader.get(warrantCsvReader.getHeader(15));
							days_left = warrantCsvReader.get(warrantCsvReader.getHeader(14));
							if(days_left.compareTo("-") == 0)
								continue;
							d_days_left = Double.parseDouble(days_left);
							if(d_days_left < 80)
								continue;							
							//effective_lever = warrantCsvReader.get(warrantCsvReader.getHeader(16));
							effective_lever = warrantCsvReader.get(warrantCsvReader.getHeader(15));
							//siv = warrantCsvReader.get(warrantCsvReader.getHeader(18));
							siv = warrantCsvReader.get(warrantCsvReader.getHeader(8));
							matchWarrants.add(	"warrant_no:" + warrant_no + 
												"warrant_name:" + warrant_name +
												"buy_price:" + buy_price +
												"sell_price:" + sell_price +
												"real_bid_ask_spread:" + real_bid_ask_spread +
												"reasonable_bid_ask_spread:" + reasonable_bid_ask_spread +
												"io_money:" + io_money +
												"days_left:" + days_left +
												"effective_lever:" + effective_lever +
												"siv:" + siv );
							
							System.out.println(	"warrant_no:" + warrant_no + 
												"warrant_name:" + warrant_name +
												"buy_price:" + buy_price +
												"sell_price:" + sell_price +
												"real_bid_ask_spread:" + real_bid_ask_spread +
												"reasonable_bid_ask_spread:" + reasonable_bid_ask_spread +
												"io_money:" + io_money +
												"days_left:" + days_left +
												"effective_lever:" + effective_lever +
												"siv:" + siv );
						}
						warrantCsvReader.close();
					} catch (FileNotFoundException e) {
						System.out.println("message1 : " + e.getMessage());
						e.printStackTrace();
					} catch (IOException e) {
						System.out.println("message2 : " + e.getMessage());
						e.printStackTrace();
					}
			        System.out.println("try to get");
			        System.out.println("matchWarrants size = " + matchWarrants.size());			        
				} else if(urls[i].compareTo("parse_warrant_condition") == 0) {
					String warrantURLStr = null;
					Document doc = null;
					Element table0 = null;
					int td_size = 0;
					progressTotal = matchWarrants.size();
					progressCurrent = 0;
					
					for(j=0; j < matchWarrants.size(); j++) {
						progressCurrent++;
						msg = new Message();	
						msg.what = UPDATE_PROGRESS;
						msg.arg1 = 30 + (progressCurrent*70/progressTotal);
						mHandler.sendMessage(msg);							
						
						warrantURLStr = urls[i+1] + matchWarrants.get(j).substring(matchWarrants.get(j).lastIndexOf("warrant_no:") + 11, matchWarrants.get(j).lastIndexOf("warrant_name:"));			
						url = new URL(warrantURLStr);	
						System.out.println("warrantURLStr : " + warrantURLStr);
						doc = Jsoup.parse(url, 3000);
						if(doc.select("table[border=0]").size() < 2) {
					    	System.out.println("error : table size is less than 2");
					    	matchWarrants.remove(j);
					    	j--;
					    	continue;
					    }
						table0 = doc.select("table[border=0]").get(1);	   
						td_size = table0.select("td").size();
				        if( td_size < 24) {
				        	matchWarrants.remove(j);
					    	j--;
					    	continue;
				        }
				        delta = table0.select("td").get(13).text();
			    		theta = table0.select("td").get(19).text();
			    		if(theta.compareTo("-") == 0) {
			    			matchWarrants.remove(j);
					    	j--;
					    	continue;
			    		} else {
			    			d_theta = Math.abs(Double.parseDouble(theta));
			    			if(d_theta > 0.05) {
			    				System.out.println("Remove : " + matchWarrants.get(j).substring(matchWarrants.get(j).lastIndexOf("warrant_no:") + 11, matchWarrants.get(j).lastIndexOf("warrant_name:")) + ", theta : " +  theta);
			    				matchWarrants.remove(j);
						    	j--;
						    	continue;
			    			}  			
			    		}				    		
			    		outstanding_percent = table0.select("td").get(23).text();
			    		if(outstanding_percent.compareTo("-%") == 0) {
			    			matchWarrants.remove(j);
					    	j--;
					    	continue;
			    		} else {
			    			d_outstanding_percent = Double.parseDouble(outstanding_percent.substring(0, outstanding_percent.lastIndexOf("%")));	
			    			if((d_outstanding_percent > 75) || (d_outstanding_percent < 5)) {
			    				System.out.println("Remove : " + matchWarrants.get(j).substring(matchWarrants.get(j).lastIndexOf("warrant_no:") + 11, matchWarrants.get(j).lastIndexOf("warrant_name:")) + ", outstanding_percent : " +  outstanding_percent);
			    				matchWarrants.remove(j);
						    	j--;
						    	continue;
			    			}
			    		}			   
			    		matchWarrants.set(j, matchWarrants.get(j).concat("theta:" + theta + "delta:" + delta + "outstanding_percent:" + outstanding_percent));			    		
			    		System.out.println("Match : " + matchWarrants.get(j));
					}
	
					String curStr = null;
					String nextStr = null;
					
					for(j=0; j < matchWarrants.size()-1; j++) {
						for(k=0; k< matchWarrants.size()-j-1; k++) {
							curStr = matchWarrants.get(k);
							nextStr = matchWarrants.get(k+1);
							
							if( Double.parseDouble(curStr.substring(curStr.lastIndexOf("real_bid_ask_spread:") + 20, curStr.lastIndexOf("%reasonable_bid_ask_spread:")))
								> Double.parseDouble(nextStr.substring(nextStr.lastIndexOf("real_bid_ask_spread:") + 20, nextStr.lastIndexOf("%reasonable_bid_ask_spread:")))){
								matchWarrants.set(k, nextStr);
								matchWarrants.set(k+1, curStr);
							}
						}	
					}

					msg = new Message();
					data = new Bundle();
					data.putStringArrayList("str", matchWarrants);  
					msg.what = PARSE_CSV;
					msg.setData(data);
					msg.arg1 = i+1;
					msg.arg2 = urls.length;
					mHandler.sendMessage(msg);	
				}
			} 
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
					progressbar1.setProgress(msg.arg1);
					break;				
				case PARSE_CSV:
					total = msg.arg2;
					current = msg.arg1;
	                count = current * 100 / total;
	        		List<Map<String, Object>> items = new ArrayList<Map<String,Object>>();
	        		Map<String, Object> item = new HashMap<String, Object>();
	        		String itemStr = null;
	        		item.put("textWarrantNo", "權證代號");
	        		item.put("textWarrantName", "權證名稱");    		
                    item.put("textBuyPrice", "買價");
                    item.put("textSellPrice", "賣價");
                    item.put("textRealBidAskSpread", "買賣價差");
                    item.put("textReasonableBidAskSpread", "合理買賣價差");
                    item.put("textEffectiveLever", "有效槓桿");
                    item.put("textDaysLeft", "剩餘天數");
                    item.put("textTheta", "Theta");
                    item.put("textDelta", "Delta");
                    item.put("textIOtheMoney", "價內外");
                    item.put("textOutstandingPercent", "流通在外比例");
                    item.put("textSIV", "SIV");
                    items.add(item);
                    
	                for (int i = 0; i < bundle.getStringArrayList("str").size(); i++) {
	                	item = new HashMap<String, Object>();
	                	itemStr = bundle.getStringArrayList("str").get(i);
	                	item.put("textWarrantNo", itemStr.substring(itemStr.lastIndexOf("warrant_no:") + 11, itemStr.lastIndexOf("warrant_name:")));
	                	item.put("textWarrantName", itemStr.substring(itemStr.lastIndexOf("warrant_name:") + 13, itemStr.lastIndexOf("buy_price:")));  	
	                    item.put("textBuyPrice", itemStr.substring(itemStr.lastIndexOf("buy_price:") + 10, itemStr.lastIndexOf("sell_price:")));
	                    item.put("textSellPrice", itemStr.substring(itemStr.lastIndexOf("sell_price:") + 11, itemStr.lastIndexOf("real_bid_ask_spread:")));
	                    item.put("textRealBidAskSpread", itemStr.substring(itemStr.lastIndexOf("real_bid_ask_spread:") + 20, itemStr.lastIndexOf("reasonable_bid_ask_spread:")));
	                    item.put("textReasonableBidAskSpread", itemStr.substring(itemStr.lastIndexOf("reasonable_bid_ask_spread:") + 26, itemStr.lastIndexOf("io_money:")));
	                    item.put("textEffectiveLever", itemStr.substring(itemStr.lastIndexOf("effective_lever:") + 16, itemStr.lastIndexOf("siv:")));
	                    item.put("textDaysLeft", itemStr.substring(itemStr.lastIndexOf("days_left:") + 10, itemStr.lastIndexOf("effective_lever:")));
	                    item.put("textTheta", itemStr.substring(itemStr.lastIndexOf("theta:") + 6, itemStr.lastIndexOf("delta:")));
	                    item.put("textDelta", itemStr.substring(itemStr.lastIndexOf("delta:") + 6, itemStr.lastIndexOf("outstanding_percent:")));                    
	                    item.put("textIOtheMoney", itemStr.substring(itemStr.lastIndexOf("io_money:") + 9, itemStr.lastIndexOf("days_left:")));
	                    item.put("textOutstandingPercent", itemStr.substring(itemStr.lastIndexOf("outstanding_percent:") + 20));	                    
	                    item.put("textSIV", itemStr.substring(itemStr.lastIndexOf("siv:") + 4, itemStr.lastIndexOf("theta:")));
	                    items.add(item);
	                //    System.out.println("r:" + bundle.getStringArrayList("str").get(i));
	                }
	                	                
	                simpleAdapter = new SimpleAdapter(getActivity(), 
	                        items, R.layout.fragmentwarrant_simpleadapter, 
	                        new String[]{"textWarrantNo", "textWarrantName",
	                					"textBuyPrice", "textSellPrice",
	                					"textRealBidAskSpread", "textReasonableBidAskSpread",
	                					"textEffectiveLever", "textDaysLeft",
	                					"textTheta", "textDelta", "textIOtheMoney", 
	                					"textOutstandingPercent", "textSIV" },
	                        new int[]{	R.id.textWarrantNo, R.id.textWarrantName,
	                					R.id.textBuyPrice, R.id.textSellPrice, 
	                					R.id.textRealBidAskSpread, R.id.textReasonableBidAskSpread, 
	                					R.id.textEffectiveLever, R.id.textDaysLeft, 
	                					R.id.textTheta, R.id.textDelta, R.id.textIOtheMoney, 
	                					R.id.textOutstandingPercent, R.id.textSIV });
	                listView.setAdapter(simpleAdapter);
	                break;
				case FINISH:
	                progressbar1.setProgress(100);
	                progressbar1.setVisibility(View.INVISIBLE);
	                break;
			}
		}
	};
}
