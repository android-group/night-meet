#!/usr/bin/env bash

mongo_port="27017"
mongo_db_directory="/opt/mongo/mongo.db"
mongo_log_file="/opt/mongo/mongo.log"
artifact_name="night-meet.jar"


#create files, which needs for work of mongo
if [ ! -d ${mongo_db_directory} ]
    then
        echo "create directory $mongo_db_directory"
        mkdir ${mongo_db_directory}
        chmod -R 777 ${mongo_db_directory}
fi

#run mongo
echo "run mongo on port: ${mongo_port}"
mongod  --dbpath ${mongo_db_directory} --port ${mongo_port} --logpath ${mongo_log_file} &

java -jar ../target/scala-2.11/${artifact_name}





