# practicum: Микросервисное приложение Мой Банк (my-bank)

### Состав:
- `my-bank-frontend`: Единое фронтенд приложение на основе WebMVC
- `my-bank-accounts`: Сервис хранения и выполнения операций над счетами пользователя
- `my-bank-cash`: Сервис обработки операций с наличными
- `my-bank-transfer`: Сервис обработки операций с переводами между пользователями
- `my-bank-notifications`: Сервис отправки уведомлений об осуществлении операций пользователям
- `bank-keycloak`: Единый сервис авторизации
- `vault`: Сервис хранения секретов

### Требования:
- jdk 21
- docker engine
- k8s
- helm

### Запуск тестов со сборкой jar:
- Запустить `docker engine`
- Выполнить `mvn clean install`

### Запуск приложения:
- В `hosts` вашей ОС добавить запись `127.0.0.1 auth-local`
- Выполнить `mvn clean package`
- Запустить `docker engine`
- Выполнить `docker-build.sh`
- Выполнить `docker compose up -d`
- Выполнить `helm-common-install.sh`
- Выполнить `helm dependency build ./my-bank-chart/charts/backend-chart`
- Выполнить `helm dependency build ./my-bank-chart`
- Выполнить `helm lint ./my-bank-chart` для валидации конфигурации
- Выполнить `helm upgrade --install --dry-run my-bank-release ./my-bank-chart` для валидации в K8s api
- Выполнить `helm upgrade --install my-bank-release ./my-bank-chart`
- Выполнить `helm test my-bank-release` для тестирования сервисов
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
  - `realm-management`
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