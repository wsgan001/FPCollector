package edu.handong.csee.isel.fpcollector.refactoring;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.WhileStatement;

public class BNFChecker {
	Info info = new Info();
	ArrayList<SimpleName> violatedNode = new ArrayList<>();
	ArrayList<SimpleName> violatedNodeInRange = new ArrayList<>();
	ArrayList<ASTNode> patterns = new ArrayList<>();
	PatternVector patternVector = new PatternVector();
	
	public BNFChecker(Info info) {
		this.info = info;
		buildAST();
	}
	
	private void buildAST() {
		JavaASTParser javaParser = new JavaASTParser(info.source);
		violatedNode = javaParser.getViolatedNames(info.varName);
		
		checkInRange();
	}
	
	private void checkInRange() {
		int flag = 1;
		int start = 0;
		int end = 0;
		for(SimpleName violatedNode : violatedNode) {
			if(flag == 1) {
			for(int i =0 ; i < Integer.parseInt(info.start); i ++) {
				info.source = info.source.replaceFirst("\n", "NewLineOccurred");
			}
			start = info.source.lastIndexOf("NewLineOccurred");
			for(int i = 0; i < Integer.parseInt(info.end) - Integer.parseInt(info.start); i ++) {
				info.source = info.source.replaceFirst("\n", "NewLineOccurred");
			}
			end = info.source.lastIndexOf("NewLineOccurred");
			flag--;
			}
			
			if(violatedNode.getStartPosition() >= start 
					&&  violatedNode.getStartPosition() <= end) {
				violatedNodeInRange.add(violatedNode);
			} else {
				continue;
			}
		}
		
		getBNF();
		
	}
	
	public void getBNF(){
		
		
		for(SimpleName node : violatedNodeInRange) {
			//pattern initiation
			Pattern tempPattern = new Pattern();
			tempPattern.addPattern(node.getClass().getName());
			//get Pattern which is member of <Block>
			for(ASTNode tempNode = node.getParent(); !(tempNode instanceof TypeDeclaration); tempNode = tempNode.getParent()) {
				//Hashmap check (if empty, true)
				
				
				//pattern update
				if(tempNode instanceof EnhancedForStatement ||
					tempNode instanceof AnonymousClassDeclaration ||
					tempNode instanceof CatchClause ||
					tempNode instanceof DoStatement || 
					tempNode instanceof EnumConstantDeclaration || 
					tempNode instanceof EnumDeclaration || 
					tempNode instanceof ForStatement ||
					tempNode instanceof IfStatement ||
					tempNode instanceof WhileStatement||
					tempNode instanceof TryStatement|| 
					tempNode instanceof SwitchStatement|| 
					tempNode instanceof MethodDeclaration) {
					patternVector.addNodes(tempNode);
					patternVector.addNodes(tempNode, tempPattern);
					tempPattern.addPattern(tempNode.getClass().getName());
				}
			}
			System.out.println("Break");
			for( String elem : tempPattern.getPattern()) {
				System.out.println(elem);
			}
		}
	}
	
	
	
}
