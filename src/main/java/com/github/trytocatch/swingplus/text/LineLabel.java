package com.github.trytocatch.swingplus.text;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.TextUI;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;

/**
 * a component to show the line number of JTextComponent, 
 * you can use it in this way:
 * <blockquote>
 * <pre>
 * JTextArea jta = new JTextArea();
 * JScrollPane jsp = new JScrollPane(jta);
 * jsp.setRowHeaderView(new LineLabel(jta));
 * </pre>
 * </blockquote>
 * @author trytocatch
 */
public class LineLabel extends JComponent {
	private static final long serialVersionUID = -2415042750630275725L;
	private DocumentListener documentListener;
	private int offset = 3;
	private Color repetitiveLineColor = Color.lightGray;
	private int width;
	protected int rowCount;
	protected JTextComponent jTextComponent;

	public LineLabel(JTextComponent jTextComponent) {
		if (jTextComponent == null)
			throw new IllegalArgumentException("jTextComponent can't be null");
		this.jTextComponent = jTextComponent;
		setFont(new Font(Font.MONOSPACED, Font.PLAIN, this.jTextComponent
				.getFont().getSize()));
		setOpaque(true);
		setBackground(new Color(238,238,238));
		setRowCount(this.jTextComponent.getDocument().getDefaultRootElement()
				.getElementCount());
		documentListener = new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				setRowCount(e.getDocument().getDefaultRootElement()
						.getElementCount());
				repaint();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				setRowCount(e.getDocument().getDefaultRootElement()
						.getElementCount());
				repaint();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				setRowCount(e.getDocument().getDefaultRootElement()
						.getElementCount());
				repaint();
			}
		};
		this.jTextComponent.getDocument().addDocumentListener(documentListener);
		this.jTextComponent
				.addPropertyChangeListener(new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						if ("font".equals(evt.getPropertyName()))
							resetFont((Font) evt.getNewValue());
						else if ("document".equals(evt.getPropertyName())) {
							((Document) evt.getOldValue())
									.removeDocumentListener(documentListener);
							((Document) evt.getNewValue())
									.addDocumentListener(documentListener);
						}
					}
				});
		this.jTextComponent.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				setSize(getPreferredSize());
			}
		});
	}

	private void setRowCount(int rowCount) {
		if (this.rowCount != rowCount) {
			this.rowCount = rowCount;
			updateWidth();
		}
	}

	private void updateWidth() {
		int widthTemp = offset
				* 2
				+ getFontMetrics(getFont()).stringWidth(
						String.valueOf(this.rowCount));
		if (widthTemp != width) {
			width = widthTemp;
			revalidate();
		}
	}

	private void resetFont(Font newFont) {
		setFont(getFont().deriveFont((float) newFont.getSize()));
		updateWidth();
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(width, jTextComponent.getHeight());
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics g2 = g.create();
		Rectangle clipRect = g2.getClipBounds();
		if (isOpaque()) {
			g2.setColor(getBackground());
			g2.fillRect(clipRect.x, clipRect.y, clipRect.width, clipRect.height);
		}
		g2.setColor(getForeground());
		g2.setFont(getFont());
		Document document = jTextComponent.getDocument();
		Graphics greyG2 = g2.create();
		greyG2.setColor(repetitiveLineColor);
		try {
			Element rootElement;
			if (document instanceof AbstractDocument)
				((AbstractDocument) document).readLock();
			rootElement = document.getDefaultRootElement();
			FontMetrics myFontMetrics = getFontMetrics(getFont());
			FontMetrics textFontMetrics = jTextComponent
					.getFontMetrics(jTextComponent.getFont());
			TextUI ui = jTextComponent.getUI();
			int rowNum = rootElement.getElementIndex(ui.viewToModel(
					jTextComponent, clipRect.getLocation()));
			int ascent = textFontMetrics.getAscent();
			int rowHeight = textFontMetrics.getHeight();
			Element element = rootElement.getElement(rowNum);
			if (element == null)
				return;
			String rowNumStr = "";
			int x = 0, y, nextY, originalStartY, maxY;
			try {
				originalStartY = ui.modelToView(jTextComponent,
						element.getStartOffset()).y;
				maxY = ui.modelToView(jTextComponent,
						rootElement.getEndOffset() - 1).y;
			} catch (BadLocationException e1) {
				return;
			}
			maxY = Math.min(maxY, clipRect.y + clipRect.height);
			y = originalStartY;
			if (y < clipRect.y)
				y = clipRect.y - (clipRect.y - y) % rowHeight;
			nextY = 0;
			for (; y <= maxY; y += rowHeight) {
				if (y < nextY) {
					greyG2.drawString(rowNumStr, x, y + ascent);
				} else {
					rowNumStr = String.valueOf(rowNum + 1);
					x = width - offset - myFontMetrics.stringWidth(rowNumStr);
					// nextY == 0 means that it's the first time
					if (nextY != 0 || y == originalStartY)
						g2.drawString(rowNumStr, x, y + ascent);
					else
						y -= rowHeight;
					rowNum++;
					if (rowNum >= rowCount)
						nextY = Integer.MAX_VALUE;
					else
						try {
							nextY = ui.modelToView(jTextComponent, rootElement
									.getElement(rowNum).getStartOffset()).y;
						} catch (BadLocationException e) {
							break;
						}
				}
			}
		} finally {
			if (document instanceof AbstractDocument)
				((AbstractDocument) document).readUnlock();
			g2.dispose();
			greyG2.dispose();
		}
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public Color getRepetitiveLineColor() {
		return repetitiveLineColor;
	}

	public void setRepetitiveLineColor(Color repetitiveLineColor) {
		this.repetitiveLineColor = repetitiveLineColor;
	}
}