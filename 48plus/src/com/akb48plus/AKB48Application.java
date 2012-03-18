/**
 * 
 */
package com.akb48plus;

import com.akb48plus.common.Const;
import com.google.gson.Gson;
import android.app.Application;
import android.content.SharedPreferences;

/**
 * @author QuSheng
 *
 */
public class AKB48Application extends Application {
    
    /**
     * 
     */
    @Override
    public void onCreate() {
        SharedPreferences preferences = getSharedPreferences(Const.PREF_AKB_LIST_NAME, MODE_PRIVATE);
        
        // init AKB48 Member List
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Const.PREF_AKB_LIST_NAME, new Gson().toJson(Const.DEFAULT_AKB_MEMBER_LIST));
        // init SKE48 Member List
        editor.putString(Const.PREF_SKE_LIST_NAME, new Gson().toJson(Const.DEFAULT_SKE_MEMBER_LIST));
        // init NMB48 Member List
        editor.putString(Const.PREF_NMB_LIST_NAME, new Gson().toJson(Const.DEFAULT_NMB_MEMBER_LIST));
        // init HKT48 Member List
        editor.putString(Const.PREF_HKT_LIST_NAME, new Gson().toJson(Const.DEFAULT_HKT_MEMBER_LIST));
        
        editor.commit();
    }
}
