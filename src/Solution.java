import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author mshah 
 * @created 7/8/2016.
 * The program accepts command line argument or prompts the user for input 
 * and prints the introductory paragraph of it's Wikipedia Page.
 */

public class Solution {

	public static void main(String[] args)throws Exception {

		/* Declaration of string and StringBuilder variables to be used */
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		String line="",word="";
		StringBuilder s=new StringBuilder();
		StringBuilder modified=new StringBuilder();
		
		/* Condition to check if any command line arguments passed, else user is prompted for input */
		if(args.length==0)
		{
			while(word.equals(""))
			{
				System.out.println("Enter the word to search");
				word=br.readLine();
			}
		}
		else
		{
			for(String app:args)
				word+=app+" ";
		}

		/* User input cleaned and prepared to pass to an API */
		word=word.trim();
		word=capitalizeFirstLetter(word);
		line=URLString(word);
		
		
		/* API call which retrieves information from wikipedia page. 
		 * MediaWIKI is API used for retrieving information */
		URL myURL = new URL("https://en.wikipedia.org/w/api.php?action=query&prop=revisions&rvprop=content&format=json&titles="+line);
		URLConnection myURLConnection = myURL.openConnection();
		myURLConnection.connect();
		BufferedReader br1=new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));

		while((line=br1.readLine())!=null)
		{
			s.append(line);

		}

		br1.close();

		/* Taking introductory paragraph from retrieved Wiki information  
		 * Error thrown if no information about user input on Wiki */
		try
		{
			s.delete(0, s.indexOf("'''"+word+"'''"));
		}
		catch(Exception e)
		{
			System.out.println("Not found.");
			System.exit(0);
		}

		/* Remove tags and unnecessary text from complete wiki page	*/
		s.delete(s.indexOf("\\n\\n"),s.length());
		line=s.toString();
		line=line.replaceAll("<ref>.*?</ref>", "");
		s=new StringBuilder(line);
		for(int i=0;i<s.length();i++)
		{
			if(s.charAt(i)=='(' && s.charAt(i+1)=='{')
			{
				i=trimElements(i,s,'(',')');
			}
			else if(s.charAt(i)=='<')
			{
				i=trimElements(i,s,'<','>');
			}
			else if(s.charAt(i)=='{')
			{
				i=trimElements(i,s,'{','}');
			}
			else if(s.charAt(i)=='[') {
				int start=i;
				i=trimElements(start,s,'[',']');
				line = s.substring(start, i + 1);
				if (line.lastIndexOf("|") != -1) {
					line = line.substring(line.lastIndexOf("|") + 1);
					line = line.replaceAll("]", "");
				}
				line=line.replaceAll("[\\[\\]]","");
				modified.append(line);
			}
			else if(s.charAt(i)=='\'' | s.charAt(i)=='\\')
			{
				continue;
			}
			else
				modified.append(s.charAt(i));
		}
		
		// Final Result displaying introductory paragraph 
		System.out.println(modified);
	}

	/*
	 * Function used to prepare input search in API call format
	 * @param word - string which is redesigned in API call format
	 * @return String used in API
	 */
	private static String URLString(String word) {
		String trim[]=word.split(" ");
		if(trim.length==1)
			return word;
		else
		{
			word=trim[0];
			for(int i=1;i<trim.length;i++)
				word+="%20"+trim[i];
			return word;
		}
	}

	/*
	 * Function used to capitalize each letter of a word of searched query
	 * @param s - string to be capitalized
	 * @return Capitalized string ready for use
	 */
	public static String capitalizeFirstLetter(String s)
	{
		s=s.toLowerCase();
		s=s.trim();
		char c=Character.toUpperCase(s.charAt(0));
		s=c+s.substring(1);
		for(int i=1; i<s.length(); i++)
		{
			if(s.charAt(i)==' ')
			{
				c=Character.toUpperCase(s.charAt(i+1));
				s=s.substring(0, i) + " "+c + s.substring(i+2);
			}
		}
		return s;
	}

	/*
	 * Function used to find particular character pairs which are trimmed later
	 * e.g. {},() etc
	 * @param i - position of starting character
	 * @param s - StringBuilder containing WikiPage information
	 * @param a - starting at this character (counter increments)
	 * @param b - ending at this character (counter decrements)
	 * @return ending character position to remove contents between start and end characters
	 */
	public static int trimElements(int i,StringBuilder s,char a,char b)
	{
		int counter = 1;
		while (i < s.length() && !(counter == 0)) {
			i++;
			if (s.charAt(i) == a)
				counter++;
			if (s.charAt(i) == b)
				counter--;
		}
		return i;
	}
}


