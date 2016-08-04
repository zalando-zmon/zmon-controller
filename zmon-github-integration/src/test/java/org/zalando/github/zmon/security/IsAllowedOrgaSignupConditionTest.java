package org.zalando.github.zmon.security;

import java.net.URI;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.social.github.api.GitHub;
import org.springframework.social.github.api.UserOperations;
import org.springframework.web.client.RestOperations;

public class IsAllowedOrgaSignupConditionTest {

    private GithubSignupConditionProperties signupProperties;

    private GitHub github = Mockito.mock(GitHub.class);
    private UserOperations userOperations = Mockito.mock(UserOperations.class);
    private RestOperations restOperations = Mockito.mock(RestOperations.class);

    @Before
    public void setUp() {
        signupProperties = new GithubSignupConditionProperties();
        signupProperties.getAllowedOrgas().add("zalando");

        Mockito.reset(github, userOperations, restOperations);
        Mockito.when(github.userOperations()).thenReturn(userOperations);
        Mockito.when(userOperations.getProfileId()).thenReturn("pmueller");
        Mockito.when(github.restOperations()).thenReturn(restOperations);
    }

    @Test
    public void buildUri() {
        IsAllowedOrgaSignupCondition condition = new IsAllowedOrgaSignupCondition(signupProperties);
        URI uri = condition.buildUri("zalando");
        Assertions.assertThat(uri.toString()).isEqualTo("https://api.github.com/orgs/zalando/members?per_page=1");
    }

    @Test
    public void emptyOrgasShouldReturnFalse() {
        IsAllowedOrgaSignupCondition condition = new IsAllowedOrgaSignupCondition(
                new GithubSignupConditionProperties());
        boolean isMember = condition.isOrgaMember(github);
        Assertions.assertThat(isMember).isFalse();
    }

    
    @Test
    public void emptyOrgasShouldReturnFalseWhenNotInOrga() {
        ResponseEntity<Object> response = Mockito.mock(ResponseEntity.class);
        Mockito.when(restOperations.getForEntity(Mockito.any(), Mockito.any())).thenReturn(response);
        Mockito.when(response.getStatusCode()).thenReturn(HttpStatus.FOUND);

        IsAllowedOrgaSignupCondition condition = new IsAllowedOrgaSignupCondition(signupProperties);
        boolean isMember = condition.isOrgaMember(github);
        Assertions.assertThat(isMember).isFalse();
    }
    @Test
    public void emptyOrgasShouldReturnTrueWhenInOrga() {
        ResponseEntity<Object> response = Mockito.mock(ResponseEntity.class);
        Mockito.when(restOperations.getForEntity(Mockito.any(), Mockito.any())).thenReturn(response);
        Mockito.when(response.getStatusCode()).thenReturn(HttpStatus.OK);

        IsAllowedOrgaSignupCondition condition = new IsAllowedOrgaSignupCondition(signupProperties);
        boolean isMember = condition.isOrgaMember(github);
        Assertions.assertThat(isMember).isTrue();
    }

}
