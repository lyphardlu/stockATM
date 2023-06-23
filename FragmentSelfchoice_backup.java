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
import com.stock.money.FragmentSearch_Fundamentals.UserSchema;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
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
import android.widget.Toast;

public class FragmentSelfchoice extends Fragment {
	private static final int LOAD = 1;
	private static final int UPDATE_PROGRESS = 2;
	private static final int FINISH = 8;
	private View v;
	private ListView listView;
    private SimpleAdapter simpleAdapter;
	private ProgressDialog progressBar;
    private Bundle data;
    private String[] searchCmd = new String[2*2];
    private	Uri mUriDB;
    private static final int TIMEOUTMILLIS = 5000; // 10 seconds
    List<Map<String, Object>> items = new ArrayList<Map<String,Object>>();
	public interface UserSchema {
		String TABLE_NAME = "Users";           	//Table Name
		String ID = "_id";                    	//ID
		String STOCK_NO = "stock_no";       	//Game Name
	}
	
	final String[] FROM = 
	{   
    	UserSchema.ID,
    	UserSchema.STOCK_NO
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		v = inflater.inflate(R.layout.fragmentselfchoice, container, false);
		listView = (ListView)v.findViewById(R.id.fragmentselfchoice_list);
		progressBar = ProgressDialog.show(v.getContext(), null, "讀取中, 請稍後 ...");	
        listView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				if(position==0 || id==0)
					return;		
				TextView textViewStockName = (TextView) view.findViewById(R.id.textStockName);
				TextView textViewStockNum = (TextView) view.findViewById(R.id.textStockNum);
	            System.out.println("pos : " + position + " , id : " + id);
				final String stockName = textViewStockName.getText().toString(); 
	            final String stockNum = textViewStockNum.getText().toString(); 			
	            System.out.println("stockNum : " + stockNum);
	            final int curPos = position;
				new AlertDialog.Builder(view.getContext())
				.setMessage(stockName + "(" + stockNum + ")")
	            .setTitle("執行自選清單中的動作 ?")
		        .setPositiveButton("查詢", new DialogInterface.OnClickListener() {
		            @Override
		            public void onClick(DialogInterface dialog, int which) {     
						Bundle args = new Bundle();
		    	        args.putStringArray("stockNum", new String[]{stockNum});     	             
		    	        Fragment newFragment = new FragmentSearch();
			        	FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
			        	newFragment.setArguments(args);	        	
			        	ft.hide(getActivity().getSupportFragmentManager().findFragmentById(R.id.realtabcontent));
			        	ft.add(R.id.realtabcontent, newFragment);	    		
			            ft.setTransition(FragmentTransaction.TRANSIT_NONE);
			            ft.addToBackStack(null);
			            ft.commit();	
		             }
		        })
		        .setNeutralButton("刪除", new DialogInterface.OnClickListener() {
		        	@Override
		        	public void onClick(DialogInterface dialog, int which) {
		        		Cursor cur = v.getContext().getContentResolver().query(mUriDB, FROM , "stock_no='" + stockNum + "'", null, null);        
		            	String where = UserSchema.STOCK_NO + " = " + stockNum;
		        		if(cur.getCount()>0) {
		        			items.remove(curPos);
		        			simpleAdapter.notifyDataSetChanged();
		        			v.getContext().getContentResolver().delete(mUriDB, where, null);
		        			Toast.makeText(v.getContext(), stockName + "(" + stockNum + ")" + " 已刪除", Toast.LENGTH_SHORT).show();
		        		}       
		        	}
		        })
		        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
		        	@Override
		        	public void onClick(DialogInterface dialog, int which) {
		        	}
		        })    	        
		        .show();
			}
        });

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
		Document doc = null;
		URL url;
		DecimalFormat df=new DecimalFormat("#.####");
		Message msg = null;
		Bundle data = null;
		String stock_name = null;
		String stock_num = null;
		String price = null;
		String rise_fall = null;
		String highest_price = null;
		String lowest_price = null;
		double d_real_bid_ask_spread = 0;		
		double d_reasonable_bid_ask_spread = 0;
		double d_io_money = 0;
		double d_days_left = 0;
		double d_theta = 0;
		double d_outstanding_percent = 0;
		int i = 0, j = 0, k = 0;	
		ArrayList<String> stockAryList = new ArrayList<String>();
		Element table0 = null;
		try {	
			mUriDB = Uri.parse("content://com.stock.money.provider.dbprovider");  
	        Cursor cur = v.getContext().getContentResolver().query(mUriDB, null, null, null, null);
	        cur.moveToFirst();
	        System.out.println("Loading stock list ...");
			System.out.println("curCount : " + cur.getCount());			
	    	for (i = 0; i < cur.getCount(); i++) {
				stock_num = cur.getString(1);
				url = new URL("https://tw.stock.yahoo.com/q/q?s=" + stock_num);
			    doc = null;	
				cur.moveToNext();
				try {
					doc = Jsoup.parse(url, TIMEOUTMILLIS);
				} catch (MalformedURLException e) {
					System.out.println(e.toString());		        
				} catch (IOException e) {
					System.out.println(e.toString());
				}	
			    if((doc==null) || (doc.select("table[border=2]").size() < 1))
			    	continue;
				table0 = doc.select("table[border=2]").get(0);
				if( table0.select("td").size() < 11 )
					continue;
				stock_name = table0.select("td").get(0).text();
				stock_name = stock_name.substring(stock_num.length(), stock_name.lastIndexOf("加"));
				price = table0.select("td").get(2).text();
				rise_fall = table0.select("td").get(5).text();
				highest_price = table0.select("td").get(9).text();
				lowest_price = table0.select("td").get(10).text();
				stockAryList.add(	"stock_name:" + stock_name +
						"stock_num:" + stock_num +				
						"current_price:" + price +
						"rise_fall:" + rise_fall +
						"highest_price:" + highest_price +
						"lowest_price:" + lowest_price );
				System.out.println(stockAryList.get(stockAryList.size()-1));
	    	}
	    	cur.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		msg = new Message();
		data = new Bundle();
		msg.what = LOAD;
	    data.putStringArrayList("stockAryList", stockAryList);
		msg.setData(data);
		mHandler.sendMessage(msg);
		
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
				case LOAD:
				/*
					total = msg.arg2;
					current = msg.arg1;
	                count = current * 100 / total;                   
				*/				
					int i = 0;
					Map<String, Object> item = new HashMap<String, Object>();
					String itemStr = null;
					item.put("textStockName", 	"股票");
					item.put("textStockNum", 	"代碼");				
					item.put("textPrice", 		"成交");
					item.put("textRiseFall", 	"漲跌");
					item.put("textHighestPrice", "最高");
					item.put("textLowestPrice", "最低");
					items.add(item);
	                for (i = 0; i < bundle.getStringArrayList("stockAryList").size(); i++) {
						System.out.println("Load3");
	                	item = new HashMap<String, Object>();
	                	itemStr = bundle.getStringArrayList("stockAryList").get(i);	
						item.put("textStockName", itemStr.substring(itemStr.lastIndexOf("stock_name:") + 11, itemStr.lastIndexOf("stock_num:")));
						item.put("textStockNum", itemStr.substring(itemStr.lastIndexOf("stock_num:") + 10, itemStr.lastIndexOf("current_price:")));
						item.put("textPrice", itemStr.substring(itemStr.lastIndexOf("current_price:") + 14, itemStr.lastIndexOf("rise_fall:")));
						item.put("textRiseFall", itemStr.substring(itemStr.lastIndexOf("rise_fall:") + 10, itemStr.lastIndexOf("highest_price:")));
						item.put("textHighestPrice", itemStr.substring(itemStr.lastIndexOf("highest_price:") + 14, itemStr.lastIndexOf("lowest_price:")));
						item.put("textLowestPrice", itemStr.substring(itemStr.lastIndexOf("lowest_price:") + 13));				
	                    items.add(item);
	                //    System.out.println("r:" + bundle.getStringArrayList("str").get(i));
	                }              
					simpleAdapter = new SimpleAdapter(getActivity(), 
	    				items, R.layout.fragmentselfchoice_simpleadapter, 
	    				new String[]{	"textStockName", "textStockNum",
										"textPrice", "textRiseFall", 
										"textHighestPrice", "textLowestPrice" },
						new int[]{	R.id.textStockName, R.id.textStockNum,
									R.id.textPrice, R.id.textRiseFall, 
									R.id.textHighestPrice, R.id.textLowestPrice }) {
						@Override
	                    public View getView(int position, View convertView, ViewGroup parent) {
							System.out.println("getview : " + position);
							View view = convertView;
							if(view == null) {
								LayoutInflater vi = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
								view = vi.inflate(R.layout.fragmentselfchoice_simpleadapter, null);
							}				
							TextView textViewStockName = (TextView) view.findViewById(R.id.textStockName);
							textViewStockName.setText((String) items.get(position).get("textStockName"));
							TextView textViewStockNum = (TextView) view.findViewById(R.id.textStockNum);
							textViewStockNum.setText((String) items.get(position).get("textStockNum"));
							TextView textViewPrice = (TextView) view.findViewById(R.id.textPrice);
							textViewPrice.setText((String) items.get(position).get("textPrice"));
							TextView textViewRiseFall = (TextView) view.findViewById(R.id.textRiseFall);
							String strRiseFall = (String) items.get(position).get("textRiseFall");							
							textViewRiseFall.setText(strRiseFall);
							if(strRiseFall.contains("▽") || strRiseFall.contains("▼")) {
								textViewPrice.setTextColor(Color.GREEN);
								textViewRiseFall.setTextColor(Color.GREEN);
							}
							else if(strRiseFall.contains("△") || strRiseFall.contains("▲")) {
								textViewPrice.setTextColor(Color.RED);
								textViewRiseFall.setTextColor(Color.RED);
							}
							TextView textViewHighestPrice = (TextView) view.findViewById(R.id.textHighestPrice);
							textViewHighestPrice.setText((String) items.get(position).get("textHighestPrice"));
							TextView textViewLowestPrice = (TextView) view.findViewById(R.id.textLowestPrice);
							textViewLowestPrice.setText((String) items.get(position).get("textLowestPrice"));
	                        return view;
	                    }
	                };          
					listView.setAdapter(simpleAdapter);
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
