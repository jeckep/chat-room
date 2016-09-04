package com.jeckep.chat.chat;


import com.jeckep.chat.session.persist.PSF;
import com.jeckep.chat.user.User;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class AuthedUserListHolder implements  HttpSessionAttributeListener {
    private static final AuthedUserListHolder instance = new AuthedUserListHolder();

    private  Map<String, User> sessionCookieToUser = new ConcurrentHashMap<>();
    private  Map<User, String> userToSessionCookie = new ConcurrentHashMap<>();

    public static AuthedUserListHolder getInstance(){
        return instance;
    }

    public void put(@NonNull String sessionCookie, @NonNull User user){
            sessionCookieToUser.put(sessionCookie, user);
            userToSessionCookie.put(user, sessionCookie);
            log.info("User " + user.getId() + " added.");
    }

    void remove(User user){
        String sessionCookie = userToSessionCookie.get(user);
        if(sessionCookie != null){
            sessionCookieToUser.remove(sessionCookie);
            userToSessionCookie.remove(user);
            log.info("User " + user.getId() + " removed.");
        }else{
            log.warn("User " + user.getId() + " already removed");
        }
    }

    public User getBySessionCookie(String sessionCookie){
        return sessionCookieToUser.get(sessionCookie);
    }

    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {
        if("currentUser".equals(event.getName())){
            final String sessionCookieValue = (String) event.getSession().getAttribute(PSF.COOKIE_NAME);
            if(sessionCookieValue == null){
                log.error(PSF.COOKIE_NAME + " not found in session attrs. It should be placed in attrs before all others");
                return;
            }
            final User currentUser = (User) event.getValue();
            put(sessionCookieValue, currentUser);
        }
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent event) {
        if("currentUser".equals(event.getName())){
            final User currentUser = (User) event.getValue();
            remove(currentUser);
        }
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent event) {
        if("currentUser".equals(event.getName())) {
            final String sessionCookieValue = (String) event.getSession().getAttribute(PSF.COOKIE_NAME);
            final User newCurrentUser = (User) event.getSession().getAttribute(event.getName());
            put(sessionCookieValue, newCurrentUser);
        }
    }
}
