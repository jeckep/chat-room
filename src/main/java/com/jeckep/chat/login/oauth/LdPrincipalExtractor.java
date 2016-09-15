package com.jeckep.chat.login.oauth;

import com.jeckep.chat.domain.User;
import com.jeckep.chat.service.UserService;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;

import java.util.Map;

public class LdPrincipalExtractor implements PrincipalExtractor {
    final UserService userService;

    public LdPrincipalExtractor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Object extractPrincipal(Map<String, Object> map) {
        final String name =  (String) map.get("firstName");
        final String surname = (String) map.get("lastName");
        final String email = (String) map.get("emailAddress");
        final String picture = (String) map.get("pictureUrl");

        return userService.findOrCreate(new User(name, surname, email, picture));
    }
}
