package org.zalando.zmon.security.permission;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zalando.zmon.domain.AlertDefinition;
import org.zalando.zmon.domain.CheckDefinition;
import org.zalando.zmon.domain.DefinitionStatus;
import org.zalando.zmon.exception.ZMonAuthorizationException;
import org.zalando.zmon.persistence.*;
import org.zalando.zmon.security.authority.*;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultZMonPermissionServiceTest {
    @Mock
    private AlertDefinitionSProcService alertDefinitionSProc;

    @Mock
    private CheckDefinitionSProcService checkDefinitionSProc;

    @Mock
    private DashboardSProcService dashboardSProc;

    @InjectMocks
    private DefaultZMonPermissionService service;

    @Test(expected = ZMonAuthorizationException.class)
    public void testVerifyDeleteUnusedCheckDefinitionPermission_DefinitionNotFound() {
        when(checkDefinitionSProc.getCheckDefinitions(any(), any())).thenReturn(null);
        service.verifyDeleteUnusedCheckDefinitionPermission(100500);
    }

    @Test(expected = ZMonAuthorizationException.class)
    public void testVerifyDeleteUnusedCheckDefinitionPermission_MoreThanOneDefinitionFound() {
        when(checkDefinitionSProc.getCheckDefinitions(any(), any()))
                .thenReturn(Arrays.asList(new CheckDefinition(), new CheckDefinition()));
        service.verifyDeleteUnusedCheckDefinitionPermission(100500);
    }

    @Test(expected = ZMonAuthorizationException.class)
    public void testVerifyDeleteUnusedCheckDefinitionPermission_DeletedDefinitionFound() {
        final CheckDefinition def = new CheckDefinition();
        def.setStatus(DefinitionStatus.DELETED);

        when(checkDefinitionSProc.getCheckDefinitions(any(), any())).thenReturn(Arrays.asList(def));
        service.verifyDeleteUnusedCheckDefinitionPermission(100500);
    }

    @Test(expected = ZMonAuthorizationException.class)
    public void testVerifyDeleteUnusedCheckDefinitionPermission_UserHasNoAccess() {
        final CheckDefinition def = new CheckDefinition();
        def.setStatus(DefinitionStatus.ACTIVE);

        Authentication auth = mock(Authentication.class);
        // Access is never granted to ViewerAuthority
        ZMonAuthority authority = new ZMonViewerAuthority("foo", ImmutableSet.of("bar"));
        doReturn(Collections.singletonList(authority))
                .when(auth).getAuthorities();

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        when(checkDefinitionSProc.getCheckDefinitions(any(), any())).thenReturn(Arrays.asList(def));
        service.verifyDeleteUnusedCheckDefinitionPermission(100500);
    }

    @Test
    public void testVerifyDeleteUnusedCheckDefinitionPermission_AccessGranted() {
        final CheckDefinition def = new CheckDefinition();
        def.setStatus(DefinitionStatus.ACTIVE);

        Authentication auth = mock(Authentication.class);
        // Access is always granted to AdminAuthority
        ZMonAuthority authority = new ZMonAdminAuthority("foo", ImmutableSet.of("bar"));
        doReturn(Collections.singletonList(authority))
                .when(auth).getAuthorities();

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        when(checkDefinitionSProc.getCheckDefinitions(any(), any())).thenReturn(Arrays.asList(def));
        service.verifyDeleteUnusedCheckDefinitionPermission(100500);
    }

    @Test
    public void testVerifyEditAlertDefinitionPermission_AddAlert() {
        Authentication auth = mock(Authentication.class);

        ZMonAuthority authority = new ZMonUserAuthority("foo", ImmutableSet.of("bar"));
        doReturn(Collections.singletonList(authority)).when(auth).getAuthorities();

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        final AlertDefinition newAlertDefinition = new AlertDefinition();
        newAlertDefinition.setTeam("bar");
        service.verifyEditAlertDefinitionPermission(newAlertDefinition);
    }

    @Test
    public void testVerifyEditAlertDefinitionPermission_EditAlert() {
        Authentication auth = mock(Authentication.class);

        ZMonAuthority authority = new ZMonUserAuthority("foo", ImmutableSet.of("bar"));
        doReturn(Collections.singletonList(authority)).when(auth).getAuthorities();

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);


        final AlertDefinition alertDefinition = new AlertDefinition();
        alertDefinition.setId(1);
        alertDefinition.setTeam("bar");

        doReturn(Collections.singletonList(alertDefinition))
                .when(alertDefinitionSProc).getAlertDefinitions(null, Collections.singletonList(1));
        service.verifyEditAlertDefinitionPermission(alertDefinition);
    }

    @Test(expected = ZMonAuthorizationException.class)
    public void testVerifyEditAlertDefinitionPermission_NotAllowed() {
        Authentication auth = mock(Authentication.class);

        ZMonAuthority authority = new ZMonUserAuthority("foo", ImmutableSet.of("bar"));
        doReturn(Collections.singletonList(authority)).when(auth).getAuthorities();

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        final AlertDefinition newAlertDefinition = new AlertDefinition();
        newAlertDefinition.setTeam("buz");
        service.verifyEditAlertDefinitionPermission(newAlertDefinition);
    }
}


