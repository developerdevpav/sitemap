# **Sitemap Parser**

#### _Структура проекта_
```
.
|── docker        # контейнеризация проекта
|── src           # Исходный код, конфигурация
|── .gitignore
|── pom.xml       # Maven конфигурация
|── README.md     # Документация
```

#### _Запуск проекта_

Находясь в корневой директории переходим в директорию `./docker`:

```cmd
cd ./docker
```

Выполняем в командной строке `CMD` сборку docker images:

```cmd
system.local.build.bat 
```

Запускаем контейнеры:

```cmd
system.local.run.bat 
```

#### _Ручное тестирование_

Запускаем bat файл с `curl` запросом внутри, который проверит работоспособность запущенного ПО

```cmd
system.local.test.backend.bat 
```

Содержимое:

```cmd
@echo off

curl -X GET "http://localhost/api/sitemap/analytic?resource=https://www.belta.by/"
```
