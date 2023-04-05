#!/bin/sh
sudo apt-get install gnupg

wget -qO - https://www.mongodb.org/static/pgp/server-4.4.asc | sudo apt-key add -

echo "deb [ arch=amd64,arm64 ] https://repo.mongodb.org/apt/ubuntu focal/mongodb-org/4.4 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-4.4.list

sudo apt-get update
sudo apt-get install libssl1.1
echo "deb http://security.ubuntu.com/ubuntu focal-security main" | sudo tee /etc/apt/sources.list.d/focal-security.list
sudo apt-get update
sudo apt-get install -y mongodb-org
systemctl start mongod

git clone https://github.com/yongmandooo/23Spring_SoftwareEngineering_Group5.git
cd 23Spring_SoftwareEngineering_Group5/cse364-project
mvn package
java -jar target/cse364-project-0.0.1-SNAPSHOT.jar

 
