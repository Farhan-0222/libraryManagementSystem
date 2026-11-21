# Build the existing Java 11 Maven WAR project.
FROM maven:3.9-eclipse-temurin-11 AS build
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn -B clean package -DskipTests

# Run the WAR on the project's required Tomcat 9 / javax.servlet runtime.
FROM tomcat:9.0-jdk11-temurin
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /build/target/LibraryManagementSystem.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
# Render supplies PORT. Tomcat's HTTP connector must listen on that port.
CMD sed -i "s/port=\"8080\"/port=\"${PORT:-8080}\"/" /usr/local/tomcat/conf/server.xml && catalina.sh run
