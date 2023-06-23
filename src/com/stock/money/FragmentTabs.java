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
        mTabManager.addTab(mTabHost.newTabSpec("�d��").setIndicator("�d��",this.getResources().getDrawable(R.drawable.butterfly)),
        		FragmentSearch.class, null);
        mTabManager.addTab(mTabHost.newTabSpec("�ۿ�").setIndicator("�ۿ�",this.getResources().getDrawable(R.drawable.cloud_sun)),
        		FragmentSelfchoice.class, null);        
        mTabManager.addTab(mTabHost.newTabSpec("�z��").setIndicator("�z��",this.getResources().getDrawable(R.drawable.tree)),
        		FragmentAutofilter.class, null);
        mTabManager.addTab(mTabHost.newTabSpec("�~��").setIndicator("�~��",this.getResources().getDrawable(R.drawable.flower)),
        		FragmentForeignCurrency.class, null);
        mTabManager.addTab(mTabHost.newTabSpec("�v��").setIndicator("�v��",this.getResources().getDrawable(R.drawable.pink_flower)),
        		FragmentWarrant.class, null);
        
        DisplayMetrics dm = new DisplayMetrics();   
        getWindowManager().getDefaultDisplay().getMetrics(dm); //�����o�ù��ѪR��  
        int screenWidth = dm.widthPixels;   //���o�ù����e   
        TabWidget tabWidget = mTabHost.getTabWidget();   //���otab������
        int count = tabWidget.getChildCount();   //���otab���������X��
        if (count > 3) {   //�p�G�W�L�T�ӴN�ӳB�z�ư�
            for (int i = 0; i < count; i++) {   
                tabWidget.getChildTabViewAt(i).setMinimumWidth((screenWidth) / 3);//�]�w�C�@�Ӥ����̤p���e��   
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
				.setMessage("�P�±z�ϥΥ��{��")
				.setPositiveButton("�T�w", dialogClickListener)
				.setNegativeButton("����", dialogClickListener)
				.setCancelable(false)
				.setTitle("���}�ѥ����ھ� ?");
				builder.show();
			} 
		}
		return true;
	}
    
}