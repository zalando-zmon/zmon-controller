package org.zalando.zmon.generator;

import java.util.Date;

import org.zalando.zmon.domain.AlertCommentImport;

public class AlertCommentGenerator implements DataGenerator<AlertCommentImport> {

    @Override
    public AlertCommentImport generate() {
        final Date now = new Date();
        final AlertCommentImport comment = new AlertCommentImport();
        comment.setCreated(now);
        comment.setCreatedBy("pribeiro");
        comment.setLastModified(now);
        comment.setLastModifiedBy("pribeiro");
        comment.setComment("comment");
        comment.setEntityId("myhost123");

        return comment;
    }
}
