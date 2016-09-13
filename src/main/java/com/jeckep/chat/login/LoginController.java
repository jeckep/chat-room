package com.jeckep.chat.login;

import com.jeckep.chat.login.oauth.OAuth;
import com.jeckep.chat.model.IUser;
import com.jeckep.chat.model.User;
import com.jeckep.chat.repository.UserService;
import com.jeckep.chat.util.Path;
import com.jeckep.chat.util.ViewUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

import static com.jeckep.chat.util.MvcUtil.redirect;

@Slf4j
@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @GetMapping(Path.Web.LOGIN)
    public String serveLoginPage(Map<String, Object> model, HttpServletRequest request) {
        ViewUtil.putLayoutVars(request, model);
        return Path.Template.LOGIN;
    }

    @GetMapping(Path.Web.LOGOUT)
    public String serveLogOut(HttpServletRequest request) {
        request.getSession().removeAttribute("currentUser");
        request.getSession().setAttribute("loggedOut", true);
        return redirect(Path.Web.INDEX);
    }

    @GetMapping(Path.Web.LOGIN_AUTH2)
    public String handleLoginOAuth2(HttpServletRequest request, @PathVariable String service) {
        final String url = OAuth.getAuthorizationUrl(service);
        return redirect(url);
    }

    @GetMapping(Path.Web.OAUTH2_CALLBACK)
    public String handleCallbackOAuth2(Map<String, Object> model, HttpServletRequest request, @PathVariable String service) throws IOException {
        final String code = request.getParameter("code");
        if(code == null){
            //There are two options: user refused to grant permissions or we made incorrect auth request
            // TODO handle second option
            return  redirect(Path.Web.LOGIN);
        }
        final IUser iuser = OAuth.service(service).retriveInfo(code);
        final User user = userService.findOrCreate(iuser);

        //user authenticated
        //set currentUser To Session. If user is in the session it means that they are logged in
        request.getSession().setAttribute("currentUser", user);

        String redirectUrl = (String) request.getSession().getAttribute("loginRedirect");
        if(redirectUrl != null){
            request.getSession().removeAttribute("loginRedirect");
            return redirect(redirectUrl);
        }
        ViewUtil.putLayoutVars(request, model);
        return Path.Template.INDEX;
    }


    // The origin of the request (request.pathInfo()) is saved in the session so
    // the user can be redirected back after login
    public static boolean ensureUserIsLoggedIn(HttpServletRequest request) {
        if (request.getSession().getAttribute("currentUser") == null) {
            request.getSession().setAttribute("loginRedirect", request.getRequestURI());
            return false;
        }else{
            return true;
        }
    }
}
