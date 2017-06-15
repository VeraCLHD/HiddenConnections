package evaluation;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.store.FSDirectory;

import io.Reader;

/** Simple command-line based search demo. */
public class LuceneSearcherForEval {

  public LuceneSearcherForEval() {}

 /** Simple command-line based search demo. */
  public static void main(String[] args) throws Exception {
	  LuceneSearcherForEval ls = new LuceneSearcherForEval();
	  Set<String> set = ls.doSearch("\"" + "such as" +"\"", "IndexDirectory");
	  for(String path: set){
		  String str = Reader.readContentOfFile(path);
		  System.out.println(path);
	  }
  }
  
  public Set<String> doSearch(String queryString, String indexDir) throws IOException, ParseException{
	  
	  String index = indexDir;
	    String field = "contents";
	   String queries = null;

	    boolean raw = true;
	    int hitsPerPage = 1;
	   
	   IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
	   IndexSearcher searcher = new IndexSearcher(reader);
	   // this makes an empty set of stoppwords -> we don't want to remove stoppwords
	   // needed: https://stackoverflow.com/questions/9066347/lucene-multi-word-phrases-as-search-terms
	    Analyzer analyzer = new StandardAnalyzer(CharArraySet.EMPTY_SET);
	  
	   BufferedReader in = null;
	    if (queries != null) {
	      in = Files.newBufferedReader(Paths.get(queries), StandardCharsets.UTF_8);
	   } else {
	      in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
	   }
	    
	   
	   QueryParser parser = new QueryParser(field, analyzer);
	   //parser.setDefaultOperator(QueryParser.Operator.AND);

	      String line = queryString != null ? queryString : in.readLine();
	      if(line !=null){
	    	  line = line.trim();
	      }
	      // escapes all the characters that have to be escaped
	      Query query = parser.parse(line);
	      
	      /*PhraseQuery.Builder builder = new PhraseQuery.Builder();
	      builder.add(new Term("contents", "n-3"), 0);
	      builder.add(new Term("contents", "polysaturated"), 1);
	      PhraseQuery pq = builder.build();
	      BooleanQuery.Builder apiQuery = new BooleanQuery.Builder();
	      apiQuery.add(new TermQuery(new Term("contents", "polysaturated")), BooleanClause.Occur.MUST);
	      apiQuery.add(new TermQuery(new Term("contents", "fatty")), BooleanClause.Occur.MUST);
	      BooleanQuery b = apiQuery.build();*/
	     //System.out.println("Searching for: " + query.toString(field));
	           
	      Set<String> paths = new HashSet<String>();
		  
		  TotalHitCountCollector collector = new TotalHitCountCollector();
		  searcher.search(query, collector);
		  TopDocs topDocs = searcher.search(query, Math.max(1, collector.getTotalHits()));
	    // Collect enough docs to show 5 pages
	    //TopDocs results = searcher.search(query);
	    //ScoreDoc[] hits = results.scoreDocs;
	    
	    //int numTotalHits = results.totalHits;
	    if(topDocs.totalHits> 0 ){
	  	  ScoreDoc[] docs = topDocs.scoreDocs;
	      for (int i = 0; i < topDocs.totalHits; i++) {

	        Document doc = searcher.doc(docs[i].doc);
	        String path = doc.get("path");
	        if (path != null) {
	        	
	     	 
	          paths.add(path);
	        } else {
	          System.out.println((i+1) + ". " + "No path for this document");
	         }
	                   
	       }
	    }
	
	      //res = doPagingSearch(in, searcher, query, hitsPerPage, raw, queries == null && queryString == null);

	  
	    reader.close();
	    
	    
		
		return paths;
  }
  
 
}