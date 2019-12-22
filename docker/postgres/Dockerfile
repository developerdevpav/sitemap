FROM postgres:9.6.1

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

#ADD init.sql /docker-entrypoint-initdb.d/
#RUN chmod 755 /docker-entrypoint-initdb.d/init.sql

VOLUME  ["/etc/postgresql", "/var/log/postgresql", "/var/lib/postgresql/data"]