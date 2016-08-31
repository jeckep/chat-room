package sparkexample;


import org.flywaydb.core.Flyway;
import org.sql2o.Sql2o;
import org.sql2o.quirks.PostgresQuirks;
import redis.clients.jedis.Jedis;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.mustache.MustacheTemplateEngine;
import sparkexample.constants.Envs;
import sparkexample.db.DB;
import sparkexample.db.DBImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static spark.Spark.after;
import static spark.Spark.get;
import static spark.Spark.staticFiles;

public class App {
    private static final String DB_URL = System.getenv(Envs.DB_URL);

    public static void main(String[] args) {
        migrateDB();
        final Jedis jedis = initRedis();
        final DB db = initDB();
        final Map model = initModel(jedis);

        staticFiles.location("/static");

        get("/",          (req, res) -> renderMobydock(model));
        get("/feed", (ICRoute) (req) -> incrCountAndUpdateMessage(jedis,model,db));
        get("/seed", (ICRoute) (req) -> populateDB(db));

        after((req, res) -> {
            if (res.body() == null) { // if we didn't try to return a rendered response
                res.body(renderMobydock(model));
            }
        });
    }

    private static Jedis initRedis(){
        Jedis jedis =  new Jedis("redis");
        if(!jedis.exists("feed_count")){
            jedis.set("feed_count", "0");
        }
        return jedis;
    }

    private static DB initDB(){
        Sql2o sql2o = new Sql2o(DB_URL,
                System.getenv(Envs.DB_USER), System.getenv(Envs.DB_PASSWORD), new PostgresQuirks());
        return new DBImpl(sql2o);
    }

    private static Map initModel(Jedis jedis){
        Map model = new HashMap();
        model.put("feed_count", jedis.get("feed_count"));
        return model;
    }

    private static void populateDB(DB db){
        final String[] messages = new String[]{
                "Thanks good sir! I'm feeling quite healthy!",
                "Thanks for the meal buddy.",
                "Please stop feeding me. I'm getting huge!"
        };

        for(String msg: messages){
            db.createMessage(msg);
        }
    }

    private static void migrateDB(){
        Flyway flyway = new Flyway();
        flyway.setDataSource(DB_URL, System.getenv(Envs.DB_USER), System.getenv(Envs.DB_PASSWORD));
        flyway.migrate();
    }

    private static String renderMobydock(Map model) {
        return renderTemplate("layout.html", model);
    }

    public static void incrCountAndUpdateMessage(Jedis jedis, Map model, DB db){
        jedis.incr("feed_count");
        model.put("feed_count", jedis.get("feed_count"));
        model.put("message", db.getAllMessages().get(new Random().nextInt(3)));
    }

    private static String renderTemplate(String template, Map model) {
        return new MustacheTemplateEngine().render(new ModelAndView(model, template));
    }

    @FunctionalInterface
    private interface ICRoute extends Route {
        default Object handle(Request request, Response response) throws Exception {
            handle(request);
            return "";
        }
        void handle(Request request) throws Exception;
    }
}