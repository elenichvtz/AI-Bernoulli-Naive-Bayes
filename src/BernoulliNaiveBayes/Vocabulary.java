package BernoulliNaiveBayes;

import java.util.*;
import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.LinkOption;

public class Vocabulary {
	
	private List<int[]> vectors;
	private String t;
	private int all = 1;
	private final static int FILES_TO_READ = 4;

	//read files
	public Object[] vocabulary() {
		
		System.out.println("Creating vocabulary...");
		
		//table for data
		List<Integer> vocabulary = new ArrayList<Integer>();
		File f = null;
		BufferedReader reader = null;
		String line;
		int counter = 0;
		//a variable is for how many pu files to read
		int a = 1;
		
		int x = FILES_TO_READ;
		
		try {

			Path dirPath = Paths.get("pu_corpora_public"); // create directory path: pu_corpora_public

			try(DirectoryStream<Path> dirPathFolders = Files.newDirectoryStream(dirPath)) {
				
				for(Path folders : dirPathFolders) {
					if(a!=0) {
						
						String b = "";
						b = Integer.toString(a);
						Path dirPathFolder = Paths.get(dirPath.toString().concat("//pu").concat(b)); 	//directory path: pu_corpora_public//puA
						
						try(DirectoryStream<Path> dirPathsFolder = Files.newDirectoryStream(dirPathFolder)) {
							
							for(Path folder : dirPathsFolder) {
								
								try (DirectoryStream<Path> dirPaths = Files.newDirectoryStream(dirPathFolder)) {	// get all files from directory
								
									//partX folders
									for(Path file : dirPaths) {
										if(x!=0) {
										
											String d = "";
											d = Integer.toString(x);
											Path dirPathTxt = Paths.get(dirPathFolder.toString().concat("//part").concat(d));
												
											try (DirectoryStream<Path> dirPathsTxt = Files.newDirectoryStream(dirPathTxt)) {
												//in specific part folder
												for (Path txt : dirPathsTxt) {
														
													try {
														this.t = txt.getFileName().toString();
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
																
																	//first check if it already exists in the vocabulary
																	if(vocabulary.contains(s)) {
																		continue;
																	}
																	else {
																		vocabulary.add(s);
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
													all++;
												}
											}
											x--;
										}
										else break;
									}
								}
							}	
						}
						x = FILES_TO_READ;
						a--;
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		//UNKNOWN position for development and testing
		vocabulary.add(null);
		
		return vocabulary.toArray();
	}
	
	//list that contains all vectors from training data
	public List<int[]> TrainVectors(Object[] vocabulary) {
		
		System.out.println("Creating training data vectors...");
		
		//last element is 0(ham) or 1(spam)
		List<int[]> vectors = new ArrayList<int[]>();
		File f = null;
		BufferedReader reader = null;
		String line;
		int counter = 0;
		
		int x = FILES_TO_READ;
		int y = 0;
		//a variable is for how many pu files to read
		int a = 1;
		
		try {
			
			Path dirPath = Paths.get("pu_corpora_public"); // create directory path: pu_corpora_public

			try(DirectoryStream<Path> dirPathFolders = Files.newDirectoryStream(dirPath)) {
	
				for(Path folders : dirPathFolders) {
					if(a!=0) {
			
						String b = "";
						b = Integer.toString(a);
						Path dirPathFolder = Paths.get(dirPath.toString().concat("//pu").concat(b)); 	//directory path: pu_corpora_public//puA
			
						try(DirectoryStream<Path> dirPathsFolder = Files.newDirectoryStream(dirPathFolder)) {
				
							for(Path folder : dirPathsFolder) {
			
								try (DirectoryStream<Path> dirPaths = Files.newDirectoryStream(dirPathFolder)) {	// get all files from directory
								
									//partX folders
									for(Path file : dirPaths) {
										if(y<all) {
											if(x!=0) {
												
												String d = "";
												d = Integer.toString(x);
												Path dirPathTxt = Paths.get(dirPathFolder.toString().concat("//part").concat(d));
												
												try (DirectoryStream<Path> dirPathsTxt = Files.newDirectoryStream(dirPathTxt)) {
													//in specific part folder
													for (Path txt : dirPathsTxt) {
														
														try {
															t = txt.getFileName().toString();
															
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
															int[] temp = new int[vocabulary.length+1];
															
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
																	
																		//find the word in vocabulary[]
																		for(int i=0; i<vocabulary.length; i++) {
																			if(vocabulary[i].equals(s)) {
																				temp[i] = 1;
																				break;
																			}
																		}
																	}	
																}
																
																line = reader.readLine();
															}
															
															if(t.contains("legit")) {
																//it is ham
																temp[temp.length-1] = 0;
															}
															else {
																//it is spam
																temp[temp.length-1] = 1;
															}
															
															vectors.add(temp);
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
														y++;
													}
												}
												x--;
											}
										}
										else break;
									}
								}
							}
						}
						x = FILES_TO_READ;
						a--;
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return vectors;
	}
}