package com.jeckep.chat.login.oauth.vk;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

public interface ResourceServerTokenServices  {
    OAuth2Authentication loadAuthentication(OAuth2AccessToken accessToken) throws AuthenticationException, InvalidTokenException;
}
