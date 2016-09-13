package com.jeckep.chat.controller;

import com.jeckep.chat.util.Path;
import com.jeckep.chat.util.ViewUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class IndexController {

    @GetMapping(Path.Web.INDEX)
    public String index(Map<String, Object> model, HttpServletRequest request) {
        model.put("nav_active", "home");
        ViewUtil.putLayoutVars(request, model);
        return Path.Template.INDEX;
    }
}
