package ch.itds.pbs.portal.web;


import ch.itds.pbs.portal.domain.*;
import ch.itds.pbs.portal.repo.*;
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
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;

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

    @Autowired
    MidataGroupRepository midataGroupRepository;

    @Autowired
    MidataPermissionRepository midataPermissionRepository;

    @Autowired
    MidataRoleRepository midataRoleRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    MasterTileRepository masterTileRepository;

    @Autowired
    GroupDefaultTileRepository groupDefaultTileRepository;

    @LocalServerPort
    private int port;

    WebDriverWait wait2s;

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
                break;
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

        MidataGroup pbsGroup = ensurePbsGroup();
        MidataGroup pkbGroup = ensurePkbGroup();

        if (testUser.getPrimaryMidataGroup() == null) {
            testUser.setPrimaryMidataGroup(pkbGroup);
            testUser = userRepository.save(testUser);
        }
        MidataRole pbsItSupport = midataRoleRepository.findByGroupAndName(pbsGroup, "IT Support");
        if (pbsItSupport == null) {
            pbsItSupport = new MidataRole();
            pbsItSupport.setName("IT Support");
            pbsItSupport.setClazz("Group::Bund::ItSupport");
            pbsItSupport.setGroup(pbsGroup);
            pbsItSupport = midataRoleRepository.save(pbsItSupport);
        }
        MidataPermission pbsItSupportAdmin = midataPermissionRepository.findByUserAndRoleAndPermission(testUser, pbsItSupport, "group_and_below_full");
        if (pbsItSupportAdmin == null) {
            pbsItSupportAdmin = new MidataPermission();
            pbsItSupportAdmin.setUser(testUser);
            pbsItSupportAdmin.setRole(pbsItSupport);
            pbsItSupportAdmin.setPermission("group_and_below_full");
            pbsItSupportAdmin = midataPermissionRepository.save(pbsItSupportAdmin);
        }


        MidataGroup bezOeGroup = ensureBezOeGroup();

        ensurePbsNonRestrictedMasterTile();
        ensurePkbNonRestrictedMasterTile();
        ensureBezOeGroupDefaultTile();

        testUser.setMidataGroupHierarchy(new Integer[]{pkbGroup.getMidataId(), pbsGroup.getMidataId()});
        userRepository.save(testUser);

        mockUserFilter.authenticateNextRequestAs("3113");

        seleniumHelper = new SeleniumHelper(port);

        seleniumHelper.getDriver().get(seleniumHelper.getBaseUrl() + "/favicon.ico"); // to enable session creation with auth

        wait2s = new WebDriverWait(seleniumHelper.getDriver(), Duration.ofSeconds(2));
    }


    @AfterAll
    public void tearDown() {
        if (seleniumHelper != null) {
            seleniumHelper.close();
        }
    }

    protected MasterTile ensurePbsNonRestrictedMasterTile() {
        List<MasterTile> masterTiles = masterTileRepository.findAllByMidataGroupOnly(ensurePbsGroup());
        MasterTile masterTile;
        if (masterTiles.isEmpty()) {
            Category category = ensureACategory();

            LocalizedString title = new LocalizedString();
            title.setDe("PBS Titel");
            LocalizedString content = new LocalizedString();
            content.setDe("PBS Content...");

            masterTile = new MasterTile();
            masterTile.setCategory(category);
            masterTile.setMidataGroupOnly(ensurePbsGroup());
            masterTile.setTitle(title);
            masterTile.setContent(content);
            masterTile.setRestricted(false);
            masterTile.setBackgroundColor(Color.DEFAULT);
            masterTile = masterTileRepository.saveAndFlush(masterTile);
        } else {
            masterTile = masterTiles.get(0);
        }
        return masterTile;
    }

    protected MasterTile ensurePkbNonRestrictedMasterTile() {
        List<MasterTile> masterTiles = masterTileRepository.findAllByMidataGroupOnly(ensurePkbGroup());
        MasterTile masterTile;
        if (masterTiles.isEmpty()) {
            Category category = ensureACategory();

            LocalizedString title = new LocalizedString();
            title.setDe("PKB Titel");
            LocalizedString content = new LocalizedString();
            content.setDe("PKB Content...");

            masterTile = new MasterTile();
            masterTile.setCategory(category);
            masterTile.setMidataGroupOnly(ensurePkbGroup());
            masterTile.setTitle(title);
            masterTile.setContent(content);
            masterTile.setRestricted(false);
            masterTile.setBackgroundColor(Color.DEFAULT);
            masterTile = masterTileRepository.saveAndFlush(masterTile);
        } else {
            masterTile = masterTiles.get(0);
        }
        return masterTile;
    }

    protected MasterTile ensureBezOeRestrictedMasterTile() {
        List<MasterTile> masterTiles = masterTileRepository.findAllByMidataGroupOnly(ensureBezOeGroup());
        MasterTile masterTile;
        if (masterTiles.isEmpty()) {
            Category category = ensureACategory();

            LocalizedString title = new LocalizedString();
            title.setDe("Bez OE Titel");
            LocalizedString content = new LocalizedString();
            content.setDe("Bez OE Content...");

            masterTile = new MasterTile();
            masterTile.setCategory(category);
            masterTile.setMidataGroupOnly(ensureBezOeGroup());
            masterTile.setTitle(title);
            masterTile.setContent(content);
            masterTile.setRestricted(true);
            masterTile.setBackgroundColor(Color.DEFAULT);
            masterTile = masterTileRepository.saveAndFlush(masterTile);
        } else {
            masterTile = masterTiles.get(0);
        }
        return masterTile;
    }

    protected Category ensureACategory() {
        Category category;
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            LocalizedString categoryName = new LocalizedString();
            categoryName.setDe("Kategorie");

            category = new Category();
            category.setName(categoryName);
            category = categoryRepository.save(category);
        } else {
            category = categories.get(0);
        }
        return category;
    }

    protected MidataGroup ensurePbsGroup() {
        MidataGroup midataGroup = midataGroupRepository.findByMidataId(1);
        if (midataGroup == null) {
            midataGroup = new MidataGroup();
            midataGroup.setName("Pfadibewegung Schweiz");
            midataGroup.setMidataId(1);
            midataGroup = midataGroupRepository.save(midataGroup);
        }
        return midataGroup;
    }

    protected MidataGroup ensurePkbGroup() {
        MidataGroup midataGroup = midataGroupRepository.findByMidataId(2);
        if (midataGroup == null) {
            midataGroup = new MidataGroup();
            midataGroup.setName("Pfadi Kanton Bern");
            midataGroup.setMidataId(2);
            midataGroup = midataGroupRepository.save(midataGroup);
        }
        return midataGroup;
    }

    protected MidataGroup ensureBezOeGroup() {
        MidataGroup midataGroup = midataGroupRepository.findByMidataId(3);
        if (midataGroup == null) {
            midataGroup = new MidataGroup();
            midataGroup.setName("Bezirk Obere Emme");
            midataGroup.setMidataId(3);
            midataGroup = midataGroupRepository.save(midataGroup);
        }
        return midataGroup;
    }

    protected GroupDefaultTile ensurePbsGroupDefaultTile() {
        MidataGroup group = ensurePbsGroup();
        MasterTile tile = ensurePbsNonRestrictedMasterTile();
        List<GroupDefaultTile> groupDefaultTiles = groupDefaultTileRepository.findAllByGroupAndMasterTile(group, tile);
        if (groupDefaultTiles.isEmpty()) {
            GroupDefaultTile groupDefaultTile = new GroupDefaultTile();
            groupDefaultTile.setGroup(group);
            groupDefaultTile.setMasterTile(tile);
            groupDefaultTile.setPosition(1);
            return groupDefaultTileRepository.save(groupDefaultTile);
        } else {
            return groupDefaultTiles.get(0);
        }
    }

    protected GroupDefaultTile ensureBezOeGroupDefaultTile() {
        MidataGroup group = ensureBezOeGroup();
        MasterTile tile = ensureBezOeRestrictedMasterTile();
        List<GroupDefaultTile> groupDefaultTiles = groupDefaultTileRepository.findAllByGroupAndMasterTile(group, tile);
        if (groupDefaultTiles.isEmpty()) {
            GroupDefaultTile groupDefaultTile = new GroupDefaultTile();
            groupDefaultTile.setGroup(group);
            groupDefaultTile.setMasterTile(tile);
            groupDefaultTile.setPosition(1);
            return groupDefaultTileRepository.save(groupDefaultTile);
        } else {
            return groupDefaultTiles.get(0);
        }
    }

}
