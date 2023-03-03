package com.trickbd.codegpt.action;

import com.trickbd.codegpt.settings.SettingsPanel;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.vfs.VirtualFile;

public class UpdateOrChangeApiKeyAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        SettingsPanel settingsPanel = new SettingsPanel(e,null);
        settingsPanel.show();
    }

    @Override
    public void update(AnActionEvent e) {
        // Enable the menu item only if a file is selected
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        e.getPresentation().setEnabledAndVisible(file != null && !file.isDirectory());
    }
}

