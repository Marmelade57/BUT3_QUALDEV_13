# Use Tomcat 9 with Java 17
FROM tomcat:9.0.85-jdk17-temurin-jammy

# Install necessary tools
RUN apt-get update && apt-get install -y \
    iputils-ping \
    mysql-client \
    && rm -rf /var/lib/apt/lists/*

# Create logs directory and set permissions
RUN mkdir -p /usr/local/tomcat/logs && \
    chmod -R 777 /usr/local/tomcat/logs && \
    touch /usr/local/tomcat/logs/banque.log && \
    touch /usr/local/tomcat/logs/banque-app.log && \
    chmod 666 /usr/local/tomcat/logs/*.log

# Remove default ROOT application
RUN rm -rf /usr/local/tomcat/webapps/ROOT

# Download required dependencies
RUN mkdir -p /tmp/deps && \
    cd /tmp/deps && \
    curl -O https://repo1.maven.org/maven2/org/freemarker/freemarker/2.3.31/freemarker-2.3.31.jar && \
    curl -O https://repo1.maven.org/maven2/ognl/ognl/3.2.21/ognl-3.2.21.jar && \
    curl -O https://repo1.maven.org/maven2/org/apache/struts/struts2-core/2.3.37/struts2-core-2.3.37.jar

# Copy the WAR file to Tomcat's webapps directory
COPY target/_00_ASBank2025-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

# Add logging configuration
ENV CATALINA_OPTS="-Dlog4j.configuration=file:/usr/local/tomcat/conf/log4j.properties -Dlog4j.debug=true"

# Copy log4j configuration files
COPY src/main/resources/log4j.properties /usr/local/tomcat/conf/
COPY src/main/resources/log4j2.xml /usr/local/tomcat/conf/

# Expose port 8080
EXPOSE 8080

# Start Tomcat with debugging output
CMD ["catalina.sh", "run"]