package br.com.organicadigital.daoartefacts

import grails.util.Holders
import groovy.sql.Sql

import java.sql.Connection

import javax.sql.DataSource

class DaoArtefactsUtil {

	static ThreadLocal<Sql> threadLocal = new ThreadLocal<Sql>()
	
	/**
	 * Wrap sql calls of the Daos objects to use the same database connection.
	 * Useful when you have database objects that are stateful.  
	 * @param cls
	 * @return
	 */
	static withSameConnection(Closure cls) {
		Sql sql = threadLocal.get() 
		boolean owner = sql == null 
		
		if( owner ) {
			Connection connection = getDataSource().connection
			sql = new Sql(connection)
			threadLocal.set(sql)
		}
		try {
			
			cls()
			
		} finally {
			if(owner) {
				threadLocal.remove()
				sql.close()
			}
		}
		
	} 

	static getSql() {
		Sql sql = threadLocal.get()
		if(!sql) {
			sql = new Sql(getDataSource())
		}
		return sql
	}
	
	static handleMethodMissing(String methodName, args) {
		Sql s = getSql()
		try {
			s.invokeMethod(methodName, args)
		} finally {
			if(!threadLocal.get()) {
				s.close()
			}
		}
		
	}
	
	private static DataSource getDataSource() {
		Holders.getGrailsApplication().mainContext.getBean("dataSource")
	}
	
}
