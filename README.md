Example webapp with chatroom
============================

It's a web application that utilize a lot of cool frameworks. It can be used as a starting point or a template to create new web application based on it.
Main features are: chatroom based on websockets, messages are stored in postgres, sessions are stored in redis, login via Google, VK, Linkedin using OAuth2, responsive bootstrap design, localisation.

Dependencies
------------
* Web framework: [spark](https://github.com/perwendel/spark)
* Template engine: [velocity](http://velocity.apache.org/)
* DB migration: [flywaydb](https://flywaydb.org/)
* DBMS: [postgres](https://www.postgresql.org/)
* DBMS client: [sql2o](https://github.com/aaberg/sql2o) 
* Session storage: [redis](http://redis.io/)
* Redis client: [jedis](https://github.com/xetorthio/jedis)
* OAuth2 library: [scribejava](https://github.com/scribejava/scribejava)
* Java additions: [lombok](https://projectlombok.org/)
* Development envs: [docker](https://github.com/docker/docker) and [docker-compose](https://github.com/docker/compose)

Deployment
----------

To deploy app as docker container, with other components(nginx, postgres, redis) use instructions and deploy scripts [here](https://github.com/jeckep/chat-room-deploy-scripts)

