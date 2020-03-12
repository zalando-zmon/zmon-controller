package org.zalando.zmon.generator;

import java.util.Date;

import org.zalando.zmon.domain.AlertCommentRecord;

public class AlertCommentGenerator implements DataGenerator<AlertCommentRecord> {

    @Override
    public AlertCommentRecord generate() {
        final Date now = new Date();
        final AlertCommentRecord comment = new AlertCommentRecord();
        comment.setCreated(now);
        comment.setCreatedBy("pribeiro");
        comment.setLastModified(now);
        comment.setLastModifiedBy("pribeiro");
        comment.setComment("comment");
        comment.setEntityId("myhost123");

        return comment;
    }
}
