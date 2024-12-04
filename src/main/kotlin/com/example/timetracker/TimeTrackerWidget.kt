package com.example.timetracker

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory
import com.intellij.util.Consumer
import java.awt.event.MouseEvent
import java.time.Duration
import javax.swing.Timer

/**
 * Фабрика для создания виджета в статусной строке.
 * Регистрирует виджет в системе IDE.
 */
class TimeTrackerWidgetFactory : StatusBarWidgetFactory {
    // Уникальный идентификатор виджета
    override fun getId(): String = "TimeTrackerWidget"
    // Отображаемое имя в настройках IDE
    override fun getDisplayName(): String = "Time Tracker"
    // Виджет доступен для всех проектов
    override fun isAvailable(project: Project): Boolean = true
    // Создает экземпляр виджета для проекта
    override fun createWidget(project: Project): StatusBarWidget = TimeTrackerWidget(project)
    // Удаляет виджет
    override fun disposeWidget(widget: StatusBarWidget) {}
    // Виджет можно включить для любой статусной строки
    override fun canBeEnabledOn(statusBar: StatusBar): Boolean = true
}

/**
 * Виджет статусной строки, показывающий время в текущем файле.
 * Обновляется каждую секунду и форматирует время в удобный вид.
 */
class TimeTrackerWidget(private val project: Project) : StatusBarWidget {
    private var statusBar: StatusBar? = null
    // Таймер для обновления виджета каждую секунду
    private val updateTimer = Timer(1000) { updateWidget() }

    override fun ID(): String = "TimeTrackerWidget"

    // Определяет внешний вид и поведение виджета
    override fun getPresentation(): StatusBarWidget.WidgetPresentation {
        return object : StatusBarWidget.TextPresentation {
            // Возвращает текст для отображения в статусной строке
            override fun getText(): String {
                val currentFile = FileEditorManager.getInstance(project).selectedEditor?.file
                return currentFile?.let { file ->
                    val duration = TimeTracker.getInstance().getTimeForFile(file)
                    formatDuration(duration)
                } ?: "No file open"
            }

            // Возвращает текст для подсказки
            override fun getTooltipText(): String = "Time spent in current file"

            // Обработчик клика по виджету
            override fun getClickConsumer(): Consumer<MouseEvent>? = null

            // Выравнивание виджета в статусной строке
            override fun getAlignment(): Float = 0f
        }
    }

    // Устанавливает виджет в статусную строку
    override fun install(statusBar: StatusBar) {
        this.statusBar = statusBar
        updateTimer.start()
    }

    // Удаляет виджет из статусной строки
    override fun dispose() {
        updateTimer.stop()
    }

    // Обновляет виджет
    private fun updateWidget() {
        statusBar?.updateWidget(ID())
    }

    // Форматирует продолжительность в читаемый вид
    private fun formatDuration(duration: Duration): String {
        val hours = duration.toHours()
        val minutes = duration.toMinutesPart()
        val seconds = duration.toSecondsPart()
        return when {
            // Для времени больше часа используем формат ЧЧ:ММ:СС
            hours > 0 -> "%d:%02d:%02d".format(hours, minutes, seconds)
            // Для времени меньше часа используем формат ММ:СС
            else -> "%02d:%02d".format(minutes, seconds)
        }
    }
}
