package br.com.organicadigital.daoartefacts;

import org.codehaus.groovy.grails.commons.GrailsClass;

/**
 * @author <a href="mailto:lauro.becker@gmail.com">Lauro L. V. Becker</a>
 */
public interface DaoArtefactClass extends GrailsClass {
	/**
	 * @return
	 */
	Boolean getTransactional();
}
