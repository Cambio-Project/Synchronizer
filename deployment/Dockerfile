FROM ubuntu:20.04

# INFLUXDB_ADD can be used to set a default InfluxDB adder
# ENV INFLUXDB_ADD=192.168.172.2:8086

COPY ./install_dependencies.sh install_dependencies.sh
RUN ./install_dependencies.sh

#Expose influxdb
EXPOSE 8086
