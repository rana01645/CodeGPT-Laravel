package com.trickbd.codegpt.action;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.vfs.VirtualFile;
import com.trickbd.codegpt.constants.Constants;
import com.trickbd.codegpt.generator.CodeExplainer;
import com.trickbd.codegpt.repository.api.OpenAIChatApi;
import com.trickbd.codegpt.repository.data.file.FileManager;
import com.trickbd.codegpt.repository.data.local.LocalData;
import com.trickbd.codegpt.services.CodeExplanationService;
import com.trickbd.codegpt.services.OpenAIChatCodeExplanationService;
import com.trickbd.codegpt.settings.SettingsPanel;

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
        String contents = FileManager.getInstance().readFile(file);
        if (contents == null) {
            return;
        }

        showExplanation(e, contents);
    }

    private void showExplanation(AnActionEvent e, String selectedText) {
        String apiKey = LocalData.getInstance(PropertiesComponent.getInstance()).get(Constants.API_KEY);
        if (apiKey == null || apiKey.isEmpty()) {
            SettingsPanel settingsPanel = new SettingsPanel(e, apiKey1 -> {
                if (apiKey1 != null && !apiKey1.isEmpty()) {
                    OpenAIChatApi api = OpenAIChatApi.getInstance(apiKey1);
                    CodeExplanationService service = OpenAIChatCodeExplanationService.getInstance(api);
                    (new CodeExplainer(service, e)).explain(Constants.MODEL, selectedText);
                }
            });
            settingsPanel.show();
            return;
        }

        OpenAIChatApi api = OpenAIChatApi.getInstance(apiKey);
        CodeExplanationService service = OpenAIChatCodeExplanationService.getInstance(api);
        (new CodeExplainer(service, e)).explain(Constants.MODEL, selectedText);
    }

    @Override
    public void update(AnActionEvent e) {
        // Enable the menu item only if a file is selected
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        e.getPresentation().setEnabledAndVisible(file != null && !file.isDirectory());
    }
}

