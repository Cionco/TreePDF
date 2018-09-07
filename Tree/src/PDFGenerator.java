
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.midi.Synthesizer;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import lib.Functions;

public class PDFGenerator {

	private static final int TEXT_SIZE = 18;
	private static final int LINE_SPACE = 50;
	private final float PAGE_HEIGHT = PDRectangle.A4.getWidth();
	private final float PAGE_WIDTH = PDRectangle.A4.getHeight();
	private final float TRANSFORMATION = 55.458564767f;
	private final float PADDING = 3.0f;
	private final float ELLIPSE_PADDING = 10.0f;
	public final String FILENAME = "Tree.pdf";
	private final float RY = 15.0f;
	
	private Tree tree;
	private float heightCounter;
	private float widthCounter;
	
	public PDFGenerator(Tree t) {
		this.tree = t;
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
			content.setFont(PDType1Font.HELVETICA, TEXT_SIZE);
			drawNodeTexts(content, 0, tree, 0, PAGE_WIDTH);
			content.endText();
			
			//Can't do both at once because the drawLine function is not usable between beginText() and endText()
			//TODO Maybe fix this later on by opening and closing Text block every time when creating the text			
			drawNodeRectangles(content, 0, tree, 0, PAGE_WIDTH); 
			
			drawLines(content, 0, tree, 0, PAGE_WIDTH, 0, 0);
			
			content.close();
			doc.save(FILENAME);
			doc.close();
		}  catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void drawTest() {
		
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
		
		if(t.head.leaf && t.head.value != null) {
			centeredTextAtPosition(stream, middle, PAGE_HEIGHT - LINE_SPACE * (level + 1), t.head.value);
		}
	}
	
	private void drawNodeRectangles(PDPageContentStream stream, int level, Tree t, float leftBorder, float rightBorder) throws IOException {
		float middle = (leftBorder + rightBorder) / 2;
		
		centeredRectangleAroundTextAtPosition(stream, middle, PAGE_HEIGHT - LINE_SPACE * (level + 1), t.head.name);
		
		level++;
		
		ArrayList<Tree.Node> children = t.head.children;
		float width = (rightBorder - leftBorder) / children.size();
		for(int i = 0; i < children.size(); i++) {
			drawNodeRectangles(stream, level, new Tree(children.get(i)), leftBorder + i * width, leftBorder + (i + 1) * width);
		}
		
		if(t.head.leaf && t.head.value != null) {
//			centeredTextAtPosition(stream, middle, PAGE_HEIGHT - LINE_SPACE * (level + 1), t.head.value);
			
			centeredEllipseAroundTextAtPosition(stream, middle, PAGE_HEIGHT - LINE_SPACE * (level + 1), t.head.value);
		}
	}
	
	private void drawLines(PDPageContentStream stream, int level, Tree t, float leftBorder, float rightBorder, float parentx, float parenty) {
		float middle = (leftBorder + rightBorder) / 2;
		float y = PAGE_HEIGHT - LINE_SPACE * (level + 1);
		if(!(parentx == 0 && parenty == 0))
			Functions.drawLine(stream, middle, y + getTextHeight() + PADDING, parentx, parenty - PADDING);
		
		level++;
		
		ArrayList<Tree.Node> children = t.head.children;
		float width = (rightBorder - leftBorder) / children.size();
		for(int i = 0; i < children.size(); i++) {
			drawLines(stream, level, new Tree(children.get(i)), leftBorder + i * width, leftBorder + (i + 1) * width, middle, y);
		}
		
		if(t.head.leaf && t.head.value != null) {
//			centeredTextAtPosition(stream, middle, PAGE_HEIGHT - LINE_SPACE * (level + 1), t.head.value);
//			centeredEllipseAroundTextAtPosition(stream, middle, PAGE_HEIGHT - LINE_SPACE * (level + 1), t.head.value);
			Functions.drawLine(stream, middle, y - PADDING, middle, PAGE_HEIGHT - LINE_SPACE * (level + 1) + 15.0f + getTextHeight() / 2);
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
	 * 			centered around the cursor position. Returns negative value --> Movement to the left
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
	
	/**
	 * Creates a x-Centered (y-Bottom) Rectangle at the specified position adding {@link #PADDING} on each side
	 * @param content
	 * @param x
	 * @param y
	 * @param text
	 * @throws IOException
	 */
	private void centeredRectangleAroundTextAtPosition(PDPageContentStream content, float x, float y, String text) throws IOException {
		float height = getTextHeight();
		
		float top = y + height + PADDING
			, bottom = y - PADDING
			, left = x - -getAlignmentConstant(text) - PADDING
			, right = x + -getAlignmentConstant(text) + PADDING;
		
		
		
		Functions.drawLine(content, left, bottom, right, bottom);
		Functions.drawLine(content, left, bottom, left, top);
		Functions.drawLine(content, left, top, right, top);
		Functions.drawLine(content, right, top, right, bottom);
	}
	
	/**
	 * Creates a centered Ellipse at the specified position
	 * @param content
	 * @param x x-middle of ellipse
	 * @param y y-bottom of text in ellipse
	 * @param text
	 * @throws IOException
	 */
	private void centeredEllipseAroundTextAtPosition(PDPageContentStream content, float x, float y, String text) throws IOException {
		float height = getTextHeight();
		float 	rx = -getAlignmentConstant(text) + ELLIPSE_PADDING;
		
		Functions.drawEllipse(content, x, y + height / 2, Math.max(rx, 25), RY);
	}

	private float getTextHeight() {
		//source: https://stackoverflow.com/questions/17171815/get-the-font-height-of-a-character-in-pdfbox
		float height = (PDType1Font.HELVETICA.getFontDescriptor().getCapHeight()) / 1000 * TEXT_SIZE;
		return height;
	}
	
}
