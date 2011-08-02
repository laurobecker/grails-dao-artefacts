package br.com.organicadigital.daoartefacts;

import org.codehaus.groovy.grails.commons.ArtefactHandlerAdapter;

/**
 * @author <a href="mailto:lauro.becker@gmail.com">Lauro L. V. Becker</a>
 */
public class DaoArtefactHandler extends ArtefactHandlerAdapter {
	static public final String TYPE = "DaoArtefact";
	static public final String SUFFIX = "Dao";

	/**
     * 
     */
	public DaoArtefactHandler() {
		super(TYPE, DaoArtefactClass.class, DefaultDaoArtefactClass.class,
				SUFFIX);
	}
}