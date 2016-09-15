package com.jeckep.chat.util;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static com.jeckep.chat.util.RequestUtil.getCurrentUser;
import static com.jeckep.chat.util.RequestUtil.getSessionLocale;

public class ViewUtil {
    public static void putLayoutVars(HttpServletRequest request, Map<String, Object> model) {
        model.put("msg", new MessageBundle(getSessionLocale(request)));
        model.put("currentUser", getCurrentUser());
        model.put("WebPath", Path.Web.class); // Access application URLs from templates
        if(!model.containsKey("nav_active")){
            model.put("nav_active", "");
        }
    }
}
