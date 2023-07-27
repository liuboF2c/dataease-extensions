#!/bin/sh
cd sqlite-frontend
npm run buildPlugin
cd ..

mvn clean package -U -Dmaven.test.skip=true

cp sqlite-backend/target/sqlite-backend-1.18.8.jar .

zip -r sqlite.zip  ./sqlite-backend-1.18.8.jar ./sqliteDriver   ./plugin.json

mv sqlite.zip ~/Downloads/

cp sqlite-backend/target/sqlite-backend-1.18.8.jar /opt/dataease/plugins/thirdpart/

scp sqlite-backend/target/sqlite-backend-1.18.8.jar root@10.1.13.116:/opt/dataease/plugins/thirdpart/