package com.jeckep.chat.controller;

import com.jeckep.chat.util.Path;
import com.jeckep.chat.util.ViewUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class PaymentsController {
    @GetMapping(Path.Web.PAYMENTS)
    public String index(Map<String, Object> model, HttpServletRequest request) {
        model.put("nav_active", "payments");
        ViewUtil.putLayoutVars(request, model);
        return Path.Template.PAYMENTS;
    }
}
