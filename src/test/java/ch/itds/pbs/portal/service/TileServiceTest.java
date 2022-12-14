package ch.itds.pbs.portal.service;

import ch.itds.pbs.portal.domain.*;
import ch.itds.pbs.portal.dto.TileOverrideCreateRequest;
import ch.itds.pbs.portal.repo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class TileServiceTest {

    @Autowired
    TileService tileService;

    @Autowired
    MasterTileRepository masterTileRepository;

    @Autowired
    UserTileRepository userTileRepository;

    @Autowired
    TileOverrideRepository tileOverrideRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MidataGroupRepository midataGroupRepository;

    @Autowired
    CategoryRepository categoryRepository;

    LocalizedString deLanguage;

    LocalizedString frLanguage;

    LocalizedString allLanguages;

    LocalizedString noLanguages;

    Long masterTileId;

    @BeforeEach
    public void initMocks() {

        MidataGroup midataGroup = new MidataGroup();
        midataGroup.setName("Testgroup");
        midataGroup = midataGroupRepository.save(midataGroup);

        LocalizedString catDefault = new LocalizedString();
        catDefault.setDe("DE default");
        catDefault.setFr("FR default");
        catDefault.setIt("IT default");
        catDefault.setEn("EN default");

        Category category = new Category();
        category.setName(catDefault);
        category.setMidataGroupOnly(midataGroup);
        category = categoryRepository.save(category);

        User user0 = new User();
        user0.setMail("user0@example.com");
        user0.setUsername("user0@example.com");
        user0.setMidataUserId(0L);
        user0.setLanguage(Language.DE);
        user0.setPrimaryMidataGroup(midataGroup);

        User user1 = new User();
        user1.setMail("user1@example.com");
        user1.setUsername("user1@example.com");
        user1.setMidataUserId(1L);
        user1.setLanguage(Language.FR);
        user1.setPrimaryMidataGroup(midataGroup);

        User user2 = new User();
        user2.setMail("user2@example.com");
        user2.setUsername("user2@example.com");
        user2.setMidataUserId(2L);
        user2.setLanguage(Language.EN);
        user2.setPrimaryMidataGroup(midataGroup);

        user0 = userRepository.save(user0);
        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);

        LocalizedString mt0default = new LocalizedString();
        mt0default.setDe("DE default");
        mt0default.setFr("FR default");
        mt0default.setIt("IT default");
        mt0default.setEn("EN default");

        MasterTile mt0 = new MasterTile();
        mt0.setTitle(mt0default);
        mt0.setContent(mt0default);
        mt0.setMidataGroupOnly(midataGroup);
        mt0.setCategory(category);
        mt0 = masterTileRepository.save(mt0);
        masterTileId = mt0.getId();

        UserTile ut0 = new UserTile();
        ut0.setUser(user0);
        ut0.setMasterTile(mt0);

        UserTile ut1 = new UserTile();
        ut1.setUser(user1);
        ut1.setMasterTile(mt0);

        UserTile ut2 = new UserTile();
        ut2.setUser(user2);
        ut2.setMasterTile(mt0);

        userTileRepository.saveAll(List.of(ut0, ut1, ut2));

        deLanguage = new LocalizedString();
        deLanguage.setDe("Text DE");

        frLanguage = new LocalizedString();
        frLanguage.setFr("Text FR");

        allLanguages = new LocalizedString();
        allLanguages.setDe("Text DE");
        allLanguages.setFr("Text FR");
        allLanguages.setIt("Text IT");
        allLanguages.setEn("Text EN");

        noLanguages = new LocalizedString();

    }

    @Test
    public void overrideForCompleteMessageCreateRequestOfOneUser() {
        LocalDateTime validUntil = LocalDateTime.now();
        TileOverrideCreateRequest tileOverrideCreateRequest = new TileOverrideCreateRequest();
        tileOverrideCreateRequest.setTitle(allLanguages);
        tileOverrideCreateRequest.setContent(deLanguage);
        tileOverrideCreateRequest.setUrl(frLanguage);
        tileOverrideCreateRequest.setBackgroundColor(Color.OCHER);
        tileOverrideCreateRequest.setLimitToUserIds(List.of(1L));
        tileOverrideCreateRequest.setValidUntil(validUntil);

        TileOverride override = tileService.createTileOverride(masterTileId, tileOverrideCreateRequest);

        assertEquals(validUntil, override.getValidUntil());
        assertEquals(allLanguages, override.getTitle());
        assertEquals(deLanguage, override.getContent());
        assertEquals(frLanguage, override.getUrl());
        assertEquals(Color.OCHER, override.getBackgroundColor());


        List<UserTile> userTiles = userTileRepository.findAllByMasterTileIdAndUserMidataIdWithUser(masterTileId, Collections.singletonList(1L));
        assertEquals(1, userTiles.size());
        assertEquals(override.getId(), userTiles.get(0).getOverride().getId());
    }

    @Test
    public void overrideForCompleteMessageCreateRequestOfAllUsers() {
        LocalDateTime validUntil = LocalDateTime.now();
        TileOverrideCreateRequest tileOverrideCreateRequest = new TileOverrideCreateRequest();
        tileOverrideCreateRequest.setTitle(allLanguages);
        tileOverrideCreateRequest.setContent(deLanguage);
        tileOverrideCreateRequest.setUrl(frLanguage);
        tileOverrideCreateRequest.setBackgroundColor(Color.OCHER);
        tileOverrideCreateRequest.setValidUntil(validUntil);

        TileOverride override = tileService.createTileOverride(masterTileId, tileOverrideCreateRequest);

        assertEquals(validUntil, override.getValidUntil());
        assertEquals(allLanguages, override.getTitle());
        assertEquals(deLanguage, override.getContent());
        assertEquals(frLanguage, override.getUrl());
        assertEquals(Color.OCHER, override.getBackgroundColor());


        List<UserTile> userTiles = userTileRepository.findAllByMasterTileIdAndUserMidataIdWithUser(masterTileId, List.of(0L, 1L, 2L));
        assertEquals(3, userTiles.size());
        for (UserTile ut : userTiles) {
            assertNull(ut.getOverride());
            assertEquals(override.getId(), ut.getMasterTile().getOverride().getId());
        }
    }

}
