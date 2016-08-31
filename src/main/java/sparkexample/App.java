package sparkexample;


import org.sql2o.Sql2o;
import org.sql2o.quirks.PostgresQuirks;
import redis.clients.jedis.Jedis;
import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;
import sparkexample.db.DB;
import sparkexample.db.DBImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static spark.Spark.get;
import static spark.Spark.staticFiles;

public class App {
    final static String[] messages = new String[]{
            "Thanks good sir! I'm feeling quite healthy!",
            "Thanks for the meal buddy.",
            "Please stop feeding me. I'm getting huge!"
    };


    public static void main(String[] args) {
        staticFiles.location("/static");

        //postgres
        //sql2o
        final Sql2o sql2o = new Sql2o("jdbc:postgresql://postgres:5432/mobydock",
                "mobydock", "yourpassword", new PostgresQuirks());
        DB db = new DBImpl(sql2o);

        Jedis jedis = new Jedis("redis");
        initRedis(jedis);

        Map model = new HashMap();
        model.put("feed_count", jedis.get("feed_count"));

        // layout.html file is in resources/templates directory
        get("/", (rq, rs) -> {
            return new ModelAndView(model, "layout.html");
        }, new MustacheTemplateEngine());

        get("/feed", (rq, rs) -> {
            jedis.incr("feed_count");
            model.put("feed_count", jedis.get("feed_count"));
            model.put("message", db.getAllMessages().get(new Random().nextInt(3)));
            return new ModelAndView(model, "layout.html");
        }, new MustacheTemplateEngine());

        get("/seed", (rq, rs) -> {
            for(String msg: messages){
                db.createMessage(msg);
            }
            return new ModelAndView(model, "layout.html");
        }, new MustacheTemplateEngine());
    }

    public static void initRedis(Jedis jedis){
        if(!jedis.exists("feed_count")){
            jedis.set("feed_count", "0");
        }
    }
}