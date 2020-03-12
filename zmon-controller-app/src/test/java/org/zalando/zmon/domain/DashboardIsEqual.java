package org.zalando.zmon.domain;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.zalando.zmon.domain.DashboardRecord;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class DashboardIsEqual extends BaseMatcher<DashboardRecord> {

    private final DashboardRecord dashboard;

    public DashboardIsEqual(final DashboardRecord dashboard) {
        this.dashboard = dashboard;
    }

    public boolean matches(final Object o) {
        final DashboardRecord other = (DashboardRecord) o;

        return Objects.equal(dashboard.getId(), other.getId()) && Objects.equal(dashboard.getName(), other.getName())
                && Objects.equal(dashboard.getCreatedBy(), other.getCreatedBy())
                && Objects.equal(dashboard.getLastModified(), other.getLastModified())
                && Objects.equal(dashboard.getLastModifiedBy(), other.getLastModifiedBy())
                && Objects.equal(dashboard.getWidgetConfiguration(), other.getWidgetConfiguration())
                && Objects.equal(dashboard.getAlertTeams(), other.getAlertTeams())
                && Objects.equal(dashboard.getViewMode(), other.getViewMode())
                && Objects.equal(dashboard.getTags(), other.getTags());

    }

    public void describeTo(final Description desc) {
        desc.appendText("{Id is ").appendValue(dashboard.getId()).appendText(", name is ")
            .appendValue(dashboard.getName()).appendText(", created by ").appendValue(dashboard.getCreatedBy())
            .appendText(", last modified by ").appendValue(dashboard.getLastModified())
            .appendText(", last modified at ").appendValue(dashboard.getLastModifiedBy())
            .appendText(", widget configuration is ").appendValue(dashboard.getWidgetConfiguration())
            .appendText(", alert teams are ").appendValue(dashboard.getAlertTeams()).appendText(", view mode is ")
            .appendValue(dashboard.getViewMode()).appendText(", tags are ").appendValue(dashboard.getTags()).appendText(
                "}");
    }

    // factory methods for fluent language
    public static Matcher<? super DashboardRecord> equalTo(final DashboardRecord dashboard) {
        Preconditions.checkNotNull(dashboard, "dashboard");
        return new DashboardIsEqual(dashboard);
    }

    public static Collection<Matcher<? super DashboardRecord>> equalTo(final Iterable<DashboardRecord> dashboards) {
        Preconditions.checkNotNull(dashboards, "dashboard");

        final List<Matcher<? super DashboardRecord>> matchers = new LinkedList<>();
        for (final DashboardRecord dashboard : dashboards) {
            matchers.add(new DashboardIsEqual(dashboard));
        }

        return matchers;
    }
}
