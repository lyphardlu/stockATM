package com.stock.money;

import com.stock.money.R;
import com.stock.money.TabManager.TabInfo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TabHost;
import android.widget.TabWidget;

/**
 * This demonstrates how you can implement switching between the tabs of a
 * TabHost through fragments.  It uses a trick (see the code below) to allow
 * the tabs to switch between fragments instead of simple views.
 */
public class FragmentTabs extends FragmentActivity {
    private TabHost mTabHost;
    private TabManager mTabManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.fragment_tabs);
        mTabHost = (TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup();
        
        mTabManager = new TabManager(this, mTabHost, R.id.realtabcontent);
        
        mTabHost.setCurrentTab(0);
        mTabManager.addTab(mTabHost.newTabSpec("查詢").setIndicator("查詢",this.getResources().getDrawable(R.drawable.butterfly)),
        		FragmentSearch.class, null);
        mTabManager.addTab(mTabHost.newTabSpec("自選").setIndicator("自選",this.getResources().getDrawable(R.drawable.cloud_sun)),
        		FragmentSelfchoice.class, null);        
        mTabManager.addTab(mTabHost.newTabSpec("篩選").setIndicator("篩選",this.getResources().getDrawable(R.drawable.tree)),
        		FragmentAutofilter.class, null);
        mTabManager.addTab(mTabHost.newTabSpec("外匯").setIndicator("外匯",this.getResources().getDrawable(R.drawable.flower)),
        		FragmentForeignCurrency.class, null);
        mTabManager.addTab(mTabHost.newTabSpec("權證").setIndicator("權證",this.getResources().getDrawable(R.drawable.pink_flower)),
        		FragmentWarrant.class, null);
        
        DisplayMetrics dm = new DisplayMetrics();   
        getWindowManager().getDefaultDisplay().getMetrics(dm); //先取得螢幕解析度  
        int screenWidth = dm.widthPixels;   //取得螢幕的寬   
        TabWidget tabWidget = mTabHost.getTabWidget();   //取得tab的物件
        int count = tabWidget.getChildCount();   //取得tab的分頁有幾個
        if (count > 3) {   //如果超過三個就來處理滑動
            for (int i = 0; i < count; i++) {   
                tabWidget.getChildTabViewAt(i).setMinimumWidth((screenWidth) / 3);//設定每一個分頁最小的寬度   
            }   
        }        
        /*
        tabWidget.getChildAt(0).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	System.out.println("onClick Tab");
            }
        }); */
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK){
			if(getSupportFragmentManager().getBackStackEntryCount() > 0)
				getSupportFragmentManager().popBackStack();
			else {			
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						switch (which){
						case DialogInterface.BUTTON_POSITIVE:
							//Yes button clicked
						   finish();
							break;

						case DialogInterface.BUTTON_NEGATIVE:
							//No button clicked
							break;
						}
					}
				};
				AlertDialog.Builder builder = new AlertDialog.Builder(this)
				.setMessage("感謝您使用本程式")
				.setPositiveButton("確定", dialogClickListener)
				.setNegativeButton("取消", dialogClickListener)
				.setCancelable(false)
				.setTitle("離開股市提款機 ?");
				builder.show();
			} 
		}
		return true;
	}
    
}