package com.jeckep.chat.login;

import com.jeckep.chat.Application;
import com.jeckep.chat.user.*;
import com.jeckep.chat.util.*;
import com.jeckep.chat.util.Path;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;
import java.util.Map;

import static com.jeckep.chat.util.RequestUtil.*;

public class LoginController {

    public static Route serveLoginPage = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();
        model.put("loggedOut", removeSessionAttrLoggedOut(request));
        model.put("loginRedirect", removeSessionAttrLoginRedirect(request));
        return ViewUtil.render(request, model, Path.Template.LOGIN);
    };

    public static Route handleLoginPost = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();
        if (!UserController.authenticate(getQueryUsername(request), getQueryPassword(request))) {
            model.put("authenticationFailed", true);
            return ViewUtil.render(request, model, Path.Template.LOGIN);
        }
        model.put("authenticationSucceeded", true);

        //user authenticated
        //set currentUserToSession
        request.session().attribute("currentUser", getQueryUsername(request));
        AuthedUserListHolder.put(request, Application.userDao.getUserByUsername(getQueryUsername(request)));
        if (getQueryLoginRedirect(request) != null) {
            response.redirect(getQueryLoginRedirect(request));
        }
        return ViewUtil.render(request, model, Path.Template.LOGIN);
    };

    public static Route handleLogoutPost = (Request request, Response response) -> {
        AuthedUserListHolder.remove(Application.userDao.getUserByUsername(request.session().attribute("currentUser")));
        request.session().removeAttribute("currentUser");
        request.session().attribute("loggedOut", true);
        response.redirect(Path.Web.LOGIN);
        return null;
    };

    // The origin of the request (request.pathInfo()) is saved in the session so
    // the user can be redirected back after login
    public static void ensureUserIsLoggedIn(Request request, Response response) {
        if (request.session().attribute("currentUser") == null) {
            request.session().attribute("loginRedirect", request.pathInfo());
            response.redirect(Path.Web.LOGIN);
        }
    };

}
