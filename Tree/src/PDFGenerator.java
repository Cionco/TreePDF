
import java.io.IOException;
import java.util.ArrayList;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import lib.Functions;

public class PDFGenerator {

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
		Functions.drawLine(stream, middle, 0, middle, PAGE_HEIGHT - 25 * level);
		
		level++;
		
		ArrayList<Tree.Node> children = t.head.children;
		float width = (rightBorder - leftBorder) / children.size();
		for(int i = 0; i < children.size(); i++) {
			drawLines(stream, level, new Tree(children.get(i)), leftBorder + i * width, leftBorder + (i + 1) * width);
		}
	}
	
	
	public void drawTree() {
		
		System.out.println(PAGE_HEIGHT + " " + PAGE_WIDTH);
		
		try {
			PDDocument doc = new PDDocument();
			PDPage page = new PDPage(new PDRectangle(PAGE_WIDTH, PAGE_HEIGHT));
			doc.addPage(page);
			
			PDRectangle mediabox = page.getMediaBox();
			{
		        float startX = mediabox.getLowerLeftX();
		        float startY = mediabox.getUpperRightY();
		        heightCounter = startY;
		        widthCounter = startX;
			}

			PDPageContentStream content = new PDPageContentStream(doc, page);
			content.beginText();
			
			content.setFont(PDType1Font.HELVETICA, 18);
			String text = "HALLO ICH BINS";
			/*newLineAtOffset(content, PAGE_WIDTH / 2, PAGE_HEIGHT - 50);
			content.showText(text);
			newLineAtOffset(content, getAlignmentConstant(text), 20);
			content.showText(text);
			*/
			
			newCenteredLine(content, PAGE_WIDTH / 2, PAGE_HEIGHT - 50, text);
			newCenteredLine(content, 0, 20, "HALLO ICH BINS UND ICH GEHE JETZT IN DIE TV LOUNGE");
			
			
			content.endText();
			
			content.close();
			doc.save(FILENAME);
			doc.close();
			
		} catch (IOException e) {
			e.printStackTrace();
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
		System.out.println("Before: " + heightCounter +" " + widthCounter);
		heightCounter -= tx;
		widthCounter += ty;
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
	
	
}
