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


public class FragmentSearch_Fundamentals extends Fragment {
	private static final int PRICE = 1;
	private static final int CAPITAL = 2;
	private static final int SHAREHOLDER = 3;
	private static final int PSR = 4;
	private static final int HISTORY_STOCK = 5;
	private static final int HISTORY_PER = 6;
	private static final int HISTORY_CASH_DIVIDEND = 7;
	private static final int HISTORY_PBR = 8;
	private static final int STOP_PRICE = 9;
	private static final int EXCLUDE_DIVIDEND_RIGHT = 10;			   // 埃舦埃
	private static final int FINISH = 11;
	private static final int TIMEOUTMILLIS = 5000; // 10 seconds
	private View v;
	private Bundle data;
	private TextView textView00; 
	private TextView textView01; 
	private TextView textView02; 
	private TextView textView03;
	private TextView textView04;
	private TextView textView05;
//	private TextView textView06;
	private TextView textView07;
	private TextView textView08;
	private TextView textView09;
	private TextView textView10;
	private TextView textView11;
	private TextView textView12;
	private TextView textView13;
	private TextView textView14;
	private Button buttonAdd;
	private Button buttonAnalysis;
	private String[] searchCmd = new String[11*2];
	private String[] analysis_results = null;
	private ProgressBar progressbar1;
	private	Uri mUriDB;
	private ContentValues mContentValues = new ContentValues();
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		data = getArguments();
		v = inflater.inflate(R.layout.fragmentsearch_fundamentals, container, false);	
		if(data == null || data.getStringArray("stockNum") == null) {
			return v;
		}
		final String stockNum = data.getStringArray("stockNum")[0];
		textView00 = (TextView)v.findViewById(R.id.textView0); 
        textView01 = (TextView)v.findViewById(R.id.textView1); 
        textView02 = (TextView)v.findViewById(R.id.textView2);  
        textView03 = (TextView)v.findViewById(R.id.textView3);
        textView04 = (TextView)v.findViewById(R.id.textView4);
        textView05 = (TextView)v.findViewById(R.id.textView5);
  //      textView06 = (TextView)v.findViewById(R.id.textView6);
        textView07 = (TextView)v.findViewById(R.id.textView7);  
        textView08 = (TextView)v.findViewById(R.id.textView8); 
        textView09 = (TextView)v.findViewById(R.id.textView9); 
        textView10 = (TextView)v.findViewById(R.id.textView10); 
        textView11 = (TextView)v.findViewById(R.id.textView11);
        textView12 = (TextView)v.findViewById(R.id.textView12);
        textView13 = (TextView)v.findViewById(R.id.textView13);
        textView14 = (TextView)v.findViewById(R.id.textView14);
        buttonAdd = (Button)v.findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(new Button.OnClickListener(){ 
            @Override 
            public void onClick(View v) { 
                // TODO Auto-generated method stub
//            	textView06.setText(analysis_results[0]);
            	System.out.println("insert data into DB");
            	mContentValues.put(UserSchema.STOCK_NO, stockNum);
                v.getContext().getContentResolver().insert(mUriDB, mContentValues);
                Toast.makeText(v.getContext(), stockNum + " ЧΘ", Toast.LENGTH_SHORT).show();
                buttonAdd.setEnabled(false);
            }         
        });     

