import java.io.File;
import java.io.IOException;

public class GeneBankCreateBTree {
	int debugLevel = -1, cacheSize, degree = -1, sequenceLength = 0;
	boolean useCache = false;
	private File file;
	private File output;
	KeyMaker genKey;
	

	public GeneBankCreateBTree()throws IOException {
		
	}

	public File getFile() {
		return this.file;
	}

	public File setFile(File f) {
		return this.file = f;
	}

	public File setOutputfile(File f){
		return this.output = f;
	}
	public File getOutputfile(){
		return this.output;
	}
	public int getDebugLevel() {
		return debugLevel;
	}

	public void setDebugLevel(int debugLevel) {
		this.debugLevel = debugLevel;
	}

	public int getCacheSize() {
		return cacheSize;
	}

	public void setCacheSize(int cacheSize) {
		this.cacheSize = cacheSize;
	}

	public int getDegree() {
		return degree;
	}

	public void setDegree(int degree) {
		this.degree = degree;
	}

	public int getSequenceLength() {
		return sequenceLength;
	}

	public void setSequenceLength(int sequenceLength) {
		this.sequenceLength = sequenceLength;
	}
	private void assessArguments(String[] args, GeneBankCreateBTree btree) throws IOException{
		// Check arg length
		try {
			if (args.length < 4 || args.length > 6) {
				printUsage();
				System.exit(0);
			}

			// Check for cache
			if (args[0].equals("0") || args[0].equals("1")) {
				if (args[0].equals("0")) {
					btree.useCache = false;
				} else {
					btree.useCache = true;
				}
			} else {
				printUsage();
				System.exit(0);
			}

			if (Integer.parseInt(args[1]) >= 0) {
				if (btree.getDegree() == 0) {
					btree.setDegree(127);
				} else {
					btree.setDegree(Integer.parseInt(args[1]));
				}
			} else {
				printUsage();
				System.exit(0);
			}
			// Check file
			if (!args[2].contains(".gbk")) {
				System.out.println("Invalid file");
				printUsage();
				System.exit(0);
			}
			if (Integer.parseInt(args[3]) > 1 || Integer.parseInt(args[3]) < 31) {
				btree.setSequenceLength(Integer.parseInt(args[3]));
			} else {
				printUsage();
				System.exit(0);
			}
			btree.setFile(new File(args[2]));
			
			btree.setOutputfile(new File(btree.getFile().getName() + ".btree.data." + btree.getSequenceLength() + "." + btree.getDegree()));

			// Check sequence length
			

			// Check Debug and Cache Size
			if (args.length == 6 && btree.useCache == false) {
				printUsage();
				System.exit(0);
			} else if (args.length == 6 && btree.useCache == true) {
				btree.setCacheSize(Integer.parseInt(args[4]));
				btree.setDebugLevel(Integer.parseInt(args[5]));
				if (btree.getDebugLevel() > 1 || btree.getDebugLevel() < 0) {
					printUsage();
					System.exit(0);
				}
			} else if (args.length == 5 && btree.useCache == true) {
				btree.setCacheSize(Integer.parseInt(args[4]));
			} else if (args.length == 5 && btree.useCache == false) {
				btree.setDebugLevel(Integer.parseInt(args[4]));
				if (btree.getDebugLevel() > 1 || btree.getDebugLevel() < 0) {
					printUsage();
					System.exit(0);
				}
			}
		
			if (btree.debugLevel == 0) {
				// do some stuff
			}
			if (btree.debugLevel == 1) {
				// File dump = new File("dump");
				// tree.debugPrintIOT(dump);
			}
			

		} catch (NumberFormatException e) {
			printUsage();
		} catch (Exception e) {
			e.printStackTrace();
			printUsage();
		}	
	}

	public static void main(String[] args) throws IOException {
		GeneBankCreateBTree btree = new GeneBankCreateBTree();
		btree.assessArguments(args, btree);
		try {
			System.out.println("Degree: " + btree.getDegree() + " Seq Length: " + btree.getSequenceLength() + " File: " + btree.getFile() + " Output File: " + btree.getOutputfile());
		BTree tree = new BTree(btree.getDegree(), btree.getOutputfile());
		KeyMaker genKey = new KeyMaker(btree);
		long key = genKey.getNextKey();
	//	System.out.println(key);
		while(key != -1){
		TreeObject o = new TreeObject(key);
		
				 tree.insertNode(o);
				 key = genKey.getNextKey();
//				 System.out.println(key);
				 
				 
		}
		tree.WriteRoot();
		tree.printTree();
		System.out.println("End of File");
				 } catch (IOException e) {
				 e.printStackTrace();
				 } 
					catch (Exception e) {
					 e.printStackTrace();
					 }
	}

	
	

	private static void printUsage() {
		System.err.println("GeneBankCreateBTree <0/1(no/with Cache)> <degree> <gbk file> "
				+ "<sequence length> 1<=k<=31>" + "[<cache size>] [<debug level>]" + "[<debug level>]\n");
	}
}
