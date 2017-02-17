package org.zalando.zmon.persistance;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.zalando.zmon.domain.OnetimeTokenInfo;
import org.zalando.zmon.persistence.OnetimeTokensSProcService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by jmussler on 17.02.17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class OnetimeTokenSprocsIT {

    @Autowired
    OnetimeTokensSProcService onetimeSprocs;

    @Test
    public void createAndFindTests() {

        List<OnetimeTokenInfo> tokens = onetimeSprocs.bindOnetimeToken("", "192.168.0.2", "RandomSessionId");
        assertThat(tokens.size()).isEqualTo(0);

        onetimeSprocs.createOnetimeToken("user_name","192.168.0.1","MyRandomToken", 10);
        onetimeSprocs.createOnetimeToken("user_name","192.168.0.1","MyRandomTokenNumber2", 10);

        // no matching tokens
        tokens = onetimeSprocs.bindOnetimeToken("", "192.168.0.2", "RandomSessionId");
        assertThat(tokens.size()).isEqualTo(0);
        tokens = onetimeSprocs.bindOnetimeToken("SomeOtherToken", "192.168.0.2", "RandomSessionId");
        assertThat(tokens.size()).isEqualTo(0);

        // bind token
        tokens = onetimeSprocs.bindOnetimeToken("MyRandomToken", "192.168.0.2", "RandomSessionId");
        assertThat(tokens.size()).isEqualTo(1);

        // token already bound with other id
        tokens = onetimeSprocs.bindOnetimeToken("MyRandomToken", "192.168.0.2", "RandomSessionId-Changed");
        assertThat(tokens.size()).isEqualTo(0);

        // token already bound with other IP
        tokens = onetimeSprocs.bindOnetimeToken("MyRandomToken", "192.168.0.3", "RandomSessionId");
        assertThat(tokens.size()).isEqualTo(0);

        // reload bound token
        tokens = onetimeSprocs.bindOnetimeToken("MyRandomToken", "192.168.0.2", "RandomSessionId");
        assertThat(tokens.size()).isEqualTo(1);
    }
}
