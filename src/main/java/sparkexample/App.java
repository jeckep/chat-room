package sparkexample;


import org.flywaydb.core.Flyway;
import org.sql2o.Sql2o;
import org.sql2o.quirks.PostgresQuirks;
import redis.clients.jedis.Jedis;
import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;
import sparkexample.constants.Envs;
import sparkexample.db.DB;
import sparkexample.db.DBImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static spark.Spark.get;
import static spark.Spark.staticFiles;

public class App {
    public static final String DB_URL = System.getenv(Envs.DB_URL);

    public static void main(String[] args) {
        migrateDB();
        final Jedis jedis = initRedis();
        final DB db = initDB();
        final Map model = initModel(jedis);

        staticFiles.location("/static");

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
            populateDB(db);
            return new ModelAndView(model, "layout.html");
        }, new MustacheTemplateEngine());
    }

    public static Jedis initRedis(){
        Jedis jedis =  new Jedis("redis");
        if(!jedis.exists("feed_count")){
            jedis.set("feed_count", "0");
        }
        return jedis;
    }

    public static DB initDB(){
        Sql2o sql2o = new Sql2o(DB_URL,
                System.getenv(Envs.DB_USER), System.getenv(Envs.DB_PASSWORD), new PostgresQuirks());
        return new DBImpl(sql2o);
    }

    public static Map initModel(Jedis jedis){
        Map model = new HashMap();
        model.put("feed_count", jedis.get("feed_count"));
        return model;
    }

    public static void populateDB(DB db){
        final String[] messages = new String[]{
                "Thanks good sir! I'm feeling quite healthy!",
                "Thanks for the meal buddy.",
                "Please stop feeding me. I'm getting huge!"
        };

        for(String msg: messages){
            db.createMessage(msg);
        }
    }

    public static void migrateDB(){
        Flyway flyway = new Flyway();
        flyway.setDataSource(DB_URL, System.getenv(Envs.DB_USER), System.getenv(Envs.DB_PASSWORD));
        flyway.migrate();
    }
}