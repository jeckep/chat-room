package com.jeckep.chat.login.oauth.vk;

import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.util.Map;

public interface UserInfoResponseModifier {
    Map<String, Object> modify(Map<String, Object> map, OAuth2AccessToken accessToken);

    public static class Simple implements UserInfoResponseModifier {

        @Override
        public Map<String, Object> modify(Map<String, Object> map, OAuth2AccessToken accessToken) {
            return map;
        }
    }
}
