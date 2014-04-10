package GUI;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;

import client.Client;

public class ChatWindow extends JFrame implements KeyListener, ActionListener,
		MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Client client;

	Boolean wantPersTabs = false;

	String myName;

	Container cont;
	JPanel mainFrame;
	JPanel menuBar;
	JPanel sendBar;
	JCheckBox checkTabs;

	Dimension windowSize = new Dimension(800, 600);

	JTextField typeArea = new JTextField();
	DefaultListModel<String> list = new DefaultListModel<String>();
	JList<String> textArea = new JList<String>(list);
	JScrollPane msgScroller;

	JButton exit;
	JButton send;

	JLabel title;
	JLabel pusher;

	BufferedImage iconBuff;
	ImageIcon icon;

	public ArrayList<String> pNameList = new ArrayList<String>();
	DefaultListModel<String> pList = new DefaultListModel<String>();
	JList<String> pArea = new JList<String>(pList);
	JScrollPane pListScroller = new JScrollPane(pArea);

	Dimension buttonDim = new Dimension(96, 64);
	Dimension ptDim = new Dimension(96, 16);
	Dimension menuBarDim = new Dimension(96, 576);

	boolean done = false;

	public ChatWindow(String name) {
		super("Pigeon");
		client = new Client(this, name);
		myName = name;
		init();
	}

	private void init() {
		cont = getContentPane();
		setSize(windowSize);
		setBackground(Color.BLACK);

		mainFrame = new JPanel();
		mainFrame.setBackground(Color.DARK_GRAY);
		mainFrame.setLayout(new BorderLayout());

		sendBar = new JPanel();
		sendBar.setBackground(Color.DARK_GRAY);

		typeArea.setEditable(true);

		typeArea.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 3));

		typeArea.addKeyListener(this);
		sendBar.setLayout(new GridBagLayout());

		msgScroller = new JScrollPane(textArea);

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		sendBar.add(typeArea, c);
		c.fill = GridBagConstraints.HORIZONTAL;

		c.gridx = 1;

		send = new JButton("Send");
		send.setPreferredSize(new Dimension(96, 32));
		send.addActionListener(this);
		sendBar.add(send);

		menuBar = new JPanel();
		menuBar.setLayout(new GridBagLayout());
		menuBar.setBackground(Color.DARK_GRAY);
		menuBar.setPreferredSize(menuBarDim);
		// Menu buttons

		title = new JLabel("");
		try {
			iconBuff = ImageIO.read(new FileInputStream("res/PigeonTitle.png"));
			icon = new ImageIcon(iconBuff);
		} catch (IOException e) {
			System.out.println("Fuck the image!");
		}
		title.setIcon(icon);
		title.setForeground(Color.WHITE);
		title.setPreferredSize(buttonDim);
		title.setMaximumSize(buttonDim);
		title.setMinimumSize(buttonDim);
		c.weighty = 1;
		menuBar.add(title, c);

		pArea.setBackground(Color.DARK_GRAY);
		pArea.setForeground(Color.WHITE);
		pArea.addMouseListener(this);
		updateNames(myName);
		c.gridy++;
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 5000;
		c.anchor = GridBagConstraints.NORTH;
		menuBar.add(pListScroller, c);

		// options here

		checkTabs = new JCheckBox(
				"<html>Seperate<br>window for<br>personal<br>chat</html>");
		c.gridy++;
		c.weighty = -5000;
		c.anchor = GridBagConstraints.SOUTH;
		menuBar.add(checkTabs, c);

		exit = new JButton("Exit");
		exit.addActionListener(this);
		exit.setPreferredSize(buttonDim);
		exit.setMinimumSize(buttonDim);
		exit.setMaximumSize(buttonDim);
		c.gridy++;
		menuBar.add(exit, c);

		// ////////////////////////////

		mainFrame.add(sendBar, BorderLayout.SOUTH);
		mainFrame.add(msgScroller, BorderLayout.CENTER);
		mainFrame.add(menuBar, BorderLayout.WEST);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		cont.add(mainFrame);
		setVisible(true);
	}

	protected void addText(String txt) {
		String[] words = txt.split(" ");

		if (words.length >= 3 && words[1].equals("/w")) {
			typeArea.setText(words[1] + " " + words[2] + " ");
			String target = words[2];
			words[1] = "";
			words[2] = "";
			String data = "";
			if (words.length == 3) {
				data = " ";
			} else {
				for (int i = 3; i < words.length; i++) {
					data = data + " " + words[i];
				}
			}
			data = "To " + target + ": " + data;
			list.addElement(data + "\n");
			textArea.ensureIndexIsVisible(list.getSize() - 1);
			client.sendPrivate(target, data);
		} else {
			client.sendPacket(txt);
			typeArea.setText("");
		}

	}

	public void incoming(String txt) {
		txt = txt.replace("8)", "😎");
		txt = txt.replace(":)", "😉");
		list.addElement(txt + "\n");
		textArea.ensureIndexIsVisible(list.getSize() - 1);
	}

	private String generateLine(String text) {
		return myName + ": " + text;
	}

	public void disconnect(String name) {
		if (pNameList.contains(name)) {
			int index = pNameList.indexOf(name);
			pList.remove(index);
			pNameList.remove(index);
		}
	}

	public void updateNames(String name) {
		if (!pNameList.contains(name)) {
			pList.addElement(name);
			pNameList.add(name);

			list.addElement(name + " joined Pigeon!" + "\n");
			textArea.ensureIndexIsVisible(list.getSize() - 1);
		}
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyCode() == 10 && !typeArea.getText().equals("")) {
			String txt = typeArea.getText();
			txt = generateLine(txt);
			this.addText(txt);
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// i dont care

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		typeArea.requestFocus();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == this.exit) {
			System.exit(0);
		} else if (arg0.getSource() == this.send) {
			String txt = typeArea.getText();
			this.addText(generateLine(txt));
			// also.. send the text
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		JList list = (JList) arg0.getSource();
		Rectangle r = list.getCellBounds(0, list.getLastVisibleIndex());
		if (arg0.getClickCount() == 2 && r != null
				&& r.contains(arg0.getPoint())) {
			String person = pNameList.get(list.getSelectedIndex());
			if(checkTabs.isSelected()){
			new PersonalChat(this, client, myName, person);}
			else{
				typeArea.setText("/w " + person + " ");
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

}