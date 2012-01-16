package com.bhle.access.jms.util;

import java.io.StringReader;
import java.util.List;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.parser.Parser;


import com.bhle.access.domain.Method;

public class FedoraAtomMessage {

	private static final String FEDORA_TYPES_DSID = "fedora-types:dsID";
	private static final Abdera abdera = new Abdera();

	private String pid;
	private String dsID;
	private Method methodName;

	public FedoraAtomMessage(String entryText) {
		Entry atom = createAtom(entryText);
		this.pid = atom.getSummary();
		this.methodName = Method.getInstance(atom.getTitle());
		this.dsID = getDsIsFromAtom(atom);
	}

	private String getDsIsFromAtom(Entry atom) {
		List<Category> categories = atom.getCategories(FEDORA_TYPES_DSID);
		if (categories != null && categories.size() == 1) {
			return categories.get(0).getTerm();
		} else {
			return null;
		}
	}

	private Entry createAtom(String entryText) {
		Parser parser = abdera.getParser();
		Document<Entry> doc = parser.parse(new StringReader(entryText));
		return doc.getRoot();
	}

	public String getPid() {
		return pid;
	}

	public String getDsID() {
		return dsID;
	}

	public Method getMethodName() {
		return methodName;
	}

}
