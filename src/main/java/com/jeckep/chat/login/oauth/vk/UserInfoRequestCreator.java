package com.jeckep.chat.login.oauth.vk;

import org.springframework.security.oauth2.common.OAuth2AccessToken;

public interface UserInfoRequestCreator {
    String create(String userInfoEndpointUrl, OAuth2AccessToken accessToken);

    public static class Simple implements UserInfoRequestCreator{
        @Override
        public String create(String userInfoEndpointUrl, OAuth2AccessToken accessToken) {
            return userInfoEndpointUrl;
        }
    }
}

