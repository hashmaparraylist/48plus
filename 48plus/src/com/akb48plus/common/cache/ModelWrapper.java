/**
 * 
 */
package com.akb48plus.common.cache;


import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.akb48plus.common.model.Model;

/**
 * @author Thinkpad
 *
 */
public abstract class ModelWrapper {
    
    private static String TAG = ModelWrapper.class.getName();
    public static String DB_NAME = "48plus";
    protected SQLiteDatabase dbHelper = null;
    
    public ModelWrapper(Context ctx) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        dbHelper = ctx.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        
        Cursor cursor = dbHelper.rawQuery(
                "select * from sqlite_master where type='table'"
                        + "and tbl_name = '" + getTableName() + "'", null);
        
        if (cursor.getCount() < 1) {
            Log.d(TAG, "Stand By Table");
            createTable();
        }
    }
    
    public Model get(String id) {
        Model model = null;
        Cursor cursor = dbHelper.rawQuery(
                "select * from "+ getTableName()+ " where id = '" + id + "'", null);
        
        while (cursor.moveToNext()) {
            Map<String, String> bean = new HashMap<String, String>();
            String colName[] = cursor.getColumnNames();
            for (String col : colName) {
                String value = cursor.getString(cursor.getColumnIndex(col));
                bean.put(col, value);
            }
            model = parseModel(bean);
        }
        return model;
    }
    
    public List<Model> list() {
        List<Model> list = new ArrayList<Model>();
        Cursor cursor = dbHelper.rawQuery("select * from " + getTableName(), null);
        while (cursor.moveToNext()) {
            Map<String, String> bean = new HashMap<String, String>();
            String colName[] = cursor.getColumnNames();
            for (String col : colName) {
                String value = cursor.getString(cursor.getColumnIndex(col));
                bean.put(col, value);
            }
            Model model = parseModel(bean);
            list.add(model);
        }
        return list;
    }
    
    public void add(Model model) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        StringBuffer sb = new StringBuffer();
        StringBuffer sbKey = new StringBuffer();
        StringBuffer sbValue = new StringBuffer();

        Map<String, String> bean = model.getItem();
        sbValue.append(" '");
        sbValue.append(model.getId());
        sbValue.append("' ");
        sbKey.append(" id ");
        for(Object key : bean.keySet()) {
            sbKey.append(" ,");
            sbKey.append(key);
            
            sbValue.append(" ,'");
            sbValue.append(bean.get(key));
            sbValue.append("' ");
        }
        sb.append(" Insert into ");
        sb.append(getTableName());
        sb.append(" ( ");
        sb.append(sbKey);
        sb.append(" ) values (");
        sb.append(sbValue);
        sb.append(" )");
        Log.d(TAG, sb.toString());
        dbHelper.execSQL(sb.toString());
    }
    
    public void update(Model model) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        StringBuffer sb = new StringBuffer();
        sb.append("Update ");
        sb.append(getTableName());
        sb.append(" set  ");
        Map<String, String> bean = model.getItem();
        for(Object key : bean.keySet()) {
            sb.append(key);
            sb.append("='");
            sb.append(bean.get(key));
            sb.append("', ");
        }
        sb.append(" id='");
        sb.append(model.getId());
        sb.append("' ");
        sb.append("where id = '");
        sb.append(model.getId());
        sb.append("'");
        Log.d(TAG, sb.toString());
        dbHelper.execSQL(sb.toString());    
    }
    
    public boolean containsKey(String id) {
        Cursor cursor = dbHelper.rawQuery(
                "select * from "+ getTableName()+ " where id = '" + id + "'", null);
        
        if (cursor.getCount() < 1) {
            return false;
        }
        return true;
    }
    
    public boolean exist(Model model) {
        return containsKey(model.getId());
    }
    
    private Model parseModel(Map<String, String> bean) {
        Model model = getModelInstance();
        model.setId(bean.get("id"));
        for(String key : bean.keySet()) {
            model.set(key, bean.get(key));
        }
        
        return model;
    }
    
    private void createTable() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Model model = getModelInstance();
        StringBuffer sb = new StringBuffer();
        sb.append("CREATE TABLE ");
        sb.append(getTableName());
        sb.append(" ( ");
        sb.append("id text, ");
        Map<String, String> bean = model.getItem();
        for (String key : bean.keySet()) {
            sb.append(key);
            sb.append(" text, ");
        }
        sb.append("primary key(id asc))");
        Log.d(TAG, sb.toString());
        dbHelper.execSQL(sb.toString());
    }
    
    public abstract String getTableName();
    protected abstract Model getModelInstance();
    
}
