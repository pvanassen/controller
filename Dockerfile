FROM oracle/graalvm-ce:1.0.0-rc10
EXPOSE 8080
COPY target/christmas-tree-controller-*.jar christmas-tree-controller.jar
ADD . target

ENTRYPOINT ["java", "-jar", "christmas-tree-controller.jar"]
