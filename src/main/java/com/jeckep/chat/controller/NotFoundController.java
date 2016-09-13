package com.jeckep.chat.controller;

import com.jeckep.chat.util.Path;
import com.jeckep.chat.util.ViewUtil;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class NotFoundController implements ErrorController {

    private static final String PATH = "/error";

    @RequestMapping(PATH)
    public String index(Map<String, Object> model, HttpServletRequest request) {
        // TODO it is very ood, becouse request.session does not contain session attributes.
        // To reproduce: login, go to not existing url
        ViewUtil.putLayoutVars(request, model);
        return Path.Template.NOT_FOUND;
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }
}
