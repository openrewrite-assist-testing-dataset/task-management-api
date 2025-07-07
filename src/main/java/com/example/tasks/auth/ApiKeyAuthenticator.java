package com.example.tasks.auth;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

import java.security.Principal;
import java.util.Optional;
import java.util.Set;

public class ApiKeyAuthenticator implements Authenticator<BasicCredentials, Principal> {
    
    private static final Set<String> VALID_API_KEYS = Set.of(
        "task-api-key-123",
        "task-api-key-456", 
        "task-api-key-789",
        "task-admin-key-000",
        "task-manager-key-999"
    );
    
    @Override
    public Optional<Principal> authenticate(BasicCredentials credentials) throws AuthenticationException {
        String apiKey = credentials.getUsername();
        String password = credentials.getPassword();
        
        // API key authentication uses the username field for the key, password should be empty
        if (password != null && !password.isEmpty()) {
            return Optional.empty();
        }
        
        if (VALID_API_KEYS.contains(apiKey)) {
            return Optional.of(new ApiKeyPrincipal(apiKey));
        }
        
        return Optional.empty();
    }
    
    public static class ApiKeyPrincipal implements Principal {
        private final String apiKey;
        
        public ApiKeyPrincipal(String apiKey) {
            this.apiKey = apiKey;
        }
        
        @Override
        public String getName() {
            return apiKey;
        }
        
        public String getApiKey() {
            return apiKey;
        }
        
        @Override
        public String toString() {
            return "ApiKeyPrincipal{" + "apiKey='" + apiKey + '\'' + '}';
        }
    }
}