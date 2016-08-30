package sparkexample;


import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static spark.Spark.get;
import static spark.Spark.staticFiles;

public class Hello {
    final static String[] messages = new String[]{
            "Thanks good sir! I'm feeling quite healthy!",
            "Thanks for the meal buddy.",
            "Please stop feeding me. I'm getting huge!"
    };


    public static void main(String[] args) {
        staticFiles.location("/static");

        Map model = new HashMap();
        model.put("feed_count", 0);

        // layout.html file is in resources/templates directory
        get("/", (rq, rs) -> {
            return new ModelAndView(model, "layout.html");
        }, new MustacheTemplateEngine());

        get("/feed", (rq, rs) -> {
            model.put("feed_count", ((int)model.get("feed_count")) + 1);
            model.put("message", messages[new Random().nextInt(3)]);
            return new ModelAndView(model, "layout.html");
        }, new MustacheTemplateEngine());
    }
}