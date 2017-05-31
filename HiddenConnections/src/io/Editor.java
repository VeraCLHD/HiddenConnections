package io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * A class that handles deletion and modification of files.
 * 
 * @author Vera Boteva, Demian Gholipour
 *
 */
public class Editor {
	/**
	 * Deletes the last line of a file. Needed when crawling after interruption.
	 * 
	 * @param filename the file from which the last line is deleted
	 */
	public static void deleteLastLine(String filename) {
		
		RandomAccessFile f;
		try {
			f = new RandomAccessFile(filename, "rw");
			long length = f.length() - 1;
			byte b;
			do {                     
			  length -= 1;
			  f.seek(length);
			  b = f.readByte();
			} while(b != 10);
			f.setLength(length-1);
			f.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void deleteFile(String filename) {
		File f = new File(filename);
		f.delete();
	}
	
	/**
	 * Transfers name from one file to another and gives the first one a new name.
	 * @param giver_name file that gives the name
	 * @param receiver_name file that receives the name from giver
	 * @param new_giver_name new file for content of giver
	 */
	public static void transferFileName(String giver_name, String receiver_name, String new_giver_name) {
		File giver = new File(giver_name);
		File receiver = new File(receiver_name);
		File giver_new = new File(new_giver_name);
		giver.renameTo(giver_new);
		giver.delete();
		File new_receiver = new File(giver_name);
		receiver.renameTo(new_receiver);
		receiver.delete();
	}
	
	/**
	 * Transfers name from one file to another and deletes the first file.
	 * @param giver_name file that gives the name and gets deleted
	 * @param receiver_name file that receives the name from giver
	 */
	public static void transferFileName(String giver_name, String receiver_name) {
		File giver = new File(giver_name);
		File receiver = new File(receiver_name);
		giver.delete();
		File new_receiver = new File(giver_name);
		receiver.renameTo(new_receiver);
		receiver.delete();
	}
	
	

}
