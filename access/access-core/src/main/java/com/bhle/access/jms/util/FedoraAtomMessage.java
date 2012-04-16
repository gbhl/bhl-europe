package com.bhle.access.jms.util;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.parser.Parser;

public class FedoraAtomMessage {

	private static final String FEDORA_TYPES_DSID = "fedora-types:dsID";
	private static final String FEDORA_TYPES_STATE = "fedora-types:state";
	private static final Abdera abdera = new Abdera();

	private String pid;
	private String dsID;
	private String state;
	private Method methodName;

	public FedoraAtomMessage(String entryText) {
		Entry atom = createAtom(entryText);
		this.pid = atom.getSummary();
		this.methodName = Method.getInstance(atom.getTitle());
		this.dsID = getDsIsFromAtom(atom);
		this.state = getStateFromAtom(atom);
	}

	private String getStateFromAtom(Entry atom) {
		List<Category> categories = atom.getCategories(FEDORA_TYPES_STATE);
		if (categories != null && categories.size() == 1) {
			return categories.get(0).getTerm();
		} else {
			return null;
		}
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
		Reader reader = new StringReader(entryText);
		Document<Entry> doc = parser.parse(reader);
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	public String getState() {
		return state;
	}

}
