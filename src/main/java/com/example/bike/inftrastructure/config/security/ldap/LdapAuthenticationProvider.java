package com.example.bike.inftrastructure.config.security.ldap;


import com.example.bike.inftrastructure.config.security.Authorities;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.AuthenticationException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class LdapAuthenticationProvider implements AuthenticationProvider {
    private static Logger log = LoggerFactory.getLogger(LdapAuthenticationProvider.class);

    private static final String DEFAULT_PERMISSION = "ADMIN_PORTAL_ACCESS";

    private final LdapService ldapService;

    @Value("${ldap.active:true}")
    private boolean useLdapToAuthenticate;



    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String userUid = authentication.getName();
        if (useLdapToAuthenticate) {
            boolean authenticate = ldapService.authenticate(userUid, authentication.getCredentials().toString());
            if (authenticate) {
                return buildToken(userUid);
            }
            throw new AuthenticationException();
        }
        throw new AuthenticationException();
    }

    public UsernamePasswordAuthenticationToken buildToken(String userUid) {
        Set<String> permisos = new HashSet<>(Arrays.asList(DEFAULT_PERMISSION,
                Authorities.ADMIN.ADMIN_DEVICE,
                Authorities.ADMIN.ADMIN_MODEL_GROUP,
                Authorities.ADMIN.ADMIN_PORTAL_ACCESS,
                Authorities.ADMIN.ADMIN_USER_GROUP
        ));
        Collection<? extends GrantedAuthority> authorities = permisos.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());

        UserDetails principal = new User(userUid, "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }
}
