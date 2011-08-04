package br.com.organicadigital.daoartefacts;

import org.codehaus.groovy.grails.commons.ArtefactHandlerAdapter;

/**
 * @author <a href="mailto:lauro.becker@gmail.com">Lauro L. V. Becker</a>
 */
public class DaoArtefactResourcesHandler extends ArtefactHandlerAdapter {
	static public final String TYPE = "DaoArtefactResources";
	static public final String SUFFIX = "DaoArtefactResources";

	/**
     * 
     */
	public DaoArtefactResourcesHandler() {
		super(TYPE, DaoArtefactResourcesClass.class,
				DefaultDaoArtefactResourcesClass.class, SUFFIX);
	}
}