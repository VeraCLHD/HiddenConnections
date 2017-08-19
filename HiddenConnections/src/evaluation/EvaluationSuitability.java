package evaluation;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.queryparser.classic.ParseException;

import io.Reader;
import io.Writer;
import overall.LuceneSearcher;

public class EvaluationSuitability {
	private static final String TERMS_EVALUATION_SUITABILITY_TXT = "terms/evaluation_suitability.txt";
	public String evaluation_source = "";
	public Double allTerms = 0.0;
	public Double foundInEvalSource = 0.0;
	
	private static Set<String> terms = new HashSet<String>();
	private static final String pathToAllTerms = "terms/all_terms_and_variants_with10_filtered.txt";
	private static final String pathToOriginalTerms = "terms/original_terms.txt";
	
	public EvaluationSuitability(String evaluationSource){
		this.setEvaluation_source(evaluationSource);
		this.setFoundInEvalSource(0.0);
	}
	
	public  void readTerms(String path){
		List<String> lines = Reader.readLinesList(path);
		for(String line: lines){
			if(!line.isEmpty() && !line.equals(" ")){
				EvaluationSuitability.getTerms().add(line.trim());
			}
		}
		this.setAllTerms((double) EvaluationSuitability.getTerms().size());
	
	}

	private void luceneSearchPattern(String term, String dir) {
		LuceneSearcher ls = new LuceneSearcher();
		  Set<String> set;
		try {
			set = ls.doSearch("\"" + term +"\"", dir);
			
			if(!set.isEmpty()){
				Double found = this.getFoundInEvalSource();
				this.setFoundInEvalSource(found + 1);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void evaluateSuitability(String path){
		this.readTerms(path);
		for(String term: EvaluationSuitability.getTerms()){
			if(this.getEvaluation_source().equals("NF")){
				luceneSearchPattern(term, "IndexDirectory/");
			} else{
				luceneSearchPattern(term, "EVALUATION SETS/"+ this.getEvaluation_source() +"/INDEX " + this.getEvaluation_source() + "/");
			}
			
			
			
		}
		
		
		
	}
	
	public static void main(String[] args) {
		Writer.overwriteFile("", TERMS_EVALUATION_SUITABILITY_TXT);
		Writer.appendLineToFile("ORIGINAL TERMS", TERMS_EVALUATION_SUITABILITY_TXT);
		writeResultsToFile(pathToOriginalTerms);
		Writer.appendLineToFile("ALL TERMS", TERMS_EVALUATION_SUITABILITY_TXT);
		writeResultsToFile(pathToAllTerms);

	}

	private static void writeResultsToFile(String path) {
		EvaluationSuitability es = new EvaluationSuitability("NF");
		es.evaluateSuitability(path);
		Writer.appendLineToFile("SOURCE: " + es.getEvaluation_source(), TERMS_EVALUATION_SUITABILITY_TXT);
		Double ratio = es.getFoundInEvalSource()/es.getAllTerms();
		Writer.appendLineToFile(ratio.toString(), TERMS_EVALUATION_SUITABILITY_TXT);
		
		
		EvaluationSuitability authority = new EvaluationSuitability("AUTHORITYNUTRITION");
		authority.evaluateSuitability(path);
		Writer.appendLineToFile("SOURCE: " + authority.getEvaluation_source(), TERMS_EVALUATION_SUITABILITY_TXT);
		Double ratioA = authority.getFoundInEvalSource()/authority.getAllTerms();
		Writer.appendLineToFile(ratioA.toString(), TERMS_EVALUATION_SUITABILITY_TXT);
		
		
		EvaluationSuitability docdump = new EvaluationSuitability("DOCDUMP");
		docdump.evaluateSuitability(path);
		Writer.appendLineToFile("SOURCE: " + docdump.getEvaluation_source(), TERMS_EVALUATION_SUITABILITY_TXT);
		Double ratioD = docdump.getFoundInEvalSource()/docdump.getAllTerms();
		Writer.appendLineToFile(ratioD.toString(), TERMS_EVALUATION_SUITABILITY_TXT);
	}
	
	public Double getFoundInEvalSource() {
		return foundInEvalSource;
	}

	public void setFoundInEvalSource(Double foundInEvalSource) {
		this.foundInEvalSource = foundInEvalSource;
	}

	public Double getAllTerms() {
		return allTerms;
	}

	public void setAllTerms(Double allTerms) {
		this.allTerms = allTerms;
	}

	public void setEvaluation_source(String evaluation_source) {
		this.evaluation_source = evaluation_source;
	}

	public String getEvaluation_source() {
		return evaluation_source;
	}

	public static Set<String> getTerms() {
		return terms;
	}

	public static void setTerms(Set<String> terms) {
		EvaluationSuitability.terms = terms;
	}

	

}