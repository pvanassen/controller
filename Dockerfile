FROM openjdk:8-alpine
EXPOSE 8080
COPY target/christmas-tree-controller-*.jar christmas-tree-controller.jar
ADD . target

ENTRYPOINT ["java -jar christmas-tree-controller.jar"]
