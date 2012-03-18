/**
 * 
 */
package com.akb48plus.common.model;

import java.util.Map;

/**
 * @author QuSheng
 *
 */
public interface Model {
    public String getId();
    public void setId(String id);
    public Map<String, String> getItem();

    public String get(String key);
    public void set(String key, String value);
}
