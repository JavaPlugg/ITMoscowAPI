# ITMoscowAPI (Unofficial)

API для получения корпусов, групп, замен и расписания колледжа ИТ. Москва (бывш. МГКЭИТ)

### Официальный инстанс

`itmoscow.javaplugg.net`

### Технологии

Spring Boot 3.5.x, Java 21, JPA, Caffeine, Jsoup, BCrypt

### Принцип работы

Передаваемые данные парсятся с официального сайта и кэшируются  
Официальное API не используется, поскольку не соответствует документации и с ним невозможно работать
Проект может в любой момент поломаться, если структура сайта изменится

### Авторизация

Для работы с API нужно получить токен  
Для получения токена нужно получить одноразовый пароль  
Одноразовый пароль можно получить отправив POST запрос на /itmoscow/auth/otp (подробнее в запросах)  
Токен можно получить отправив POST запрос на /itmoscow/auth/token (подробнее в запросах)

### Конфигурация

При селф-хосте необходимо настроить следующие поля:  
`String itmoscowUrl` - в данный момент https://it-moscow.pro  
`int otpCacheLifetimeMinutes` - время жизни кэша одноразовых паролей в минутах  
`int tokenCacheLifetimeMinutes` - время жизни кэша токенов в минутах  
`int cacheLifetimeMinutes` - время жизни остального кэша  
`int maxRequestsPerMinute` - максимальное кол-во запросов в минуту от IP или по токену

### Запросы

См. модуль ITMoscowAPIDTOs

`POST` `/itmoscow/auth/otp` `Без авторизации`

Сгенерировать и отправить одноразовый пароль на указанный адрес электронной почты

```json
{
    "email": "example@gmail.com"
}
```

`POST` `/itmoscow/auth/token` `Без авторизации`

Сгенерировать токен и отправить на почту, ранее указанную в одноразовом пароле

```json
{
    "otp": "example"
}
```

`GET` `/itmoscow/api/v1/buildings/list`

Получить список корпусов колледжа

```json
{
  "buildings": [
    {
      "name": "Миллионщикова",
      "key": "ttm"
    },
    {
      "name": "Коломенская",
      "key": "ttk"
    }
  ]
}
```

`POST` `/itmoscow/api/v1/groups/list`

Получить список групп в корпусе

```json
{
    "building": {
        "name": "Миллионщикова",
        "key": "ttm"
    }
}
```
```json
{
    "groups": [
        {
            "name": "1ВР-1-25"
        },
        {
            "name": "1ИП-1-25 (п)"
        }
    ]
}
```

`POST` `/itmoscow/api/v1/schedule/day`

Получить расписание по индексу дня недели
`replacements` - применять ли замены к расписанию

```json
{
    "building": {
        "name": "Миллионщикова",
        "key": "ttm"
    },
    "group": {
        "name": "1ВР-1-25"
    },
    "weekday": 0,
    "replacements": true
}
```
```json
{
    "schedule": {
        "weekday": "Понедельник",
        "lessons": [
            {
                "number": 1,
                "time": "08:30 - 09:15",
                "subject": "ОУП. 03 Математика",
                "teacher": "Данилов Е.И.",
                "room": "Академика Миллионщикова. каб: 204"
            }
        ]
    }
}
```

`POST` `/itmoscow/api/v1/schedule/replacements`

Получить замены на сегодня

```json
{
    "building": {
        "name": "Миллионщикова",
        "key": "ttm"
    },
    "group": {
        "name": "1ВР-1-25"
    }
}
```
```json
{
    "replacements": [
        {
            "number": 1,
            "subject": "ОУП. 03 Математика",
            "teacher": "Данилов Е.И.",
            "room": "Академика Миллионщикова. каб: 204"
        }
    ]
}
```