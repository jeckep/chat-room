package com.jeckep.chat;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class Application {
    public static void main(String[] args) {

//        final JedisPool pool = new JedisPool(new JedisPoolConfig(), "redis");
//        final PSF psf = new PSF(new RedisSimplePersister(new JedisThreadSafeConnector(pool)))
//                .addEventListener(AuthedUserListHolder.getInstance());
//
//
////        enableDebugScreen();
//        before("*",                  psf.getBeforeFilter());
//
////        get("*",                     ViewUtil.notFound);
//
//        //Set up after-filters (called after each get/post)
//        after("*",                   psf.getAfterFilter());
//


        info();

        SpringApplication.run(Application.class, args);
    }



    public static void info(){
        log.info("freeMemory:" + Runtime.getRuntime().freeMemory()/1024/1204 + "Mb");
        log.info("totalMemory:" + Runtime.getRuntime().totalMemory()/1024/1204 + "Mb");
        log.info("maxMemory:" + Runtime.getRuntime().maxMemory()/1024/1204 + "Mb");
    }
}