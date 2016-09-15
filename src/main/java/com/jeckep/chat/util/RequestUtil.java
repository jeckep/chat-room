package com.jeckep.chat.util;

import com.jeckep.chat.model.User;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;

public class RequestUtil {
    public static String getSessionLocale(HttpServletRequest request) {
        return (String) request.getSession().getAttribute("locale");
    }

    public static User getCurrentUser() {
        Authentication authentication =  SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && !(authentication instanceof AnonymousAuthenticationToken)){
            return (User) authentication.getPrincipal();
        }
        return null;
    }

}
