package com.jeckep.chat.login.oauth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.apis.LinkedInApi20;
import com.github.scribejava.apis.VkontakteApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.jeckep.chat.env.Envs;
import com.jeckep.chat.user.IUser;
import com.jeckep.chat.user.User;
import lombok.Data;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OAuth {
    private static Map<String, Service> services = new HashMap<>();
    static {
        services.put(VK.serviceCode, new VK());
        services.put(Google.serviceCode, new Google());
        services.put(Linkedin.serviceCode, new Linkedin());
    }

    public static Service service(String service){
        return services.get(service);
    }

    public interface Service {
        OAuth20Service service();
        IUser retriveInfo(String authCode) throws IOException, JSONException;
    }

    private static class Google implements Service {
        private static final String serviceCode = "google";
        private static final String REQUEST_URL = "https://www.googleapis.com/userinfo/v2/me";

        private static final OAuth20Service service = new ServiceBuilder()
                .apiKey(Envs.GOOGLE_API_KEY)
                .apiSecret(Envs.GOOGLE_API_SECRET)
                .callback(callbackURL(serviceCode))
                .scope("email profile")
                .build(GoogleApi20.instance());

        public  OAuth20Service service(){
            return service;
        }

        public  IUser retriveInfo(String authCode) throws IOException {
            OAuth2AccessToken accessToken =  service.getAccessToken(authCode);
            final OAuthRequest req = new OAuthRequest(Verb.GET, REQUEST_URL, service);
            service.signRequest(accessToken,req);
            final com.github.scribejava.core.model.Response res =  req.send();
            GoogleUser gu = new ObjectMapper().readValue(res.getBody(), GoogleUser.class);
            return gu;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        private static class GoogleUser implements IUser {
            @JsonProperty("family_name") String surname;
            @JsonProperty("given_name") String name;
            String picture;
            String email;
        }

    }

    private static class VK implements Service{
        private static final String serviceCode = "vk";
        private static final String REQUEST_URL = "https://api.vk.com/method/users.get";
        private static final OAuth20Service service = new ServiceBuilder()
                .apiKey(Envs.VK_API_KEY)
                .apiSecret(Envs.VK_API_SECRET)
                .callback(callbackURL(serviceCode))
                .scope("email profile")
                .build(VkontakteApi.instance());


        public OAuth20Service service(){
            return service;
        }

        public IUser retriveInfo(String authCode) throws IOException, JSONException {
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

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        private static class VkAccessTokenResponse {
            String user_id;
            String email;
        }
    }

    private static class Linkedin implements Service {
        private static final String serviceCode = "linkedin";
        private static final String REQUEST_URL = "https://api.linkedin.com/v1/people/~:(id,picture-url,first-name,last-name,email-address)";

        private static final OAuth20Service service = new ServiceBuilder()
                .apiKey(Envs.LD_API_KEY)
                .apiSecret(Envs.LD_API_SECRET)
                .callback(callbackURL(serviceCode))
                .build(LinkedInApi20.instance());

        public  OAuth20Service service(){
            return service;
        }

        public  IUser retriveInfo(String authCode) throws IOException {
            OAuth2AccessToken accessToken =  service.getAccessToken(authCode);
            final OAuthRequest req = new OAuthRequest(Verb.GET, REQUEST_URL, service);
            req.addQuerystringParameter("format", "json");
            service.signRequest(accessToken,req);
            final com.github.scribejava.core.model.Response res =  req.send();
            LDUser ldUser = new ObjectMapper().readValue(res.getBody(), LDUser.class);
            return ldUser;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        private static class LDUser implements IUser {
            @JsonProperty("lastName") String surname;
            @JsonProperty("firstName") String name;
            @JsonProperty("pictureUrl") String picture;
            @JsonProperty("emailAddress") String email;
        }
    }

    public static String getAuthorizationUrl(String serviceCode){
        final OAuth20Service service = OAuth.service(serviceCode).service();
        return service.getAuthorizationUrl();
    }

    private static String callbackURL(String service){
        return "http://" + Envs.HOSTNAME + "/oauth2callback/" + service + "/";
    }
}
