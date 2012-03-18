/**
 * 
 */
package com.akb48plus.common.cache;

import java.lang.reflect.InvocationTargetException;

import android.content.Context;

import com.akb48plus.common.model.Comment;
import com.akb48plus.common.model.Model;

/**
 * @author sebastianqu
 *
 */
public class CommentWrapper extends ModelWrapper {

    public CommentWrapper(Context ctx) throws IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
        super(ctx);
    }

    @Override
    public String getTableName() {
        return "comment_cache";
    }

    @Override
    protected Model getModelInstance() {
        return new Comment();
    }

    @Override
    protected String getAdditionalKey() {
        return "commentId";
    }
    
    public Model parse(com.google.api.services.plus.model.Comment comment) {
        Comment model = (Comment)getModelInstance();
        model.setCommentId(comment.getId());
        model.setDisplayName(comment.getActor().getDisplayName());
        model.setPeopleId(comment.getActor().getId());
        model.setContent(comment.getPlusObject().getContent());
        return model;
    }

}
