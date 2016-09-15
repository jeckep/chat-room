package com.jeckep.chat.login.oauth.vk;

import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.util.Map;

public class VkUserInfoModifier implements UserInfoRequestCreator, UserInfoResponseModifier {
    @Override
    public String create(String userInfoEndpointUrl, OAuth2AccessToken accessToken) {
        return userInfoEndpointUrl + "&user_ids=" + accessToken.getAdditionalInformation().get("user_id");
    }

    @Override
    public Map<String, Object> modify(Map<String, Object> map, OAuth2AccessToken accessToken) {
        map.put("email", accessToken.getAdditionalInformation().get("email"));
        return map;
    }
}
