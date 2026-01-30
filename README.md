# Friendly Pets

Микросервисное приложение для управления питомцами и их владельцами. Сервисы общаются через **Apache Kafka** по асинхронному запрос-ответ паттерну с использованием correlation ID.

---

## Содержание

- [Сервисы](#сервисы)
- [Kafka: топики и потоки событий](#kafka-топики-и-потоки-событий)
- [Паттерн запрос-ответ через Kafka](#паттерн-запрос-ответ-через-kafka)
- [Базы данных](#базы-данных)
- [Безопасность](#безопасность)
- [Технологии](#технологии)
- [Требования и запуск](#требования-и-запуск)
- [API](#api)

---

## Сервисы

| Сервис        | Порт | Назначение |
|---------------|------|------------|
| **web-gateway** | 8080 | Единая точка входа: аутентификация (JWT), регистрация, REST API для питомцев и владельцев. Хранит пользователей в `gateway_db`. Все вызовы к pet/owner идут через Kafka. |
| **pet-service** | 8081 | Домен питомцев: CRUD, друзья, привязка к владельцу. Слушает топик `pet-events`, отвечает в `pet-events-response`. БД: `pet_db`. |
| **owner-service** | 8082 | Домен владельцев: CRUD, связь с пользователем (`userId`). Слушает топик `owner-events`, отвечает в `owner-events-response`. БД: `owner_db`. |

Pet-service и owner-service также могут быть вызваны напрямую по HTTP (свои контроллеры), но в типичном сценарии клиент работает только с Gateway, а Gateway общается с ними через Kafka.

---

## Kafka: топики и потоки событий

### Топики

| Топик | Создаётся в | Кто публикует | Кто потребляет |
|-------|-------------|----------------|-----------------|
| **pet-events** | pet-service, web-gateway | web-gateway (запросы: GET, GET_ALL, CREATE, UPDATE, DELETE, ADD_FRIEND, GET_MY_PETS) | pet-service |
| **pet-events-response** | pet-service, web-gateway | pet-service (ответы с тем же correlation ID) | web-gateway |
| **owner-events** | owner-service, web-gateway | web-gateway (запросы: GET, GET_ALL, CREATE, UPDATE, DELETE, GET_MY_PETS) | owner-service |
| **owner-events-response** | owner-service | owner-service (ответы с тем же ключом) | web-gateway |

### Формат сообщений

- **Ключ сообщения**: correlation ID (строка, UUID) — один и тот же для запроса и ответа.
- **Значение**: JSON (сериализация через `JsonSerializer`/`JsonDeserializer`).

### Consumer groups

- **pet-service**: `pet-service-group` (pet-events).
- **owner-service**: `owner-service-group` (owner-events).
- **web-gateway**: `web-gateway-group` (pet-events-response), `gateway-group` (owner-events-response).

---

## Паттерн запрос-ответ через Kafka

1. Клиент вызывает REST API Gateway (например, `GET /api/pets/1`).
2. Gateway генерирует `correlationId = UUID`, создаёт `CompletableFuture` и кладёт его в `Map<correlationId, Future>`.
3. Gateway отправляет в Kafka сообщение в топик запросов (`pet-events` или `owner-events`) с ключом `correlationId` и телом запроса (`type`, `id`, `data` и т.д.).
4. Соответствующий сервис (pet или owner) обрабатывает сообщение, выполняет операцию и отправляет ответ в топик ответов (`pet-events-response` или `owner-events-response`) с **тем же ключом** `correlationId`.
5. Gateway слушает топик ответов; при получении сообщения с ключом `correlationId` находит соответствующий `CompletableFuture` и завершает его результатом.
6. HTTP-запрос в Gateway завершается ответом клиенту (с таймаутом, например 5 секунд).

Таким образом, синхронный REST на границе системы превращается в асинхронное взаимодействие между Gateway и доменными сервисами через Kafka.

---

## Базы данных

Используется **PostgreSQL** (один инстанс, разные базы):

| База | Сервис | Назначение |
|------|--------|------------|
| **gateway_db** | web-gateway | Пользователи (логин, пароль, роли, привязка к owner). |
| **pet_db** | pet-service | Питомцы (имя, тип, дата рождения, порода, цвет, владелец, друзья, версия). |
| **owner_db** | owner-service | Владельцы (имя, дата рождения, список pet id, user id). |

---

## Безопасность

- **JWT**: аутентификация через токен (секрет и время жизни задаются в конфиге; общий для gateway и сервисов).
- **Роли**:  `ADMIN`, `USER`; проверки через `@PreAuthorize` в Gateway (доступ к своим питомцам/владельцам или полный доступ для ADMIN).
- В запросах к защищённым эндпоинтам передаётся заголовок `Authorization: Bearer <token>`.

Регистрация и выдача токена: `POST /api/auth/register`, `POST /api/auth/authenticate`.

---

## Технологии

- **Java 17**, **Spring Boot 3.2**
- **Kafka** — публикация и потребление сообщений
- **PostgreSQL** — хранилище данных
- **Spring Security** + **JWT** — аутентификация и авторизация
- **Lombok** — уменьшение шаблонного кода
- **Maven** — мультимодульная сборка (корневой `pom.xml` и модули: `pet-service`, `owner-service`, `web-gateway`)

---

## Требования и запуск

### Необходимо установить и запустить

1. **PostgreSQL** на `localhost:5430` с пользователем `user` и паролем `test123`, и созданными базами:
   - `gateway_db`
   - `pet_db`
   - `owner_db`

2. **Apache Kafka** на `localhost:9092` (например, через Docker или локальную установку). Топики `pet-events`, `pet-events-response`, `owner-events`, `owner-events-response` могут быть созданы автоматически при старте сервисов (настройка `NewTopic` в конфигурации Kafka каждого модуля).

### Запуск через Docker Compose

В корне проекта есть **docker-compose.yml** для автоматического запуска всей инфраструктуры и всех сервисов.

**Требования:** установленные [Docker](https://docs.docker.com/get-docker/) и [Docker Compose](https://docs.docker.com/compose/install/).

Из корня репозитория выполните:

```bash
docker compose up --build
```

Будут запущены:

- **PostgreSQL** (порт 5430) — создаются базы `gateway_db`, `pet_db`, `owner_db` и пользователь `user`/`test123`.
- **Kafka** (порт 9092) — один брокер в режиме KRaft.
- **owner-service** (порт 8082), **pet-service** (порт 8081), **web-gateway** (порт 8080) — собираются из исходников через общий multi-stage Dockerfile.

Сервисы стартуют после успешной проверки здоровья PostgreSQL и Kafka (`depends_on` + `healthcheck`). API доступен по адресу **http://localhost:8080**.

Остановка и удаление контейнеров и тома с данными PostgreSQL:

```bash
docker compose down -v
```

### Запуск без Docker

1. Запустить PostgreSQL и Kafka вручную (см. выше).
2. Собрать проект из корня:
   ```bash
   mvn clean install
   ```
3. Запустить сервисы (в любом порядке, но для полной работы API нужны все три):
   ```bash
   # Терминал 1 — gateway (точка входа для клиентов)
   mvn -pl web-gateway spring-boot:run

   # Терминал 2 — pet-service
   mvn -pl pet-service spring-boot:run

   # Терминал 3 — owner-service
   mvn -pl owner-service spring-boot:run
   ```
---

## API

Базовый префикс для клиента: **http://localhost:8080/api**.

### Аутентификация

| Метод | Путь | Описание |
|-------|------|----------|
| POST | `/api/auth/register` | Регистрация (тело: username, password и др. по DTO). |
| POST | `/api/auth/authenticate` | Вход, получение JWT (тело: username, password). |

### Питомцы (через Gateway → Kafka → pet-service)

| Метод | Путь | Описание |
|-------|------|----------|
| GET | `/api/pets` | Список всех питомцев. |
| GET | `/api/pets/{id}` | Питомец по id. |
| POST | `/api/pets` | Создание питомца (владелец подставляется из JWT при необходимости). |
| PUT | `/api/pets/{id}` | Обновление питомца. |
| DELETE | `/api/pets/{id}` | Удаление питомца. |
| POST | `/api/pets/connect?petId=&friendId=` | Добавить друга питомцу. |
| GET | `/api/pets/me` | Питомцы текущего пользователя. |

### Владельцы (через Gateway → Kafka → owner-service)

| Метод | Путь | Описание |
|-------|------|----------|
| GET | `/api/owners` | Список всех владельцев (например, только ADMIN). |
| GET | `/api/owners/{id}` | Владелец по id. |
| GET | `/api/owners/me/pets` | Питомцы текущего владельца. |
| POST | `/api/owners` | Создание владельца. |
| PUT | `/api/owners/{id}` | Обновление владельца. |
| DELETE | `/api/owners/{id}` | Удаление владельца. |

Защищённые эндпоинты требуют заголовка `Authorization: Bearer <JWT>` и при необходимости проверяют, что пользователь имеет право на указанный ресурс (свой владелец/питомец или роль ADMIN).