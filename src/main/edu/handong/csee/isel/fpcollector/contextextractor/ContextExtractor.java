package edu.handong.csee.isel.fpcollector.contextextractor;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.handong.csee.isel.fpcollector.contextextractor.astvector.ContextVectorGetter;
import edu.handong.csee.isel.fpcollector.contextextractor.astvector.ViolationVariableGetter;
import edu.handong.csee.isel.fpcollector.contextextractor.patternminer.PatternFinder;
import edu.handong.csee.isel.fpcollector.contextextractor.patternminer.SupportCountGetter;
import edu.handong.csee.isel.fpcollector.fpsuspectsgetter.reportanalysis.ReportAnalyzer;
import edu.handong.csee.isel.fpcollector.structures.DirLineErrmsgContext;
import edu.handong.csee.isel.fpcollector.structures.VectorNode;
import edu.handong.csee.isel.fpcollector.utils.Reader;
import edu.handong.csee.isel.fpcollector.utils.Writer;

public class ContextExtractor {
	/*
	 * Input : A csv file, result of fpsuspectsgetter
	 * Output : A csv file, directory and line number and context information
	 */
	public void run(String[] infos) {
		//read result file
		ArrayList<String> resultInfo = new ArrayList<>();
		ArrayList<ArrayList<String>> lineContext = new ArrayList<>();
		String OutputPath = infos[2];
		
		DirLineErrmsgContext context;
		resultInfo = readResultFile(OutputPath);
			
		//part 1 : get AST Vector
		//find violated variable and its path
		ArrayList<String> varPath = new ArrayList<>();
		ArrayList<ArrayList<ASTNode>> contextNodeInformation = new ArrayList<>();
		ArrayList<ArrayList<VectorNode>> contextVectorInformation = new ArrayList<>();
		HashMap<ArrayList<Integer>, Integer> allSequentialPatterns = new HashMap<>();
		HashMap<ArrayList<Integer>, Integer> patternFrequency = new HashMap<>();
		
		System.out.println("Collecting violation occurred Variable and its Path...");
		varPath = getViolationVarPath(resultInfo);
		System.out.println("@@@@@ Collected Successfully\n");
		System.out.println("Collecting violation occurred Method Context...");
		contextNodeInformation = getContextNode(varPath);
		System.out.println("@@@@@ Collected Successfully\n");
		System.out.println("Vectorizing Collected Context...");
		contextVectorInformation = getContextVectorInformation(contextNodeInformation);
		System.out.println("@@@@@ Collected Successfully\n");
		
		//part 2 : get Frequent pattern
		//get all pattern(SP, OP, AP)
		//1) get SP
		allSequentialPatterns = getAllPatterns(contextVectorInformation);
		patternFrequency = getSequentialPatternFrequency(contextVectorInformation, allSequentialPatterns);
		patternFrequency = sortByFrequency(patternFrequency);
		
		
		//write a file
			System.out.println("\n----- Start to Rearrange Data -----\n");
			context = new DirLineErrmsgContext(lineContext, resultInfo);
			writeContext(context, OutputPath);
			System.out.println("@@@@@ Context Extracting Process is Completed");
	}

	public ArrayList<String> readResultFile(String path){
		Reader reader = new Reader();
		ArrayList<String> resultInfo = new ArrayList<>();
		
		resultInfo = reader.readResult(path);
		return resultInfo;	
	}
	
	public ArrayList<ArrayList<String>> getContextUsingBlame(ArrayList<String> result, String name){
		ArrayList<ArrayList<String>> lineInfo = new ArrayList<>();
		ReportAnalyzer analyzer = new ReportAnalyzer();
		
		lineInfo = analyzer.getCtxUsingBlame(result, name);
		
		return lineInfo;
	}
	
	public ArrayList<ArrayList<String>> getContextUsingFile(ArrayList<String> result, String name){
		ArrayList<ArrayList<String>> lineInfo = new ArrayList<>();
		ReportAnalyzer analyzer = new ReportAnalyzer();
		
		lineInfo = analyzer.getCtxUsingFile(result, name);
		
		return lineInfo;
	}
	
	public void writeContext(DirLineErrmsgContext context, String path) {
		Writer writer = new Writer();
		writer.writeContextsForDFA(context, path);
	}
	
	public ArrayList<String> getViolationVarPath(ArrayList<String> result){
		ArrayList<String> varPath = new ArrayList<>();
		ViolationVariableGetter vVGetter = new ViolationVariableGetter();
		
		varPath = vVGetter.getViolationVariable(result);
		
		return varPath;
	}
	
	public ArrayList<ArrayList<ASTNode>> getContextNode(ArrayList<String> varPath){
		ArrayList<ArrayList<ASTNode>> contextVector = 
				new ArrayList<>();
		ContextVectorGetter ctxGetter = new ContextVectorGetter();
		contextVector = ctxGetter.getContextNode(varPath);
		
		return contextVector;
	}
	
	public ArrayList<ArrayList<VectorNode>> 
	getContextVectorInformation(ArrayList<ArrayList<ASTNode>> nodeInfo){
		ArrayList<ArrayList<VectorNode>> contextVector = new ArrayList<>();
		ContextVectorGetter vectorGetter = new ContextVectorGetter();
		
		contextVector = vectorGetter.getContextVector(nodeInfo);
		
		return contextVector;
	}
	
	public ArrayList<ArrayList<VectorNode>> getContextFrequency
	(ArrayList<ArrayList<VectorNode>> contextVectorInformation){
		ArrayList<ArrayList<VectorNode>> frequencyPattern = new ArrayList<>();
		SupportCountGetter supportCountGetter = new SupportCountGetter();

		frequencyPattern = supportCountGetter.getSupportCount(contextVectorInformation);
		
		return frequencyPattern;
	}
	

	public HashMap<ArrayList<Integer>, Integer> getAllPatterns
	(ArrayList<ArrayList<VectorNode>> contextVectorInformation) {
		HashMap<ArrayList<Integer>, Integer> sequentialPatterns = new HashMap<>();
		HashMap<ArrayList<Integer>, Integer> orderedPatterns = new HashMap<>();
		HashMap<ArrayList<Integer>, Integer> allPatterns = new HashMap<>();
		
		PatternFinder finder = new PatternFinder();	
		
		sequentialPatterns = finder.mineAllSequentialPatterns(contextVectorInformation);
		
		return sequentialPatterns;
	}
	
	public HashMap<ArrayList<Integer>, Integer> getSequentialPatternFrequency
	(ArrayList<ArrayList<VectorNode>> contextVectorInformation, 
	HashMap<ArrayList<Integer>, Integer> allSequentialPatterns) {
		PatternFinder finder = new PatternFinder();
		HashMap<ArrayList<Integer>, Integer> frequency = new HashMap<>();
		
		frequency = finder.getSPFrequency(contextVectorInformation, allSequentialPatterns);	
		
		return frequency;
	}
	
	public HashMap<ArrayList<Integer>, Integer> sortByFrequency(
			HashMap<ArrayList<Integer>, Integer> patternFrequency) {
		
		PatternFinder finder = new PatternFinder();
		
		patternFrequency = finder.sortByCounting(patternFrequency);
		
		return patternFrequency;
	}
}
