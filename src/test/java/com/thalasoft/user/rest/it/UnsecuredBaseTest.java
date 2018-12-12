package com.thalasoft.user.rest.it;

import com.thalasoft.user.rest.config.TestConfiguration;
import com.thalasoft.user.rest.config.WebConfiguration;
import com.thalasoft.user.rest.security.NoSecurityConfiguration;

import org.junit.Before;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = { NoSecurityConfiguration.class, TestConfiguration.class, WebConfiguration.class })
public abstract class UnsecuredBaseTest extends BaseTest {

  @Before
  public void setup() throws Exception {
    super.setup();
  }

}
