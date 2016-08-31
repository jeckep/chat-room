package com.jeckep.chat;


import com.jeckep.chat.constants.Envs;
import com.jeckep.chat.db.DB;
import com.jeckep.chat.db.DBImpl;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.websocket.api.Session;
import org.flywaydb.core.Flyway;
import org.json.JSONObject;
import org.sql2o.Sql2o;
import org.sql2o.quirks.PostgresQuirks;
import redis.clients.jedis.Jedis;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.mustache.MustacheTemplateEngine;

import java.text.SimpleDateFormat;
import java.util.*;

import static j2html.TagCreator.*;
import static spark.Spark.*;

@Slf4j
public class Chat {
    static Map<Session, String> userUsernameMap = new HashMap<>();
    static int nextUserNumber = 1; //Assign to username for next connecting user
    static List<Message> messageHistory = new ArrayList<>();
    private static final String DB_URL = System.getenv(Envs.DB_URL);

    public static void main(String[] args) {
        migrateDB();
        final Jedis jedis = initRedis();
        final DB db = initDB();
        final Map model = initModel(jedis);

        staticFiles.location("/static");
        staticFiles.expireTime(600);
        webSocket("/chat", ChatWebSocketHandler.class);
        init();


        get("/",          (req, res) -> renderMobydock(model));
        get("/feed", (ICRoute) (req) -> incrCountAndUpdateMessage(jedis,model,db));
        get("/seed", (ICRoute) (req) -> populateDB(db));
        get("/chatroom", (req, res)  -> new ModelAndView(new HashMap(), "chatroom.html"), new MustacheTemplateEngine());

        after((req, res) -> {
            if (res.body() == null
                && !req.pathInfo().startsWith("/chat")) { // if we didn't try to return a rendered response
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

    //Sends a message from one user to all users, along with a list of current usernames
    public static void broadcastMessage(Message message) {
        messageHistory.add(message);
        userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
            try {
                session.getRemote().sendString(String.valueOf(new JSONObject()
                        .put("userMessage", createHtmlMessageFromSender(message))
                        .put("userlist", userUsernameMap.values())
                ));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    //Builds a HTML element with a sender-name, a message, and a timestamp,
    private static String createHtmlMessageFromSender(Message message) {
        return article().with(
                b(message.getSender() + " says:"),
                p(message.getMessage()),
                span().withClass("timestamp").withText(new SimpleDateFormat("HH:mm:ss").format(message.getTimestamp()))
        ).render();
    }

    public static void sendMessageHistoryToNewUser(Session user) {
        messageHistory.stream().sorted((a,b) -> a.getTimestamp().compareTo(b.timestamp)).forEach( message -> {
            try {
                user.getRemote().sendString(String.valueOf(new JSONObject()
                        .put("userMessage", createHtmlMessageFromSender(message))
                        .put("userlist", userUsernameMap.values())
                ));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}