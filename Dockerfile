FROM openjdk:17
ARG JAR_FILE_PATH=build/libs/*.jar
COPY ${JAR_FILE_PATH} magnet-0.0.1.jar
COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh
ENTRYPOINT ["/wait-for-it.sh", "database:3306", "--", "java","-jar","/magnet-0.0.1.jar"]