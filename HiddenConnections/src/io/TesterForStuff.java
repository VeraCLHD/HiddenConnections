package io;

import edu.stanford.nlp.simple.Sentence;

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
		
		String sentence1 = "I like those things.";
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
		System.out.println("__"+arr[0].trim()+"__");*/

		//System.out.println(RelationsFilter.candidateContainsOtherTerms("parasites and lipitor"));
		

	}

}
