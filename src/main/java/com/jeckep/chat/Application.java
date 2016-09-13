package com.jeckep.chat;


import com.jeckep.chat.env.Envs;
import com.jeckep.chat.message.MsgDao;
import com.jeckep.chat.user.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class Application {
    public static UserDao userDao;
    public static MsgDao msgDao;

    public static void main(String[] args) {
        userDao = new UserDao();
        msgDao = new MsgDao();

//        final JedisPool pool = new JedisPool(new JedisPoolConfig(), "redis");
//        final PSF psf = new PSF(new RedisSimplePersister(new JedisThreadSafeConnector(pool)))
//                .addEventListener(AuthedUserListHolder.getInstance());
//
//
////        enableDebugScreen();
//        before("*",                  psf.getBeforeFilter());
//        before("*",                  Filters.handleLocaleChange);
//
////        get("*",                     ViewUtil.notFound);
//
//        //Set up after-filters (called after each get/post)
//        after("*",                   Filters.addGzipHeader);
//        after("*",                   psf.getAfterFilter());
//
//        exception(Exception.class, (exception, request, response) -> {
//            log.error("Unhandled exception",exception);
//        });

        info();

        SpringApplication.run(Application.class, args);
    }



    public static void info(){
        log.info("freeMemory:" + Runtime.getRuntime().freeMemory()/1024/1204 + "Mb");
        log.info("totalMemory:" + Runtime.getRuntime().totalMemory()/1024/1204 + "Mb");
        log.info("maxMemory:" + Runtime.getRuntime().maxMemory()/1024/1204 + "Mb");
    }
}