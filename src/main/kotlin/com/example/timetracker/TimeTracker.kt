package com.example.timetracker

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import java.time.Duration
import java.time.Instant

/**
 * Главный сервис плагина для отслеживания времени.
 * Хранит и управляет таймерами для всех открытых файлов.
 */
@Service
class TimeTracker {
    // Хранилище таймеров, ключ - путь к файлу
    private val fileTimers = mutableMapOf<String, FileTimer>()
    
    // Начинает отслеживание времени для файла
    fun startTracking(file: VirtualFile) {
        if (!fileTimers.containsKey(file.path)) {
            fileTimers[file.path] = FileTimer()
        }
        fileTimers[file.path]?.start()
    }
    
    // Останавливает отслеживание времени для файла
    fun stopTracking(file: VirtualFile) {
        fileTimers[file.path]?.stop()
    }
    
    // Возвращает общее время, проведенное в файле
    fun getTimeForFile(file: VirtualFile): Duration {
        return fileTimers[file.path]?.getTotalTime() ?: Duration.ZERO
    }
    
    companion object {
        // Получение экземпляра сервиса
        fun getInstance(): TimeTracker = service()
    }
}

/**
 * Вспомогательный класс для подсчёта времени одного файла.
 * Отслеживает текущую сессию и общее накопленное время.
 */
private class FileTimer {
    // Время начала текущей сессии
    private var startTime: Instant? = null
    // Общее накопленное время
    private var totalTime: Duration = Duration.ZERO
    
    fun start() {
        if (startTime == null) {
            startTime = Instant.now()
        }
    }
    
    fun stop() {
        startTime?.let {
            totalTime = totalTime.plus(Duration.between(it, Instant.now()))
            startTime = null
        }
    }
    
    fun getTotalTime(): Duration = totalTime.plus(
        startTime?.let { Duration.between(it, Instant.now()) } ?: Duration.ZERO
    )
}
