FROM openjdk:11
LABEL maintainer=himzotahic@gmail.com
COPY ./target/scala-2.12/$name;format="lower,hyphen"$.jar /opt/docker/$name;format="lower,hyphen"$.jar
WORKDIR /opt/docker
RUN ["chown", "-R", "root:root", "."]
EXPOSE 8080
ENV ENVIRONMENT=dev
CMD java -Xss4m \$(java-dynamic-memory-opts 80) -jar $name;format="lower,hyphen"$.jar
