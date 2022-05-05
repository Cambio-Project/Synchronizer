FROM ubuntu:20.04

# INFLUXDB_ADD can be used to set a default InfluxDB adder
# ENV INFLUXDB_ADD=192.168.172.2:8086

COPY ./deployment/requirements.txt requirements.txt
COPY ./deployment/install_dependencies.sh install_dependencies.sh
RUN  /install_dependencies.sh

COPY ./target/CambioSynchronizer.jar CambioSynchronizer.jar

#Expose influxdb
EXPOSE 8086

#TODO: EXPOSE webserver port