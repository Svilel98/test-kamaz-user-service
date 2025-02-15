Cборка:
1. Вызвать "gradle build"
2. Вызвать "docker build -t nyura-user-service:0.0.1" 

Деплой:
1. Настроить необходимое окружение для запуска(postgres, kafka, redis)
2. Скорректировать env-переменные в "docker-compose.yaml"
3. Вызвать "docker-compose up -d"git init

Локальный запуск
1. Запустить приложение используя дефолтный профиль и application.yaml
