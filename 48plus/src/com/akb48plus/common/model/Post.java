/**
 * 
 */
package com.akb48plus.common.model;

/**
 * @author QuSheng
 *
 */
public class Post extends PlusModel {

    public Post() {
        super();
    }

    /* (non-Javadoc)
     * @see com.akb48plus.common.model.PlusModel#setupColumns()
     */
    @Override
    protected void setupColumns() {
        columns = new String[] {
                "people_id",
                "displayName",
                "profileUrl",
                "updated",
                "verb",
                "object_content",
                "attachments_type",
                "attachments_displayName",
                "attachments_url",
                "shared_displayName",
                "shared_profileUrl",
                "shared_content",
                "shared_attach_url"
            };
    }

}
