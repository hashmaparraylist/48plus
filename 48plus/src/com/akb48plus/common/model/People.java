/**
 * 
 */
package com.akb48plus.common.model;

/**
 * @author QuSheng
 *
 */
public class People extends PlusModel implements Model {
    
    public People() {
        super();
    }
    
    public String getDisplayName() {
        return this.item.get(columns[0]);
    }
    public void setDisplayName(String displayName) {
        this.item.put(columns[0], displayName);
    }
    public String getProfileUrl() {
        return this.item.get(columns[1]);
    }
    public void setProfileUrl(String profileUrl) {
        this.item.put(columns[1], profileUrl);
    }

    @Override
    protected void setupColumns() {
        columns = new String[]{"displayName", "profileUrl"};
    }
}
