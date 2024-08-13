package ch.itds.pbs.portal.controller;

import ch.itds.pbs.portal.TestAnnotationHolder;
import ch.itds.pbs.portal.security.CustomUserDetailsService;
import ch.itds.pbs.portal.security.LocaleSettingAuthenticationSuccessHandler;
import ch.itds.pbs.portal.security.oauth.MidataOAuth2UserService;
import ch.itds.pbs.portal.security.tile.TileAuthenticationManager;
import ch.itds.pbs.portal.service.MessageService;
import ch.itds.pbs.portal.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;

@TestAnnotationHolder
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseControllerTest {

    @Autowired
    protected
    MockMvc mockMvc;

    @MockBean
    RestTemplateBuilder restTemplateBuilder;

    @MockBean(name = "messageService")
    protected // name required for thymeleaf
    MessageService messageService;

    @MockBean
    UserService userService;
    @MockBean(name = "customUserDetailsService")
    CustomUserDetailsService customUserDetailsService;
    @MockBean(name = "userDetailsService")
    CustomUserDetailsService userDetailsService;
    @MockBean
    MidataOAuth2UserService midataOAuth2UserService;
    @MockBean
    LocaleSettingAuthenticationSuccessHandler localeSettingAuthenticationSuccessHandler;
    @MockBean
    TileAuthenticationManager tileAuthenticationManager;

    @BeforeAll
    public void initBaseMocks() {
        Mockito.when(messageService.countUnreadMessages(any())).thenReturn(1);
    }

}
