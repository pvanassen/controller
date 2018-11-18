FROM oracle/graalvm-ce:1.0.0-rc8
EXPOSE 8080
COPY target/christmas-tree-controller-*.jar christmas-tree-controller.jar
ADD . target
RUN java -cp christmas-tree-controller.jar io.micronaut.graal.reflect.GraalClassLoadingAnalyzer \
    && native-image --no-server \
             --class-path christmas-tree-controller.jar \
             -H:ReflectionConfigurationFiles=target/reflect.json \
             -H:EnableURLProtocols=http \
             -H:IncludeResources="logback.xml|application.yml|META-INF/services/*.*" \
             -H:Name=christmas-tree-controller \
             -H:Class=nl.pvanassen.christmas.tree.controller.Application \
             -H:+ReportUnsupportedElementsAtRuntime \
             -H:+AllowVMInspection \
             -H:-UseServiceLoaderFeature \
             --rerun-class-initialization-at-runtime='sun.security.jca.JCAUtil$CachedSecureRandomHolder,javax.net.ssl.SSLContext' \
             --delay-class-initialization-to-runtime=io.netty.handler.codec.http.HttpObjectEncoder,io.netty.handler.codec.http.websocketx.WebSocket00FrameEncoder,io.netty.handler.ssl.util.ThreadLocalInsecureRandom,com.sun.jndi.dns.DnsClient
RUN mkdir /config && \
    touch /config/application-docker.yml

ENTRYPOINT ["./christmas-tree-controller", "-Dmicronaut.config.files=/config/application-docker.yml"]
