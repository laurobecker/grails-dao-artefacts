package br.com.organicadigital.daoartefacts;

import org.codehaus.groovy.grails.commons.AbstractGrailsClass;

/**
 * @author <a href="mailto:lauro.becker@gmail.com">Lauro L. V. Becker</a>
 */
public class DefaultDaoArtefactClass extends AbstractGrailsClass implements
		DaoArtefactClass {

	/**
	 * @param clazz
	 */
	public DefaultDaoArtefactClass(Class<?> clazz) {
		super(clazz, DaoArtefactHandler.SUFFIX);
	}

	/**
	 * @see DaoArtefactClass#getTransactional()
	 */
	@Override
	public Boolean getTransactional() {
		return true;
	}
}