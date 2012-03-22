package com.bhle.access.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Olef {

	private Document doc;

	public Olef(URL url) throws IOException {
		this(url.openStream());
	}

	public Olef(InputStream inputStrem) {
		try {
			DocumentBuilderFactory domFactory = DocumentBuilderFactory
					.newInstance();
			domFactory.setNamespaceAware(true);
			DocumentBuilder builder = domFactory.newDocumentBuilder();

			this.doc = builder.parse(inputStrem);

		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	public String getTitle() {
		return (String) evaluateXpath("//mods:titleInfo[1]/mods:title/text()",
				XPathConstants.STRING);
	}

	public String getEntryPage() {
		return (String) evaluateXpath(
				"count(//olef:reference[lower-case(@type)='title']/../preceding-sibling::*)+1",
				XPathConstants.STRING);
	}

	public String getPageName(int i) {
		// TODO need more spec
		String pageName = (String) evaluateXpath(
				"//olef:itemInformation/olef:files/olef:file[" + (i + 1)
						+ "]/olef:pages/olef:page/olef:name/text()",
				XPathConstants.STRING);
		if (pageName == null || pageName.equals("")) {
			System.out.println("Not Found");
			pageName = String.valueOf(i + 1);
		}
		return pageName;
	}

	public List<String> getScientificNames(int i) {
		NodeList scientificNameNodes = (NodeList) evaluateXpath(
				"//olef:itemInformation/olef:files/olef:file["
						+ (i + 1)
						+ "]/olef:pages/olef:page/olef:taxon/dwc:scientificName/text()",
				XPathConstants.NODESET);

		List<String> scientificNames = new ArrayList<String>();

		for (int j = 0; j < scientificNameNodes.getLength(); j++) {
			scientificNames.add(scientificNameNodes.item(j).getNodeValue());
		}
		return scientificNames;
	}

	private Object evaluateXpath(String xpathExpression, QName returnType) {
		Object result = "";
		try {
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();

			xpath.setNamespaceContext(new OlefNamespaceContext());

			XPathExpression expr = xpath.compile(xpathExpression);
			Object obj = expr.evaluate(doc, returnType);
			result = obj;
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return result;
	}
}
