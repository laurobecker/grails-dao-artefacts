package br.com.organicadigital.daoartefacts;

import org.codehaus.groovy.grails.commons.AbstractGrailsClass;

/**
 * @author <a href="mailto:lauro.becker@gmail.com">Lauro L. V. Becker</a>
 */
public class DefaultDaoArtefactResourcesClass extends AbstractGrailsClass
		implements DaoArtefactResourcesClass {

	/**
	 * @param clazz
	 */
	public DefaultDaoArtefactResourcesClass(Class<?> clazz) {
		super(clazz, DaoArtefactResourcesHandler.SUFFIX);
	}
}