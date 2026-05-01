package cn.sdstudio.qutu.common.security.component;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PermissionServiceTest {

    private PermissionService permissionService;

    @BeforeEach
    void setUp() {
        permissionService = new PermissionService();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void hasPermission_emptyPermissions_returnsFalse() {
        assertFalse(permissionService.hasPermission());
    }

    @Test
    void hasPermission_nullAuthentication_returnsFalse() {
        SecurityContextHolder.getContext().setAuthentication(null);
        assertFalse(permissionService.hasPermission("anything"));
    }

    @Test
    void hasPermission_exactMatch_returnsTrue() {
        Authentication auth = mock(Authentication.class);
        when(auth.getAuthorities()).thenReturn((java.util.Collection) Collections.singletonList(new SimpleGrantedAuthority("destination_city_edit")));
        SecurityContextHolder.getContext().setAuthentication(auth);
        assertTrue(permissionService.hasPermission("destination_city_edit"));
    }

    @Test
    void hasPermission_patternMatch_returnsTrue() {
        Authentication auth = mock(Authentication.class);
        when(auth.getAuthorities()).thenReturn((java.util.Collection) Collections.singletonList(new SimpleGrantedAuthority("destination_edit")));
        SecurityContextHolder.getContext().setAuthentication(auth);
        assertTrue(permissionService.hasPermission("destination_*"));
    }

    @Test
    void hasPermission_noMatch_returnsFalse() {
        Authentication auth = mock(Authentication.class);
        when(auth.getAuthorities()).thenReturn((java.util.Collection) Collections.singletonList(new SimpleGrantedAuthority("user_read")));
        SecurityContextHolder.getContext().setAuthentication(auth);
        assertFalse(permissionService.hasPermission("destination_city_edit"));
    }

}
