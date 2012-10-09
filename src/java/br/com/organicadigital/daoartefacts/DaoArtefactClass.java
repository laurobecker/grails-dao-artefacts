package br.com.organicadigital.daoartefacts;

import org.codehaus.groovy.grails.commons.GrailsServiceClass;
import org.codehaus.groovy.grails.commons.InjectableGrailsClass;

/**
 * @author <a href="mailto:lauro.becker@gmail.com">Lauro L. V. Becker</a>
 */
public interface DaoArtefactClass extends InjectableGrailsClass {
	
	String DATA_SOURCE = GrailsServiceClass.DATA_SOURCE;
    String DEFAULT_DATA_SOURCE = GrailsServiceClass.DEFAULT_DATA_SOURCE;
    String ALL_DATA_SOURCES = GrailsServiceClass.ALL_DATA_SOURCES;
	
	/**
	 * @return
	 */
	Boolean getTransactional();
	
	/**
     * Get the datasource name that this service class works with.
     * @return the name
     */
    String getDatasource();
	
}
