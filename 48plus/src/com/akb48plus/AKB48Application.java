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
        String strAkbMemberList = preferences.getString(Const.PREF_AKB_LIST_NAME, "");
        String strSkeMemberList = preferences.getString(Const.PREF_SKE_LIST_NAME, "");
        String strNmbMemberList = preferences.getString(Const.PREF_NMB_LIST_NAME, "");
        String strHktMemberList = preferences.getString(Const.PREF_HKT_LIST_NAME, "");
        
        // init AKB48 Member List
        if (null == strAkbMemberList || "".equals(strAkbMemberList)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(Const.PREF_AKB_LIST_NAME, new Gson().toJson(Const.DEFAULT_AKB_MEMBER_LIST));
            editor.commit();
            strAkbMemberList = preferences.getString(Const.PREF_AKB_LIST_NAME, "");
        }

        // init SKE48 Member List
        if (null == strSkeMemberList || "".equals(strSkeMemberList)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(Const.PREF_SKE_LIST_NAME, new Gson().toJson(Const.DEFAULT_SKE_MEMBER_LIST));
            editor.commit();
            strSkeMemberList = preferences.getString(Const.PREF_SKE_LIST_NAME, "");
        }
        
        // init NMB48 Member List
        if (null == strNmbMemberList || "".equals(strNmbMemberList)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(Const.PREF_NMB_LIST_NAME, new Gson().toJson(Const.DEFAULT_NMB_MEMBER_LIST));
            editor.commit();
            strNmbMemberList = preferences.getString(Const.PREF_NMB_LIST_NAME, "");
        }

        // init HKT48 Member List
        if (null == strHktMemberList || "".equals(strHktMemberList)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(Const.PREF_HKT_LIST_NAME, new Gson().toJson(Const.DEFAULT_HKT_MEMBER_LIST));
            editor.commit();
            strHktMemberList = preferences.getString(Const.PREF_HKT_LIST_NAME, "");
        }
    }
}
