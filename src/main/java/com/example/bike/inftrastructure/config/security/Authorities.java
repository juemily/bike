package com.example.bike.inftrastructure.config.security;

import org.springframework.security.core.Authentication;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/** Permisos definidos en el sistema. */
public abstract class Authorities
{
	public static boolean hasAuthority(Authentication auth, String authority) {
		return auth.getAuthorities().stream().anyMatch(a -> Objects.equals(a.getAuthority(), authority));
	}

	public static boolean hasAnyAuthority(Authentication auth, String ... authorities) {
		List<String> list = Arrays.asList(authorities);
		return auth.getAuthorities().stream().anyMatch(a -> list.contains(a.getAuthority()));
	}

	public abstract class ADMIN	{
			public static final String ADMIN_DEVICE = "ADMIN_DEVICE";
			public static final String ADMIN_MODEL_GROUP = "ADMIN_MODEL_GROUP";
			public static final String ADMIN_PORTAL_ACCESS = "ADMIN_PORTAL_ACCESS";
			public static final String ADMIN_USER_GROUP = "ADMIN_USER_GROUP";
	}
}
