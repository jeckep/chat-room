package com.jeckep.chat.login.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.jeckep.chat.env.Envs;
import com.jeckep.chat.login.GoogleUser;
import com.jeckep.chat.user.IUser;

import java.io.IOException;

public class OAuth {
    public static class Google {
        public static final String REQUEST_URL = "https://www.googleapis.com/userinfo/v2/me";
        private static final OAuth20Service service = new ServiceBuilder()
                .apiKey(Envs.GOOGLE_API_KEY)
                .apiSecret(Envs.GOOGLE_API_SECRET)
                .callback("http://localhost:4567/oauth2callback/google/")
                .scope("email profile")
                .build(GoogleApi20.instance());

        public static OAuth20Service service(){
            return service;
        }

        public static IUser retriveInfo(String authCode) throws IOException {
            OAuth2AccessToken accessToken =  service.getAccessToken(authCode);
            final OAuthRequest req = new OAuthRequest(Verb.GET, OAuth.Google.REQUEST_URL, service);
            service.signRequest(accessToken,req);
            final com.github.scribejava.core.model.Response res =  req.send();
            GoogleUser gu = new ObjectMapper().readValue(res.getBody(), GoogleUser.class);
            return gu;
        }

    }
}
