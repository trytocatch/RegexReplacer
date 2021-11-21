package com.github.trytocatch.regexreplacer.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.*;
import javax.swing.undo.UndoManager;

import com.github.trytocatch.swingplus.text.LineLabel;

/**
 * @author trytocatch@163.com
 * @date 2012-12-27
 */
public class RegexReplacer {
	public static void main(String[] a) {
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				start();
			}
		});
	}
	
	private static void start(){
		if(!System.getProperty("os.name","").toLowerCase().startsWith("mac")) {
			Class<?> lookAndFeel = null;
			try {
				lookAndFeel = Class.forName("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
			} catch (ClassNotFoundException e) {
				try {
					lookAndFeel = Class.forName("javax.swing.plaf.nimbus.NimbusLookAndFeel");
				} catch (ClassNotFoundException ex) {
				}
			}
			if (lookAndFeel != null) {
				try {
					UIManager.setLookAndFeel((LookAndFeel) lookAndFeel.newInstance());
				} catch (InstantiationException ignored) {
				} catch (IllegalAccessException ignored) {
				} catch (UnsupportedLookAndFeelException ignored) {
				}
			}
		}
		final JFrame f = new JFrame(StrUtils.getStr("RegexReplacer.title"));
		JTextArea jta = new JTextArea(10, 20);
		jta.setLineWrap(true);
		final UndoManager undoManager = new UndoManager();
		int mask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		jta.getDocument().addUndoableEditListener(undoManager);
		jta.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, mask), "undo");
		jta.getActionMap().put("undo", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (undoManager.canUndo())
					undoManager.undo();
			}
		});
		jta.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, mask), "redo");
		jta.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, mask | InputEvent.SHIFT_DOWN_MASK), "redo");
		jta.getActionMap().put("redo", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (undoManager.canRedo())
					undoManager.redo();
			}
		});
		SearchPanel searchPanel = new SearchPanel(jta);
		searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
		searchPanel.setMinimumSize(searchPanel.getPreferredSize());
		JScrollPane jsp = new JScrollPane(jta);
		jta.setFont(new Font(Font.MONOSPACED, 0, 14));
		jsp.setRowHeaderView(new LineLabel(jta));
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, jsp,
				searchPanel);
		splitPane.setResizeWeight(1);
		splitPane.setDividerSize(3);
		f.getContentPane().add(splitPane);
		JMenuBar jmb = new JMenuBar();
		JMenu jm = new JMenu(StrUtils.getStr("RegexReplacer.help"));
		jm.add(new JMenuItem(new AbstractAction(StrUtils
				.getStr("HelpFrame.regexHelp")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				HelpFrame.showRegexHelp();
			}
		}));
		jm.add(new JMenuItem(new AbstractAction(StrUtils
				.getStr("HelpFrame.help")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				HelpFrame.showHelp();
			}
		}));
		jm.add(new JMenuItem(new AbstractAction(StrUtils
				.getStr("HelpFrame.functionsHelp")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				HelpFrame.showFunctionsHelp();
			}
		}));
		jm.add(new JMenuItem(new AbstractAction(StrUtils
				.getStr("HelpFrame.newFunctionHelp")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				HelpFrame.showNewFunctionHelp();
			}
		}));
		jm.add(new JMenuItem(new AbstractAction(StrUtils
				.getStr("RegexReplacer.about")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				new AboutDialog(f).setVisible(true);
			}
		}));
		jmb.add(jm);
		f.setJMenuBar(jmb);

		f.pack();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setLocationRelativeTo(null);
		f.setVisible(true);
	}
}
