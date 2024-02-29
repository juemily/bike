package com.example.bike.inftrastructure.config.security.ldap;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.AuthenticationException;
import org.springframework.ldap.CommunicationException;
import org.springframework.ldap.InvalidSearchFilterException;
import org.springframework.ldap.UncategorizedLdapException;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapEntryIdentificationContextMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.stereotype.Service;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.util.Collections;
import java.util.List;

/**
 * Centraliza el acceso al LDAP
 */
@Service
public class LdapService implements InitializingBean {
    private static Logger log = LoggerFactory.getLogger(LdapService.class);

    private LdapTemplate ldapTemplate;

    @Value("${spring.ldap.urls}")
    private String[] ldapUrls;
    @Value("${spring.ldap.username:}")
    private String userDn;
    @Value("${spring.ldap.password:}")
    private String password;

    @Value("${ldap.filter.base.users:}")
    private String baseUsersDN;

    @Value("${ldap.filter.base.groups:}")
    private String baseGroupsDN;

    @Value("${ldap.filter.search-users}")
    private String ldapUserFilter;

    @Value("${ldap.filter.search-groups}")
    private String ldapGroupFilter;



    @Override
    public void afterPropertiesSet() throws Exception {
        LdapContextSource ctx = new LdapContextSource();
        ctx.setUrls(ldapUrls);
        ctx.setUserDn(userDn);
        ctx.setPassword(password);
        ctx.afterPropertiesSet();
        ldapTemplate = new LdapTemplate(ctx);
    }

    /**
     * Valida un usuario contra el LDAP.
     */
    public boolean authenticate(String userUid, String password) throws AuthenticationException {
        String searchFilter = String.format(ldapUserFilter, userUid);
        try {return ldapTemplate.authenticate(baseUsersDN, searchFilter, password);
        } catch (CommunicationException | UncategorizedLdapException e) {
            throw new AuthenticationException();
        }
    }

    /**
     * Valida que el filtro LDAP devuelve entradas.
     */
    public boolean checkBaseUsers(String ldapFilter) {
        try {
            return !ldapTemplate.search(baseUsersDN, ldapFilter, new LdapEntryIdentificationContextMapper()).isEmpty();
        } catch (InvalidSearchFilterException e) {
            log.warn("Filtro LDAP incorrecto: {}", ldapFilter, e);
            return false;
        }
    }

    public List<String> searchGroupsOfUser(String userUid) {
        String searchFilter = String.format(ldapUserFilter, userUid);
        return ldapTemplate.search(baseUsersDN, searchFilter, new AttributesMapper<List<String>>() {
            @Override
            public List<String> mapFromAttributes(Attributes attrs) throws NamingException {
                if (attrs.get("memberOf") == null) {
                    return Collections.emptyList();
                }
                return Collections.list(attrs.get("memberOf").getAll())
                                  .stream()
                                  .map(e -> e.toString())
                                  .toList();
            }
        }).stream().flatMap(l -> l.stream()).toList();
    }

    /**
     * @return Listado de grupos definidos en el LDAP.
     */
    public List<String> getAllGroups() {
        return ldapTemplate.search(baseGroupsDN, ldapGroupFilter, new AbstractContextMapper<String>() {
            @Override
            protected String doMapFromContext(DirContextOperations ctx) {
                return ctx.getDn().toString();
            }
        });
    }
}
