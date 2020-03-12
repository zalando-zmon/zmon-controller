package org.zalando.zmon.domain;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.zalando.zmon.domain.AlertCommentRecord;

import com.google.common.base.Objects;

public class AlertCommentIsEqual extends BaseMatcher<AlertCommentRecord> {

    private final AlertCommentRecord alertComment;

    public AlertCommentIsEqual(final AlertCommentRecord alertComment) {
        this.alertComment = alertComment;
    }

    @Override
    public boolean matches(final Object item) {
        final AlertCommentRecord other = (AlertCommentRecord) item;

        return Objects.equal(alertComment.getId(), other.getId())
                && Objects.equal(alertComment.getCreated(), other.getCreated())
                && Objects.equal(alertComment.getCreatedBy(), other.getCreatedBy())
                && Objects.equal(alertComment.getLastModified(), other.getLastModified())
                && Objects.equal(alertComment.getLastModifiedBy(), other.getLastModifiedBy())
                && Objects.equal(alertComment.getComment(), other.getComment())
                && Objects.equal(alertComment.getAlertDefinitionId(), other.getAlertDefinitionId())
                && Objects.equal(alertComment.getEntityId(), other.getEntityId());

    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("{Id is ").appendValue(alertComment.getId()).appendText(", name is ")
                   .appendValue(alertComment.getCreated()).appendText(", created at ")
                   .appendValue(alertComment.getCreatedBy()).appendText(", created by ")
                   .appendValue(alertComment.getLastModified()).appendText(", modified at ")
                   .appendValue(alertComment.getLastModifiedBy()).appendText(", modified by ")
                   .appendValue(alertComment.getComment()).appendText(", comment is ")
                   .appendValue(alertComment.getAlertDefinitionId()).appendText(", alert definition id is ")
                   .appendValue(alertComment.getEntityId()).appendText(", entity id is ");
    }

    // factory methods for fluent language
    public static Matcher<? super AlertCommentRecord> equalTo(final AlertCommentRecord alertComment) {
        return new AlertCommentIsEqual(alertComment);
    }

}
