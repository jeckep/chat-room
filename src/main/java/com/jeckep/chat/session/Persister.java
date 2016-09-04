package com.jeckep.chat.session;

import java.util.Map;

public interface Persister {
    void save(String sessionCookieValue, Map<String, Object> sessionAttrs, int expire);
    Map<String, Object> restore(String sessionCookie, int expire);
}