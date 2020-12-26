//shows the files in the directory

import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.LinkOption;

public class showfiles {

	public static void main(String args[]) {
		//String p = args[0];
		try {
			Path dirPath = Paths.get("pu_corpora_public//pu1//part1");	// create directory path
			try (DirectoryStream<Path> dirPaths = Files.newDirectoryStream(dirPath)) {	// get all files from directory
				for (Path file : dirPaths) {
					System.out.println(file.getFileName().toString());	// print all files names
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}