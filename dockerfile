FROM java:8

COPY projetv0-assembly-0.1.jar /

COPY varenv.json /

ENV GOOGLE_APPLICATION_CREDENTIALS=varenv.json

CMD java -jar projetv0-assembly-0.1.jar
