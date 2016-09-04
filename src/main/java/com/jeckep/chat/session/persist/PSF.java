package com.jeckep.chat.session.persist;


import org.eclipse.jetty.server.session.AbstractSession;
import spark.Filter;
import spark.Request;
import spark.Response;

import javax.servlet.http.Cookie;
import java.util.*;

/*
* Persist session filter.
* As there is no way to modify configuration of embedded jetty
* related to session manager to use another session manager.
* The only option is to create custom filter, use own cookie
* instead of JSESSIONID cookie
*
* */

public class PSF {
    public static final String COOKIE_NAME = "PERSISTENT_SESSION";
    private static PSF instance;

    private Persister persister;
    private List<EventListener> listeners = new ArrayList<>();
    private int expire = 20 * 60; //sec = 20min
    private boolean secure = false; //cookie should be passed only over https

    public void init(){
        if(persister == null){
            throw new IllegalStateException("Persister must be initialised");
        }
        instance = this;
    }

    public static Filter beforeFilter = (Request request, Response response) -> {
        //as it is a before all filter, then event listeners must registered here, if you have them
        if(request.session().isNew()){
            for(EventListener l: instance.listeners){
                // even it is wrong to try add bean for every user, but it is the only way to do it
                // it will not be added more than once two list, because it is checked by ContainerLifeCycle#contains(l)
                ((AbstractSession)request.session().raw()).getSessionManager().addEventListener(l);
            }
        }

        Cookie sessionCookie = getCookie(request, COOKIE_NAME);
        if(sessionCookie == null){
            sessionCookie = instance.genCookie();
            //save cookie value in session to use it in after-filter
            request.session().attribute(COOKIE_NAME, sessionCookie.getValue());
        }else{
            //save cookie value in session to use it in after-filter
            //session cookie should be saved to session first!
            request.session().attribute(COOKIE_NAME, sessionCookie.getValue());
            Map<String, Object> attrs = instance.persister.restore(sessionCookie.getValue(), instance.expire);
            for(String key: attrs.keySet()){
                request.session().attribute(key, attrs.get(key));
            }
        }


        //add cookie on every response, to update expire time
        response.raw().addCookie(instance.genCookie(sessionCookie.getValue()));
    };

    public static Filter afterFilter = (Request request, Response response) -> {
        String sessionCookieValue = request.session().attribute(COOKIE_NAME);
        Map<String, Object> attrs = new HashMap<>();
        for(String key: request.session().attributes()){
            if(COOKIE_NAME.equals(key)) continue;
            attrs.put(key, request.session().attribute(key));
        }
        instance.persister.save(sessionCookieValue, attrs, instance.expire);
    };

    private  Cookie genCookie(){
        return genCookie(UUID.randomUUID().toString());
    }

    private  Cookie genCookie(String cookieValue){
        Cookie cookie = new Cookie(COOKIE_NAME, cookieValue);
        cookie.setHttpOnly(true);
        cookie.setSecure(secure);
        cookie.setMaxAge(expire);
        cookie.setPath("/");
        return cookie;
    }

    private static Cookie getCookie(Request req, String name){
        for(Cookie cookie: req.raw().getCookies()){
            if(name.equals(cookie.getName())){
                return cookie;
            }
        }
        return null;
    }

    public PSF setPersister(Persister persister) {
        this.persister = persister;
        return this;
    }

    public PSF setExpire(int expire) {
        this.expire = expire;
        return this;
    }

    public PSF setSecure(boolean secure) {
        this.secure = secure;
        return this;
    }

    public PSF addEventListener(EventListener listener){
        listeners.add(listener);
        return this;
    }
}
