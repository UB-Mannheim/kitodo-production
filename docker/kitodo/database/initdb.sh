#!/bin/sh

echo Create schema for production database...
mysql --user=goobi --password=goobi goobi < /tmp/setup/schema.sql

echo Create default users in production database...
mysql --user=goobi --password=goobi goobi < /tmp/setup/default.sql

echo Finished production database
