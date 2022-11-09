package ch.itds.pbs.portal.service;

import ch.itds.pbs.portal.domain.*;
import ch.itds.pbs.portal.dto.LocalizedTile;
import ch.itds.pbs.portal.repo.MasterTileRepository;
import ch.itds.pbs.portal.repo.MidataGroupRepository;
import ch.itds.pbs.portal.repo.UserRepository;
import ch.itds.pbs.portal.repo.UserTileRepository;
import ch.itds.pbs.portal.security.UserPrincipal;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TileService {

    private final transient UserRepository userRepository;
    private final transient UserTileRepository userTileRepository;
    private final transient MasterTileRepository masterTileRepository;

    private final transient MidataGroupRepository midataGroupRepository;

    public TileService(UserRepository userRepository, UserTileRepository userTileRepository, MasterTileRepository masterTileRepository, MidataGroupRepository midataGroupRepository) {
        this.userRepository = userRepository;
        this.userTileRepository = userTileRepository;
        this.masterTileRepository = masterTileRepository;
        this.midataGroupRepository = midataGroupRepository;
    }

    @Transactional(readOnly = true)
    public List<LocalizedTile> listTiles(UserPrincipal userPrincipal, Language language) {
        return userTileRepository.findAllEnabledWithFetchForUserId(userPrincipal.getId())
                .map(ut -> convertToLocalized(ut, language))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private LocalizedTile convertToLocalized(UserTile userTile, Language language) {

        LocalizedTile localizedTile = convertToLocalized(userTile.getMasterTile(), language);
        if (localizedTile != null) {
            localizedTile.setUserTileId(userTile.getId());
            localizedTile.setPosition(userTile.getPosition());
            localizedTile.setUnreadMessageCount(userTile.getMessages().stream().filter(msg -> !msg.isRead()).count());
        }

        return localizedTile;
    }

    public LocalizedTile convertToLocalized(MasterTile masterTile, Language language) {
        if (masterTile.getTitle() == null
                || masterTile.getContent() == null
                || Strings.isEmpty(masterTile.getTitle().getOrDefault(language, null))
                || Strings.isEmpty(masterTile.getContent().getOrDefault(language, null))) {
            return null;
        }

        LocalizedTile localizedTile = new LocalizedTile();

        localizedTile.setTitle(masterTile.getTitle().getOrDefault(language, null));
        localizedTile.setContent(masterTile.getContent().getOrDefault(language, null));
        if (masterTile.getUrl() != null) {
            localizedTile.setUrl(masterTile.getUrl().getOrDefault(language, null));
        }
        localizedTile.setCategory(masterTile.getCategory().getName().getOrDefault(language, null));

        localizedTile.setMasterTileId(masterTile.getId());
        localizedTile.setMasterTileVersion(masterTile.getVersion());
        localizedTile.setCategoryId(masterTile.getCategory().getId());

        localizedTile.setImage(masterTile.getImage());

        localizedTile.setTitleColor(masterTile.getTitleColor());
        localizedTile.setContentColor(masterTile.getContentColor());
        localizedTile.setBackgroundColor(masterTile.getBackgroundColor());

        localizedTile.setPosition(masterTile.getPosition());

        return localizedTile;

    }

    @Transactional
    public void provisioning(UserPrincipal userPrincipal) {
        User user = userRepository.findById(userPrincipal.getId()).orElseThrow(EntityNotFoundException::new);
        provisioning(user);
    }

    private void provisioning(User user) {
        List<UserTile> existingTiles = userTileRepository.findAllByUser(user);
        List<Long> existingMasterTileIds = existingTiles.stream().map(ut -> ut.getMasterTile().getId()).toList();

        Collection<GroupDefaultTile> groupDefaultTiles;
        if (user.getPrimaryMidataGroup() != null) {
            groupDefaultTiles = user.getPrimaryMidataGroup().getDefaultTiles();
        } else {
            groupDefaultTiles = midataGroupRepository.findByMidataId(1).getDefaultTiles();
        }

        List<UserTile> newTiles = groupDefaultTiles.stream()
                .filter(gdt ->
                        gdt.getMasterTile().isEnabled() &&
                                !existingMasterTileIds.contains(gdt.getMasterTile().getId()))
                .map(gdt -> {
                    UserTile ut = new UserTile();
                    ut.setUser(user);
                    ut.setMasterTile(gdt.getMasterTile());
                    ut.setPosition(gdt.getPosition());
                    return ut;
                }).toList();
        userTileRepository.saveAll(newTiles);
    }

    @Transactional(readOnly = true)
    public List<LocalizedTile> listTilesByGroupsOrNotRestricted(UserPrincipal userPrincipal, Language language) {
        return masterTileRepository.findAllEnabledWithFetchForUserByGroupsOrNotRestricted(userPrincipal.getId())
                .map(mt -> convertToLocalized(mt, language))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
