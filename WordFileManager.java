import java.io.*;
import java.util.ArrayList;

/* A class to provide words from text files to a hangman game.
 */
public class WordFileManager {
	private RandomAccessFile in = null;
	private File wordFile = null;
	private ArrayList<Integer> bytes = new ArrayList<Integer>();
	private boolean definitions = false;
	private String definition = "";

	/* Create a manager which reads from the file s.
	 */
	public WordFileManager(String s) {
		wordFile = new File(s);
		try {
			in = new RandomAccessFile(wordFile, "r");
		}
		catch (FileNotFoundException e) {
			System.out.println("File cannot be located.");
		}
		if (wordFile.toString().equals("HardWords.txt") || wordFile.toString().equals("boynames.txt") || wordFile.toString().equals("girlnames.txt")) {
			definitions = true;
		} else {
			definitions = false;
		}
	}

	/* Return a word of a specified length.
	 */
	public String findWord(int length) {
		if (!definitions) {
			String word = "Z";
			boolean wordFound = false;
			try {
				int randomLine = -1;
				int range = getLines();
				while (!wordFound) {
					randomLine = (int)(range*Math.random());
					in.seek(bytes.get(randomLine));
					word = in.readLine();
					if (word != null) {
						if (word.length() == length) {
							wordFound = true;
						}
					}
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			return word;
		} else {
			String word = "Z";
			boolean wordFound = false;
			try {
				int randomLine = -1;
				int range = getLines();
				while (!wordFound) {
					randomLine = (int)(range*Math.random());
					in.seek(bytes.get(randomLine));
					word = in.readLine();
					if (word != null) {
						int splitIndex = 0;
						for (int i = 0; i < word.length(); i++) {
							if (word.charAt(i) == ' ') {
								splitIndex = i;
								break;
							}
						}
						definition = word.substring(splitIndex+1, word.length()-1);
						word = word.substring(0, splitIndex);
						if(word.length() == length) {
							wordFound = true;
						}
					}
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			return word;
		}
	}

	/* Return the number of lines in the file and update the bytes list.
	 */
	public int getLines() {
		int lineCounter = 0;
		bytes = new ArrayList<Integer>();
		int byteCounter = 0;
		try {
			in.seek(0);
			String line;
			while ((line = in.readLine()) != null) {
				lineCounter++;
				bytes.add(byteCounter += (line.length() + 2));
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			return lineCounter;
		}
	}

	/* Return the definition associtaed with the current word.
	 */
	public String getDefinition() {
		return definition;
	}
}
