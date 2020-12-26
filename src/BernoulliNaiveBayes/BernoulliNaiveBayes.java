package BernoulliNaiveBayes;

import java.util.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.lang.Math;
import java.math.*;

public class BernoulliNaiveBayes {
	
	private Vocabulary v;
	//vocabulary table
	private Object[] vocabulary_table;
	//training vectors (last position is spam(1)/ham(0))
	private List<int[]> training_vectors;
	//for new mails
	private List<int[]> test_vectors = new ArrayList<int[]>();
	//table with test set mails names
	private Object[] mails;
	//training data spam probabilities table
	private double[] training_spam_probabilities;
	//training data ham probabilities table
	private double[] training_ham_probabilities;
	
	public BernoulliNaiveBayes() {
		
		Vocabulary v = new Vocabulary();
		
		this.v = v;
		this.vocabulary_table = v.vocabulary();
		this.training_vectors = v.TrainVectors(this.vocabulary_table);

	}
	
	//add new word to vocabulary
	public Object[] VocabularyUpdate() {
		
		System.out.println("Updating vocabulary...");
		
		File f = null;
		BufferedReader reader = null;
		String line;
		int counter = 0;
		//a variable is for how many pu files to read
		int a = 1;
		
		try {
			Path dirPath = Paths.get("pu_corpora_public");	// create directory path: pu_corpora_public
			
			try(DirectoryStream<Path> dirPathFolders = Files.newDirectoryStream(dirPath)) {
	
				for(Path folders : dirPathFolders) {
					if(a!=0) {
			
						String b = "";
						b = Integer.toString(a);
						Path dirPathFolder = Paths.get(dirPath.toString().concat("//pu").concat(b)); 	//directory path: pu_corpora_public//puA
			
						try (DirectoryStream<Path> dirPaths = Files.newDirectoryStream(dirPathFolder)) {	// get all files from directory
						
							String d = "";
							d = Integer.toString(10);
							Path dirPathTxt = Paths.get(dirPathFolder.toString().concat("//part").concat(d));
				
							//txt
							try (DirectoryStream<Path> dirPathsTxt = Files.newDirectoryStream(dirPathTxt)) {
								//in specific part folder
								for (Path txt : dirPathsTxt) {
								
									try {
										String t = txt.getFileName().toString();
										
										f = new File(dirPathTxt.toString().concat("//").concat(t));
									}
									catch (NullPointerException e){
										System.err.println("File not found.");
									}
									
									try {
										//txt files
										reader = new BufferedReader(new FileReader(f));
										}
									catch (FileNotFoundException e ){
										System.err.println("Error opening file!!!");
									}
									try	{
										counter = 0;
										line = reader.readLine();
										while(line!=null) {
									
											counter++;
											StringTokenizer st = new StringTokenizer(line," ");
											int flag = 0;
											while (st.hasMoreTokens()) {
												//put words in vocabulary array
												
												if(line.startsWith("Subject:") && flag==0) {
													st.nextToken();
													flag = 1;
												}
												
												int s;
												
												if(st.hasMoreTokens()) {
													s = Integer.parseInt(st.nextToken());
												
													for(int i=0; i<vocabulary_table.length; i++) {
														//first check if it already exists in the vocabulary
														if(vocabulary_table[i].equals(s)) {
															continue;
														}
														else {
															this.vocabulary_table[this.vocabulary_table.length-1] = s;
															break;
														}
													}
												}
											}
											
											line = reader.readLine();
										}
									}
									catch (IOException e){
										System.err.println("Error reading line " + counter + ".");
									}
									try {
										reader.close();
									}
									catch (IOException e){
										System.err.println("Error closing file.");
									}
								}
							}
						}
						a--;
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return this.vocabulary_table;
	}
	
	//list that contains all vectors from test data
	public List<int[]> getNewMails() {
		
		System.out.println("Creating test data vectors...");
		
		File f = null;
		BufferedReader reader = null;
		String line;
		int counter = 0;
		List<String> mailstemp = new ArrayList<String>();
		//a variable is for how many pu files to read
		int a = 1;
		
		try {
			
			Path dirPath = Paths.get("pu_corpora_public");	// create directory path: pu_corpora_public
			
			try(DirectoryStream<Path> dirPathFolders = Files.newDirectoryStream(dirPath)) {
	
				for(Path folders : dirPathFolders) {
					if(a!=0) {
			
						String b = "";
						b = Integer.toString(a);
						Path dirPathFolder = Paths.get(dirPath.toString().concat("//pu").concat(b)); 	//directory path: pu_corpora_public//puA
			
						try (DirectoryStream<Path> dirPaths = Files.newDirectoryStream(dirPathFolder)) {	// get all files from directory
						
							String d = Integer.toString(10);
							Path dirPathTxt = Paths.get(dirPathFolder.toString().concat("//part").concat(d));
						
							try (DirectoryStream<Path> dirPathsTxt = Files.newDirectoryStream(dirPathTxt)) {
								//in specific part folder
								for (Path txt : dirPathsTxt) {
									
									try {
										String t = txt.getFileName().toString();
										
										mailstemp.add(t);
										
										f = new File(dirPathTxt.toString().concat("//").concat(t));
									}
									catch (NullPointerException e){
										System.err.println("File not found.");
									}
										
									try {
										//txt files
										reader = new BufferedReader(new FileReader(f));
									}
									catch (FileNotFoundException e ){
										System.err.println("Error opening file!!!");
									}
									
									try	{
										int[] temp = new int[this.vocabulary_table.length+1];
										
										for(int i=0;i<temp.length; i++) {
											temp[i] = 0;
										}
										counter = 0;
										line = reader.readLine();
										while(line!=null) {
										
											counter++;
											StringTokenizer st = new StringTokenizer(line," ");
											int flag = 0;
											
											while (st.hasMoreTokens()) {
											
												if(line.startsWith("Subject:") && flag==0) {
													st.nextToken();
													flag = 1;
												}
												
												int s;
													
												//calculate how many times each word exists
												if(st.hasMoreTokens()) {
													s = Integer.parseInt(st.nextToken());
									
													//find the word in vocabulary table
													for(int i=0; i<this.vocabulary_table.length; i++) {
														if(this.vocabulary_table[i].equals(s)) {
															temp[i] = 1;
															break;
														}
													}
												}	
											}
							
											line = reader.readLine();
										}
									
										this.test_vectors.add(temp);
									}
									catch (IOException e){
										System.err.println("Error reading line " + counter + ".");
									}
							
									try {
										reader.close();
									}
									catch (IOException e){
										System.err.println("Error closing file.");
									}
								}
							}
						}
						a--;
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		this.mails = mailstemp.toArray();
		
		return this.test_vectors;
	}
	
	//calculates how many spams training_vectors has
	private int getSpams() {
		
		int spams = 0;
		
		for(int[] mail : this.training_vectors) {
			
			if(mail[mail.length-1]==1) spams++;
		}
		
		return spams;
	}
	
	//calculates how many hams training_vectors has
	private int getHams() {
		
		int hams = 0;
		
		for(int[] mail : this.training_vectors) {
			
			if(mail[mail.length-1]==0) hams++;
		}
		
		return hams;
	}
	
	//calculates the probability of a mail in training set being a spam
	public double getSpamProbability() {
		
		double spam_probability = (double) getSpams()/this.training_vectors.size();
		
		return spam_probability;
	}
	
	//calculates the probability of a mail in training set being a ham
	public double getHamProbability() {
		
		double ham_probability = (double) getHams()/this.training_vectors.size();
		
		return ham_probability;
	}
	
	//calculates the probability P(x=1|C=spam)
	public double calcSpamAttributeProbability(int x) {
		
		//counts how many times attribute x exists in training_vectors, given that the mail is a spam
		int y = 0;
		
		for(int[] mail : this.training_vectors) {
			//if attribute in cell x is 1 (mail contains word represented in cell x) and mail is spam
			if(mail[x]==1 && mail[mail.length-1]==1) y++;
		}
		
		//usage of Laplace
		double res = (double) (y+1)/(getSpams()+2);

		return res;	
	}
	
	//calculates the probability P(x=1|C=ham)
	public double calcHamAttributeProbability(int x) {
		
		//counts how many times attribute x exists in training_vectors, given that the mail is a ham
		int y = 0;
		
		for(int[] mail : this.training_vectors) {
			//if attribute in cell x is 1 (mail contains word represented in cell x) and mail is ham
			if(mail[x]==1 && mail[mail.length-1]==0) y++;
		}
		
		//usage of Laplace
		double res = (double) (y+1)/(getHams()+2);

		return res;	
	}
	
	//calculates how many times attribute x exists in train set mails given the mail is spam
	public double[] TrainSpamProbability() {
		
		this.training_spam_probabilities = new double[this.vocabulary_table.length];

		for(int x=0; x<this.training_spam_probabilities.length; x++) {
			
			for(int[] mail : this.training_vectors) {
				this.training_spam_probabilities[x] = calcSpamAttributeProbability(x);
			}
			
		}
		
		return this.training_spam_probabilities;
	}
	
	//calculates how many times attribute x exists in train set mails given the mail is spam
	public double[] TrainHamProbability() {
		
		this.training_ham_probabilities = new double[this.vocabulary_table.length];

		for(int x=0; x<this.training_ham_probabilities.length; x++) {
			
			for(int[] mail : this.training_vectors) {
				this.training_ham_probabilities[x] = calcHamAttributeProbability(x);
			}
		}
		
		return this.training_ham_probabilities;
	}
	
	//spam or ham
	public List<int[]> TestProbability() {
		
		this.test_vectors = getNewMails();
		
		System.out.println("Calculating probabilities...");
		System.out.println("");
		
		int i = 0;
		
		double spam_sum = 0;
		double ham_sum = 0;
		
		for(int[] mail : this.test_vectors) {
			for(int x=0; x<mail.length-1; x++) {
				//usage of a log transform of the probabilities to avoid underflow
				if(mail[x]==1) {
					spam_sum += Math.log(1 + this.training_spam_probabilities[x]);
					ham_sum += Math.log(1 + this.training_ham_probabilities[x]);
				}
				else if(mail[x]==0) {
					spam_sum += Math.log(2 - this.training_spam_probabilities[x]);
					ham_sum += Math.log(2 - this.training_ham_probabilities[x]);
				}
				
			}
			
			//usage of Laplace
			
			double spam_probability = Math.log(getSpamProbability())+spam_sum;
			
			double ham_probability = Math.log(getHamProbability())+ham_sum;
			
			if(spam_probability < ham_probability) mail[mail.length-1] = 0;
			else mail[mail.length-1] = 1;

			i++;
			spam_sum = 0;
			ham_sum = 0;
			
		}
		
		return this.test_vectors;
	}
	
	public void BNB() {
		
		double s = getSpamProbability();
		this.vocabulary_table = VocabularyUpdate();
		this.training_spam_probabilities = TrainSpamProbability();
		this.training_ham_probabilities = TrainHamProbability();
		this.test_vectors = TestProbability();
		
		int x = 0;
		int legit = 0;
		int spms = 0;
		int predicted_legit = 0;
		int predicted_spms = 0;
		int wrong_legit = 0;
		int wrong_spms = 0;

		for(int[] mail : this.test_vectors) {
			
			String p = (String) this.mails[x];
			if(p.contains("legit")) legit++;
			else if(p.contains("spms")) spms++;
			//said ham and was ham
			if(mail[mail.length-1]==0 && p.contains("legit")) predicted_legit++;
			//said spam and was spam
			else if(mail[mail.length-1]==1 && p.contains("spms")) predicted_spms++;
			//said ham and was spam
			if(mail[mail.length-1]==0 && p.contains("spms")) wrong_legit++;
			//said spam and was ham
			else if(mail[mail.length-1]==1 && p.contains("legit")) wrong_spms++;
			x++;
		}
		System.out.println("Total ham mails: " + legit);
		System.out.println("Total spam mails: " + spms);
		System.out.println("Found correct ham mails: " + predicted_legit);
		System.out.println("Found correct spam mails: " + predicted_spms);
		System.out.println("Found ham but were spam mails: " + wrong_legit);
		System.out.println("Found spam but were ham mails: " + wrong_spms);
		System.out.println("");
	}
}