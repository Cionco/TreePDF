
public class Test {

	public static void main(String[] args) {
		Tree t = new Tree("WhileStmt")
				.addChild(new Tree("LtExpr")
						.addChild(new Tree.Node("Name", null).setValue("j"))
						.addChild(new Tree.Node("Literal", null).setValue("10")))
				.addChild(new Tree("ExprStmt")
						.addChild(new Tree.Node("MethodCall", null)
								.addChild(new Tree.Node("Name", null).setValue("print"))
								.addChild(new Tree.Node("MulExpr", null)
										.addChild(new Tree.Node("Name", null).setValue("j"))
										.addChild(new Tree.Node("PostIncExpr", null)
												.addChild(new Tree.Node("Name", null).setValue("j"))
												)
										)
								)
						);
		System.out.println(t.toString());
		
		
		
		new PDFGenerator(t).drawLines();
	}
}
