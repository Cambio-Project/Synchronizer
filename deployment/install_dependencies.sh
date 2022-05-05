#!/bin/bash

#alternatively: set time zone here manually
export DEBIAN_FRONTEND=noninteractive
apt-get update
apt-get -y install wget curl python3-pip openjdk-11-jre


# chaos toolkit setup -works
pip3 install --no-cache-dir -q -U pip3
pip3 install --no-cache-dir -r requirements.txt


# # go setup- works
wget -O ~/go_install.tar.gz https://golang.org/dl/go1.15.6.linux-amd64.tar.gz
tar -C /usr/lib/ -xzf ~/go_install.tar.gz
export PATH=$PATH:/usr/lib/go/bin/
export GOLANG_VERSION=1.15.6
#Pumba - works
mkdir ~/pumba/
mkdir /usr/lib/pumba/
wget -O ~/pumba.tar.gz https://github.com/alexei-led/pumba/archive/0.7.7.tar.gz
tar -C ~/pumba/ -xzf ~/pumba.tar.gz --strip-components=1
cd ~/pumba/cmd
go build -o /usr/lib/pumba/pumba main.go
export PATH=$PATH:/usr/lib/pumba/

#InfluxDB -works
cd ~
wget https://dl.influxdata.com/influxdb/releases/influxdb_1.8.3_amd64.deb
dpkg -i influxdb_1.8.3_amd64.deb
service influxdb start

# if [[ -z "${INFLUXDB_ADD}" ]]; then
#     wget https://dl.influxdata.com/influxdb/releases/influxdb_1.8.3_amd64.deb
#     dpkg -i influxdb_1.8.3_amd64.deb
#     service influxdb start
#     export INFLUXDB_ADD=http://localhost:8086
# else
#     echo -e "\e[1;33m[WARNING] This container will try to use a InfluxDB at ${INFLUXDB_ADD}"
#     echo -e "          Consider setting the INFLUXDB_ADD enviornment variable to change this. \e[1;0m"
# fi