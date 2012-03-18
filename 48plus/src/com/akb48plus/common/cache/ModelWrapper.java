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
 * @author sebastianqu
 */
public abstract class ModelWrapper {
    
    private static String TAG = ModelWrapper.class.getName();
    /**
     * Local SQLite DataBase Name
     */
    public static String DB_NAME = "48plus";
    /**
     * DataBase Helper
     */
    protected SQLiteDatabase dbHelper = null;
    /**
     * Android Context
     */
    protected Context context;
    
    public ModelWrapper(Context ctx) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        this.context = ctx;
        dbHelper = this.context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        
        Cursor cursor = dbHelper.rawQuery(
                "select * from sqlite_master where type='table'"
                        + "and tbl_name = '" + getTableName() + "'", null);
        
        if (cursor.getCount() < 1) {
            Log.d(TAG, "Stand By Table");
            createTable();
        }
        cursor.close();
        dbHelper.close();
    }
    
    /**
     * 通过ID获得获得G+对象
     * @param id 对象ID
     * @return 对象链表
     */
    public List<Model> get(String id) {
        Model model = null;
        dbHelper = this.context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        Cursor cursor = dbHelper.rawQuery(
                "select * from "+ getTableName()+ " where id = '" + id + "'", null);
        List<Model> list = new ArrayList<Model>();
        while (cursor.moveToNext()) {
            Map<String, String> bean = new HashMap<String, String>();
            String colName[] = cursor.getColumnNames();
            for (String col : colName) {
                String value = cursor.getString(cursor.getColumnIndex(col));
                bean.put(col, value);
            }
            model = parseModel(bean);
            list.add(model);
        }
        cursor.close();
        dbHelper.close();
        return list;
    }
    
    /**
     * 通过指定条件获取G+对象
     * @param condition 条件
     * @return G+对象列表
     */
    public List<Model> getByCondition(String condition) {
        List<Model> list = new ArrayList<Model>();
        String sql = "select * from " + getTableName();
        if ((null != condition) && !"".equals(condition))
            sql += " where " + condition;
        dbHelper = this.context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        Cursor cursor = dbHelper.rawQuery(sql, null);
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
        cursor.close();
        dbHelper.close();
        return list;
    }
    
    /**
     * 通过附加Key来获取G+对象
     * @param model 存放附加的Key的G+对象接口
     * @return G+对象接口
     */
    public List<Model> getByKey(Model model) {
        String keys[] = getAdditionalKey().split(",");
        
        StringBuffer sb = new StringBuffer();
        if (!"".equals(model.getId())) {
            sb.append("id = '");
            sb.append(model.getId());
            sb.append("' ");
        } else {
            sb.append("1=1");
        }
       
        for (String key : keys) {
            String value = model.getItem().get(key);
            sb.append(" and ");
            sb.append(key);
            sb.append(" ='");
            sb.append(value);
            sb.append("'");
        }
        
        return getByCondition(sb.toString());
    }
    
    /** 
     * 获取G+对象
     * @return G+对象列表
     */
    public List<Model> list() {
        return getByCondition(null);
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
        dbHelper = this.context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        dbHelper.execSQL(sb.toString());
        dbHelper.close();
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
        dbHelper = this.context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        dbHelper.execSQL(sb.toString());
        dbHelper.close();
    }
    
    public boolean containsKey(String id) {
        boolean result = true;
        dbHelper = this.context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        Cursor cursor = dbHelper.rawQuery(
                "select * from "+ getTableName()+ " where id = '" + id + "'", null);
        
        
        if (cursor.getCount() < 1) {
            result = false;
        }
        cursor.close();
        dbHelper.close();
        return result;
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
        sb.append("primary key(id");
        if (!"".equals(getAdditionalKey())) {
            sb.append(",");
            sb.append(getAdditionalKey());
        }
        sb.append(" asc))");
        Log.d(TAG, sb.toString());
        dbHelper = this.context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        dbHelper.execSQL(sb.toString());
        dbHelper.close();
    }
    
    public abstract String getTableName();
    protected abstract Model getModelInstance();
    protected abstract String getAdditionalKey();
}
