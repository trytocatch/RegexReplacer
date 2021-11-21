package com.github.trytocatch.regexreplacer.ui;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;

import com.github.trytocatch.regexreplacer.model.MatchResultInfo;
import com.github.trytocatch.regexreplacer.model.RegexController;
import com.github.trytocatch.regexreplacer.model.expression.ResultObserver;
import com.github.trytocatch.swingplus.text.LineLabel;

/**
 * 
 * @author trytocatch@163.com
 * @date 2012-12-21
 */
public class SearchPanel extends JPanel implements ResultObserver {
	private static final long serialVersionUID = -1619683153966533649L;
	private static final float CENTER = 0.5f;
	private OutPutDialog outPutDlg;
	private RegexController regexController;
	private boolean isResultDisplayed;
	private JTextComponent editArea;
	private DocumentListener editorDocumentListener;
	private MyTableModel resultTableModel;
	private JTable resultTable;
	private JTextArea regexArea;
	private JTextArea replaceArea;

	private JCheckBox unixLinesCkb;
	private JCheckBox caseInsensitiveCkb;
	private JCheckBox commentsCkb;
	private JCheckBox multilineCkb;
	private JCheckBox literalCkb;
	private JCheckBox dotallCkb;
	private JCheckBox unicodeCaseCkb;
	private JCheckBox canonEqCkb;

	private JCheckBox liveUpdateCkb;
	private JCheckBox outputResultToNewWindow;
	private JCheckBox divertFocus;
	private JCheckBox expressionAvailable;

	private JButton updateNowBtn;
	private JButton replaceSelected;
	private JButton replaceAll;

	private JLabel statsLabel;
	private JLabel matchResultLabel;

	public SearchPanel(JTextComponent editArea) {
		if (editArea == null)
			throw new IllegalArgumentException("editArea can not be null");
		this.editArea = editArea;
		regexController = new RegexController();
		regexController.setResultObserver(this);
		isResultDisplayed = true;
		outPutDlg = new OutPutDialog();
		initComponent();
	}

	public RegexController getRegexController() {
		return regexController;
	}

	public void setRegexController(RegexController regexController) {
		if (regexController == null)
			throw new IllegalArgumentException("RegexController can't be null");
		this.regexController = regexController;
	}

	private void createComponent() {
		resultTableModel = new MyTableModel();
		resultTable = new JTable(resultTableModel);
		Enumeration<TableColumn> columns = resultTable.getColumnModel()
				.getColumns();
		for (int n = 0; columns.hasMoreElements(); n++) {
			columns.nextElement().setPreferredWidth(
					resultTableModel.getColumnWidth(n));
		}
		resultTable.setPreferredScrollableViewportSize(resultTable
				.getPreferredSize());
		resultTable.setAutoCreateRowSorter(false);
		// resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		regexArea = new JTextArea(3, 20);
		replaceArea = new JTextArea(3, 20);

		unixLinesCkb = new JCheckBox("UNIX_LINES");
		unixLinesCkb.setToolTipText(Helper.UNIX_LINES_TIP);
		caseInsensitiveCkb = new JCheckBox("CASE_INSENSITIVE");
		caseInsensitiveCkb.setToolTipText(Helper.CASE_INSENSITIVE_TIP);
		commentsCkb = new JCheckBox("COMMENTS");
		commentsCkb.setToolTipText(Helper.COMMENTS_TIP);
		multilineCkb = new JCheckBox("MULTILINE");
		multilineCkb.setToolTipText(Helper.MULTILINE_TIP);
		literalCkb = new JCheckBox(StrUtils.getStr("SearchPanel.LITERAL"));
		literalCkb.setToolTipText(Helper.LITERAL_TIP);
		dotallCkb = new JCheckBox("DOTALL");
		dotallCkb.setToolTipText(Helper.DOTALL_TIP);
		unicodeCaseCkb = new JCheckBox("UNICODE_CASE");
		unicodeCaseCkb.setToolTipText(Helper.UNICODE_CASE_TIP);
		canonEqCkb = new JCheckBox("CANON_EQ");
		canonEqCkb.setToolTipText(Helper.CANON_EQ_TIP);

		expressionAvailable = new JCheckBox(
				StrUtils.getStr("SearchPanel.replaceFunction"), true);
		expressionAvailable.setToolTipText(StrUtils
				.getStr("SearchPanel.replaceFunction_tip"));
		liveUpdateCkb = new JCheckBox(
				StrUtils.getStr("SearchPanel.liveUpdate"), true);
		outputResultToNewWindow = new JCheckBox(
				StrUtils.getStr("SearchPanel.getReplacementOnly"));
		outputResultToNewWindow.setToolTipText(StrUtils
				.getStr("SearchPanel.getReplacementOnly_tip"));
		divertFocus = new JCheckBox(StrUtils.getStr("SearchPanel.returnFocus"),
				true);
		divertFocus.setToolTipText(StrUtils
				.getStr("SearchPanel.returnFocus_tip"));

		updateNowBtn = new JButton(StrUtils.getStr("SearchPanel.update"));
		updateNowBtn.setEnabled(false);
		replaceSelected = new JButton(
				StrUtils.getStr("SearchPanel.replaceSelected"));
		replaceAll = new JButton(StrUtils.getStr("SearchPanel.replaceAll"));
		statsLabel = new JLabel(StrUtils.getStr("SearchPanel.authorLabel"));
		matchResultLabel = new JLabel();
	}

