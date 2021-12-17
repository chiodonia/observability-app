#!/bin/sh
docker-compose exec kafka unset KAFKA_OPTS; unset KAFKA_JMX_OPTS; unset KAFKA_JMX_PORT; kafka-topics \
  --create \
  --bootstrap-server localhost:9091 \
  --replication-factor 1 \
  --partitions 6 \
  --topic app.Foo