        buttonAnalysis = (Button)v.findViewById(R.id.buttonAnalysis);
        buttonAnalysis.setOnClickListener(new Button.OnClickListener(){ 
            @Override 
            public void onClick(View v) { 
                // TODO Auto-generated method stub
            	new AlertDialog.Builder(v.getContext())
                .setTitle("だ猂挡狦")
                .setMessage(analysis_results[0])
                .setPositiveButton("絋﹚", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {            	
                    }
                })
                .show();
            }         
        }); 
        
        progressbar1 = (ProgressBar) v.findViewById(R.id.progressBar1);
        
    	textView00.setText("布 : N/A");
        textView01.setText("Θユ : N/A");
        textView02.setText("セ : N/A");
        textView03.setText("カ犁Μゑ : N/A");
        textView04.setText("狥舦痲厨筍瞯 : N/A");
        textView05.setText("–瞓 : N/A");
   //     textView06.setText("菌基猭 : N/A");
        textView07.setText("瞶基 - 禥基 :\n    セ痲ゑ猭 : N/A\n    瞓ゑ猭 : N/A\n    菌基猭 : N/A\n    瞷 : N/A");
        textView08.setText("セ痲ゑ : N/A");
        textView09.setText("崔瞯 :\n    ユら戳 : N/A\n    瞷 : N/A\n    布 : N/A\n    埃舦把σ基 : N/A");
        textView10.setText("基Θ夹 : N/A");
        textView11.setText("玻穨摸 : N/A");
        textView12.setText("狥计 : N/A");
        textView13.setText("氨穕基 : N/A");
        textView14.setText("犁Μ糤瞯 : N/A");
        buttonAdd.setVisibility(View.INVISIBLE);
        buttonAnalysis.setVisibility(View.INVISIBLE);
        
        
	        
        String curYear = null;
        String curMonth = null;
        String curDay = null;
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("yyyy");
        curYear = sdf.format(new java.util.Date()); 
        sdf = new SimpleDateFormat("MM");
        curMonth = sdf.format(new java.util.Date());
        sdf = new SimpleDateFormat("dd");
        curDay = sdf.format(new java.util.Date());        
        searchCmd[0] = "price";
        searchCmd[1] =	"https://tw.stock.yahoo.com/q/q?s=" + stockNum;
        searchCmd[2] =	"capital";
        searchCmd[3] =	"https://tw.finance.yahoo.com/d/s/company_" + stockNum + ".html";
        searchCmd[4] =	"stop_price";
        searchCmd[5] =	"http://money.hinet.net/z/z0/z00/z00a_" + stockNum + "_" + (Integer.parseInt(curYear) - 1) + "-" + curMonth + "-" + curDay + "_" + curYear + "-" + curMonth + "-" + curDay + "_W.djhtm";
        searchCmd[6] =	"history_stock";   
        searchCmd[7] =  "http://www.twse.com.tw/exchangeReport/FMNPTK?response=html&stockNo=" + stockNum;
        searchCmd[8] =	"history_stock_retry";   
        if(curMonth.compareTo("01") == 0)
        	searchCmd[9] =	"http://www.twse.com.tw/ch/trading/exchange/FMNPTK/genpage/Report" + (Integer.parseInt(curYear) - 1) + curMonth + "/" + stockNum + "_F3_1_11.php";
        else
        	searchCmd[9] =	"http://www.twse.com.tw/ch/trading/exchange/FMNPTK/genpage/Report" + curYear + "01" + "/" + stockNum + "_F3_1_11.php";     
        searchCmd[10] =	"history_per";
        searchCmd[11] =	"http://jsjustweb.jihsun.com.tw/z/zc/zca/zca_" + stockNum + ".djhtm";      
        searchCmd[12] =	"history_cash_dividend";
        searchCmd[13] =	"https://tw.finance.yahoo.com/d/s/dividend_" + stockNum + ".html";
        searchCmd[14] =	"history_pbr";
        searchCmd[15] =	"http://goodinfo.tw/StockInfo/StockBzPerformance.asp?STOCK_ID=" + stockNum;
        searchCmd[16] =	"exclude_dividend_right";
        searchCmd[17] =	"http://www.cnyes.com/twstock/dividend/" + stockNum + ".htm";
        searchCmd[18] =	"shareholder";
        searchCmd[19] =	"http://stock.nlog.cc/c/" + stockNum + "/1"; 
        searchCmd[20] =	"psr";
        searchCmd[21] =	"https://tw.finance.yahoo.com/d/s/earning_" + stockNum + ".html";

        progressbar1.setVisibility(View.VISIBLE);
        progressbar1.setMax(100); // 砞﹚程琌100
        progressbar1.setProgress(0); // 砞﹚ヘ玡秈0	
        mUriDB = Uri.parse("content://com.stock.money.provider.dbprovider");
        
        Cursor cur = v.getContext().getContentResolver().query(mUriDB, FROM , "stock_no='" + stockNum + "'", null, null);        
		if(cur.getCount()>0)
			buttonAdd.setEnabled(false);
			
        Thread thread = new Thread(new Runnable() {
		 public void run() {
		      try {
		    	  analysis_results = loadData(searchCmd);
		      } catch (Exception e) {
		           // TODO Auto-generated catch block
		           e.printStackTrace();
		      }
		
		 }
        });
        thread.start();    
            
		return v;
	}
	
	private String[] loadData(String... urls) throws Exception {
		String[] results = new String[1];
		String name = null;
		String price = null;
		String industry = null;
		String capital = null;
		String revenue = null;
		String roe = null;
		String bookValue = null;
		String history_stock_year = null;
		String history_stock_high = null;
		String history_stock_low = null;
		String history_stock_medium = null;
		String history_per_high = null;
		String history_per_low = null;
		String history_per_medium = null;
		String history_eps = null;
		String history_cash_dividend = null;	
		String history_pbr_high = null;
		String history_pbr_low = null;
		String netValue = null; 
		String curYear = null;
		String curMonth = null;    
		String stopPrice = null;  
		ArrayList<String> curYearmonthEarning = new ArrayList<String>();
		ArrayList<String> lastYearmonthEarning = new ArrayList<String>();
		double d_capital = 0;
		double d_bookValue = 0;
		double d_roe = 0;
		double d_total_revenue = 0;
		double d_psr = -1;
		double d_price = 0;
		double d_gvi = -1;
		double d_stopPrice = -1;
		float d_history_stock_high = 0;
		float d_history_stock_low = 0;
		float d_history_stock_medium = 0;
		float d_history_per_high = 0;
		float d_history_per_low = 0;
		float d_history_per_medium = 0;
		float d_history_eps = 0;
		float d_history_cash_dividend = 0;
		float d_history_pbr_high = 0;
		float d_history_pbr_low = 0;
		float d_history_per_reasonablePrice = -1;			// セ痲ゑ
		float d_history_pbr_reasonablePrice = -1;			// 瞓ゑ		
		float d_history_stock_reasonablePrice = -1;			// 菌基
		float d_history_cash_dividend_reasonablePrice = -1;	// 猭
		float d_history_per_expansePrice = -1;				// セ痲ゑ
		float d_history_pbr_expansePrice = -1;				// 瞓ゑ		
		float d_history_stock_expansePrice = -1;			// 菌基
		float d_history_cash_dividend_expansePrice = -1;	// 猭		
		float d_netValue = 0; 
		float d_exclude_dividend = -1;
		float d_exclude_right = -1;		
		float [] d_shareholderValue = {-1, -1}; 
		float [] d_monthEarning = {-1, -1};	
		boolean b_monthEarning = false;
		int count = 0;
		int td_size = 0;
		int n_exclude_dividend_year = -1;
		DecimalFormat df=new DecimalFormat("#.##");
		Message msg = null;
		SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("yyyy");
        curYear = sdf.format(new java.util.Date()); 
        sdf = new SimpleDateFormat("MM");
        curMonth = sdf.format(new java.util.Date()); 
		int i = 0, j = 0;
		URL url = null;
		results[0] = "";
		for(i = 0; i < urls.length; i = i+2) {
			msg = new Message();
			Bundle data = new Bundle();
			if(urls[i].compareTo("price") == 0) {
				System.out.println("Parsing price ...");
				url = new URL(urls[i+1]);
			    Document doc = null;
			    try {
			    	doc = Jsoup.parse(url, TIMEOUTMILLIS);
				} catch (MalformedURLException e) {
					System.out.println(e.toString());		        
				} catch (IOException e) {
					System.out.println(e.toString());
				}	
			    if((doc==null) || (doc.select("table[border=2]").size() < 1))
			    	continue;
				Element table0 = doc.select("table[border=2]").get(0);
				// Θユ基
				price = table0.select("td").get(2).text();
				price = price.replace(",", "");
				if(price.compareTo("⌒") == 0)
					d_price = 0;
				else
					d_price = Double.parseDouble(price);
				System.out.println("price =" + price);
				name = table0.select("td").get(0).text();
				name = name.substring(0, name.lastIndexOf(""));
				if(d_price > 0)
					data.putStringArray("str", new String[]{name, df.format(d_price)});  			
				else
					data.putStringArray("str", new String[]{name, "N/A"});  	
				msg.what = PRICE;
				msg.setData(data);
				msg.arg1 = i+1;
				msg.arg2 = urls.length;
				mHandler.sendMessage(msg);
				System.out.println("name =" + name);
			} else if(urls[i].compareTo("capital") == 0) {
				System.out.println("Parsing capital ...");
				url = new URL(urls[i+1]);
			    Document doc = null;	
				try {
					doc = Jsoup.parse(url, TIMEOUTMILLIS);
				} catch (MalformedURLException e) {
					System.out.println(e.toString());		        
				} catch (IOException e) {
					System.out.println(e.toString());
				}	
			    if((doc==null) || (doc.select("table[border=0]").size() < 9))
			    	continue;
				Element table0 = doc.select("table[border=0]").get(7);
				industry = table0.select("td").get(3).text();						
				if(industry.lastIndexOf(" ") != -1)
					industry = industry.substring(0, industry.lastIndexOf(" "));
				capital = table0.select("td").get(25).text();	
				capital = capital.replace(",", "");
				System.out.println("before capital =" + capital);
				// セ 
				if(capital.lastIndexOf("货") != -1) {
					capital = capital.substring(0, capital.lastIndexOf("货"));
					d_capital = Double.parseDouble(capital);
				}
				else {
					capital = null;
					d_capital = 0;
				}
				System.out.println("capital =" + capital);
				table0 = doc.select("table[border=0]").get(8);
				if( table0.select("td").size() < 30 )
					continue;
				roe = table0.select("td").get(28).text();
				if(roe.compareTo("⌒") == 0 || roe.compareTo("") == 0)
					d_roe = 0;
				else {	
					roe = roe.replace(",", "");
					roe = roe.substring(0, roe.lastIndexOf("%"));
					d_roe = Double.parseDouble(roe);
					d_roe *= 4;
				}
				System.out.println("roe : " + d_roe);
				
				bookValue = table0.select("td").get(29).text();	
				bookValue = bookValue.replace(",", "");
				String startStr = new String("–瞓: ");
				if(bookValue.lastIndexOf("じ") != -1) {
					bookValue = bookValue.substring(startStr.length(), bookValue.lastIndexOf("じ"));
					d_bookValue = Double.parseDouble(bookValue);
				}
				else 
					d_bookValue = 0;
				System.out.println("bookValue : " + d_bookValue);
		
				for(j=0;j<4;j++) {
					history_eps = table0.select("td").get(6+6*j).text();
					history_eps = history_eps.replace(",", "");
					if(history_eps.lastIndexOf("じ") != -1) { 
						history_eps = history_eps.substring(0, history_eps.lastIndexOf("じ"));
						d_history_eps += Float.parseFloat(history_eps);
					}
				}
				if(j<4)
					d_history_eps = 0;					
		        System.out.println("history_eps : " + d_history_eps);
		        
				d_gvi = -1;
				if(d_price>0) {
					if(d_roe > 0)
						d_gvi = (d_bookValue/d_price) * Math.pow(1+ 0.01*d_roe, 5);
					else
						d_gvi = (d_bookValue/d_price);
				}
				System.out.println("gvi : " + d_gvi);	
				String strCap = "N/A";
				String strRoe = "N/A";
				String strBookValue = "N/A";
				String strGvi = "N/A";		
				if(d_capital>=0)
					strCap = df.format(d_capital) + "货";
				if(d_roe>=0)
					strRoe = df.format(d_roe) + "%";
				if(d_bookValue>=0)
					strBookValue = df.format(d_bookValue) + "じ";
				if(d_gvi>=0)
					strGvi = df.format(d_gvi);
		        data.putStringArray("str", new String[]{strCap, strRoe, strBookValue, strGvi, industry});  						
				msg.what = CAPITAL;
				msg.setData(data);
				msg.arg1 = i+1;
				msg.arg2 = urls.length;
				mHandler.sendMessage(msg);				        
			} else if(urls[i].compareTo("stop_price") == 0) {
				System.out.println("Parsing stop_price ...");
				url = new URL(urls[i+1]);
				Document doc = null;
				try {
					doc = Jsoup.parse(url, TIMEOUTMILLIS*3);
				} catch (MalformedURLException e) {
					System.out.println(e.toString());		        
				} catch (IOException e) {
					System.out.println(e.toString());
				}	
			    if((doc==null) || (doc.select("table[border=0]").size() < 2)) {
			    	System.out.println("error : table size is less than 2");
			    	continue;
			    }
			    Element table0 = doc.select("table[border=0]").get(1);
			    td_size = table0.select("td").size();  
			    count = 0;   
				j = 11 + 6*count;
				d_stopPrice = -1;
		        while((j < td_size) && (count < 20))
		        {
		        	stopPrice = table0.select("td").get(j).text();
		        	d_stopPrice += Double.parseDouble(stopPrice);
		        	count++;
		        	j = 11 + 6*count;
		        }
		        if(count > 0) {
		        	d_stopPrice /= count;
		        	data.putStringArray("str", new String[]{df.format(d_stopPrice)}); 
		        }
		        else {
		        	d_stopPrice = -1;
		        	data.putStringArray("str", new String[]{"N/A"});
		        }
		        msg.what = STOP_PRICE;
				msg.setData(data);
				msg.arg1 = i+1;
				msg.arg2 = urls.length;
				mHandler.sendMessage(msg);
			} else if(urls[i].compareTo("history_stock") == 0) {
				System.out.println("Parsing history_stock ...");
				url = new URL(urls[i+1]);
				Document doc = null;
				j = 0;
				while(j<2) {
					try {
						doc = Jsoup.parse(url, TIMEOUTMILLIS);
					} catch (MalformedURLException e) {
						System.out.println(e.toString());		        
					} catch (IOException e) {
						System.out.println(e.toString());
					}	
					if(doc!=null)
						break;
					else
						url = new URL(urls[i+3]);
					j++;
				}
			    if((doc == null) || (doc.select("table").size() < 1)) {
			    	System.out.println("error : table size is less than 1");
			    	continue;
			    }
			    Element table0 = doc.select("table").get(0);
				j = 0;
				count = 0;
				d_history_stock_low = 0;
				d_history_stock_medium = 0;
				d_history_stock_high = 0;
				d_history_stock_reasonablePrice = -1;
		        while(table0.select("td").size() > (9+j*9))
		        {
					history_stock_year = table0.select("td").get(9+j*9).text();
					if(history_stock_year == null)
						break;
					history_stock_high = table0.select("td").get(9+j*9+4).text();
					history_stock_low = table0.select("td").get(9+j*9+6).text();
					history_stock_medium = table0.select("td").get(9+j*9+8).text();
					j++;
					if( history_stock_high.compareTo("--") == 0 || history_stock_low.compareTo("--") == 0 || history_stock_medium .compareTo("--") == 0)
						continue;
					history_stock_high = history_stock_high.replace(",", "");
					history_stock_low = history_stock_low.replace(",", "");
					history_stock_medium = history_stock_medium.replace(",", "");
					d_history_stock_high += Float.parseFloat(history_stock_high);
					d_history_stock_low += Float.parseFloat(history_stock_low);
					d_history_stock_medium += Float.parseFloat(history_stock_medium);
					count++;
		        	if( (Integer.parseInt(curYear) - Integer.parseInt(history_stock_year)) == 1912)
		        		break;
		        }
		        if(count>0) {
		        	d_history_stock_high /= count;
		        	d_history_stock_low /= count;
		        	d_history_stock_medium /= count;
		        	d_history_stock_reasonablePrice = d_history_stock_medium;
		        	d_history_stock_expansePrice = d_history_stock_high;
		        }
		        System.out.println("d_history_stock_low : " + d_history_stock_low + "\nd_history_stock_medium : " + d_history_stock_medium + "\nd_history_stock_high : " + d_history_stock_high);          
				msg.what = HISTORY_STOCK;
				msg.setData(data);
				msg.arg1 = i+1;
				msg.arg2 = urls.length;
				mHandler.sendMessage(msg);						        
			} else if(urls[i].compareTo("history_per") == 0) {
				System.out.println("Parsing history_per ...");
				url = new URL(urls[i+1]);
			    Document doc = null;
			    try {
			    	doc = Jsoup.parse(url, TIMEOUTMILLIS);
				} catch (MalformedURLException e) {
					System.out.println(e.toString());		        
				} catch (IOException e) {
					System.out.println(e.toString());
				}
			    if((doc==null) || (doc.select("table[border=0]").size() < 4)) {
			    	System.out.println("error : table size is less than 4");
			    	continue;
			    }
			    Element table0 = doc.select("table[border=0]").get(2);
			    td_size = table0.select("td").size();			    
			    if(td_size > 17) 
			    	data.putStringArray("str", new String[]{table0.select("td").get(17).text()}); 
			    else
			    	data.putStringArray("str", new String[]{"N/A"});
			    table0 = doc.select("table[border=0]").get(3);
				j = 0;
				count = 0;
				td_size = table0.select("td").size();
				d_history_per_high = 0;
				d_history_per_low = 0;
				d_history_per_reasonablePrice = -1;
				while(td_size > 45 && j < 7 )
		        {							
					history_per_high = table0.select("td").get(29+j).text();
					history_per_low = table0.select("td").get(38+j).text();
					j++;
					if(history_per_high == null || history_per_high.compareTo("N/A") == 0 || history_per_high.compareTo("") == 0 ||
						history_per_low == null || history_per_low.compareTo("N/A") == 0 || history_per_low.compareTo("") == 0)
						continue;
					history_per_high = history_per_high.replace(",", "");
					history_per_low = history_per_low.replace(",", "");
					d_history_per_high += Float.parseFloat(history_per_high);
					d_history_per_low += Float.parseFloat(history_per_low);
					count++;						
		        }
		        if(count>0) {
		        	d_history_per_high /= count;
		        	d_history_per_low /= count;
		        	d_history_per_medium = (d_history_per_high + d_history_per_low) / 2;
		        	d_history_per_reasonablePrice = d_history_per_medium * d_history_eps;
		        	d_history_per_expansePrice = d_history_per_high * d_history_eps;
		        }     
		        System.out.println("d_history_per_low : " + d_history_per_low + "\nd_history_per_medium : " + d_history_per_medium + "\nd_history_per_high : " + d_history_per_high);
		        msg.what = HISTORY_PER;
				msg.setData(data);
				msg.arg1 = i+1;
				msg.arg2 = urls.length;
				mHandler.sendMessage(msg);
			} else if(urls[i].compareTo("history_cash_dividend") == 0) {
				System.out.println("Parsing history_cash_dividend ...");
				url = new URL(urls[i+1]);
			    Document doc = null;
			    try {
			    	doc = Jsoup.parse(url, TIMEOUTMILLIS);
				} catch (MalformedURLException e) {
					System.out.println(e.toString());		        
				} catch (IOException e) {
					System.out.println(e.toString());
				}
			    if((doc==null) || (doc.select("table").size() < 10)) {
			    	System.out.println("error : table size is less than 10");
			    	continue;
			    }
			    Element table0 = doc.select("table").get(9);
				count = 0;
				td_size = table0.select("td").size();
				d_history_cash_dividend_reasonablePrice = -1;
				d_history_cash_dividend = 0;
				d_exclude_dividend = -1;
	    		d_exclude_right = -1;	
	    		n_exclude_dividend_year = -1;
				j = 7 + count*6;
		        while(j < td_size)
		        {
		        	if(count==0) {		        				        		
		        		n_exclude_dividend_year = Integer.parseInt(table0.select("td").get(j-1).text().replace(",", ""))+1912;
	        			d_exclude_dividend = Float.parseFloat(table0.select("td").get(j).text().replace(",", ""));
	        			d_exclude_right = Float.parseFloat(table0.select("td").get(j+3).text().replace(",", ""));
		        	}		    		
		        	history_cash_dividend = table0.select("td").get(j).text();
		        	history_cash_dividend = history_cash_dividend.replace(",", "");
		        	d_history_cash_dividend += Float.parseFloat(history_cash_dividend);		
		            count++;
		            j = 7 + count*6;
		        }
		        if(count>0) {
		        	d_history_cash_dividend /= count;
		        	d_history_cash_dividend_reasonablePrice = d_history_cash_dividend * 20;
		        	d_history_cash_dividend_expansePrice = d_history_cash_dividend * 30;
		        }
		        System.out.println("d_history_cash_dividend : " + d_history_cash_dividend);
		        msg.what = HISTORY_CASH_DIVIDEND;
				msg.setData(data);
				msg.arg1 = i+1;
				msg.arg2 = urls.length;
				mHandler.sendMessage(msg);
			} else if(urls[i].compareTo("history_pbr") == 0) {		
				System.out.println("Parsing history_pbr ...");
				String printStr = "";
				url = new URL(urls[i+1]);
			    Document doc = null;
			    try {
			    	doc = Jsoup.parse(url, TIMEOUTMILLIS);
				} catch (MalformedURLException e) {
					System.out.println(e.toString());		        
				} catch (IOException e) {
					System.out.println(e.toString());
				}
			    /*
			    Element table0 = doc.select("table[border=0]").get(8);
			    count = 0;
				Iterator <Element> item = table0.select("td").iterator();        
		        // Print content
		        while(item.hasNext() && count < 190)
		        {
		            count++;

		            String linkText = item.next().text();
		            System.out.println("c" + count + " = " + linkText);
		            if(count==1)
		            	data.putStringArray("str", new String[]{linkText}); 
		        }
		        */
			    
			    if((doc==null) || (doc.select("table[border=0]").size() < 9)) {
			    	System.out.println("error : table size is less than 9");
			    } else {
				    d_netValue = 0;
				    d_history_pbr_low = 0;
				    d_history_pbr_high = 0;
				    d_history_pbr_reasonablePrice = -1;
				    Element table0 = doc.select("table[border=0]").get(8);	   
				    td_size = table0.select("td").size();
			        if( td_size > 80) {
			        	netValue = table0.select("td").get(49).text();
			        	System.out.println("netValue" + netValue);
						if((netValue.lastIndexOf("") != -1) && (netValue.lastIndexOf("じ") != -1)) { 
							netValue = netValue.substring(netValue.lastIndexOf("") + 2, netValue.lastIndexOf("じ") -1);
							netValue = netValue.replace(",", "");
							d_netValue = Float.parseFloat(netValue);
						}
			        	history_pbr_low = table0.select("td").get(79).text();
			        	history_pbr_high = table0.select("td").get(80).text();
			        	history_pbr_low = history_pbr_low.replace(",", "");
			        	history_pbr_high = history_pbr_high.replace(",", "");
			        	if(history_pbr_low.compareTo("-") != 0)
			        		d_history_pbr_low = Float.parseFloat(history_pbr_low);
			        	if(history_pbr_high.compareTo("-") != 0)
			        		d_history_pbr_high = Float.parseFloat(history_pbr_high);
			        }			        	
			        System.out.println("d_history_pbr_low : " + d_history_pbr_low + "\nd_history_pbr_high : " + d_history_pbr_high + "\nd_netValue : " + d_netValue);
			        if(d_history_pbr_low > 0 && d_history_pbr_high > 0 && d_netValue > 0) {
			        	d_history_pbr_reasonablePrice = (d_history_pbr_low + d_history_pbr_high)/2 * d_netValue;
			        	d_history_pbr_expansePrice = d_history_pbr_high * d_netValue;
			        }
			    }
		        if(d_history_per_reasonablePrice>=0)
		        	printStr += "\n    セ痲ゑ猭 : " + df.format(d_history_per_reasonablePrice) + " - " + df.format(d_history_per_expansePrice);
		        else 
		        	printStr += "\n    セ痲ゑ猭 : N/A";
		        if(d_history_pbr_reasonablePrice>=0)
		        	printStr += "\n    瞓ゑ猭 : " + df.format(d_history_pbr_reasonablePrice) + " - " + df.format(d_history_pbr_expansePrice);
		        else 
		        	printStr += "\n    瞓ゑ猭 : N/A";
		        if(d_history_stock_reasonablePrice>=0)
		        	printStr += "\n    菌基猭 : " + df.format(d_history_stock_reasonablePrice) + " - " + df.format(d_history_stock_expansePrice);
		        else 
		        	printStr += "\n    菌基猭 : N/A";
		        if(d_history_cash_dividend_reasonablePrice>=0)
		        	printStr += "\n    瞷 : " + df.format(d_history_cash_dividend_reasonablePrice) + " - " + df.format(d_history_cash_dividend_expansePrice);
		        else 
		        	printStr += "\n    瞷 : N/A";		
				data.putStringArray("str", new String[]{printStr}); 
		        msg.what = HISTORY_PBR;
				msg.setData(data);
				msg.arg1 = i+1;
				msg.arg2 = urls.length;
				mHandler.sendMessage(msg);	
			} else if(urls[i].compareTo("exclude_dividend_right") == 0) {
				System.out.println("Parsing exclude dividend and right ...");
				String printStr = "";
				String date = null;
				url = new URL(urls[i+1]);
			    Document doc = null; 
			    try {
				   doc = Jsoup.parse(url, TIMEOUTMILLIS);
				} catch (MalformedURLException e) {
					System.out.println(e.toString());		        
				} catch (IOException e) {
					System.out.println(e.toString());
				}	
			    if((doc==null) || (doc.select("table").size() < 2)) {
			    	date = curYear;
			    } else {
				    Element table0 = doc.select("table").get(1);
				    td_size = table0.select("td").size();  
				    if(td_size < 4 || d_price <=0 )
				    	continue;			
				    date = table0.select("td").get(0).text();
				    if(n_exclude_dividend_year==Integer.parseInt(curYear)) {
				    	if(date.contains(curYear)==false)
				    		date = curYear;
				    }
				    else {
					    d_exclude_dividend = Float.parseFloat(table0.select("td").get(1).text().replace(",", ""));
					    d_exclude_right = Float.parseFloat(table0.select("td").get(2).text().replace(",", "")) + Float.parseFloat(table0.select("td").get(3).text().replace(",", ""));
					    d_exclude_right /= 100;
				    }
			    }
			    printStr += "崔瞯 : " + df.format(((d_price*(1+d_exclude_right/10)+d_exclude_dividend) - d_price)*100 / d_price) + "%";
			    printStr += "\n    ユら戳 : " + date;
			    printStr += "\n    瞷 : " + df.format(d_exclude_dividend);
			    printStr += "\n    布 : " + df.format(d_exclude_right);
			    printStr += "\n    埃舦把σ基 : " + df.format((d_price - d_exclude_dividend) / (1+d_exclude_right/10));
				System.out.println("date : " + date + ", d_exclude_dividend : " + d_exclude_dividend + ", d_exclude_right : " + d_exclude_right); 
			    data.putStringArray("str", new String[]{printStr});  						
				msg.what = EXCLUDE_DIVIDEND_RIGHT;
				msg.setData(data);
				msg.arg1 = i+1;
				msg.arg2 = urls.length;
				mHandler.sendMessage(msg);
			} else if(urls[i].compareTo("shareholder") == 0) {
				System.out.println("Parsing shareholder ...");
				url = new URL(urls[i+1]);
			    Document doc = null; 
			    try {
				   doc = Jsoup.parse(url, TIMEOUTMILLIS);
				} catch (MalformedURLException e) {
					System.out.println(e.toString());		        
				} catch (IOException e) {
					System.out.println(e.toString());
				}	
			    if((doc==null) || (doc.select("table[id=d1]").size() < 1))
			    	continue;    
			    String shareholderStr = "";
			    String tmpYear = null;
			    String tmpValue = null;				   
			    d_shareholderValue[0] = -1;
			    d_shareholderValue[1] = -1;
			    Element table0 = doc.select("table[id=d1]").get(0);
			    td_size = table0.select("td").size();  
			    for(j=5; j >=0; j--) {
			    	if(td_size-16-16*j >= 0) {
			    		tmpValue = table0.select("td").get(td_size-1-16*j).text();
			    		tmpYear = table0.select("td").get(td_size-16-16*j).text();
			    		if(tmpYear!=null && tmpValue!=null) {
			    			shareholderStr += "\n    " + tmpYear + "    " + tmpValue;
			    			if(j==1)
			    				d_shareholderValue[0] = Float.parseFloat(tmpValue);
			    			else if((j==0) && (d_shareholderValue[0]!=-1))
			    				d_shareholderValue[1] = Float.parseFloat(tmpValue);
			    		}
			    	} else
			    		break;
			    }
			    data.putStringArray("str", new String[]{shareholderStr});  						
				msg.what = SHAREHOLDER;
				msg.setData(data);
				msg.arg1 = i+1;
				msg.arg2 = urls.length;
				mHandler.sendMessage(msg);	
			} else if(urls[i].compareTo("psr") == 0) {
				System.out.println("Parsing psr ...");
				url = new URL(urls[i+1]);
			    Document doc = null;
			    try {
			    	doc = Jsoup.parse(url, TIMEOUTMILLIS);
				} catch (MalformedURLException e) {
					System.out.println(e.toString());		        
				} catch (IOException e) {
					System.out.println(e.toString());
				}	
				if((doc==null) || (doc.select("table[border=0]").size() < 7))
			    	continue;
				Element table0 = doc.select("table[border=0]").get(6);
				count = 0;
				d_psr = -1;
				// Θユ基  	18~26 1る
				String formatStr = "%02d";
				d_monthEarning[0] = -1;
				d_monthEarning[1] = -1;
				b_monthEarning = false;
				for(j = 11; j >= 0; j--) {
					revenue = table0.select("td").get(22+j*9).text();
					revenue = revenue.replace(",", "");
					lastYearmonthEarning.add( (Integer.parseInt(curYear) - 1) + String.format(formatStr, j+1) + "    " + table0.select("td").get(20+j*9).text());
					if(revenue.compareTo("-") == 0) {
						revenue = table0.select("td").get(19+j*9).text();
						revenue = revenue.replace(",", "");
						if(revenue.compareTo("-") != 0) {
							count++;
							d_total_revenue += Double.parseDouble(revenue);
						} else
							System.out.println("error!");
					} else {
						count++;
						d_total_revenue += Double.parseDouble(revenue);
						if(curYearmonthEarning.size() < 6)
							curYearmonthEarning.add( curYear + String.format(formatStr, j+1) + "    " + table0.select("td").get(23+j*9).text());
					}
				}
				int curYearmonthEarningSize = curYearmonthEarning.size();
				for(j = 0; j < (6 - curYearmonthEarningSize); j++)
					curYearmonthEarning.add(lastYearmonthEarning.get(j));	
				if(curYearmonthEarning.size() > 2) {
					b_monthEarning = true;
					d_monthEarning[0] = Float.parseFloat(curYearmonthEarning.get(1).substring(10, curYearmonthEarning.get(1).lastIndexOf("%")));
					d_monthEarning[1] = Float.parseFloat(curYearmonthEarning.get(0).substring(10, curYearmonthEarning.get(0).lastIndexOf("%")));
				}
				if(count != 12)
					d_total_revenue = 0;
				System.out.println("total_revenue : " + d_total_revenue);	
				if(d_total_revenue > 0 && d_capital > 0 && d_price > 0) {
					d_psr =  d_capital * 10000 * d_price / d_total_revenue;	
					data.putStringArray("str", new String[]{df.format(d_psr)});
				}
				else
					data.putStringArray("str", new String[]{"n/A"});
				System.out.println("psr :" + d_psr);
				data.putStringArrayList("last6monthEarning", curYearmonthEarning); 
				msg.what = PSR;
				msg.setData(data);
				msg.arg1 = i+1;
				msg.arg2 = urls.length;
				mHandler.sendMessage(msg);	
			}
		} 
		if(d_psr>=0) {
			if(d_psr<=3)
				results[0] += "カ犁Μゑ         : O\n";
			else
				results[0] += "カ犁Μゑ         : X\n";
		}
		if(b_monthEarning) {
			if((d_monthEarning[1]>0) && (d_monthEarning[0]>0))
				results[0] += "犁Μ糤瞯         : O\n";
			else
				results[0] += "犁Μ糤瞯         : X\n";
		}
		if(d_history_per_reasonablePrice>=0) {
			if(d_history_pbr_reasonablePrice>=0) {
				if((d_price<d_history_per_reasonablePrice) && (d_price<d_history_pbr_reasonablePrice))
					results[0] += "瞶щ戈基         : O\n";
				else
					results[0] += "瞶щ戈基         : X\n";
			}
			else {
				if(d_price<d_history_per_reasonablePrice)
					results[0] += "瞶щ戈基         : O\n";
				else
					results[0] += "瞶щ戈基         : X\n";
			}
		}
		else if(d_history_pbr_reasonablePrice>=0) {
			if(d_price<d_history_pbr_reasonablePrice)
				results[0] += "瞶щ戈基         : O\n";
			else
				results[0] += "瞶щ戈基         : X\n";
		}
		else if(d_history_stock_reasonablePrice>=0) {
			if(d_price<d_history_stock_reasonablePrice)
				results[0] += "瞶щ戈基         : O\n";
			else
				results[0] += "瞶щ戈基         : X\n";
		}
		else if(d_history_cash_dividend_reasonablePrice>=0)	{
			if(d_price<d_history_cash_dividend_reasonablePrice)
				results[0] += "瞶щ戈基         : O\n";
			else
				results[0] += "瞶щ戈基         : X\n";
		}
		System.out.println(	"d_history_per_reasonablePrice : " + d_history_per_reasonablePrice +
							", d_history_pbr_reasonablePrice : " + d_history_pbr_reasonablePrice +
							", d_history_stock_reasonablePrice : " + d_history_stock_reasonablePrice +
							", d_history_cash_dividend_reasonablePrice" + d_history_cash_dividend_reasonablePrice);
		if(d_stopPrice>=0) {
			if(d_price > d_stopPrice)
				results[0] += "氨穕基繧         : O\n";
			else
				results[0] += "氨穕基繧         : X\n";
		}
		if((d_shareholderValue[0]>=0) && (d_shareholderValue[1]>=0))
		{
			if(d_shareholderValue[1] >= d_shareholderValue[0])
				results[0] += "狥         : O\n";
			else
				results[0] += "狥         : X\n";
		}		
		if(d_gvi>=0) {
			if(d_gvi>=1)
				results[0] += "基Θ夹     : O\n";
			else
				results[0] += "基Θ夹     : X\n";
		}
		System.out.println(	"d_shareholderValue[0] : " + d_shareholderValue[0] + 
							", d_shareholderValue[1] : " + d_shareholderValue[1]);
		msg = new Message();
		msg.what = FINISH;
		mHandler.sendMessage(msg);
		System.out.println("Finish");
		return results;
	}

	//砞﹚ProgressBar秈
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			Bundle bundle = msg.getData();  
			int total = 0;
			int current = 0;
			int count = 0;
			int i = 0;
			int last3monthEarningSize = 0;
			String itemStr = null;
			switch (msg.what) {
				case PRICE:
					total = msg.arg2;
					current = msg.arg1;
	                count = current * 100 / total;
	                progressbar1.setProgress(count);
	                textView00.setText("布 : " + bundle.getStringArray("str")[0]);
		            textView01.setText("Θユ : " + bundle.getStringArray("str")[1]);		            
	                break;
				case CAPITAL:
	                total = msg.arg2;
	                current = msg.arg1;
	                count = current * 100 / total;
	                progressbar1.setProgress(count);
		            textView02.setText("セ : " + bundle.getStringArray("str")[0]);
		            textView04.setText("狥舦痲厨筍瞯 : " + bundle.getStringArray("str")[1]);
		            textView05.setText("–瞓 : " + bundle.getStringArray("str")[2]);
		            textView10.setText("基Θ夹 : " + bundle.getStringArray("str")[3]);
		            textView11.setText("玻穨摸 : " + bundle.getStringArray("str")[4]);
	                break;
				case STOP_PRICE:
	                total = msg.arg2;
	                current = msg.arg1;
	                count = current * 100 / total;
	                progressbar1.setProgress(count);
	                textView13.setText("氨穕基 : " + bundle.getStringArray("str")[0]);
	                break;  
				case HISTORY_STOCK:
	                total = msg.arg2;
	                current = msg.arg1;
	                count = current * 100 / total;
	                progressbar1.setProgress(count);
	                break;
				case HISTORY_PER:
	                total = msg.arg2;
	                current = msg.arg1;
	                count = current * 100 / total;
	                progressbar1.setProgress(count);
	                textView08.setText("セ痲ゑ : " + bundle.getStringArray("str")[0]);
	                break;
				case HISTORY_CASH_DIVIDEND:
	                total = msg.arg2;
	                current = msg.arg1;
	                count = current * 100 / total;
	                progressbar1.setProgress(count);
	                break;  
				case HISTORY_PBR:
	                total = msg.arg2;
	                current = msg.arg1;
	                count = current * 100 / total;
	                progressbar1.setProgress(count);
	                textView07.setText("瞶基 - 禥基 :" + bundle.getStringArray("str")[0]);
	                break;        
				case SHAREHOLDER:
	                total = msg.arg2;
	                current = msg.arg1;
	                count = current * 100 / total;
	                progressbar1.setProgress(count);
		            textView12.setText("狥计 : " + bundle.getStringArray("str")[0]);
	                break;
				case PSR:
	                total = msg.arg2;
	                current = msg.arg1;
	                count = current * 100 / total;
	                progressbar1.setProgress(count);
		            textView03.setText("カ犁Μゑ : " + bundle.getStringArray("str")[0]);
		            
		            last3monthEarningSize = bundle.getStringArrayList("last6monthEarning").size();
		            if(last3monthEarningSize > 0) {
		            	itemStr = "犁Μ糤瞯 : ";
			            for (i = bundle.getStringArrayList("last6monthEarning").size() - 1; i >= 0; i--)
			            	itemStr = itemStr + "\n    " + bundle.getStringArrayList("last6monthEarning").get(i);
		            	textView14.setText(itemStr);
		            }	
	                break;
				case EXCLUDE_DIVIDEND_RIGHT:
	                total = msg.arg2;
	                current = msg.arg1;
	                count = current * 100 / total;
	                progressbar1.setProgress(count);
	                textView09.setText(bundle.getStringArray("str")[0]);
	                break; 
				case FINISH:
	                progressbar1.setProgress(100);
	                progressbar1.setVisibility(View.INVISIBLE);	
	                buttonAdd.setVisibility(View.VISIBLE);
	                buttonAnalysis.setVisibility(View.VISIBLE);
	                break;
			}
		}
	};
}