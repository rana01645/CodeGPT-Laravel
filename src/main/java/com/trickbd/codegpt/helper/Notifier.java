package com.trickbd.codegpt.helper;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;

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
}
