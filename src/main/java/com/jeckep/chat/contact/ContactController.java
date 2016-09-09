package com.jeckep.chat.contact;

import com.jeckep.chat.util.Path;
import com.jeckep.chat.util.ViewUtil;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;
import java.util.Map;


public class ContactController {
    public static Route serveContactPage = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();
        model.put("nav_active", "contact");
        return ViewUtil.render(request, model, Path.Template.CONTACT);
    };
}
