package com.trickbd.codegpt.action;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.vfs.VirtualFile;
import com.trickbd.codegpt.constants.Constants;
import com.trickbd.codegpt.generator.TestCaseGenerator;
import com.trickbd.codegpt.repository.data.file.FileManager;
import com.trickbd.codegpt.repository.data.local.LocalData;
import com.trickbd.codegpt.settings.SettingsPanel;

public class GenerateTestCaseAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // Get a reference to the current file
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        String contents = FileManager.getInstance().readFile(file);
        if (contents == null) {
            return;
        }

        //log the contents
        String apiKey = LocalData.getInstance(PropertiesComponent.getInstance()).get(Constants.API_KEY);

        if (apiKey == null || apiKey.isEmpty()) {
            SettingsPanel settingsPanel = new SettingsPanel(e, apiKey1 -> {
                if (apiKey1 != null && !apiKey1.isEmpty()) {
                    (new TestCaseGenerator(apiKey1, contents, file, e)).generateTestCase();
                }

            });
            settingsPanel.show();
            return;
        }

        (new TestCaseGenerator(apiKey, contents, file, e)).generateTestCase();

    }

    @Override
    public void update(AnActionEvent e) {
        // Enable the menu item only if a file is selected
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        e.getPresentation().setEnabledAndVisible(file != null && !file.isDirectory());
    }
}

