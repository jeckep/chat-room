package com.jeckep.chat.contact;

import com.jeckep.chat.util.Path;
import com.jeckep.chat.util.ViewUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class ContactController {
    @GetMapping(Path.Web.CONTACT)
    public String index(Map<String, Object> model, HttpServletRequest request) {
        model.put("nav_active", "contact");
        ViewUtil.putLayoutVars(request, model);
        return Path.Template.CONTACT;
    }
}
