package invasion;

import java.io.IOException;
import java.io.RandomAccessFile;

public class RafUtils
{

	private final static  char TERM_CHAR = '\0';
	
	/**
	 * Helper file for reading a String from a Random Access File.  This method should be used in tandem with writeStringToRaf
	 * to successfully write objects to files
	 * @param nSize    Size of string to read
	 * @param obRaf    Random Access File where Record is stored.
	 * @return
	 * @throws IOException
	 */
	public static String readStringFromRaf(int nSize, RandomAccessFile obRaf) throws IOException
	{
		byte[] aRead = new byte[nSize];
		
		obRaf.read(aRead);
		
		String sReturn = new String(aRead);
		
		if ( sReturn.indexOf(TERM_CHAR) != -1)
		{
			return sReturn.substring(0, sReturn.indexOf(TERM_CHAR));
		}
		else
		{
			return sReturn;
		}
		
	}
	
	/**
	 * Helper method for writing a string (as part of an object) to a Random Access File.  
	 * 	This method should be used in tandem with 
	 * 		readStringFromRaf to handle Random object access to and from these files.
	 * @param sVals
	 * @param nSize
	 * @param obRaf
	 * @throws IOException
	 */
	public static void writeStringToRaf(String sVals, int nSize, RandomAccessFile obRaf) throws IOException
	{
		
		byte[] aWrite = sVals.getBytes();
		
		
		int i=0;
		
		for (; i<Math.min(aWrite.length, nSize); i++)
		{
			obRaf.writeByte(aWrite[i]);
		}
		
		for (; i<nSize; i++)
		{
			obRaf.writeByte(TERM_CHAR);
		}
		
	}
	
	/**
	 * Helper method for reading a string from a csv where the contents could be enclosed with in strings and may contain a ,
	 * @param sLine
	 * @return
	 */
	public static String[] parseCSVLine(String sLine)
	{
		String sPattern = ",(?=([^\"]*\"[^\"]*\")*(?![^\"]*\"))";
		
		String[]  sFields = sLine.split(sPattern);
		for (int i = 0; i < sFields.length; i++) {
            // Get rid of residual double quotes
            sFields[i] = sFields[i].replace("\"", "");
        }
        return sFields;
	}
	
	
}
