package com.jeckep.chat.controller;

import com.jeckep.chat.util.Path;
import com.jeckep.chat.util.ViewUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
@Controller
public class LoginController {
    @GetMapping(Path.Web.LOGIN)
    public String serveLoginPage(Map<String, Object> model, HttpServletRequest request) {
        ViewUtil.putLayoutVars(request, model);
        return Path.Template.LOGIN;
    }
}
