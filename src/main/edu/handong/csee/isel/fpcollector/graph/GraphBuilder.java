package edu.handong.csee.isel.fpcollector.graph;

import edu.handong.csee.isel.fpcollector.refactoring.Info;

public class GraphBuilder {
	public ControlNode root;
	
	public void run(Info info) {
		JavaASTParser parser = new JavaASTParser(info);
		
		parser.run();
		this.root = parser.root;
	}
}

//public class GraphBuilder {
//	public ArrayList<GraphNode> getGraph(ArrayList<MethodAST> methodASTs){
//		ArrayList<GraphNode> graphs = new ArrayList<>();
//		
//		for (MethodAST mAST : methodASTs) {
//			GraphNode tempRoot = new ControlNode(mAST.asts.get(0));
//			for(ASTNode node : mAST.asts) {
//				if(node instanceof MethodDeclaration) {
//					continue;
//				}
//				//controlNode
//				else if((tempRoot != null) && (node instanceof IfStatement ||
//						node instanceof DoStatement ||
//						node instanceof SwitchStatement ||						
//						node instanceof ContinueStatement ||
//						node instanceof BreakStatement ||
//						node instanceof TryStatement ||
//						node instanceof ForStatement ||						
//						node instanceof EnhancedForStatement ||
//						node instanceof ConditionalExpression ||
//						node instanceof SwitchCase)
////						temp.getParent() instanceof MethodDeclaration
//						&& !(node instanceof Block)
//						) {
//					if(node.getParent().getParent() == tempRoot.getNode()) {
//						tempRoot.addNode(node);
//					}
//				}
//				//terminateNode
//				else if((tempRoot != null) && (
//						node instanceof ReturnStatement ||												
//						node instanceof ThrowStatement)
////						temp.getParent() instanceof MethodDeclaration
//						&& !(node instanceof Block)
//						) {
//					if(node.getParent().getParent() == tempRoot.getNode()) {
//						tempRoot.addNode(node);
//					}
//				}
//				else if (node instanceof SimpleName){
//					
//				}
//			}
//			graphs.add(tempRoot);
//		}
//		
//		return graphs;
//	}
//}

