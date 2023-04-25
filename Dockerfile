FROM payara/server-full:6.2023.2-jdk17

EXPOSE 4848
EXPOSE 8080
EXPOSE 8181
EXPOSE 9009

COPY target/cargo-tracker.war $DEPLOY_DIR
