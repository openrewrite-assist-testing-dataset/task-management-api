package com.example.tasks.auth;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BasicAuthenticator implements Authenticator<BasicCredentials, Principal> {
    
    private static final Map<String, String> VALID_USERS = new HashMap<String, String>() {{
        put("admin", "password");
        put("user", "user123");
        put("manager", "manager456");
        put("taskmaster", "tasks789");
    }};
    
    @Override
    public Optional<Principal> authenticate(BasicCredentials credentials) throws AuthenticationException {
        String username = credentials.getUsername();
        String password = credentials.getPassword();
        
        if (VALID_USERS.containsKey(username) && VALID_USERS.get(username).equals(password)) {
            return Optional.of(new BasicPrincipal(username));
        }
        
        return Optional.empty();
    }
    
    public static class BasicPrincipal implements Principal {
        private final String name;
        
        public BasicPrincipal(String name) {
            this.name = name;
        }
        
        @Override
        public String getName() {
            return name;
        }
        
        @Override
        public String toString() {
            return "BasicPrincipal{" + "name='" + name + '\'' + '}';
        }
    }
}