import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JFrame;
import java.util.ArrayList;

/* A game of hangman.
 */
public class Hangman extends Applet implements Runnable, KeyListener, ItemListener {
	private Image dbImage;
	private Graphics dbGraphics;
	private Thread gameThread;
	private Image head, body, leftArm, rightArm, leftLeg, rightLeg, stand;
	private CheckboxMenuItem[] difficulties = {new CheckboxMenuItem("Easy",true), new CheckboxMenuItem("Medium"), new CheckboxMenuItem("Hard"), new CheckboxMenuItem("Boy Names"), new CheckboxMenuItem("Girl Names")};
	private CheckboxMenuItem[] wordLengths = {new CheckboxMenuItem("4"), new CheckboxMenuItem("5"), new CheckboxMenuItem("6"), new CheckboxMenuItem("7"), new CheckboxMenuItem("8"), new CheckboxMenuItem("9"), new CheckboxMenuItem("10"), new CheckboxMenuItem("11"), new CheckboxMenuItem("12"), new CheckboxMenuItem("Any",true)};
	private CheckboxMenuItem[] letterFonts = {new CheckboxMenuItem("Arial",true), new CheckboxMenuItem("Script"), new CheckboxMenuItem("Stencil"), new CheckboxMenuItem("Music")};
	private ArrayList<Character> wrongLetters;
	private char[] word;
	private char[] rightLetters;
	private Font[] fonts = {new Font("Arial Black",Font.BOLD,25), new Font("Freestyle Script",Font.BOLD,25), new Font("Stencil",Font.BOLD,25), new Font("Opus Figured Bass",Font.BOLD,25) };
	private int fontIndex = 0;
	private WordFileManager wordFileManager;
	private Menu difficultyMenu, wordLengthMenu, fontMenu;
	private MenuBar mb;

	/* Applet initialization. Set size, create images, add window components, initialize game varibales.
	 */
	public void init() {
		setSize(900,500);
		addKeyListener(this);

		Toolkit tk = Toolkit.getDefaultToolkit();
		head = tk.createImage("dafuq.JPG");
		body = tk.createImage("body.JPG");
		rightArm = tk.createImage("rightArm.JPG");
		leftArm = tk.createImage("leftArm.JPG");
		rightLeg = tk.createImage("rightLeg.JPG");
		leftLeg = tk.createImage("leftLeg.JPG");
		stand = tk.createImage("stand.JPG");

		wordFileManager = new WordFileManager("EasyWords.txt");
		int randomLength = 4 + (int)(9*Math.random());
		word = wordFileManager.findWord(randomLength).toCharArray();
		for (int i = 0; i < word.length; i++) {
			word[i] = Character.toUpperCase(word[i]);
		}
		rightLetters = new char[word.length];
		for (int i = 0; i < rightLetters.length; i++) {
			rightLetters[i] = '!';
		}
		difficultyMenu = new Menu("Difficulty");
		for (CheckboxMenuItem j : difficulties) {
			difficultyMenu.add(j);
			j.addItemListener(this);
		}
		wordLengthMenu = new Menu("Word Length");
		for (CheckboxMenuItem j : wordLengths) {
			wordLengthMenu.add(j);
			j.addItemListener(this);
		}
		fontMenu = new Menu("Font");
		for (CheckboxMenuItem j : letterFonts) {
			fontMenu.add(j);
			j.addItemListener(this);
		}
		mb = new MenuBar();
		mb.add(difficultyMenu);
		mb.add(wordLengthMenu);
		mb.add(fontMenu);
		Object f = getParent();
		while (! (f instanceof Frame)) {
			f = ((Component) f).getParent(); //go back until a parent is found that is instanceof Frame
		}
  		Frame frame = (Frame)f;
  		frame.setMenuBar(mb);
		wrongLetters = new ArrayList<Character>();
		//System.out.println(word);
	}

