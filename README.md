### Автор: Хаванкин Матвей, группа М3216

# Time-tracker для IDE от JetBrains

#### Плагин отслеживает суммарное время, потраченное на работу над каждый отдельным файлом в вашем проекте.

Информация о времени отображается в нижнем статус-баре интерфейса IDE.

## Архитектура

Основные компоненты:

- TimeTracker.kt - главный сервис плагина:

    
    - Использует @Service для интеграции с IntelliJ Platform

    - Хранит Map с таймерами для каждого файла

    - Использует вспомогательный класс FileTimer для подсчёта времени:

        - startTime - время начала текущей сессии

        - totalTime - общее накопленное время

        - Методы start(), stop() и getTotalTime() для управления

    - FileEditorListener.kt - слушатель событий редактора:
    
        - Реализует FileEditorManagerListener из\newline com.intellij.openapi.fileEditor.FileEditorManagerListener 

        - Отслеживает три события:
        
            - fileOpened - когда файл открывается

            - fileClosed - когда файл закрывается

            - selectionChanged - когда пользователь переключается между файлами

        - При каждом событии:

            - Обновляет состояние таймера через TimeTracker

            - Обновляет виджет в статусной строке

    - TimeTrackerWidget.kt - виджет статусной строки:

        - TimeTrackerWidgetFactory - фабрика для создания виджетов (стандартная практика, использует StatusBarWidgetFactory из com.intellij.openapi.wm.StatusBarWidgetFactory)

        - TimeTrackerWidget - сам виджет:
        
            - Использует Timer для обновления каждую секунду

            - getPresentation() возвращает текстовое представление времени

            - formatDuration() форматирует время в читаемый вид:
            
                - "MM:SS" для времени меньше часа

                - "HH:MM:SS" для времени больше часа

## Взаимодействие компонентов:

- FileEditorListener отслеживает действия пользователя

- При действии FileEditorListener вызывает соответствующие методы TimeTracker

- TimeTracker обновляет время через FileTimer

- TimeTrackerWidget каждую секунду запрашивает текущее время у TimeTracker и обновляет отображение

## Примечание:

- Собирается через Gradle

- Протестировано на Mac OS

- Совместим с версиями IntelliJ IDEA 2023.1-2024.3

## Сборка и интеграция:

- Сверяем все конфигурационные вайлы - версии JVM, Gradle и IntelliJ IDEA

- Собираем плагин командой ./gradlew clean buildPlugin. В итоге получаем файл time-tracker-1.0-SNAPSHOT.zip в директории build/distributions/

- Добавляем плагин в нашу IDE: Settings $\longrightarrow$ Plugins $\longrightarrow$ иконка шестерёнки в верхней панели окна настроек $\longrightarrow$ Install Plugin From Disk... $\longrightarrow$ выбираем наш файл time-tracker-1.0-SNAPSHOT.zip