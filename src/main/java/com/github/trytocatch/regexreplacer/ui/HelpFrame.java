package com.github.trytocatch.regexreplacer.ui;

import java.io.IOException;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

public class HelpFrame extends JFrame {
	private static final long serialVersionUID = 3929728074482181820L;
	private static final String regexHelp = StrUtils
			.getStr("HelpFrame.regexHelp");
	private static final String help = StrUtils.getStr("HelpFrame.help");
	private static final String functionsHelp = StrUtils
			.getStr("HelpFrame.functionsHelp");
	private static final String newFunctionHelp = StrUtils
			.getStr("HelpFrame.newFunctionHelp");

	private static HelpFrame instance = new HelpFrame();

	private JTabbedPane jtp;

	private HelpFrame() {
		setTitle(StrUtils.getStr("HelpFrame.title"));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		jtp = new JTabbedPane();
		try {
			JEditorPane regexPane = new JEditorPane(RegexReplacer.class
					.getClassLoader().getResource(
							StrUtils.getStr("html.JavaRegex")));
			regexPane.setEditable(false);
			// regexPane.addHyperlinkListener(new HyperlinkListener() {
			// @Override
			// public void hyperlinkUpdate(HyperlinkEvent e) {
			// if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED){
			// URL url = e.getURL();
			// try {
			// ((JEditorPane) e.getSource()).setPage(url);
			// } catch (IOException e1) {
			// e1.printStackTrace();
			// }
			// }
			// }
			// });
			jtp.addTab(regexHelp, new JScrollPane(regexPane));

			JEditorPane helpPane = new JEditorPane(RegexReplacer.class
					.getClassLoader().getResource(StrUtils.getStr("html.Help")));
			helpPane.setEditable(false);
			jtp.addTab(help, new JScrollPane(helpPane));
			JEditorPane functionsPane = new JEditorPane(RegexReplacer.class
					.getClassLoader().getResource(
							StrUtils.getStr("html.Functions")));
			functionsPane.setEditable(false);
			jtp.addTab(functionsHelp, new JScrollPane(functionsPane));
			JEditorPane newFunctionPane = new JEditorPane(RegexReplacer.class
					.getClassLoader().getResource(
							StrUtils.getStr("html.NewFunction")));
			newFunctionPane.setEditable(false);
			jtp.addTab(newFunctionHelp, new JScrollPane(newFunctionPane));
		} catch (IOException e) {
			e.printStackTrace();
		}
		add(jtp);
		setSize(700, 500);
		setLocationRelativeTo(null);
	}

	public static void showRegexHelp() {
		instance.jtp.setSelectedIndex(0);
		instance.setVisible(true);
	}

	public static void showHelp() {
		instance.jtp.setSelectedIndex(1);
		instance.setVisible(true);
	}

	public static void showFunctionsHelp() {
		instance.jtp.setSelectedIndex(2);
		instance.setVisible(true);
	}

	public static void showNewFunctionHelp() {
		instance.jtp.setSelectedIndex(3);
		instance.setVisible(true);
	}
}