	/* Applet main loop. DPaint images and strings.
	 */
	public void paint(Graphics g) {
		g.setColor(Color.BLACK);
		g.drawImage(stand, -10, 30, this);
		for (int i = 0; i < word.length; i++) {
			g.drawLine(50*i + 300, 350, 50*i + 325, 350);
		}
		g.setFont(fonts[fontIndex]);
		g.setColor(Color.RED);
		for (int i = 0; i < wrongLetters.size(); i++) {
			g.drawString(wrongLetters.get(i) + "", 400 + 40*i,150);
			if (i >= 0) {
				g.drawImage(head, 180, 80, this);
				if (i >= 1) {
					g.drawImage(body, 205, 134, this);
					if (i >= 2) {
						g.drawImage(rightArm, 212, 135, this);
						if (i >= 3) {
							g.drawImage(leftArm, 157, 132, this);
							if (i >= 4) {
								g.drawImage(rightLeg, 208, 273, this);
								if (i >= 5) {
									g.drawImage(leftLeg, 162, 273, this);
									g.setFont(new Font("Rockwell Extra Bold",Font.BOLD, 25));
									g.drawString("YOU GOT HUNG",400,200);
									g.setFont(new Font("Times New Roman",Font.PLAIN, 20));
									g.setColor(Color.GRAY);
									g.drawString("Select a game option from a menu to start a new game.", 300, 250);
									g.setColor(Color.BLUE);
									if (difficulties[2].getState()) {
										g.drawString(wordFileManager.getDefinition(), 300, 380);
									}
									g.setFont(fonts[fontIndex]);
									g.setColor(Color.RED);
									for (int j = 0; j < word.length; j++) {
										if(rightLetters[j] == '!') {
											g.drawString(word[j] + "", 50*j + 305, 350);
										}
									}
								}
							}
						}
					}
				}

			}
		}
		g.setColor(Color.BLACK);
		boolean win = true;
		for (int i = 0; i < rightLetters.length; i++) { //check for a win
			if (rightLetters[i] != '!') {
				g.drawString(rightLetters[i] + "", 50*i + 305,350);
			} else {
				win = false;
			}
		}
		if (win) {
			g.setFont(new Font("Rockwell Extra Bold",Font.BOLD, 25));
			g.setColor(Color.GREEN);
			g.drawString("YOU SAVED YOURSELF THIS TIME...", 300, 200);
			g.setFont(new Font("Times New Roman",Font.PLAIN, 20));
			g.setColor(Color.GRAY);
			g.drawString("Select a game option from a menu to start a new game.", 300, 250);
			g.setFont(fonts[fontIndex]);
			g.setColor(Color.RED);
		}
	}

	/* Double-buffer the Applet.
	 */
	public void update(Graphics g) {
		if (dbImage == null) {
			dbImage = createImage(getSize().width, getSize().height);
			dbGraphics = dbImage.getGraphics();
		}
		dbGraphics.setColor(getBackground());
		dbGraphics.fillRect(0, 0, getSize().width, getSize().height);
		dbGraphics.setColor(getForeground());
		paint(dbGraphics);
		g.drawImage(dbImage, 0, 0, this);
	}

	public void keyPressed(KeyEvent e) {}

	/* Deal with a guessed letter.
	 */
	public void keyReleased(KeyEvent e) {
		char letter = (char)e.getKeyCode();
		boolean found = false;
		for (int i = 0; i < word.length; i++) {
			if (letter == word[i]) {
				rightLetters[i] = word[i];
				found = true;
			}
			if (i == word.length-1 && !found) {
				boolean repeat = false;
				for (char c : wrongLetters) {
					if (c == letter) {
						repeat = true;
					}
				}
				if(!repeat) {
					wrongLetters.add(letter);
				}
			}
		}

	}

	public void keyTyped(KeyEvent e) {}

