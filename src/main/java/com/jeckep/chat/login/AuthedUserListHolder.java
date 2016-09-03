package com.jeckep.chat.login;


import com.jeckep.chat.user.User;
import com.jeckep.chat.util.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import spark.Request;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class AuthedUserListHolder {
    private static Map<String, User> jsessionidToUser = new ConcurrentHashMap<>();
    private static Map<User, String> userToJsessionid = new ConcurrentHashMap<>();

    public static void put(Request request, User user){
        String jsessionid = RequestUtil.getJsessionid(request);
        put(jsessionid, user);
    }

    public static void put(String jsessionid, User user){
        if(jsessionid != null){
            jsessionidToUser.put(jsessionid, user);
            userToJsessionid.put(user, jsessionid);
        }else{
            log.warn("JSESSIONID not found in cookies");
        }
        log.info("User " + user.getId() + " added.");
    }

    static void remove(User user){
        String jsessionid = userToJsessionid.get(user);
        if(jsessionid != null){
            jsessionidToUser.remove(jsessionid);
            userToJsessionid.remove(user);
        }else{
            log.warn("User " + user.getId() + " already removed");
        }
        log.info("User " + user.getId() + " removed.");
    }

    public static User getByJsessionid(String jsessionid){
        return jsessionidToUser.get(jsessionid);
    }


}
