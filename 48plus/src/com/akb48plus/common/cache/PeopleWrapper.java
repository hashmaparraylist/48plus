/**
 * 
 */
package com.akb48plus.common.cache;

import java.lang.reflect.InvocationTargetException;

import android.content.Context;

import com.akb48plus.common.model.Model;
import com.akb48plus.common.model.People;

/**
 * 
 * @author QuSheng
 */
public class PeopleWrapper extends ModelWrapper {
    
    private static String TABLE_NAME = "people_cache";
    
    /**
     * 
     * @param ctx
     * @throws NoSuchMethodException 
     * @throws InvocationTargetException 
     * @throws IllegalAccessException 
     */
    public PeopleWrapper(Context ctx) throws 
            IllegalAccessException,
            InvocationTargetException,
            NoSuchMethodException {
        super(ctx);
    }
    
    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected Model getModelInstance() {
        return new People();
    }

    @Override
    protected String getAdditionalKey() {
        return "";
    }
}
