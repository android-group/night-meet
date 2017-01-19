#!/usr/bin/env bash

mongo_port="27017"
mongo_db_directory="/tmp/mongo.db"
mongo_log_file="/tmp/mongo.log"


# check the existence of mongo application
packages=$(dpkg --get-selections | grep mongod)

if [[ -z package ]]
   then
          echo "mongod has been not installed"
          apt-get install -y mongo
else
   echo "mongod has been installed";
fi

#create files, which needs for mongo work
if [ ! -d ${mongo_db_directory} ]
    then
        echo "create directory $mongo_db_directory"
        mkdir ${mongo_db_directory}
fi
if [ -f ${mongo_log_file} ]
    then
        echo "delete $mongo_log_file"
        rm ${mongo_log_file}
fi


#run mongo
echo "run mongo on port: ${mongo_port}"
mongod  --dbpath ${mongo_db_directory} --port ${mongo_port} --logpath ${mongo_log_file} &





