package com.jeckep.chat.login.oauth;

import com.jeckep.chat.model.User;
import com.jeckep.chat.repository.UserService;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;

import java.util.Map;

public class GithubPrincipalExtractor implements PrincipalExtractor {
    final UserService userService;

    public GithubPrincipalExtractor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Object extractPrincipal(Map<String, Object> map) {
        final String name =  ((String) map.get("name")).split("\\s")[0];
        final String surname = ((String) map.get("name")).split("\\s")[1];
        final String email = (String) map.get("email");
        final String picture = (String) map.get("avatar_url");

        return userService.findOrCreate(new User(name, surname, email, picture));
    }
}
