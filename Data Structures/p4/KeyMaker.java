import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

class KeyMaker {

	public enum States {
		START, SEQUENCE, UNKNOWN_CHARS, END_SEQUENCE, END
	}

	private States state = States.START;
	Scanner scan;
	private String line;
	private String key;
	private char ch;
	
	BTree tree;
	File file;
	GeneBankCreateBTree create;
	int currentChar;
	LinkedList<Character> thisArray;

	@SuppressWarnings("resource")
	KeyMaker(GeneBankCreateBTree create) throws IOException {
		this.create = create;
		file = create.getFile();
		tree = new BTree(create.getDegree(), create.getFile());
		scan = new Scanner(create.getFile()).useDelimiter("\\s*");
		thisArray = new LinkedList<Character>();
		currentChar = 0;
		ch = '-';
		
	}

	long getNextKey() throws Exception{
		long finalKey = 0;
		while(finalKey == 0){
			finalKey = genNext();
		}
		return finalKey;
		}
	private long genNext() throws Exception {
		
		switch (state) {

		case START: {
			start();
			
			break;
		}
		case SEQUENCE: {
			String seq = sequence();
			if(seq != ""){
				key = "";
				System.out.println(seq);
				return encode(seq);
			}
	
			break;
		}
		
		case UNKNOWN_CHARS: {
			unknownChar();
			state = States.SEQUENCE;
			break;
		}

		case END_SEQUENCE: {
			endSequence();
			break;
		}
		case END:{
			scan.close();
			return -1;
		}
		default:
			break;
		}
		//returns if key is not complete
		return 0;
	}
	private void start(){
		key = "";
			while(scan.hasNextLine()){
				line = scan.nextLine();
			
			if (line == null) {
				state = States.END;
				break;

			} else if (line.contains("ORIGIN")) {
				state = States.SEQUENCE;
				ch = (char)scan.next().charAt(0);
				break;
			}
			}
		
	}
	
	private String sequence(){
			if(Character.isDigit(ch)){
				ch = (char)scan.next().charAt(0);
			}

			if (ch == '/') {
				state = States.END_SEQUENCE;
				
			} else if (ch == 'n') {

				state = States.UNKNOWN_CHARS;
				
			} else if (ch == 'a' || ch == 't' || ch == 'c' || ch == 'g') {
				if(thisArray.size() != create.getSequenceLength()){
					thisArray.addLast(ch);
					ch = scan.next().charAt(0);
					if(thisArray.size() == create.getSequenceLength()){
						String thisString = thisArray.toString().replaceAll("[,\\[\\] ]", "");
						return thisString;
					}
				}else{
					thisArray.addLast(ch);
					thisArray.removeFirst();
					ch = scan.next().charAt(0);
					return thisArray.toString().replaceAll("[,\\[\\] ]", "");
				}
			}

		
		return "";
	}
	
	private void unknownChar(){
		while(ch != 'a' || ch != 't' || ch != 'c' || ch != 'g'){
			if(ch == '/'){
				state = States.END_SEQUENCE;
			}
			else{
				ch = (char)scan.nextByte();
			}
		}
	}
	private void endSequence(){
		
			if(scan.hasNextLine())
			line = scan.nextLine();
			else{
				state = States.END;
				scan.close();
			}
				if (line.contains("ORIGIN")) {
					state = States.SEQUENCE;
				}
			
	
	}
	
	private long encode(String seq) throws Exception {
		if (seq.length() != create.getSequenceLength()) {
			throw new Exception("String of length " + seq.length() + " was passed to btree with sequenceLenght of "
					+ create.getSequenceLength());
		}
		long sequence = 0;
		for (int i = 0; i < seq.length(); i++) {
			sequence = sequence << 2;
			char c = seq.charAt(i);
			if (c == 'a' || c == 'A') {
				sequence = sequence | 0x0L;
			} else if (c == 't' || c == 'T') {
				sequence = sequence | 0x3L;
			} else if (c == 'c' || c == 'C') {
				sequence = sequence | 0x1L;
			} else if (c == 'g' || c == 'G') {
				sequence = sequence | 0x2L;
			} else {
				throw new Exception("Unexpected character: " + c);
			}
		}
		return sequence;
	}
}
