package com.jeckep.chat.login.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.apis.VkontakteApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.jeckep.chat.env.Envs;
import com.jeckep.chat.login.GoogleUser;
import com.jeckep.chat.user.IUser;
import com.jeckep.chat.user.User;
import lombok.Data;
import org.json.JSONException;
import org.json.JSONObject;

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

    public static class VK {
        public static final String REQUEST_URL = "https://api.vk.com/method/users.get";
        private static final OAuth20Service service = new ServiceBuilder()
                .apiKey(Envs.VK_API_KEY)
                .apiSecret(Envs.VK_API_SECRET)
                .callback("http://localhost:4567/oauth2callback/vk/")
                .scope("email profile")
                .build(VkontakteApi.instance());


        public static OAuth20Service service(){
            return service;
        }

        public static IUser retriveInfo(String authCode) throws IOException, JSONException {
            final OAuth2AccessToken accessToken =  service.getAccessToken(authCode);
            final VkAccessTokenResponse resp = new ObjectMapper().readValue(accessToken.getRawResponse(), VkAccessTokenResponse.class);
            final OAuthRequest req = new OAuthRequest(Verb.GET, REQUEST_URL, service);
            req.addQuerystringParameter("user_ids", resp.getUser_id());
            req.addQuerystringParameter("fields", "photo_100");
            service.signRequest(accessToken,req);
            final com.github.scribejava.core.model.Response res =  req.send();
            JSONObject jo = new JSONObject(res.getBody());

            final String name = jo.getJSONArray("response").getJSONObject(0).getString("first_name");
            final String surname = jo.getJSONArray("response").getJSONObject(0).getString("last_name");
            final String picture = jo.getJSONArray("response").getJSONObject(0).getString("photo_100");
            final String email = resp.getEmail();

            return new User(name, surname, email, picture);
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VkAccessTokenResponse {
        String user_id;
        String email;
    }
}
