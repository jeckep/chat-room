package com.jeckep.chat.login;

import com.jeckep.chat.Application;
import com.jeckep.chat.login.oauth.OAuth;
import com.jeckep.chat.user.IUser;
import com.jeckep.chat.user.User;
import com.jeckep.chat.util.Path;
import com.jeckep.chat.util.ViewUtil;
import lombok.extern.slf4j.Slf4j;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;
import java.util.Map;

import static com.jeckep.chat.util.RequestUtil.*;

@Slf4j
public class LoginController {

    public static Route serveLoginPage = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();
        model.put("loggedOut", removeSessionAttrLoggedOut(request));
        model.put("loginRedirect", removeSessionAttrLoginRedirect(request));
        return ViewUtil.render(request, model, Path.Template.LOGIN);
    };

    public static Route handleLogout = (Request request, Response response) -> {
        request.session().removeAttribute("currentUser");
        request.session().attribute("loggedOut", true);
        response.redirect(Path.Web.INDEX);
        return null;
    };

    public static Route handleLoginOAuth2 = (Request request, Response response) -> {
        final String url = OAuth.getAuthorizationUrl(request.params("service"));
        response.redirect(url);
        return null;
    };

    public static Route handleCallbackOAuth2 = (Request request, Response response) -> {
        final Map<String, Object> model = new HashMap<>();
        final String code = request.queryParams("code");
        if(code == null){
            //There are two options: user refused to grant permissions or we made incorrect auth request
            // TODO handle second option
            response.redirect(Path.Web.LOGIN);
            return null;
        }
        final IUser iuser = OAuth.service(request.params("service")).retriveInfo(code);
        final User user = Application.userDao.findOrCreate(iuser);

        //user authenticated
        //set currentUser To Session. If user is in the session it means that they are logged in
        request.session().attribute("currentUser", user);
        if (getQueryLoginRedirect(request) != null) {
            response.redirect(getQueryLoginRedirect(request));
        }
        return ViewUtil.render(request, model, Path.Template.LOGIN);
    };

    // The origin of the request (request.pathInfo()) is saved in the session so
    // the user can be redirected back after login
    public static void ensureUserIsLoggedIn(Request request, Response response) {
        if (request.session().attribute("currentUser") == null) {
            request.session().attribute("loginRedirect", request.pathInfo());
            response.redirect(Path.Web.LOGIN);
        }
    }
}
