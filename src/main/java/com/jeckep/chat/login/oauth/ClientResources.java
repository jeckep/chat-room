package com.jeckep.chat.login.oauth;

import com.jeckep.chat.login.oauth.vk.UserInfoRequestCreator;
import com.jeckep.chat.login.oauth.vk.UserInfoResponseModifier;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;

public class ClientResources {
    private PrincipalExtractor principalExtractor;
    private UserInfoRequestCreator userInfoRequestCreator = new UserInfoRequestCreator.Simple();
    private UserInfoResponseModifier modifier = new UserInfoResponseModifier.Simple();

    public ClientResources(PrincipalExtractor principalExtractor) {
        this.principalExtractor = principalExtractor;
    }

    public ClientResources(PrincipalExtractor principalExtractor, UserInfoRequestCreator userInfoRequestCreator, UserInfoResponseModifier modifier) {
        this.principalExtractor = principalExtractor;
        this.userInfoRequestCreator = userInfoRequestCreator;
        this.modifier = modifier;
    }

    @NestedConfigurationProperty
    private AuthorizationCodeResourceDetails client = new AuthorizationCodeResourceDetails();

    @NestedConfigurationProperty
    private ResourceServerProperties resource = new ResourceServerProperties();

    public AuthorizationCodeResourceDetails getClient() {
        return client;
    }

    public ResourceServerProperties getResource() {
        return resource;
    }

    public PrincipalExtractor getPrincipalExtractor() {
        return principalExtractor;
    }

    public UserInfoRequestCreator getUserInfoRequestCreator() {
        return userInfoRequestCreator;
    }

    public UserInfoResponseModifier getModifier() {
        return modifier;
    }
}
