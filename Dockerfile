FROM eclipse-temurin:17-focal

ARG JAR_FILE=build/libs/demo-0.0.1-SNAPSHOT.jar
COPY $JAR_FILE app.jar
ENTRYPOINT ["java","-jar","/app.jar", "--spring.profiles.active=prod"]

# EXPOSE 8080
#
# CMD [\
#     "/bin/bash", "-c",\
#     "chmod +x set_prod_env.sh && source set_prod_env.sh && java -jar app.jar"\
# ]
