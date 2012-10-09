package br.com.organicadigital.daoartefacts;

import org.codehaus.groovy.grails.commons.AbstractInjectableGrailsClass;

/**
 * @author <a href="mailto:lauro.becker@gmail.com">Lauro L. V. Becker</a>
 */
public class DefaultDaoArtefactClass extends AbstractInjectableGrailsClass implements
		DaoArtefactClass {

	private String datasourceName;
	
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
	
	public String getDatasource() {
        if (datasourceName == null) {
            if (getTransactional()) {
                CharSequence name = getStaticPropertyValue(DATA_SOURCE, CharSequence.class);
                datasourceName = name == null ? null : name.toString();
                if (datasourceName == null) {
                    datasourceName = DEFAULT_DATA_SOURCE;
                }
            }
            else {
                datasourceName = "";
            }
        }

        return datasourceName;
    }

    public boolean usesDatasource(final String name) {
        return getDatasource().equals(name);
    }
	
}