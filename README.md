Example webapp with chatroom
============================

It's a web application that utilize a lot of cool frameworks. It can be used as a starting point or a template to create new web application based on it.
Main features are: chatroom based on websockets, messages are stored in postgres, sessions are stored in redis, login via Google, VK, Linkedin, FB, Github using OAuth2, responsive bootstrap design, localisation.

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

Live instance
-------------

https://jeckep.online/

Hosted on digitalocean.com droplet: 512MB / 1 CPU / 20GB SSD

<pre>
wrk -t12 -c400 -d30s https://jeckep.online
Running 30s test @ https://jeckep.online
  12 threads and 400 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   791.85ms  479.19ms   1.99s    60.40%
    Req/Sec    27.63     21.61   190.00     73.08%
  8010 requests in 30.10s, 26.95MB read
  Socket errors: connect 0, read 0, write 0, timeout 888
Requests/sec:    266.14
Transfer/sec:      0.90MB
</pre>

