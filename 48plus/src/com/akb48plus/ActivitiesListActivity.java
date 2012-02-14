/**
 * 
 */
package com.akb48plus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


/**
 * @author Thinkpad
 *
 */
public class ActivitiesListActivity extends android.app.Activity {
    private static String TAG = ActivitiesListActivity.class.getName();
    
    public void onCreate(Bundle savedState) {
        Log.d(TAG,"onCreate()");
        super.onCreate(savedState);

        Intent intent = getIntent();
        
        if (intent == null || !intent.hasExtra(ProfileListActivity.INTENT_MEMBER_SELECTED)) {
            return;
        }

        String memberId = intent.getExtras().getString(ProfileListActivity.INTENT_MEMBER_SELECTED);

        setContentView(R.layout.activities_list);
    }

}
