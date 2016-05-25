# Kitodo.Production

# This Dockerfile needs kitodo.war (copy from goobi-ce-*.war) and
# scripts (copy from the scripts directory of Kitodo.Production).

# Compile it like this:
# docker build -t production .

# Start MariaDB, create a database and fill it with default values.
# docker run --detach=true --name kitodo-database kitodo/database:latest

# Start Kitodo.Production.
# docker run -it --rm -p 8888:8080 --name kitodo-production --link kitodo-database:mysql kitodo/production

# Useful links:
# https://docs.docker.com/engine/reference/builder/
# https://docs.docker.com/engine/userguide/eng-image/dockerfile_best-practices/
# https://hub.docker.com/_/tomcat/

# tomcat:7-jre7 fails because of unresolved symbols.
#FROM tomcat:7-jre7
FROM tomcat:8-jre8

MAINTAINER Stefan Weil <stefan.weil@bib.uni-mannheim.de>

# Add the web application (modified to support docker).
ADD kitodo.war /usr/local/tomcat/webapps/kitodo.war

# Add the script files.
ADD scripts /usr/local/goobi/scripts

# Unpack the Kitodo war file because we must patch the configuration later.
RUN /usr/bin/unzip /usr/local/tomcat/webapps/kitodo.war -d /usr/local/tomcat/webapps/kitodo

EXPOSE 8080

# Fix database URL in Kitodo configuration before starting Tomcat.
CMD /bin/sed -i s/localhost/$MYSQL_PORT_3306_TCP_ADDR:$MYSQL_PORT_3306_TCP_PORT/ /usr/local/tomcat/webapps/kitodo/WEB-INF/classes/hibernate.cfg.xml && \
    catalina.sh run

# CMD ["catalina.sh", "run"]
