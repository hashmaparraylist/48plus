package com.akb48plus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.TextView;

import com.akb48plus.common.Const;
import com.akb48plus.common.auth.AuthUtils;

/**
 * 
 * @author QuSheng
 */
public class MainActivity extends android.app.TabActivity implements OnCheckedChangeListener {

    public static final String TAG = MainActivity.class.getName();
    public static final String INTENT_GROUP_SELECTED = "INTENT_GROUP_SELECTED";
    
    private Intent akb48intent;
    private Intent ske48intent;
    private Intent nmb48intent;
    private Intent hkt48intent;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "MainActivity.onCreate");
        super.onCreate(savedInstanceState);
        requestWindowFeature(android.view.Window.FEATURE_CUSTOM_TITLE);

        final Intent intent = getIntent();
        if (intent == null || !intent.hasExtra("token")) {
            AuthUtils.refreshAuthToken(this);
            return;
        }

        setContentView(R.layout.main);
        getWindow().setFeatureInt(android.view.Window.FEATURE_CUSTOM_TITLE, R.layout.title);
        TextView txtSubTitle = (TextView) findViewById(R.id.txtSubTitle);
        txtSubTitle.setText(R.string.main_sub_title);
       
        setRadioOnCheckedChangedEvent();

        // Init Intent
        akb48intent = new Intent(this, ProfileListActivity.class);
        akb48intent.putExtra(INTENT_GROUP_SELECTED, Const.PREF_AKB_LIST_NAME);
        ske48intent = new Intent(this, ProfileListActivity.class);
        ske48intent.putExtra(INTENT_GROUP_SELECTED, Const.PREF_SKE_LIST_NAME);
        nmb48intent = new Intent(this, ProfileListActivity.class);
        nmb48intent.putExtra(INTENT_GROUP_SELECTED, Const.PREF_NMB_LIST_NAME);
        hkt48intent = new Intent(this, ProfileListActivity.class);
        hkt48intent.putExtra(INTENT_GROUP_SELECTED, Const.PREF_HKT_LIST_NAME);
        
        
        TabHost localTabHost = getTabHost();
        
        // Display AKB48 All Member
        localTabHost.addTab(localTabHost.newTabSpec(getString(R.string.akb48))
                .setIndicator(getString(R.string.akb48))
                .setContent(akb48intent));

        // Display SKE48 All Member
        localTabHost.addTab(localTabHost.newTabSpec(getString(R.string.ske48))
                .setIndicator(getString(R.string.ske48))
                .setContent(ske48intent));
        // Display NMB48 All Member
        localTabHost.addTab(localTabHost.newTabSpec(getString(R.string.nmb48))
                .setIndicator(getString(R.string.nmb48))
                .setContent(nmb48intent));
        // Display HKT48 All Member
        localTabHost.addTab(localTabHost.newTabSpec(getString(R.string.hkt48))
                .setIndicator(getString(R.string.hkt48))
                .setContent(hkt48intent));
    }
    
    /**
     * 
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            TabHost localTabHost = getTabHost();
            switch (buttonView.getId()) {
            case R.id.radio_akb48:
                localTabHost.setCurrentTabByTag(getString(R.string.akb48));
                break;
            case R.id.radio_ske48:
                localTabHost.setCurrentTabByTag(getString(R.string.ske48));
                break;
            case R.id.radio_nmb48:
                localTabHost.setCurrentTabByTag(getString(R.string.nmb48));
                break;
            case R.id.radio_hkt48:
                localTabHost.setCurrentTabByTag(getString(R.string.hkt48));
                break;
            }
        }
    }
    
    /**
     * Set RadioButton's onCheckedChanged event
     */
    private void setRadioOnCheckedChangedEvent() {
        ((RadioButton) findViewById(R.id.radio_akb48)).setOnCheckedChangeListener(this);
        ((RadioButton) findViewById(R.id.radio_ske48)).setOnCheckedChangeListener(this);
        ((RadioButton) findViewById(R.id.radio_nmb48)).setOnCheckedChangeListener(this);
        ((RadioButton) findViewById(R.id.radio_hkt48)).setOnCheckedChangeListener(this);
    }
}