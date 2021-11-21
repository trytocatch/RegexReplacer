package com.github.trytocatch.regexreplacer.ui;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;

public class AboutDialog extends JDialog {
    public AboutDialog(Container parent){
        setModal(true);
        setTitle(StrUtils.getStr("RegexReplacer.about"));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        JEditorPane aboutEditorPane = new JEditorPane("text/html",StrUtils.getStr("AboutDialog.html"));
        aboutEditorPane.setEditable(false);
        aboutEditorPane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    try {
                        Desktop.getDesktop().browse(e.getURL().toURI());
                    } catch (IOException ignored) {
                    } catch (URISyntaxException ignored) {
                    }
                }
            }
        });
        add(aboutEditorPane);
        pack();
        setLocationRelativeTo(parent);
    }
}
