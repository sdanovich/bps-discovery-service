FROM openjdk:8u181-jre
VOLUME /tmp
ENV PORT 9980
ADD docker/vault.dev.ssl.cert vault.dev.ssl.cert
RUN $JAVA_HOME/bin/keytool -keystore /etc/ssl/certs/java/cacerts -storepass changeit -noprompt -trustcacerts -importcert -alias vault.dev -file vault.dev.ssl.cert

ARG JAR_FILE
ADD target/${JAR_FILE} app.jar
RUN sh -c 'touch /app.jar'
EXPOSE $PORT

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-jar","/app.jar"]
