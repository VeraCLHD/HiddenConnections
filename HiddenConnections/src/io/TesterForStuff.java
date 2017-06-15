package io;

import java.io.IOException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.queryparser.classic.ParseException;

import edu.stanford.nlp.simple.Sentence;
import evaluation.LuceneSearcherForEval;
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
		
		 //luceneSearch();
		
		/*String sent = "U.S. ake the energy to fuel our brains and muscles. Our perceived level of energy relates to our mood, general happiness, and productivity.After being on a plant-based diet for five and a half months in a study looking at how an inflammation-reducing diet could affect persons with depression, a group of overweight or diabetic individuals reported increased energy, along with improved digestion, better sleep, better work productivity, and an increase in physical functioning, general health, vitality, and mental health.In a study treating women’s painful menstrual periods with a vegan diet, the women not only had fewer cramps, but lost weight and experienced increased energy, better digestion, and better sleep.Raisins worked as well as commercial energy supplements in a study looking at replacing glycogen stores—the body’s source of quick energy—during athletic performance.Among other things, caffeine increases energy availability and expenditure, and decreases fatigue and the sense of effort associated with physical activity.Beets can enhance energy production at the subcellular level and thereby improve athletic performance. Human energy production (mitochondrial efficiency) was improved by consuming a beet-juice beveragebeet juice.Fatty and sugary foods are energy-dense foods, but eating a calorie-dense diet leads to a numbing of the dopamine response, making it harder to feel satisfied without increasing our consumption.";
		//sent = sent.replaceAll("[,.!?;:]", "$0 ").replaceAll("\\s+", " ");
		String processedDoc = sent;

		sent = sent.replaceAll("([\\p{Lower}\\d\\\\p{Punct}][,.!?;:])" +
				 "(\\p{Upper})", "$1 $2").replaceAll("\\s+", " ");
		System.out.println(sent);*/
		/*Matcher matcher = Pattern.compile(
				"\\p{Lower}?" +
				"[,.!?;:]" +
				 "\\p{Upper}"
				 ).matcher(processedDoc);
		if(matcher.find()){
			String matchWithoutTerms = matcher.group();
			System.out.println(matchWithoutTerms);
		}*/
		
		luceneSearch( "inflammatory bowel disease",  "ulcerative colitis", "such as");
		//luceneSearch( "n-3",  "fatty acid", "polyunsaturated");
		
	
	}
	// improvements: if one term contains other, it will not work
	// 2. with this search any order of the three is found
	private static void luceneSearch(String term1, String term2, String pattern) {
		LuceneSearcherForEval ls = new LuceneSearcherForEval();
		  Set<String> set;
		try {
			// any order of the three: "\"" + term1 + "\"" + "AND" + "\"" + pattern + "\"" + "AND"  + "\"" + term2 + "\"", "EVALUATION SETS/DOCDUMP/INDEX PARAGRAPHS/"
			set = ls.doSearch("\""+term1+"\"" +"AND" +"\""+pattern+"\""  +"AND" + "\""+term2+"\"", "EVALUATION SETS/DOCDUMP/INDEX PARAGRAPHS/");
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
	}

}
