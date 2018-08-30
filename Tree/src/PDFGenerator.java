
import java.io.IOException;
import java.util.ArrayList;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import lib.Functions;

public class PDFGenerator {

	private static final int LINE_SPACE = 40;
	final float PAGE_HEIGHT = PDRectangle.A4.getWidth();
	final float PAGE_WIDTH = PDRectangle.A4.getHeight();
	final float TRANSFORMATION = 55.458564767f;
	final String FILENAME = "Tree.pdf";
	
	Tree tree;
	private float heightCounter;
	private float widthCounter;
	
	public PDFGenerator(Tree t) {
		this.tree = t;
	}
	
	public void drawNodeTexts() {
		try {
			PDDocument doc = new PDDocument();
			PDPage page = new PDPage(new PDRectangle(PAGE_WIDTH, PAGE_HEIGHT));
			doc.addPage(page);
			
			PDRectangle mediabox = page.getMediaBox();
			setCounters(mediabox);
			
			PDPageContentStream content = new PDPageContentStream(doc, page);
			
			content.beginText();
			content.setFont(PDType1Font.HELVETICA, 18);
			drawNodeTexts(content, 0, tree, 0, PAGE_WIDTH);
			
			content.endText();
			content.close();
			doc.save("texts.pdf");
			doc.close();
		}  catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void drawNodeTexts(PDPageContentStream stream, int level, Tree t, float leftBorder, float rightBorder) throws IOException {
		float middle = (leftBorder + rightBorder) / 2;
		
		centeredTextAtPosition(stream, middle, PAGE_HEIGHT - LINE_SPACE * (level + 1), t.head.name);
		
		level++;
		
		ArrayList<Tree.Node> children = t.head.children;
		float width = (rightBorder - leftBorder) / children.size();
		for(int i = 0; i < children.size(); i++) {
			drawNodeTexts(stream, level, new Tree(children.get(i)), leftBorder + i * width, leftBorder + (i + 1) * width);
		}
	}
	
	public void drawLines() {
		try {
			PDDocument doc = new PDDocument();
			PDPage page = new PDPage(new PDRectangle(PAGE_WIDTH, PAGE_HEIGHT));
			doc.addPage(page);
			
			PDPageContentStream content = new PDPageContentStream(doc, page);
			
			drawLines(content, 0, tree, 0, PAGE_WIDTH);
			
			content.close();
			doc.save("lines.pdf");
			doc.close();
		}  catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void drawLines(PDPageContentStream stream, int level, Tree t, float leftBorder, float rightBorder) {
		float middle = (leftBorder + rightBorder) / 2;
		Functions.drawLine(stream, middle, 0, middle, PAGE_HEIGHT - LINE_SPACE * level);
		
		level++;
		
		ArrayList<Tree.Node> children = t.head.children;
		float width = (rightBorder - leftBorder) / children.size();
		for(int i = 0; i < children.size(); i++) {
			drawLines(stream, level, new Tree(children.get(i)), leftBorder + i * width, leftBorder + (i + 1) * width);
		}
	}
	
	public void drawTree() {
		
		try {
			PDDocument doc = new PDDocument();
			PDPage page = new PDPage(new PDRectangle(PAGE_WIDTH, PAGE_HEIGHT));
			doc.addPage(page);
			
			PDRectangle mediabox = page.getMediaBox();
			setCounters(mediabox);

			PDPageContentStream content = new PDPageContentStream(doc, page);
			content.beginText();
			
			content.setFont(PDType1Font.HELVETICA, 18);
			String text = "HALLO ICH BINS";
			/*newLineAtOffset(content, PAGE_WIDTH / 2, PAGE_HEIGHT - 50);
			content.showText(text);
			newLineAtOffset(content, getAlignmentConstant(text), 20);
			content.showText(text);
			*/
			
			//newCenteredLine(content, PAGE_WIDTH / 2, PAGE_HEIGHT - 50, text);
			centeredTextAtPosition(content, PAGE_WIDTH / 2, PAGE_HEIGHT - 50, text);
			newCenteredLine(content, 0, -20, "HALLO ICH BINS UND ICH GEHE JETZT IN DIE TV LOUNGE");
			
			centeredTextAtPosition(content, 0, 0, "hallo");
			centeredTextAtPosition(content, 300, 200, "BLABLA");
			
			content.endText();
			
			content.close();
			doc.save(FILENAME);
			doc.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

	private void setCounters(PDRectangle mediabox) {
		{
		    float startX = mediabox.getLowerLeftX();
		    float startY = mediabox.getLowerLeftY();
		    heightCounter = startY;
		    widthCounter = startX;
		}
	}

	/**
	 * 
	 * @param text
	 * @return Float literal by how much the text has to be moved to the left so it is
	 * 			centered around the cursor position
	 * @throws IOException
	 */
	private float getAlignmentConstant(String text) throws IOException {
		return -PDType1Font.HELVETICA.getStringWidth(text) / (2 * TRANSFORMATION);
	}
	
	/**
	 * Set cursor position to offset (currentX + tx / currentY + ty)
	 * and adapts {@link #heightCounter} and {@link #widthCounter}
	 * @param content
	 * @param tx Offset X
	 * @param ty Offset Y
	 * @throws IOException @see PDPageContentStream.newLineAtOffset
	 */
	private void newLineAtOffset(PDPageContentStream content, float tx, float ty) throws IOException {
//		System.out.println("Before: " + heightCounter +" " + widthCounter);
		widthCounter += tx;
		heightCounter += ty;
//		System.out.println("After: " + heightCounter +" " + widthCounter);
		content.newLineAtOffset(tx, ty);
	}
	
	/**
	 * Set cursor position to offset {@link #newLineAtOffset(PDPageContentStream, float, float)}
	 * and generates text cursor position will be the center of the text
	 * @param content
	 * @param tx Offset X
	 * @param ty Offset Y
	 * @param text Text to show
	 * @throws IOException
	 */
	private void newCenteredLine(PDPageContentStream content, float tx, float ty, String text) throws IOException {
		newLineAtOffset(content, tx, ty);
		newLineAtOffset(content, getAlignmentConstant(text), 0);
		content.showText(text);
		newLineAtOffset(content, -getAlignmentConstant(text), 0);
	}
	
	private void centeredTextAtPosition(PDPageContentStream content, float x, float y, String text) throws IOException {
		float tx = x - widthCounter;
		float ty = y - heightCounter;
		newCenteredLine(content, tx, ty, text);
	}
	
	
}
