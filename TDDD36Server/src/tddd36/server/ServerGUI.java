package tddd36.server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;


public class ServerGUI extends JFrame {

	private Server server = null;
	// Yttre containern
	private JPanel mainPanel = new JPanel();

	// Center-displayen
	private JPanel centerPanel = new JPanel();
	private JTextPane display = new JTextPane();
	private JScrollPane centerPane = new JScrollPane(display);

	// Vänster meny
	private JPanel leftPanel = new JPanel();
	private JButton runButton = new JButton("Start");

	public ServerGUI() {
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(centerPane, BorderLayout.CENTER);

		display.setEditable(false);
		display.setFont(new Font("Courier New", Font.PLAIN, 13));
		
		Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
		for (Font f: fonts) {
			System.out.println(f.getFontName());
		}

		runButton.addActionListener(new ButtonListener());
		leftPanel.setBorder(new EmptyBorder(2, 4, 2, 4));
		leftPanel.add(runButton);
		mainPanel.add(leftPanel, BorderLayout.WEST);

		add(mainPanel);

		setDefaultValues();
	}

	private void setDefaultValues() {
		setTitle("Server Graphjcal User Interface");
		setSize(1200, 800);
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private class ButtonListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == runButton) {
				boolean run = server.getRun();
				server.setRun(!run);
				if (run)
					runButton.setText("Start");
				else
					runButton.setText("Stop");
			}
		}
	}

	public static void main(String[] args) {
		try {
			// Set System L&F
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println(e);
		}
		new Server(new ServerGUI());
	}

	public void print(String text) {
		append(Color.red, text);
	}

	public void append(Color c, String s) {
		
		s = "["+getCurrentTime()+"] " + s;
		
		display.setEditable(true);
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
				StyleConstants.Foreground, c);

		int len = display.getDocument().getLength(); 
		display.setCaretPosition(len); 
		display.setCharacterAttributes(aset, false);
		display.replaceSelection(s + "\n");
		display.setEditable(false);
	}

	public void printOK(String text) {
		append(new Color(30, 179, 63), text);
	}

	public void setServer(Server s) {
		server = s;
	}
	
	private String getCurrentTime() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		
		return sdf.format(date);
	}

}
