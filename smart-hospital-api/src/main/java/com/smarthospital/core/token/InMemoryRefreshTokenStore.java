package com.smarthospital.core.token;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory refresh token store — for Phase 1a development only.
 *
 * Limitations vs the Redis implementation:
 *   - Tokens are lost on restart (forces re-login — acceptable in dev)
 *   - No TTL enforcement (tokens live until logout or server restart)
 *   - Does NOT survive horizontal scaling (single node only)
 *
 * Switch to RedisRefreshTokenStore by setting:
 *   app.token-store=redis   in application.yml
 */
@Component
@ConditionalOnProperty(name = "app.token-store", havingValue = "memory", matchIfMissing = true)
public class InMemoryRefreshTokenStore implements RefreshTokenStore {

    private final ConcurrentHashMap<String, String> store = new ConcurrentHashMap<>();

    @Override
    public void save(String userId, String token) {
        store.put(userId, token);
    }

    @Override
    public String get(String userId) {
        return store.get(userId);
    }

    @Override
    public void delete(String userId) {
        store.remove(userId);
    }
}
