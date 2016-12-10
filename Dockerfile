FROM java:8
MAINTAINER Yevgeniy Poluektov <yevgeniy.v.poluektov@gmail.com>

# Install maven
RUN apt-get update && apt-get install -y maven

ENV INSTALL_PATH /mobydock
RUN mkdir -p $INSTALL_PATH

WORKDIR $INSTALL_PATH


# Prepare by downloading dependencies
ADD pom.xml /$INSTALL_PATH/pom.xml
RUN ["mvn", "dependency:resolve"]
RUN ["mvn", "verify"]

# Adding source, compile and package into a fat jar
ADD src /$INSTALL_PATH/src
RUN ["mvn", "package"]

VOLUME ["$INSTALL_PATH/src/main/resources/static"]


EXPOSE 4567
CMD ["/usr/lib/jvm/java-8-openjdk-amd64/bin/java","-Xmx1024m", "-jar", "target/sparkexample-jar-with-dependencies.jar"]
