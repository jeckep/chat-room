package com.jeckep.chat.index;

import com.jeckep.chat.util.*;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;
import java.util.Map;


public class IndexController {
    public static Route serveIndexPage = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();
        model.put("nav_active", "nav_home");
        return ViewUtil.render(request, model, Path.Template.INDEX);
    };
}
