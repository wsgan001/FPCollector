package edu.handong.csee.isel.fpcollector.refactoring;

import org.eclipse.jdt.core.dom.ASTNode;

public class PatternNode {
	ASTNode node;
	int count;
	
	public ASTNode getNode() {
		return node;
	}
	
	public PatternNode (ASTNode n) {
		this.node = n;
		this.count = 0;
	}
}
