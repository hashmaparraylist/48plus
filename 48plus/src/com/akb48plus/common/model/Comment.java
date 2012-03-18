/**
 * 
 */
package com.akb48plus.common.model;

/**
 * 
 * @author sebastianqu
 *
 */
public class Comment extends PlusModel {

    public Comment() {
        super();
    }

    public String getCommentId() {
        return this.item.get("commentId");
    }
    public String getPeopleId() {
        return this.item.get("peopleId");
    }
    public String getDisplayName() {
        return this.item.get("displayName");
    }
    public String getContent() {
        return this.item.get("content");
    }

    public void setCommentId(String commentId) {
        this.item.put("commentId", commentId);
    }
    public void setPeopleId(String peopleId) {
        this.item.put("peopleId", peopleId);
    }
    public void setDisplayName(String displayName) {
        this.item.put("displayName", displayName);
    }
    public void setContent(String content) {
        this.item.put("content", content);
    }

    @Override
    protected void setupColumns() {
        columns = new String [] {
            "commentId",
            "peopleId",
            "displayName",
            "content"
        };
    }
}
