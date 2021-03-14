FROM openjdk:8

COPY --from=gcloud gs://jar-files-pfe/projetv0_2.12-0.1.jar /

COPY --from=gcloud  gs://jar-files-pfe/varenv.json /

ENV GOOGLE_APPLICATION_CREDENTIALS=varenv.json

CMD java -jar projetv0-assembly-0.1.jar
