# practicum: Микросервисное приложение Мой Банк (my-bank)

### Состав:
- `my-bank-frontend`: Единое фронтенд приложение на основе WebMVC
- `my-bank-gateway`: Единый шлюз для обращения фронтенда к микросервисам бэкенда
- `my-bank-accounts`: Сервис хранения и выполнения операций над счетами пользователя
- `my-bank-cash`: Сервис обработки операций с наличными
- `my-bank-transfer`: Сервис обработки операций с переводами между пользователями
- `my-bank-notifications`: Сервис отправки уведомлений об осуществлении операций пользователям
- `bank-keycloak`: Единый сервис авторизации
- `bank-consul`: Сервис регистрации самописных микросервисов
- `bank-accounts-db`: СУБД PostgreSql для хранения счетов пользователей

### Требования:
- jdk 21
- docker engine

### Запуск тестов со сборкой jar:
- Запустить `docker engine`
- Выполнить `mvn clean install`

### Запуск приложения:
- В `hosts` вашей ОС добавить запись `127.0.0.1 auth.local`
- Выполнить `mvn clean package`
- Запустить `docker engine`
- Выполнить `docker compose up -d`
- После успешной сборки и запуска приложение будет доступно по адресу `http://localhost`

### Тестовые пользователи (login / pass):
- `user1` / `user1` - Сергеев Иван
- `user2` / `user2` - Иванов Сергей
- `user3` / `user3` - Семенов Василий

### Преднастроенные роли и права пользователей и сервисов:
- Пользователи:
  - `USER`
    - `accounts.read`
    - `accounts.write`
    - `transfer.read`
    - `transfer.write`
    - `cash.write`
- `accounts-service`:
  - `SERVICE`
    - `notifications.write`
- `transfer-service`:
    - `SERVICE`
        - `notifications.write`
    - `accounts.read`
    - `accounts.write`
- `cash-service`:
    - `SERVICE`
        - `notifications.write`
    - `accounts.read`
    - `accounts.write`