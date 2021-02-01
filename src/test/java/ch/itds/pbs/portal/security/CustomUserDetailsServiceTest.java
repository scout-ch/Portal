package ch.itds.pbs.portal.security;

import ch.itds.pbs.portal.domain.User;
import ch.itds.pbs.portal.repo.UserRepository;
import org.hibernate.ObjectNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
class CustomUserDetailsServiceTest {

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @MockBean
    UserRepository userRepository;


    @BeforeEach
    void setUp() {

        User user = new User();
        user.setId(1234L);
        user.setMidataUserId(5432L);
        user.setEnabled(true);
        user.setAccountExpired(false);
        user.setAccountLocked(false);
        user.setPasswordExpired(false);
        user.setMail("bob@example.com");

        Mockito.when(userRepository.findByUsername(eq("5432"))).thenReturn(Optional.of(user));
        Mockito.when(userRepository.findByUsername(not(eq("5432")))).thenReturn(Optional.empty());

        Mockito.when(userRepository.findById(eq(1234L))).thenReturn(Optional.of(user));
        Mockito.when(userRepository.findById(not(eq(1234L)))).thenReturn(Optional.empty());


    }

    @Test
    void loadUserByExistingUsername() {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("5432");
        assertEquals("5432", userDetails.getUsername());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
    }

    @Test
    void loadUserByNotExistingUsername() {
        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername("bob@example.com"); // Note: MiDataUserID is our username!
        });
    }

    @Test
    void loadUserById() {
        UserDetails userDetails = customUserDetailsService.loadUserById(1234L);
        assertEquals("5432", userDetails.getUsername());
    }

    @Test
    void loadUserByIdNotExisting() {
        Assertions.assertThrows(ObjectNotFoundException.class, () -> {
            customUserDetailsService.loadUserById(-1L);
        });
    }
}