# Observability

## Build
```
mvn clean install
```

## Run
http://localhost:8080/produce?records=1000&size=1024&delay=1000
http://localhost:8090/consume?polls=100&duration=100&delay=1000
http://localhost:8080/actuator/prometheus
http://localhost:8090/actuator/prometheus

bootstrap.server=localhost:9094
http://localhost:3000
http://localhost:9090
http://localhost:8000
http://localhost:9411
http://localhost:16686

## Anomaly detection
https://about.gitlab.com/blog/2019/07/23/anomaly-detection-using-prometheus/

### Producers
```
# HELP kafka_producer_record_send_total The total number of records sent.
# TYPE kafka_producer_record_send_total counter
kafka_producer_record_send_total{client_id="app.Producer",kafka_version="3.0.0",} 54.0
```
#### Using z-score for anomaly detection
z-scores works only with normally distributed data. Testing your data for normal distribution:
```
(max_over_time(job:kafka_producer_record_send:rate1m[1h]) - avg_over_time(job:kafka_producer_record_send:rate1m[1h])) / stddev_over_time(job:kafka_producer_record_send:rate1m[1h])
(min_over_time(job:kafka_producer_record_send:rate1m[1h]) - avg_over_time(job:kafka_producer_record_send:rate1m[1h])) / stddev_over_time(job:kafka_producer_record_send:rate1m[1h])
```
z-score should be roughly between [-4, +4]

```
(job:kafka_producer_record_send:rate1m - job:kafka_producer_record_send:rate1m:avg_over_time_1h) /  job:kafka_producer_record_send:rate1m:stddev_over_time_1h
```
We can assume that any value that falls outside roughly [-3, +3] is an anomaly!

### Consumers
```
kafka_consumer_fetch_manager_records_consumed_total{client_id="app.Consumer",kafka_version="3.0.0",topic="app_Foo",} 340.0

kafka_consumer_fetch_manager_records_lag_max{client_id="app.Consumer",kafka_version="3.0.0",partition="1",topic="app_Foo",} NaN
```

```
kafka_consumergroup_group_max_lag{cluster_name="local",group="app.Consumer",}
```

## Metrics
https://www.datadoghq.com/blog/monitoring-kafka-performance-metrics/#kafka-producer-metrics

count(count(kube_pod_labels{app=~".*"}) by (app))

count(kafka_app_info_start_time_ms{env="test", kafka_version=~".*"} by (kafka_version))

count_values by ("kafka_version", kafka_version) (kafka_app_info_start_time_ms{env="test"})


count by (kafka_version) (kafka_app_info_start_time_ms{env="test"})

consumer and producer metrics:
    kafka_producer_io_wait_ratio ~ 1: client is idle -> issue on the brokers
    kafka_producer_io_ratio ~ 1: client busy on the communication with the brokers
    kafka_producer_io_wait_ratio and kafka_producer_io_ratio ~ 0: client spend the time in processing data -> all good!

producer:
    {client_id}
        kafka_producer_batch_size_avg
        kafka_producer_compression_rate_avg
    {client_id, topic}
        kafka_producer_topic_record_send_rate
        kafka_producer_topic_byte_rate
        kafka_producer_topic_record_error_rate

consumer:
    {client_id}
        kafka_consumer_fetch_manager_records_lag_max
        kafka_consumer_fetch_manager_fetch_rate
        kafka_consumer_fetch_manager_records_consumed_rate
        kafka_consumer_fetch_manager_bytes_consumed_rate
        kafka_consumer_request_size_avg
        kafka_consumer_incoming_byte_rate

