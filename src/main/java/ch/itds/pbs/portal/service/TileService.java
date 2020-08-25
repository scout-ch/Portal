package ch.itds.pbs.portal.service;

import ch.itds.pbs.portal.domain.*;
import ch.itds.pbs.portal.dto.LocalizedTile;
import ch.itds.pbs.portal.repo.CategoryRepository;
import ch.itds.pbs.portal.repo.MasterTileRepository;
import ch.itds.pbs.portal.repo.UserRepository;
import ch.itds.pbs.portal.repo.UserTileRepository;
import ch.itds.pbs.portal.security.UserPrincipal;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TileService {

    private final transient CategoryRepository categoryRepository;
    private final transient UserRepository userRepository;
    private final transient UserTileRepository userTileRepository;
    private final transient MasterTileRepository masterTileRepository;

    public TileService(CategoryRepository categoryRepository, UserRepository userRepository, UserTileRepository userTileRepository, MasterTileRepository masterTileRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.userTileRepository = userTileRepository;
        this.masterTileRepository = masterTileRepository;
    }

    @Transactional(readOnly = true)
    public List<LocalizedTile> listTiles(UserPrincipal userPrincipal, Language language) {
        return userTileRepository.findAllWithFetchForUserId(userPrincipal.getId())
                .map(ut -> convertToLocalized(ut, language))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private LocalizedTile convertToLocalized(UserTile userTile, Language language) {
        if (userTile.getMasterTile().getTitle() == null
                || userTile.getMasterTile().getContent() == null
                || Strings.isEmpty(userTile.getMasterTile().getTitle().getOrDefault(language, null))
                || Strings.isEmpty(userTile.getMasterTile().getContent().getOrDefault(language, null))) {
            return null;
        }

        LocalizedTile localizedTile = new LocalizedTile();

        localizedTile.setTitle(userTile.getMasterTile().getTitle().getOrDefault(language, null));
        localizedTile.setContent(userTile.getMasterTile().getContent().getOrDefault(language, null));
        if (userTile.getMasterTile().getUrl() != null) {
            localizedTile.setUrl(userTile.getMasterTile().getUrl().getOrDefault(language, null));
        }
        localizedTile.setCategory(userTile.getMasterTile().getCategory().getName());

        localizedTile.setMasterTileId(userTile.getMasterTile().getId());
        localizedTile.setUserTileId(userTile.getId());
        localizedTile.setCategoryId(userTile.getMasterTile().getCategory().getId());

        localizedTile.setImage(userTile.getMasterTile().getImage());

        localizedTile.setTitleColor(userTile.getMasterTile().getTitleColor());
        localizedTile.setContentColor(userTile.getMasterTile().getContentColor());
        localizedTile.setBackgroundColor(userTile.getMasterTile().getBackgroundColor());

        localizedTile.setPosition(userTile.getMasterTile().getPosition());
        localizedTile.setMessageCount(userTile.getMessages().size());

        return localizedTile;

    }

    @Transactional
    public void provisioning(UserPrincipal userPrincipal) {
        User user = userRepository.findById(userPrincipal.getId()).orElseThrow(() -> new EntityNotFoundException());
        List<UserTile> existingTiles = userTileRepository.findAllByUser(user);
        List<Long> existingMasterTileIds = existingTiles.stream().map(ut -> ut.getMasterTile().getId()).collect(Collectors.toList());
        List<UserTile> newTiles = masterTileRepository.findAll().stream()
                .filter(mt -> !existingMasterTileIds.contains(mt.getId()))
                .map(mt -> {
                    UserTile ut = new UserTile();
                    ut.setUser(user);
                    ut.setMasterTile(mt);
                    return ut;
                }).collect(Collectors.toList());
        userTileRepository.saveAll(newTiles);
    }

    public void init() {

        String mainName = "Hauptkategorie";
        Category main = categoryRepository.findByName(mainName).orElseGet(() -> new Category());
        main.setName(mainName);
        main = categoryRepository.save(main);

        String midataName = "MiData";
        String midataContent = "MiData Content";
        MasterTile midata = masterTileRepository.findByNameDe(midataName).orElseGet(() -> new MasterTile());
        midata.getTitle().put(Language.DE, midataName);
        midata.getTitle().put(Language.FR, midataName);
        midata.getTitle().put(Language.IT, midataName);
        midata.getTitle().put(Language.EN, midataName);
        midata.getContent().put(Language.DE, midataContent);
        midata.getContent().put(Language.FR, midataContent);
        midata.getContent().put(Language.IT, midataContent);
        midata.getContent().put(Language.EN, midataContent);
        midata.setPosition(0);
        midata.setCategory(main);
        midata = masterTileRepository.save(midata);

    }

}
