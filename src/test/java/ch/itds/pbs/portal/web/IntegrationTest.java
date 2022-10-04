package ch.itds.pbs.portal.web;


import ch.itds.pbs.portal.domain.Role;
import ch.itds.pbs.portal.domain.User;
import ch.itds.pbs.portal.repo.RoleRepository;
import ch.itds.pbs.portal.repo.UserRepository;
import ch.itds.pbs.portal.web.util.MockUserFilter;
import ch.itds.pbs.portal.web.util.ScreenshotOnFailureExtension;
import ch.itds.pbs.portal.web.util.SeleniumHelper;
import ch.itds.pbs.portal.web.util.SetScreenshotDataExtension;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.HashSet;

@ExtendWith({SeleniumJupiter.class, ScreenshotOnFailureExtension.class, SetScreenshotDataExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class IntegrationTest {

    Logger log = (Logger) LoggerFactory.getLogger(IntegrationTest.class);

    SeleniumHelper seleniumHelper;
    @Autowired
    MockUserFilter mockUserFilter;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;

    @LocalServerPort
    private int port;

    @BeforeAll
    public void start() {

        log.setLevel(Level.INFO);

        User testUser = userRepository.findByUsernameWithRoles("3113").orElseGet(() -> {
            User u = new User();
            u.setRoles(new HashSet<>());
            u = userRepository.save(u);
            return u;
        });
        testUser.setUsername("3113");
        testUser.setMidataUserId(3113L);
        testUser.setEnabled(true);
        testUser.setAccountExpired(false);
        testUser.setPasswordExpired(false);
        testUser.setAccountLocked(false);
        testUser = userRepository.save(testUser);

        boolean isAdmin = false;
        for (Role r : testUser.getRoles()) {
            if (r.getName().equals("ADMIN")) {
                isAdmin = true;
            }
        }
        if (!isAdmin) {
            Role admin = roleRepository.findByName("ADMIN").orElseGet(() -> {
                Role r = new Role();
                r.setName("ADMIN");
                r = roleRepository.save(r);
                return r;
            });
            testUser.getRoles().add(admin);
            testUser = userRepository.save(testUser);
        }
        mockUserFilter.authenticateNextRequestAs("3113");

        seleniumHelper = new SeleniumHelper(port);

        seleniumHelper.getDriver().get(seleniumHelper.getBaseUrl() + "/favicon.ico"); // to enable session creation with auth
    }


    @AfterAll
    public void tearDown() {
        if (seleniumHelper != null) {
            seleniumHelper.close();
        }
    }

}
