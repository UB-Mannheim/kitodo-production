#!/bin/sh

echo Create schema for production database...
mysql --user=kitodo --password=kitodo kitodo < /tmp/setup/schema.sql

echo Create default users in production database...
mysql --user=kitodo --password=kitodo kitodo < /tmp/setup/default.sql

echo Finished production database
