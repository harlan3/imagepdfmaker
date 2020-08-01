
package orbisoftware.imagepdfmaker;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ImagePdfMaker {

	private GeneratePDF generatePDF = new GeneratePDF();

	private String fileName = "settings.xml";
	private HashMap<String, String> xmlMap = new HashMap<>();
	
	public static void main(String[] args) {

		ImagePdfMaker imagePdfMaker = new ImagePdfMaker();
		imagePdfMaker.loadXML();

		imagePdfMaker.generatePDF.saveFileName = imagePdfMaker.xmlMap.get("PdfFilename");
		imagePdfMaker.generatePDF.setPageSize(imagePdfMaker.xmlMap.get("PdfPageSize"));
		imagePdfMaker.generatePDF.borderPercent = Float.parseFloat(imagePdfMaker.xmlMap.get("BorderPercent"));
		
		imagePdfMaker.readImageDir(imagePdfMaker.xmlMap.get("ImageDir"));
	}

	private void loadXML() {

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(fileName);
			Element rootElem = doc.getDocumentElement();

			if (rootElem != null) {
				parseElements(rootElem);
			}
		} catch (Exception e) {

			System.out.println("Exception in loadXML(): " + e.toString());
		}
	}

	private void parseElements(Element root) {

		String name = "";

		if (root != null) {

			NodeList nl = root.getChildNodes();

			if (nl != null) {
				
				for (int i = 0; i < nl.getLength(); i++) {
					Node node = nl.item(i);

					if (node.getNodeName().equalsIgnoreCase("setting")) {

						NodeList childNodes = node.getChildNodes();

						for (int j = 0; j < childNodes.getLength(); j++) {

							Node child = childNodes.item(j);

							if (child.getNodeName().equalsIgnoreCase("name"))
								name = child.getTextContent();
							else if (child.getNodeName().equalsIgnoreCase("value"))
								xmlMap.put(name, child.getTextContent());
						}
					}
				}
			}
		}
	}

	private void readImageDir(String imageDir) {

		File dir = new File(imageDir);

		File[] files = dir.listFiles();

		Arrays.sort(files);

		for (File file : files) {
			if (!file.isHidden()) {
				if (!file.isDirectory()) {
					BufferedImage image = null;
					try {
						image = ImageIO.read(file);
					} catch (IOException e) {
						System.out.println("Error reading image: " + file.getName());
					} finally {
						generatePDF.imageList.add(image);
					}
				}
			}
		}

		generatePDF.generatePdf();
	}
}
