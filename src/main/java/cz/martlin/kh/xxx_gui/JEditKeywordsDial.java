package cz.martlin.kh.xxx_gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import cz.martlin.kh.logic.Config;
import cz.martlin.kh.logic.harvest2.TreeHarvestProcessData;

/**
 * Frame for seeing, removing and adding keywords.
 * 
 * @author martin
 * 
 */
public class JEditKeywordsDial extends JDialog {

	private static final long serialVersionUID = 3879474364565354878L;

	public static final Dimension BUTT_PREF_SIZE = new Dimension(100, 20);

	private final DefaultListModel<String> model = new DefaultListModel<>();
	private TreeHarvestProcessData data;

	private JList<String> keywordsLst;
	private JTextField addTextBox;

	public JEditKeywordsDial(JMainFrame parent, Config config) {
		super(parent, "View, add or remove keywords", false);

		initializeComponents();

		Dimension size = new Dimension(250, 400);
		setPreferredSize(size);
		setMinimumSize(size);

		pack();
	}

	private void initializeComponents() {
		getContentPane().setLayout(new BorderLayout(10, 10));

		JLabel headerLbl = new JLabel("List of keywords to process:");
		getContentPane().add(headerLbl, BorderLayout.NORTH);

		// JPanel sidePane = createSidePane();
		// getContentPane().add(sidePane, BorderLayout.EAST);

		JPanel bottomPane = createBottomPane();
		getContentPane().add(bottomPane, BorderLayout.SOUTH);

		JScrollPane centerPane = createCenterPane();

		getContentPane().add(centerPane, BorderLayout.CENTER);

	}

	private JScrollPane createCenterPane() {
		JScrollPane pane = new JScrollPane();

		keywordsLst = new JList<>(model);
		keywordsLst.setToolTipText("Double-click to remove keyword from list");
		keywordsLst.addMouseListener(new KeywordsLstMouseListener());

		pane.setViewportView(keywordsLst);

		return pane;
	}

	private JPanel createBottomPane() {
		JPanel pane = new JPanel();
		pane.setLayout(new BoxLayout(pane, BoxLayout.LINE_AXIS));

		addTextBox = new JTextField();
		pane.add(addTextBox);

		JButton addButt = new JButton("Add");
		addButt.setPreferredSize(BUTT_PREF_SIZE);
		addButt.addActionListener(new AddButtActionListener());
		getRootPane().setDefaultButton(addButt);
		pane.add(addButt);

		return pane;
	}

	/**
	 * Sets data to this frame. Data must not be null!
	 * 
	 * @param data
	 */
	public void setToData(TreeHarvestProcessData data) {
		this.data = data;

		model.clear();
		for (String keyword : data.getWaitingProcess()) {
			model.addElement(keyword);
		}
	}

	/**
	 * Returns back modified data.
	 * 
	 * @return
	 */
	public TreeHarvestProcessData getData() {
		return data;
	}

	/**
	 * Adss keyword from {@link #addTextBox}.
	 */
	public void addNewKeyword() {
		String newKeyword = addTextBox.getText();
		addTextBox.setText("");

		if (newKeyword == null || newKeyword.isEmpty()) {
			return;
		}

		data.addToData(newKeyword);
		model.addElement(newKeyword);
	}

	/**
	 * Removes given keyword.
	 * 
	 * @param keyword
	 */
	public void removeKeyword(String keyword) {
		data.removeFromData(keyword);
		model.removeElement(keyword);
	}

	// public void submitFrame() {
	// this.setVisible(false);
	// }
	//
	// public void cancelFrame() {
	// this.data = null;
	// this.setVisible(false);
	// }

	// public class CancelButtActionListener implements ActionListener {
	//
	// @Override
	// public void actionPerformed(ActionEvent e) {
	// cancelFrame();
	// }
	//
	// }
	//
	// public class SubmitButtActionListener implements ActionListener {
	//
	// @Override
	// public void actionPerformed(ActionEvent e) {
	// submitFrame();
	// }
	//
	// }

	public class AddButtActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			addNewKeyword();
		}

	}

	/**
	 * On doubleclick calls {@link JEditKeywordsDial#removeKeyword(String)}.
	 * 
	 * @author martin
	 * 
	 */
	public class KeywordsLstMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				int index = keywordsLst.locationToIndex(e.getPoint());

				if (index < 0 || index > model.size()) {
					return;
				}

				String keyword = model.get(index);
				removeKeyword(keyword);
			}
		}
	}
}