	private int getRegexFlag() {
		int flag = 0;
		if (unixLinesCkb.isSelected())
			flag |= Pattern.UNIX_LINES;
		if (caseInsensitiveCkb.isSelected())
			flag |= Pattern.CASE_INSENSITIVE;
		if (commentsCkb.isSelected())
			flag |= Pattern.COMMENTS;
		if (multilineCkb.isSelected())
			flag |= Pattern.MULTILINE;
		if (literalCkb.isSelected())
			flag |= Pattern.LITERAL;
		if (dotallCkb.isSelected())
			flag |= Pattern.DOTALL;
		if (unicodeCaseCkb.isSelected())
			flag |= Pattern.UNICODE_CASE;
		if (canonEqCkb.isSelected())
			flag |= Pattern.CANON_EQ;
		return flag;
	}

	@SuppressWarnings("serial")
	private void installListener() {
		ItemListener flagListener = new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				regexController.setPatternFlag(getRegexFlag());
			}
		};
		unixLinesCkb.addItemListener(flagListener);
		caseInsensitiveCkb.addItemListener(flagListener);
		commentsCkb.addItemListener(flagListener);
		multilineCkb.addItemListener(flagListener);
		literalCkb.addItemListener(flagListener);
		dotallCkb.addItemListener(flagListener);
		unicodeCaseCkb.addItemListener(flagListener);
		canonEqCkb.addItemListener(flagListener);
		editorDocumentListener = new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				regexController.setText(editArea.getText());
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				regexController.setText(editArea.getText());
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				// needed?
				regexController.setText(editArea.getText());
			}
		};
		editArea.getDocument().addDocumentListener(editorDocumentListener);
		editArea.addPropertyChangeListener("document",
				new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						((Document) evt.getOldValue())
								.removeDocumentListener(editorDocumentListener);
						((Document) evt.getNewValue())
								.addDocumentListener(editorDocumentListener);
					}
				});
		regexArea.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				regexController.setRegexStr(regexArea.getText());
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				regexController.setRegexStr(regexArea.getText());
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				// needed?
				regexController.setRegexStr(regexArea.getText());
			}
		});
		final UndoManager regexAreaundoManager = new UndoManager();
		int mask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		regexArea.getDocument().addUndoableEditListener(regexAreaundoManager);
		regexArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, mask), "undo");
		regexArea.getActionMap().put("undo", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (regexAreaundoManager.canUndo())
					regexAreaundoManager.undo();
			}
		});
		regexArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, mask), "redo");
		regexArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, mask | InputEvent.SHIFT_DOWN_MASK), "redo");
		regexArea.getActionMap().put("redo", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (regexAreaundoManager.canRedo())
					regexAreaundoManager.redo();
			}
		});
		replaceArea.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				regexController.setReplaceExpression(replaceArea.getText());
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				regexController.setReplaceExpression(replaceArea.getText());
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				// needed?
				regexController.setReplaceExpression(replaceArea.getText());
			}
		});
		final UndoManager replaceAreaUndoManager = new UndoManager();
		replaceArea.getDocument().addUndoableEditListener(replaceAreaUndoManager);
		replaceArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, mask), "undo");
		replaceArea.getActionMap().put("undo", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (replaceAreaUndoManager.canUndo())
					replaceAreaUndoManager.undo();
			}
		});
		replaceArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, mask), "redo");
		replaceArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, mask | InputEvent.SHIFT_DOWN_MASK), "redo");
		replaceArea.getActionMap().put("redo", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (replaceAreaUndoManager.canRedo())
					replaceAreaUndoManager.redo();
			}
		});
		resultTable.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				selectMatchedText(resultTableModel.getRowObject(resultTable
						.getSelectionModel().getLeadSelectionIndex()));
			}

		});
		expressionAvailable.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				regexController.setExpressionAvailable(expressionAvailable
						.isSelected());
			}
		});
		liveUpdateCkb.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				updateNowBtn.setEnabled(!liveUpdateCkb.isSelected());
				regexController.setLiveUpdate(liveUpdateCkb.isSelected());
			}
		});
		ActionListener buttonsActions = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object source = e.getSource();
				if (updateNowBtn == source)
					regexController.update();
				else if (replaceSelected == source)
					doReplace(false);
				else if (replaceAll == source)
					doReplace(true);
			}
		};
		updateNowBtn.addActionListener(buttonsActions);
		replaceSelected.addActionListener(buttonsActions);
		replaceAll.addActionListener(buttonsActions);
	}

	private void initComponent() {
		JLabel labelTemp;
		createComponent();
		installListener();
		ToolTipManager m = ToolTipManager.sharedInstance();
		m.setDismissDelay(30000);
		m.setReshowDelay(800);
		setLayout(new BorderLayout(0, 3));
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		labelTemp = new JLabel(StrUtils.getStr("SearchPanel.regexFlags"));
		labelTemp.setAlignmentX(CENTER);
		leftPanel.add(labelTemp);
		JPanel flagPanel = new JPanel();
		flagPanel.setLayout(new GridLayout(4, 2, 0, 0));
		flagPanel.add(unixLinesCkb);
		flagPanel.add(caseInsensitiveCkb);
		flagPanel.add(commentsCkb);
		flagPanel.add(multilineCkb);
		flagPanel.add(literalCkb);
		flagPanel.add(dotallCkb);
		flagPanel.add(unicodeCaseCkb);
		flagPanel.add(canonEqCkb);
		flagPanel.setMaximumSize(flagPanel.getPreferredSize());
		leftPanel.add(flagPanel);
		labelTemp = new JLabel(StrUtils.getStr("SearchPanel.regularExpression"));
		labelTemp.setAlignmentX(CENTER);
		leftPanel.add(labelTemp);
		leftPanel.add(new JScrollPane(regexArea));
		labelTemp = new JLabel(StrUtils.getStr("SearchPanel.replaceExpression"));
		labelTemp.setAlignmentX(CENTER);
		leftPanel.add(labelTemp);
		leftPanel.add(new JScrollPane(replaceArea));

		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		labelTemp = new JLabel(StrUtils.getStr("SearchPanel.matchResult"));
		labelTemp.setAlignmentX(CENTER);
		rightPanel.add(labelTemp);
		rightPanel.add(new JScrollPane(resultTable,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				leftPanel, rightPanel);
		splitPane.setDividerSize(3);
		JPanel leftButtonsPanel = new JPanel();
		JPanel rightButtonsPanel = new JPanel();
		leftButtonsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 0));
		rightButtonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 15, 0));
		leftButtonsPanel.add(expressionAvailable);
		leftButtonsPanel.add(liveUpdateCkb);
		leftButtonsPanel.add(updateNowBtn);
		rightButtonsPanel.add(divertFocus);
		rightButtonsPanel.add(outputResultToNewWindow);
		rightButtonsPanel.add(replaceAll);
		rightButtonsPanel.add(replaceSelected);
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new BorderLayout());
		buttonsPanel.add(leftButtonsPanel, BorderLayout.WEST);
		buttonsPanel.add(rightButtonsPanel, BorderLayout.EAST);

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());
		centerPanel.add(splitPane);
		centerPanel.add(buttonsPanel, BorderLayout.SOUTH);
		this.add(centerPanel, BorderLayout.CENTER);
		JPanel statsPanel = new JPanel();
		statsPanel.setLayout(new BorderLayout());
		statsPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0,
				statsLabel.getBackground().darker()));
		statsPanel.add(statsLabel, BorderLayout.WEST);
		statsPanel.add(matchResultLabel, BorderLayout.EAST);
		// ensure statsLabel has same height while setText(null)
		statsPanel.setPreferredSize(statsLabel.getPreferredSize());
		this.add(statsPanel, BorderLayout.SOUTH);

	}

	private void selectMatchedText(MatchResultInfo resultInfo) {
		if (regexController.isResultUptodate() == false)
			statsLabel.setText(StrUtils.getStr("SearchPanel.resultOutOfDate"));
		else if (resultInfo != null && divertFocus.isSelected()) {
			try {
				Rectangle r = editArea.getUI().modelToView(editArea,
						resultInfo.getEndPos());
				editArea.scrollRectToVisible(r);
				editArea.setCaretPosition(resultInfo.getEndPos());
				editArea.moveCaretPosition(resultInfo.getStartPos());
				editArea.requestFocusInWindow();
			} catch (BadLocationException e) {
			}
		}
	}

	private void doReplace(boolean isReplaceAll) {
		if (regexController.isResultUptodate() == false) {
			statsLabel.setText(StrUtils.getStr("SearchPanel.resultOutOfDate"));
			return;
		}
		List<MatchResultInfo> result;
		if (isReplaceAll)
			if (regexController.update() == RegexController.UPDATE_COMMITTED
					|| isResultDisplayed == false) {
				statsLabel.setText(StrUtils
						.getStr("SearchPanel.replacingCanceled"));
				return;
			} else {
				result = resultTableModel.getAllData();
				if (result == null || result.size() == 0) {
					statsLabel.setText(StrUtils
							.getStr("SearchPanel.resultIsEmpty"));
					return;
				}
			}
		else {
			int[] indexes = resultTable.getSelectedRows();
			if (indexes.length == 0) {
				statsLabel.setText(StrUtils
						.getStr("SearchPanel.selectionIsEmpty"));
				return;
			} else
				try {
					result = regexController.getRealReplaceResult(indexes);
					if (isResultDisplayed == false) {
						statsLabel.setText(StrUtils
								.getStr("SearchPanel.replacingCanceled"));
						return;
					}

				} catch (Exception e) {
					statsLabel.setText(StrUtils
							.getStr("SearchPanel.replacingCanceled"));
					return;
				}
		}
		if (result != null) {
			StringBuilder targetText = new StringBuilder();
			String originalText = editArea.getText();
			if (outputResultToNewWindow.isSelected()) {
				for (MatchResultInfo match : result)
					targetText.append(match.getReplaceStr());
				outPutDlg.showOutPutDlg(
						SwingUtilities.windowForComponent(this),
						targetText.toString());
			} else {
				int oldStartPos = 0;
				for (MatchResultInfo match : result) {
					targetText.append(originalText, oldStartPos,
							match.getStartPos());
					targetText.append(match.getReplaceStr());
					oldStartPos = match.getEndPos();
				}
				targetText.append(originalText, oldStartPos,
						originalText.length());
				editArea.setText(targetText.toString());
			}
		}
	}

	@Override
	public void onStart() {
		matchResultLabel.setText(StrUtils.getStr("SearchPanel.calculating"));
	}

	@Override
	public void onResultChanged(final List<MatchResultInfo> result,
			final String errorInfo) {
		isResultDisplayed = false;
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				statsLabel.setText(errorInfo);
				resultTableModel.setData(result);
				matchResultLabel.setText(StrUtils.getStr(
						"SearchPanel.resultLabel",
						result == null ? 0 : result.size()));
				isResultDisplayed = true;
			}
		});
	}

	static private class OutPutDialog {
		private JTextArea outPutArea = new JTextArea();
		private JDialog dlg;

		synchronized JDialog getDlgInstance(Window owner) {
			if (dlg == null) {
				dlg = new JDialog(owner,
						StrUtils.getStr("SearchPanel.replacement"),
						ModalityType.DOCUMENT_MODAL);
				dlg.setSize(500, 500);
				JScrollPane jsp = new JScrollPane(outPutArea);
				jsp.setRowHeaderView(new LineLabel(outPutArea));
				dlg.getContentPane().add(jsp);
				dlg.getContentPane().add(
						new JButton(new AbstractAction(
								StrUtils.getStr("SearchPanel.copyAndClose")) {
							private static final long serialVersionUID = -4859439907152041642L;

							@Override
							public void actionPerformed(ActionEvent e) {
								Toolkit.getDefaultToolkit()
										.getSystemClipboard()
										.setContents(
												new StringSelection(outPutArea
														.getText()), null);
								dlg.dispose();
							}
						}), BorderLayout.SOUTH);
				dlg.setLocationRelativeTo(owner);
			}
			return dlg;
		}

		void showOutPutDlg(Window owner, String result) {
			outPutArea.setText(result);
			getDlgInstance(owner).setVisible(true);
		}
	}

	static class MyTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 8375142542174693067L;
		private static String[] columnNames = {
				StrUtils.getStr("SearchPanel.sequence"),
				StrUtils.getStr("SearchPanel.matchedContent"),
				StrUtils.getStr("SearchPanel.replacement"),
				StrUtils.getStr("SearchPanel.posStart"),
				StrUtils.getStr("SearchPanel.posEnd") };
		private static int[] columnWidth = { 50, 150, 150, 45, 45 };
		private List<MatchResultInfo> data;

		void setData(List<MatchResultInfo> data) {
			this.data = data;
			fireTableDataChanged();
		}

		/** may be null */
		List<MatchResultInfo> getAllData() {
			return data;
		}

		MatchResultInfo getRowObject(int index) {
			if (data == null || index < 0 || index >= data.size())
				return null;
			else
				return data.get(index);
		}

		@Override
		public int getRowCount() {
			return data == null ? 0 : data.size();
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (data == null)
				return null;
			switch (columnIndex) {
			case 0:
				return rowIndex + 1;
			case 1:
				return data.get(rowIndex).getMatchedStr();
			case 2:
				return data.get(rowIndex).getReplaceStr();
			case 3:
				return data.get(rowIndex).getStartPos();
			case 4:
				return data.get(rowIndex).getEndPos();
			}
			return null;
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}

		public int getColumnWidth(int column) {
			return columnWidth[column];
		}
	}

	static private class Helper {
		public static final String UNIX_LINES_TIP = StrUtils
				.getStr("SearchPanel.UNIX_LINES_TIP");
		public static final String CASE_INSENSITIVE_TIP = StrUtils
				.getStr("SearchPanel.CASE_INSENSITIVE_TIP");
		public static final String COMMENTS_TIP = StrUtils
				.getStr("SearchPanel.COMMENTS_TIP");
		public static final String MULTILINE_TIP = StrUtils
				.getStr("SearchPanel.MULTILINE_TIP");
		public static final String LITERAL_TIP = StrUtils
				.getStr("SearchPanel.LITERAL_TIP");
		public static final String DOTALL_TIP = StrUtils
				.getStr("SearchPanel.DOTALL_TIP");
		public static final String UNICODE_CASE_TIP = StrUtils
				.getStr("SearchPanel.UNICODE_CASE_TIP");
		public static final String CANON_EQ_TIP = StrUtils
				.getStr("SearchPanel.CANON_EQ_TIP");
	}
}