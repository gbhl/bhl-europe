/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.bhl_europe.ingest_standalone;

import java.io.File;
import java.io.FilenameFilter;

/**
 *
 * @author wkoller
 */
public class SipFileFilter implements FilenameFilter {
    private String m_regex;

    public SipFileFilter() {
        m_regex = Ingest.m_settings.getProperty("sip.filter.regex");
    }

    @Override
    public boolean accept(File dir, String name) {
        return name.matches(m_regex);
    }
}
