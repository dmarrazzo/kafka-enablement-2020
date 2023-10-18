## Java build

mvn clean package

## OpenShift deployment

build and launch:

```sh
mkdir -p target/deploy
cp src/main/k8s/Dockerfile target/deploy
mv target/basic-consumer-1.0-SNAPSHOT-shaded.jar target/deploy/
oc new-build --strategy docker --binary --name=basic-consumer
oc start-build basic-consumer --from-dir target/deploy --follow
oc new-app -e KAFKA_BOOTSTRAP_SERVERS=europe-kafka-bootstrap:9092 -e KAFKA_GROUP_ID=basic-consumer -e TOPIC_PATTERN=topic-us-active basic-consumer
```

remove all:

```sh
oc delete all --selector="app=basic-consumer"
oc delete all --selector="deployment=basic-consumer"
oc delete all --selector="build=basic-consumer"
```