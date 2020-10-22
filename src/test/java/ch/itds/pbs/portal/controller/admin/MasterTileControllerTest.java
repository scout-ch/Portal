package ch.itds.pbs.portal.controller.admin;

import ch.itds.pbs.portal.MockedWebConversion;
import ch.itds.pbs.portal.WithMockedWebConversion;
import ch.itds.pbs.portal.controller.BaseControllerTest;
import ch.itds.pbs.portal.domain.Category;
import ch.itds.pbs.portal.domain.Color;
import ch.itds.pbs.portal.domain.LocalizedString;
import ch.itds.pbs.portal.domain.MasterTile;
import ch.itds.pbs.portal.repo.CategoryRepository;
import ch.itds.pbs.portal.repo.MasterTileRepository;
import ch.itds.pbs.portal.service.FileService;
import ch.itds.pbs.portal.service.TileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MasterTileController.class)
@WithMockUser(roles = {"USER", "ADMIN"})
@Import({WithMockedWebConversion.class})
public class MasterTileControllerTest extends BaseControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    FileService fileService;

    @MockBean
    TileService tileService;

    @MockBean
    MasterTileRepository masterTileRepository;

    @MockBean
    CategoryRepository categoryRepository;

    @Autowired
    MockedWebConversion mockedWebConversion;

    @BeforeEach
    public void initMocks() {

        LocalizedString categoryName = new LocalizedString();
        categoryName.setDe("Kategorie");

        Category category = new Category();
        category.setId(5L);
        category.setName(categoryName);

        LocalizedString title = new LocalizedString();
        title.setDe("Titel");
        LocalizedString content = new LocalizedString();
        content.setDe("Content...");

        MasterTile masterTile = new MasterTile();
        masterTile.setCategory(category);
        masterTile.setTitle(title);
        masterTile.setContent(content);
        masterTile.setBackgroundColor(Color.DEFAULT);

        Mockito.when(masterTileRepository.findAllWithCategory()).thenReturn(List.of(masterTile));
        Mockito.when(masterTileRepository.findById(eq(1L))).thenReturn(Optional.of(masterTile));
        Mockito.when(masterTileRepository.findById(eq(2L))).thenReturn(Optional.empty());

        mockedWebConversion.register(category);
        Mockito.when(categoryRepository.findById(any())).thenReturn(Optional.of(category));

    }

    @Test
    public void regularIndex() throws Exception {

        mockMvc.perform(get("/admin/masterTile"))
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    public void indexAsAnonymous() throws Exception {
        mockMvc.perform(get("/admin/masterTile"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void create() throws Exception {
        mockMvc.perform(get("/admin/masterTile/create"))
                .andExpect(status().isOk());
    }

    @Test
    public void createSaveWithFile() throws Exception {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.set("title.de", "Titel");
        params.set("content.de", "Inhalt");
        params.set("category", "5");
        params.set("backgroundColor", "DEFAULT");

        MockMultipartFile mockMultipartFile = new MockMultipartFile("imageUpload", "file.txt",
                "text/plain", "content...".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/admin/masterTile/create")
                .file(mockMultipartFile).params(params).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/masterTile"));
    }

    @Test
    public void createSaveWithEmptyFile() throws Exception {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.set("title.de", "Titel");
        params.set("content.de", "Inhalt");
        params.set("category", "5");
        params.set("backgroundColor", "DEFAULT");

        MockMultipartFile mockMultipartFile = new MockMultipartFile("imageUpload", "file.txt",
                "text/plain", "".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/admin/masterTile/create")
                .file(mockMultipartFile).params(params).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/masterTile"));
    }

    @Test
    public void createSaveWithMissingTitle() throws Exception {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        //params.set("title.de", "Titel");
        params.set("content.de", "Inhalt");
        params.set("category", "5");
        params.set("backgroundColor", "DEFAULT");

        MockMultipartFile mockMultipartFile = new MockMultipartFile("imageUpload", "file.txt",
                "text/plain", "".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/admin/masterTile/create")
                .file(mockMultipartFile).params(params).with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    public void edit() throws Exception {
        mockMvc.perform(get("/admin/masterTile/edit/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void editNotFound() throws Exception {
        mockMvc.perform(get("/admin/masterTile/edit/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void editSaveWithFileReplacement() throws Exception {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.set("title.de", "Titel");
        params.set("content.de", "Inhalt");
        params.set("category", "5");
        params.set("backgroundColor", "DEFAULT");
        params.set("imageUpload-delete", "true");

        MockMultipartFile mockMultipartFile = new MockMultipartFile("imageUpload", "file.txt",
                "text/plain", "test data".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/admin/masterTile/edit/1")
                .file(mockMultipartFile).params(params).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/masterTile"));
    }

    @Test
    public void editSaveWithNoFile() throws Exception {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.set("title.de", "Titel");
        params.set("content.de", "Inhalt");
        params.set("category", "5");
        params.set("backgroundColor", "DEFAULT");
        params.set("imageUpload-delete", "true");

        MockMultipartFile mockMultipartFile = new MockMultipartFile("imageUpload", "file.txt",
                "text/plain", "".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/admin/masterTile/edit/1")
                .file(mockMultipartFile).params(params).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/masterTile"));
    }

    @Test
    public void editSaveWithMissingTitle() throws Exception {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        //params.set("title.de", "Titel");
        params.set("content.de", "Inhalt");
        params.set("category", "5");
        params.set("backgroundColor", "DEFAULT");
        params.set("imageUpload-delete", "true");

        MockMultipartFile mockMultipartFile = new MockMultipartFile("imageUpload", "file.txt",
                "text/plain", "test data".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/admin/masterTile/edit/1")
                .file(mockMultipartFile).params(params).with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    public void delete() throws Exception {
        mockMvc.perform(post("/admin/masterTile/delete/1").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/masterTile"));
    }

}
