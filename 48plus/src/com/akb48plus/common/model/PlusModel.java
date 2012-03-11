/**
 * 
 */
package com.akb48plus.common.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author QuSheng
 *
 */
public abstract class PlusModel implements Model {
    
    private String id = "";
    
    protected String columns[];
    protected HashMap<String, String> item = new HashMap<String, String>();
    
    public PlusModel() {
        setupColumns();
        for (int i = 0; i < columns.length; i++) {
            item.put(columns[i], "");
        }
    }

    /* (non-Javadoc)
     * @see com.akb48plus.common.model.Model#getId()
     */
    @Override
    public String getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see com.akb48plus.common.model.Model#setId(java.lang.String)
     */
    @Override
    public void setId(String id) {
        this.id = id;
    }
    
    public Map<String, String> getItem() {
        return item;
    }
    
    public void set(String key, String value) {
        this.item.put(key, value);
    }
    
    protected abstract void setupColumns();
}
