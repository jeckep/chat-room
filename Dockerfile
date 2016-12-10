FROM mobydock:forspped
MAINTAINER Yevgeniy Poluektov <yevgeniy.v.poluektov@gmail.com>

# Install maven
RUN apt-get update && apt-get install -y maven

ENV INSTALL_PATH /mobydock
RUN mkdir -p $INSTALL_PATH

WORKDIR $INSTALL_PATH

#Clean
RUN rm -rf /$INSTALL_PATH/pom.xml /$INSTALL_PATH/src
# Adding source, compile and package into a fat jar
ADD pom.xml /$INSTALL_PATH/pom.xml
ADD src /$INSTALL_PATH/src
RUN ["mvn", "package"]

VOLUME ["$INSTALL_PATH/src/main/resources/static"]


EXPOSE 4567
CMD ["/srv/java/jdk1.8.0_112/bin/java","-Xmx1024m", "-jar", "target/chat-1.0-SNAPSHOT.jar"]
