package io;

import java.io.IOException;
import java.util.Set;

import org.apache.lucene.queryparser.classic.ParseException;

import edu.stanford.nlp.simple.Sentence;
import overall.LuceneSearcher;

public class TesterForStuff {

	public static void main(String[] args) {
		/*String sentenceString = "I like the things I like.";
		String pattern = "like";
		final Matcher matcher = Pattern.compile( "\\b" +
				 Pattern.quote(pattern) + "\\b" ).matcher(sentenceString);
		while(matcher.find()){
		    String before = sentenceString.substring(0, matcher.start()).trim();
		    String after = sentenceString.substring(matcher.end()).trim();
		    System.out.println(before);
		    System.out.println(after);
		}*/
		
		/*String sentence1 = "I like those things.";
		String sentence2 = "I like the things";
		
		Sentence sent1 = new Sentence(sentence1);
		Sentence sent2 = new Sentence(sentence1);
		
		System.out.println(sent1.posTags().toString().equals(sent2.posTags().toString()));
		System.out.println(sent1.posTags());
		System.out.println(sent2.posTags());
		
		/*String str = Reader.readContentOfFile("current_match");
		String[] arr = str.split("\t");
		
		System.out.println(arr[0].matches("\\W"));
		System.out.println("such".split(" ").length == 1);
		System.out.println(arr[0].equals(" "));
		System.out.println("__"+arr[0].trim()+"__");

		//System.out.println(RelationsFilter.candidateContainsOtherTerms("parasites and lipitor"));
		double d = (double)1/2;
		System.out.println(d);*/
		
		 LuceneSearcher ls = new LuceneSearcher();
		  Set<String> set;
		try {
			set = ls.doSearch("\"" + "beverages" + "\"" + "AND" +  "\"" + "hibiscus" + "\"");
			for(String path: set){
				  String str = Reader.readContentOfFile(path);
				  System.out.println(path);
			  }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String sent = "nd that’s not just-ploizt happening in our arm; the lining of our whole vascular tree gets inflamed, stiffened, crippled, from just one meal!And just as it starts to calm down, five or six hours later, we may whack it with another load of meat, eggs, or dairy for lunch—such that most of our lives, we’re stuck in this chronic low-grade inflammation danger zone, which may set us up for inflammatory diseases, such as heart disease, diabetes, cancer, kind of one meal at a time.Does the same thing happen in our lungs?".toLowerCase();
		sent = sent.replaceAll("\\p{P}", "");
		System.out.println(sent);
		  
		
			
	}

}
