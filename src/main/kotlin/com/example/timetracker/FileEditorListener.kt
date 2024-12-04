package com.example.timetracker

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.WindowManager

/**
 * Слушатель событий редактора файлов.
 * Автоматически запускает/останавливает таймеры при переключении между файлами.
 * Обновляет виджет в статусной строке при каждом событии.
 */
class FileEditorListener : FileEditorManagerListener {
    // Вызывается при открытии нового файла
    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        TimeTracker.getInstance().startTracking(file)
        updateStatusBar(source)
    }

    // Вызывается при закрытии файла
    override fun fileClosed(source: FileEditorManager, file: VirtualFile) {
        TimeTracker.getInstance().stopTracking(file)
        updateStatusBar(source)
    }

    // Вызывается при переключении между файлами
    override fun selectionChanged(event: FileEditorManagerEvent) {
        // Останавливаем таймер предыдущего файла
        event.oldFile?.let { TimeTracker.getInstance().stopTracking(it) }
        // Запускаем таймер нового файла
        event.newFile?.let { TimeTracker.getInstance().startTracking(it) }
        // Обновляем отображение в статусной строке
        event.manager?.let { updateStatusBar(it) }
    }

    // Обновляет виджет в статусной строке
    private fun updateStatusBar(source: FileEditorManager) {
        source.project.let { project ->
            WindowManager.getInstance().getStatusBar(project)?.updateWidget("TimeTrackerWidget")
        }
    }
}
