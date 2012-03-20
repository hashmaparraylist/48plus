/**
 * 
 */
package com.akb48plus;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import com.akb48plus.common.Const;
import com.akb48plus.common.cache.PeopleWrapper;
import com.akb48plus.common.model.People;
import com.google.gson.Gson;

/**
 * @author QuSheng
 *
 */
public class AKB48Application extends Application {
    
    private final static String TAG = AKB48Application.class.getName();
    
    /**
     * 
     */
    @Override
    public void onCreate() {
        SharedPreferences preferences = getSharedPreferences(Const.PREF_AKB_LIST_NAME, MODE_PRIVATE);
        
        // init AKB48 Member List
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Const.PREF_AKB_LIST_NAME, new Gson().toJson(Const.DEFAULT_AKB_MEMBER_LIST[0]));
        // init SKE48 Member List
        editor.putString(Const.PREF_SKE_LIST_NAME, new Gson().toJson(Const.DEFAULT_SKE_MEMBER_LIST[0]));
        // init NMB48 Member List
        editor.putString(Const.PREF_NMB_LIST_NAME, new Gson().toJson(Const.DEFAULT_NMB_MEMBER_LIST[0]));
        // init HKT48 Member List
        editor.putString(Const.PREF_HKT_LIST_NAME, new Gson().toJson(Const.DEFAULT_HKT_MEMBER_LIST[0]));
        
        editor.commit();
        try {
            PeopleWrapper wrapper = new PeopleWrapper(getApplicationContext());
            for (int i = 0; i < Const.DEFAULT_AKB_MEMBER_LIST[0].length; i++) {
                String id = Const.DEFAULT_AKB_MEMBER_LIST[0][i];
                String displayName = Const.DEFAULT_AKB_MEMBER_LIST[1][i];
                
                if (wrapper.containsKey(id)) {
                    continue;
                }
                
                People people = new People();
                people.setId(id);
                people.setDisplayName(displayName);
                wrapper.add(people);
            }
            for (int i = 0; i < Const.DEFAULT_SKE_MEMBER_LIST[0].length; i++) {
                String id = Const.DEFAULT_SKE_MEMBER_LIST[0][i];
                String displayName = Const.DEFAULT_SKE_MEMBER_LIST[1][i];
                
                if (wrapper.containsKey(id)) {
                    continue;
                }
                
                People people = new People();
                people.setId(id);
                people.setDisplayName(displayName);
                wrapper.add(people);
            }
            for (int i = 0; i < Const.DEFAULT_NMB_MEMBER_LIST[0].length; i++) {
                String id = Const.DEFAULT_NMB_MEMBER_LIST[0][i];
                String displayName = Const.DEFAULT_NMB_MEMBER_LIST[1][i];
                
                if (wrapper.containsKey(id)) {
                    continue;
                }
                
                People people = new People();
                people.setId(id);
                people.setDisplayName(displayName);
                wrapper.add(people);
            }
            for (int i = 0; i < Const.DEFAULT_HKT_MEMBER_LIST[0].length; i++) {
                String id = Const.DEFAULT_HKT_MEMBER_LIST[0][i];
                String displayName = Const.DEFAULT_HKT_MEMBER_LIST[1][i];
                
                if (wrapper.containsKey(id)) {
                    continue;
                }
                
                People people = new People();
                people.setId(id);
                people.setDisplayName(displayName);
                wrapper.add(people);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
