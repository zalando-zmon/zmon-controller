package org.zalando.zmon.security;

import org.junit.Assert;
import org.junit.Test;
import org.zalando.zmon.domain.AlertDefinition;
import org.zalando.zmon.domain.DefinitionStatus;
import org.zalando.zmon.security.authority.ZMonLeadAuthority;

import com.google.common.collect.ImmutableSet;

public class ZMonLeadAuthorityTest {

    @Test
    public void testEditAlertDefinitionWithSameTeam() {
        final ZMonLeadAuthority authority = new ZMonLeadAuthority("foobar", ImmutableSet.of("Platform/Software"));

        final AlertDefinition toEdit = new AlertDefinition();
        toEdit.setTeam("PLATFORM/SOFTWARE");
        toEdit.setResponsibleTeam("Platform/System");
        toEdit.setStatus(DefinitionStatus.ACTIVE);

        Assert.assertTrue(authority.hasEditAlertDefinitionPermission(toEdit));
    }

    @Test
    public void testEditInactiveAlertDefinitionWithSameResponsibleTeam() {
        final ZMonLeadAuthority authority = new ZMonLeadAuthority("foobar", ImmutableSet.of("Platform/System"));

        final AlertDefinition toEdit = new AlertDefinition();
        toEdit.setTeam("PLATFORM/SOFTWARE");
        toEdit.setResponsibleTeam("Platform/System");
        toEdit.setStatus(DefinitionStatus.INACTIVE);

        Assert.assertTrue(authority.hasEditAlertDefinitionPermission(toEdit));
    }

    @Test
    public void testEditActiveAlertDefinitionWithSameResponsibleTeam() {
        final ZMonLeadAuthority authority = new ZMonLeadAuthority("foobar", ImmutableSet.of("Platform/System"));

        final AlertDefinition toEdit = new AlertDefinition();
        toEdit.setTeam("PLATFORM/SOFTWARE");
        toEdit.setResponsibleTeam("Platform/System");
        toEdit.setStatus(DefinitionStatus.ACTIVE);

        Assert.assertTrue(authority.hasEditAlertDefinitionPermission(toEdit));
    }

    @Test
    public void testEditAlertDefinitionDifferentTeam() {
        final ZMonLeadAuthority authority = new ZMonLeadAuthority("foobar", ImmutableSet.of("Backend/Order"));

        final AlertDefinition toEdit = new AlertDefinition();
        toEdit.setTeam("Backend");
        toEdit.setResponsibleTeam("Backend/Article");
        toEdit.setStatus(DefinitionStatus.ACTIVE);

        Assert.assertFalse(authority.hasEditAlertDefinitionPermission(toEdit));
    }

    @Test
    public void testEditAlertDefinitionPrefix() {
        final ZMonLeadAuthority authority = new ZMonLeadAuthority("foobar", ImmutableSet.of("Backend"));

        final AlertDefinition toEdit = new AlertDefinition();
        toEdit.setTeam("Backend/Order");
        toEdit.setResponsibleTeam("Platform/Database");
        toEdit.setStatus(DefinitionStatus.ACTIVE);

        Assert.assertTrue(authority.hasEditAlertDefinitionPermission(toEdit));
    }

}
