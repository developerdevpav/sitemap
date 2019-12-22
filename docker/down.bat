@echo off

docker-compose -f %1 down -v --rmi all
docker-compose -f %1 down --remove-orphans