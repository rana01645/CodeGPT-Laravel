package com.trickbd.codegpt.helper;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;


public class Notifier {
    //notify error
    public static void notifyError(String title, String message) {
        Notifications.Bus.notify(new Notification(
                "GenerateTestCase",
                title,
                message,
                NotificationType.ERROR
        ));
    }
    //notify info
    public static void notifyInfo(String title, String message) {
        Notifications.Bus.notify(new Notification(
                "GenerateTestCase",
                title,
                message,
                NotificationType.INFORMATION
        ));
    }

    //notify warning
    public static void notifyWarning(String title, String message) {
        Notifications.Bus.notify(new Notification(
                "GenerateTestCase",
                title,
                message,
                NotificationType.WARNING
        ));
    }

    //notify success
    public static void notifySuccess(String title, String message) {
        Notifications.Bus.notify(new Notification(
                "GenerateTestCase",
                title,
                message,
                NotificationType.INFORMATION
        ));
    }

    //notify and open file
    public static void notifyAndOpenFIle(String title, String message, String path, Project project) {

        Notification notification = new Notification(
                "GenerateTestCase",
                title,
                message,
                NotificationType.INFORMATION
        );

        notification.addAction(new NotificationAction("Open Test file") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(path);
                if (virtualFile != null) {
                    FileEditorManager.getInstance(project).openFile(virtualFile, true);
                }
                notification.expire();
            }
        });

        Notifications.Bus.notify(notification);
    }
}