	/* Change the game settings.
	 */
	public void itemStateChanged(ItemEvent e) {
		Object source = e.getSource();
		if (source == difficulties[0]) {
			difficulties[0].setState(true);
			difficulties[1].setState(false);
			difficulties[2].setState(false);
			difficulties[3].setState(false);
			difficulties[4].setState(false);
			wordFileManager = new WordFileManager("EasyWords.txt");
			int randomLength = 4 + (int)(9*Math.random());
			word = wordFileManager.findWord(randomLength).toCharArray();
			for (int i = 0; i < word.length; i++) {
				word[i] = Character.toUpperCase(word[i]);
			}
		} else if(source == difficulties[1]) {
			difficulties[1].setState(true);
			difficulties[0].setState(false);
			difficulties[2].setState(false);
			difficulties[3].setState(false);
			difficulties[4].setState(false);
			wordFileManager = new WordFileManager("MediumWords.txt");
			int randomLength = 4 + (int)(9*Math.random());
			word = wordFileManager.findWord(randomLength).toCharArray();
			for (int i = 0; i < word.length; i++) {
				word[i] = Character.toUpperCase(word[i]);
			}
		} else if(source == difficulties[2]) {
			difficulties[2].setState(true);
			difficulties[0].setState(false);
			difficulties[1].setState(false);
			difficulties[3].setState(false);
			difficulties[4].setState(false);
			wordFileManager = new WordFileManager("HardWords.txt");
			int randomLength = 4 + (int)(9*Math.random());
			word = wordFileManager.findWord(randomLength).toCharArray();
			for (int i = 0; i < word.length; i++) {
				word[i] = Character.toUpperCase(word[i]);
			}
		} else if(source == difficulties[3]) {
			difficulties[3].setState(true);
			difficulties[2].setState(false);
			difficulties[0].setState(false);
			difficulties[1].setState(false);
			difficulties[4].setState(false);
			wordFileManager = new WordFileManager("boynames.txt");
			int randomLength = 4 + (int)(9*Math.random());
			word = wordFileManager.findWord(randomLength).toCharArray();
			for (int i = 0; i < word.length; i++) {
				word[i] = Character.toUpperCase(word[i]);
			}
		} else if(source == difficulties[4]) {
			difficulties[4].setState(true);
			difficulties[2].setState(false);
			difficulties[0].setState(false);
			difficulties[1].setState(false);
			difficulties[3].setState(false);
			wordFileManager = new WordFileManager("girlnames.txt");
			int randomLength = 4 + (int)(9*Math.random());
			word = wordFileManager.findWord(randomLength).toCharArray();
			for (int i = 0; i < word.length; i++) {
				word[i] = Character.toUpperCase(word[i]);
			}
		}
		for (int i = 0; i < 9; i++) {
			if (source == wordLengths[i]) {
				wordLengths[i].setState(true);
				wordLengths[9].setState(false);
				word = wordFileManager.findWord(i+4).toCharArray();
				for (int j = 0; j < word.length; j++) {
					word[j] = Character.toUpperCase(word[j]);
				}
			} else {
				wordLengths[i].setState(false);
			}
		}
		if (source == wordLengths[9]) {
			wordLengths[9].setState(true);
			for (int i = 0; i < 9; i++) wordLengths[i].setState(false);
			int randomLength = 4 + (int)(9*Math.random());
			word = wordFileManager.findWord(randomLength).toCharArray();
			for (int i=0; i < word.length; i++) {
				word[i] = Character.toUpperCase(word[i]);
			}
		}
		boolean fontSwitch = false;
		for (int i = 0; i < 4; i++) {
			if (source == letterFonts[i]) {
				letterFonts[i].setState(true);
				fontIndex = i;
				fontSwitch = true;
			}
			else {
				letterFonts[i].setState(false);
			}
		}
		if(!fontSwitch) {
			reset();
		}
	}

	/* Reset the current word.
	 */
	public void reset() {
		rightLetters = new char[word.length];
		for (int i = 0; i < rightLetters.length; i++) {
			rightLetters[i] = '!';
		}
		wrongLetters = new ArrayList<Character>();
		//System.out.println(word);
	}

	/* Start the Thread.
	 */
	public void start() {
		if (gameThread == null) {
			gameThread = new Thread(this);
			gameThread.start();
		}
	}

	/* Thread's main loop.
	 */
	public void run() {
		while (gameThread != null) {
			repaint();
			try	{
				Thread.sleep(20);
			}
			catch(InterruptedException e) {
			}
		}
	}

	/* Kill the thread.
	*/
	public void stop() {
		gameThread = null;
	}

	/* Pack the Applet into a JFrame and run it.
	 */
	public static void main(String[] args){
		JFrame frame = new JFrame("Hangman");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.getContentPane().setBackground(Color.WHITE);
		Applet thisApplet = new Hangman();
		frame.getContentPane().add(thisApplet, BorderLayout.CENTER);
		thisApplet.init();
		frame.setSize(thisApplet.getSize());
		thisApplet.start();
		frame.setVisible(true);
	}
}

