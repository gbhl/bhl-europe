package com.bhle.access.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

public class MetadataUtil implements ResourceLoaderAware {
	private static ResourceLoader resourceLoader;

	public void setResourceLoader(ResourceLoader resourceLoader) {
		MetadataUtil.resourceLoader = resourceLoader;
	}

	private static TransformerFactory tfactory = TransformerFactory
			.newInstance();

	public static InputStream olefToDc(InputStream xmlInputStream) {
		InputStream xslInputStream = null;
		Resource olef2DcXsl = resourceLoader
				.getResource("classpath:xslt/OLEF2simpleDC.xsl");
		try {
			xslInputStream = olef2DcXsl.getInputStream();
			InputStream result = transformXMLInputStream(xmlInputStream,
					xslInputStream);
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				xmlInputStream.close();
				xslInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static InputStream olefToMods(InputStream xmlInputStream) {
		InputStream xslInputStream = null;
		Resource olef2DcXsl = resourceLoader
				.getResource("classpath:xslt/OLEF2MODS.xsl");
		try {
			xslInputStream = olef2DcXsl.getInputStream();
			InputStream result = transformXMLInputStream(xmlInputStream,
					xslInputStream);
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				xmlInputStream.close();
				xslInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static InputStream olefToMarc21(InputStream xmlInputStream) {
		InputStream xslInputStream = null;
		Resource olef2DcXsl = resourceLoader
				.getResource("classpath:xslt/OLEF2MARC21slim.xsl");
		try {
			xslInputStream = olef2DcXsl.getInputStream();
			InputStream result = transformXMLInputStream(xmlInputStream,
					xslInputStream);
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				xmlInputStream.close();
				xslInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static InputStream olefToBibText(InputStream xmlInputStream) {
		InputStream modsInputStream = MetadataUtil.olefToMods(xmlInputStream);
		try {
			xmlInputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return BibUtils.mods2BibTex(modsInputStream);
	}

	public static InputStream olefToEndnote(InputStream xmlInputStream) {
		InputStream modsInputStream = MetadataUtil.olefToMods(xmlInputStream);
		try {
			xmlInputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return BibUtils.mods2Endnote(modsInputStream);
	}

	private static InputStream transformXMLInputStream(
			InputStream xmlInputStream, InputStream xslInputStream) {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();

		try {
			Transformer transformer = tfactory.newTransformer(new StreamSource(
					xslInputStream));
			transformer.transform(new StreamSource(xmlInputStream),
					new StreamResult(bout));
		} catch (TransformerException e) {
			e.printStackTrace();
		} finally {
			try {
				xmlInputStream.close();
				xslInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		InputStream bin = new ByteArrayInputStream(bout.toByteArray());
		try {
			bout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bin;
	}
}
