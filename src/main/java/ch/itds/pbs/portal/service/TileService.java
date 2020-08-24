package ch.itds.pbs.portal.service;

import ch.itds.pbs.portal.domain.*;
import ch.itds.pbs.portal.repo.CategoryRepository;
import ch.itds.pbs.portal.repo.MasterTileRepository;
import ch.itds.pbs.portal.repo.UserRepository;
import ch.itds.pbs.portal.repo.UserTileRepository;
import ch.itds.pbs.portal.security.UserPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
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

    public List<MasterTile> listTiles(UserPrincipal userPrincipal) {
        return masterTileRepository.findAllForUserId(userPrincipal.getId());
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
        MasterTile midata = masterTileRepository.findByNameDe(midataName).orElseGet(() -> new MasterTile());
        midata.getTitle().put(Language.DE, midataName);
        midata.getTitle().put(Language.FR, midataName);
        midata.getTitle().put(Language.IT, midataName);
        midata.getTitle().put(Language.EN, midataName);
        midata.setPosition(0);
        midata.setCategory(main);
        midata = masterTileRepository.save(midata);

    }

}
