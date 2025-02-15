Cборка:
1. Вызвать "gradle build"
2. Вызвать "docker build -t nyura-user-service:0.0.1" 

Деплой:
1. Настроить необходимое окружение для запуска(postgres, kafka, redis)
2. Скорректировать env-переменные в "docker-compose.yaml"
3. Вызвать "docker-compose up -d"git init

Локальный запуск
1. Запустить приложение используя дефолтный профиль и application.yaml

Работа с сервисом:
1. Необходимо пройти процесс авторизации, используя контроллер /login используя данные базового пользователя
2. Базовый пользователь: admin/root
3. Далее необходимо забрать из ответа access токен и использовать его в качестве хедер Authorization в формате, "Bearer <access_token>"