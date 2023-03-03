package com.trickbd.codegpt.settings;

import com.trickbd.codegpt.interfaces.SettingsPanelListener;
import com.trickbd.codegpt.repository.data.LocalData;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.DialogWrapper;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends DialogWrapper {
    private JTextField apiKeyTextField;
    SettingsPanelListener listener;

    public SettingsPanel(AnActionEvent event, SettingsPanelListener listener) {
        super(event.getProject());
        this.listener = listener;
        setTitle("Settings");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(5, 5, 5, 5);

        // Add label for API key
        JLabel apiKeyLabel = new JLabel("OpenAI API Key:");
        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(apiKeyLabel, constraints);

        // Add text field for API key
        apiKeyTextField = new JTextField(20);
        constraints.gridx = 1;
        constraints.gridy = 0;
        panel.add(apiKeyTextField, constraints);

        //update the text field with the current api key
        String apiKey = LocalData.get("apiKey");
        if (apiKey != null) {
            apiKeyTextField.setText(apiKey);
        }

        // Add message about obtaining API key
        JLabel messageLabel = new JLabel("<html>Get your OpenAI API key at<br><a href=\"https://platform.openai.com/account/api-keys\">https://platform.openai.com/account/api-keys</a></html>");
        messageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        messageLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                try {
                    java.awt.Desktop.getDesktop().browse(java.net.URI.create("https://platform.openai.com/account/api-keys"));
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
            }
        });
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        panel.add(messageLabel, constraints);

        // Add button to clear API key
        JButton clearApiKeyButton = new JButton("Clear API Key");
        clearApiKeyButton.addActionListener(e -> {
            LocalData.remove("apiKey");
            apiKeyTextField.setText("");
        });
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        panel.add(clearApiKeyButton, constraints);

        return panel;
    }

    @Override
    protected void doOKAction() {
        String apiKey = apiKeyTextField.getText();
        LocalData.set("apiKey", apiKey);
        if (listener != null) {
            listener.onOKClicked(apiKey);
        }
        super.doOKAction();
    }
}
