package com.trickbd.codegpt.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.trickbd.codegpt.generator.CodeExplainer;
import com.trickbd.codegpt.generator.TestCaseGenerator;
import com.trickbd.codegpt.repository.data.FileManager;
import com.trickbd.codegpt.repository.data.LocalData;
import com.trickbd.codegpt.settings.SettingsPanel;

import javax.swing.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ExplainCodeAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor != null) {
            String selectedText = editor.getSelectionModel().getSelectedText();
            if (selectedText != null && !selectedText.isEmpty()) {
                showExplanation(e, selectedText);
                return;
            }
        }

        // Get a reference to the current file
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        String contents = (new FileManager()).readFile(file);
        if (contents == null) {
            return;
        }

        showExplanation(e, contents);
    }

    private void showExplanation(AnActionEvent e, String selectedText) {
        String apiKey = LocalData.get("apiKey");
        if (apiKey == null || apiKey.isEmpty()) {
            SettingsPanel settingsPanel = new SettingsPanel(e, apiKey1 -> {
                if (apiKey1 != null && !apiKey1.isEmpty()) {
                    (new CodeExplainer(apiKey1, selectedText, e)).explain();
                }
            });
            settingsPanel.show();
            return;
        }
        (new CodeExplainer(apiKey, selectedText, e)).explain();
    }

    @Override
    public void update(AnActionEvent e) {
        // Enable the menu item only if a file is selected
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        e.getPresentation().setEnabledAndVisible(file != null && !file.isDirectory());
    }
}

