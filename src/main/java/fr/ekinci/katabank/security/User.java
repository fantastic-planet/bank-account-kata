package fr.ekinci.katabank.security;

import lombok.Data;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import java.io.Serializable;
import java.util.Collection;

@Data
public class User implements Authentication {
	private Collection<? extends GrantedAuthority> authorities;
	private Serializable credentials;
	private Serializable details;
	private String principal = ""; // Principal must not be null
	private boolean authenticated;
	private String name;
}
