package com.smartclinic.core.token;

/**
 * Abstraction for refresh-token persistence.
 *
 * Dev:  InMemoryRefreshTokenStore  (no external deps)
 * Prod: RedisRefreshTokenStore     (swap in when Redis is available)
 */
public interface RefreshTokenStore {

    void save(String userId, String token);

    /** Returns the stored token, or null if absent / expired. */
    String get(String userId);

    void delete(String userId);
}
