package com.jeckep.chat.login.oauth;

import com.jeckep.chat.domain.User;
import com.jeckep.chat.service.UserService;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;

import java.util.Map;

public class GooglePrincipalExtractor implements PrincipalExtractor {
    final UserService userService;

    public GooglePrincipalExtractor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Object extractPrincipal(Map<String, Object> map) {
        final String name =  (String) map.get("given_name");
        final String surname = (String) map.get("family_name");
        final String email = (String) map.get("email");
        final String picture = (String) map.get("picture");

        return userService.findOrCreate(new User(name, surname, email, picture));
    }
}