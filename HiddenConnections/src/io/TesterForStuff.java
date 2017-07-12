package io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.queryparser.classic.ParseException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import bootstrapping.Bootstrapper;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.parser.lexparser.Options;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import overall.LuceneSearcher;

public class TesterForStuff {
	public static final String pathToAllTerms = "terms/all_terms_and_variants_with10_filtered.txt";

	public static void main(String[] args) {
		/*Document doc;
		try {
			 Connection.Response loginForm = Jsoup.connect("http://scienceofdiet.com/login").userAgent("vera bachelorarbeit")
			            .method(Connection.Method.GET)
			            .execute();

			    Connection.Response mainPage = Jsoup.connect("http://scienceofdiet.com/login")
			            .data("user", "vera.boteva@yahoo.de")
			            .data("senha", "vera2561")
			            .cookies(loginForm.cookies())
			            .execute();

			    Map<String, String> cookies = mainPage.cookies();

			    Document evaluationPage = Jsoup.connect("http://scienceofdiet.com/login")
			            .cookies(cookies)
			            .execute().parse();
			    
			   System.out.println(evaluationPage);
			doc = Jsoup.connect("http://scienceofdiet.com/login").userAgent("vera bachelor thesis").get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
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
		
		/*String sent = "Since the same hormonal changes associated with eating more plant-based diets seemed to improve premenstrual and menstrual symptoms such as breast pain (see my video Plant-Based Diets For Breast Pain), researchers decided to test whether flax seeds would help as well.";
		//sent = sent.replaceAll("[,.!?;:]", "$0 ").replaceAll("\\s+", " ");
		String processedDoc = sent;

		sent = sent.replaceAll("([\\p{Lower}\\d\\\\p{Punct}][,.!?;:])" +
				 "(\\p{Upper})", "$1 $2").replaceAll("\\s+", " ");
		System.out.println(sent);/
		/*Matcher matcher = Pattern.compile(
				"\\p{Lower}?" +
				"[,.!?;:]" +
				 "\\p{Upper}"
				 ).matcher(processedDoc);
		if(matcher.find()){
			String matchWithoutTerms = matcher.group();
			System.out.println(matchWithoutTerms);
		}*/
		
		//TesterForStuff.GetNounPhrases();
		//parseTree();
		//tagTerms();
	
	}
	
	public static void tagTerms(){
		Writer.overwriteFile("", "terms/cancidates_raus.txt");
		List<String> lines = Reader.readLinesList(pathToAllTerms);
		Set<String> termsNoNoun = new HashSet<String>();
		for(String term: lines){

			Sentence sent1 = new Sentence(term);
			List<String> pos = sent1.posTags();
			String lastPOS = pos.get(pos.size()-1);
			if(!lastPOS.equals("NN") && !lastPOS.equals("NNS")){
				termsNoNoun.add(term);
				Writer.appendLineToFile(term, "terms/cancidates_raus.txt");
			}
			
			
		}
		System.out.println(termsNoNoun.size());

	}

	private static void parseTree() {
		String sent2 = "eating fruit";
		Sentence sent2_1 = new Sentence(sent2);
		LexicalizedParser lp1 = LexicalizedParser.loadModel();
		
		Tree parse = lp1.parse(sent2);
		List<Tree> phraseList=new ArrayList<Tree>();
		
	for(Tree subtree: parse){
		System.out.println(subtree.getChildrenAsList());
	}
	}
	
	public static void GetNounPhrases()
	{
		LexicalizedParser lp1 = LexicalizedParser.loadModel();
		
		String sentence = "Since the same hormonal changes associated with eating more plant-based diets seemed to improve premenstrual and menstrual symptoms.".replaceAll("[^a-zA-Z]+$", "");
		// output: [degenerative, cancer, cancer], [alzheimers]. The risk of terms not being recognized is too high
		/*Sentence sent = new Sentence(sentence);
		sent.posTags();
		sent.words();*/
		
		Tree parse = lp1.parse(sentence);
		//(Adjective | Noun)* (Noun Preposition)? (Adjective | Noun)* Noun
		
		// Create a reusable pattern object 
		//https://nlp.stanford.edu/software/tregex/The_Wonderful_World_of_Tregex.ppt/ - NP parent of NP extracts the longest NP
		//TregexPattern patternMW = TregexPattern.compile("NP [ << NNS | << NN | << NNPS | << NNP ]");
		// NP < (@NP !<< @NP . (/^such/ . /^as/))
		TregexPattern patternMW = TregexPattern.compile("(@NP !<< @NP . (/^such/ . /^as/))");
		TregexPattern patternM2 = TregexPattern.compile("(@NP !<< @NP)"); 
		// Run the pattern on one particular tree 
		TregexMatcher matcher = patternM2.matcher(parse); 
		// Iterate over all of the subtrees that matched
		List<Tree> phraseList=new ArrayList<Tree>();
		if (matcher.findNextMatchingNode()) { 
		  Tree match = matcher.getMatch(); 
		  // do what we want to with the subtree
		  phraseList.add(match);
		  
		 
		}
	for(Tree subtree: phraseList){
		System.out.println(subtree.getLeaves().toString());
	}
	
	
	/*for(int i = 0; i< phraseList.size(); i++){
		for(int j = i; i< phraseList.size(); j++){
		
		List<Tree> np1 = phraseList.get(i).getLeaves();
		List<Tree> np2 = phraseList.get(j).getLeaves();
		
		//Arrays.asList(phraseList.subList(i+1, phraseList.size())).containsAll(Arrays.asList(inner));
	}*/
		
	    /*List<Tree> phraseList=new ArrayList<Tree>();
	    for (Tree subtree: parse)
	    {

	      if(subtree.label().value().equals("NP"))
	      {

	        phraseList.add(subtree);
	       // System.out.println(subtree);
	        System.out.println(String.join(" ", subtree.getLeaves().toString()));

	      }
	    }*/

	      //return phraseList;

	}

	private static void luceneSearch() {
		LuceneSearcher ls = new LuceneSearcher();
		  Set<String> set;
		try {
			set = ls.doSearch("\"" + "hormones" + "\"" + "AND" +  "\"" + "nicotine" + "\"", "IndexDirectory");
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
