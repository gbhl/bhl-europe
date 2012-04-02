package com.bhle.access.feed;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.bhle.access.convert.ConverterManager;
import com.bhle.access.util.FedoraURI;
import com.bhle.access.util.StaticURI;
import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.feed.atom.Link;

public class AtomFactory {
	public static Feed buildAtom(List<URI> uris) {
		Feed feed = new Feed();
		buildAtomHead(feed, uris);
		buildAtomEntries(feed, uris);
		return feed;
	}

	private static void buildAtomHead(Feed feed, List<URI> uris) {
		feed.setTitle("Test Title");
	}

	private static void buildAtomEntries(Feed feed, List<URI> uris) {
		List<Entry> entries = new ArrayList<Entry>();
		for (URI uri : uris) {
			Entry entry = buildEntry(uri);
			entries.add(entry);
		}
		feed.setEntries(entries);
	}

	private static Entry buildEntry(URI uri) {
		Entry entry = new Entry();
		entry.setTitle(uri.toString());
		List<Link> links = buildLinks(uri);
		entry.setOtherLinks(links);
		List<Content> contents = buildContents(uri);
		entry.setContents(contents);
		return entry;
	}

	private static List<Content> buildContents(URI uri) {
		List<Content> contents = new ArrayList<Content>();
		FedoraURI fedoraURI = new FedoraURI(uri);
		if (fedoraURI.getDsid() != null && fedoraURI.getSerialNumber() == null) {
			Content content = new Content();
			contents.add(content);
		}
		return contents;
	}

	private static List<Link> buildLinks(URI uri) {
		List<Link> links = new ArrayList<Link>();
		FedoraURI fedoraURI = new FedoraURI(uri);
		if (fedoraURI.getDsid() != null) {
			buildNativeLink(fedoraURI, links);
			buildStaticLink(fedoraURI, links);
		}
		return links;
	}

	private static void buildStaticLink(FedoraURI fedoraURI, List<Link> links) {
		if(fedoraURI.getDsid() != null){
			Link link = new Link();
			URI staticUri = StaticURI.toStaticHttpUri(fedoraURI);
			link.setRel("enclosure");
			link.setHref(staticUri.toString());
			links.add(link);
		}
	}

	private static void buildNativeLink(FedoraURI fedoraURI, List<Link> links) {
		Link link = new Link();
		if (fedoraURI.getSerialNumber() == null) {
			link.setRel("enclosure");
			link.setHref("./" + fedoraURI.getDsid().toLowerCase());
		} else {
			link.setHref("./" + fedoraURI.getDsid().toLowerCase() + "/"
					+ fedoraURI.getSerialNumber());
		}
		link.setType(ConverterManager.getMimeType(fedoraURI.getDsid()));
		links.add(link);
	}
}
