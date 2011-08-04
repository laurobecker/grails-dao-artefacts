import grails.spring.BeanBuilder;

import java.lang.reflect.Method

import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.codehaus.groovy.grails.commons.DefaultGrailsClass
import org.codehaus.groovy.grails.commons.spring.TypeSpecifyableTransactionProxyFactoryBean
import org.codehaus.groovy.grails.orm.support.GroovyAwareNamedTransactionAttributeSource
import org.springframework.beans.factory.config.MethodInvokingFactoryBean
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.transaction.annotation.Transactional

import br.com.organicadigital.daoartefacts.DaoArtefactClass
import br.com.organicadigital.daoartefacts.DaoArtefactHandler
import br.com.organicadigital.daoartefacts.DaoArtefactResourcesHandler;

class DaoArtefactsGrailsPlugin {
	def version = "0.2"
	def grailsVersion = "1.3.7 > *"
	def dependsOn = [:]
	def loadAfter = ["hibernate"]
	def pluginExcludes = [
		"grails-app/views/error.gsp"
	]

	def author = "Lauro L. V. Becker"
	def authorEmail = "lauro.becker@gmail.com"
	def title = "Dao Artefacts"
	def description = '''\\
'''

	def documentation = "http://grails.org/plugin/dao-artefacts"

	def artefacts = [DaoArtefactHandler, DaoArtefactResourcesHandler]

	def watchedResources = [
		"file:./grails-app/daos/**/*Dao.groovy",
		"file:../../plugins/*/daos/**/*Dao.groovy",
		"file:./grails-app/conf/**/*DaoArtefactResources.groovy",
		"file:../../plugins/*/conf/**/*DaoArtefactResources.groovy"
	]

	private def daoFactory;
	
	/**
	 * 
	 */
	def doWithSpring = {
		xmlns tx:"http://www.springframework.org/schema/tx"
		tx.'annotation-driven'('transaction-manager' : 'transactionManager')

		Map<String, DaoArtefactClass> beanNames = new HashMap<String, DaoArtefactClass>();
		String dsName = getDataSourceName();
		for (daoClass in application.daoArtefactClasses) {
			String beanName = daoClass.propertyName;
			if (otherDsBean(beanName)) {
				continue;
			}

			Boolean specific = false;
			if (dsName) {
				if (beanName.indexOf(dsName) > -1) {
					beanName = beanName.replace(dsName, "")
					specific = true;
				}
			}

			DaoArtefactClass oldInfo = beanNames[beanName];
			if (oldInfo) {
				if (specific) {
					beanNames[beanName] = daoClass
				}
			} else {
				beanNames[beanName] = daoClass
			}
		}

		beanNames.each { String name, DaoArtefactClass daoClass ->
			def fctClass = getDaoFactory(application)?."$name";
			if(fctClass) {
				daoClass = application.addArtefact(DaoArtefactHandler.TYPE, fctClass);
			}
			
			def scope = daoClass.getPropertyValue("scope");
			"${daoClass.fullName}DaoClass"(MethodInvokingFactoryBean) { bean ->
				bean.lazyInit = true
				targetObject = ref("grailsApplication", true)
				targetMethod = "getArtefact"
				arguments = [
					DaoArtefactHandler.TYPE,
					daoClass.fullName
				]
			}

			def hasDataSource = (application.config?.dataSource || application.domainClasses)
			if (hasDataSource && shouldCreateTransactionalProxy(daoClass)) {
				def props = new Properties()
				props."*" = "PROPAGATION_REQUIRED"
				"${name}"(TypeSpecifyableTransactionProxyFactoryBean, daoClass.clazz) { bean ->
					if (scope) bean.scope = scope
					bean.lazyInit = true
					target = { innerBean ->
						innerBean.lazyInit = true
						innerBean.factoryBean = "${daoClass.fullName}DaoClass"
						innerBean.factoryMethod = "newInstance"
						innerBean.autowire = "byName"
						if (scope) innerBean.scope = scope
					}
					proxyTargetClass = true
					transactionAttributeSource = new GroovyAwareNamedTransactionAttributeSource(transactionalAttributes:props)
					transactionManager = ref("transactionManager")
				}
			}
			else {
				"${name}"(daoClass.getClazz()) { bean ->
					bean.autowire =  true
					bean.lazyInit = true
					if (scope) {
						bean.scope = scope
					}
				}
			}
		}
		
		
		
	}

	/**
	 *
	 */
	def onChange = { event ->
		daoFactory = null;
		
		if (!event.source || !event.ctx) {
			return
		}

		/* Looking for a specific class. */
		String sourceName = event.source.name;
		String specificName = sourceName.substring(0, sourceName.indexOf("Dao")) + dataSourceName + "Dao";
		def oldClass = application.getDaoArtefactClass(specificName)
		if (!oldClass) {
			oldClass = application.getDaoArtefactClass(sourceName)
		}

		def daoClass = application.addArtefact(DaoArtefactHandler.TYPE, event.source);
		def daoName = daoClass.propertyName.replace(getDataSourceName()?:"", "");
		if(otherDsBean(daoName)) {
			return;
		}

		application.daoArtefactClasses.each {
			if (it.clazz != event.source && oldClass.clazz.isAssignableFrom(it.clazz)) {
				def newClass = application.classLoader.reloadClass(it.clazz.name)
				if (daoName == new DefaultGrailsClass(newClass).propertyName.replace(getDataSourceName()?:"", "")) {
					daoClass = application.addArtefact(DaoArtefactHandler.TYPE, newClass)
				}
			}
		}

		def scope = daoClass.getPropertyValue("scope");
		if (shouldCreateTransactionalProxy(daoClass) && event.ctx.containsBean("transactionManager")) {
			def beans = beans {
				"${daoClass.fullName}DaoClass"(MethodInvokingFactoryBean) {
					targetObject = ref("grailsApplication", true)
					targetMethod = "getArtefact"
					arguments = [
						DaoArtefactHandler.TYPE,
						daoClass.fullName
					]
				}
				def props = new Properties()
				props."*"="PROPAGATION_REQUIRED"
				"${daoName}"(TypeSpecifyableTransactionProxyFactoryBean, daoClass.clazz) { bean ->
					if (scope) bean.scope = scope
					target = { innerBean ->
						innerBean.factoryBean = "${daoClass.fullName}DaoClass"
						innerBean.factoryMethod = "newInstance"
						innerBean.autowire = "byName"
						if (scope) innerBean.scope = scope
					}
					proxyTargetClass = true
					transactionAttributeSource = new GroovyAwareNamedTransactionAttributeSource(transactionalAttributes:props)
					transactionManager = ref("transactionManager")
				}
			}
			beans.registerBeans(event.ctx)
		}
		else {
			def beans = beans {
				"$daoName"(daoClass.getClazz()) { bean ->
					bean.autowire =  true
					if (scope) {
						bean.scope = scope
					}
				}
			}
			beans.registerBeans(event.ctx)
		}
	}

	/**
	 * @param daoClass 
	 */
	boolean shouldCreateTransactionalProxy(DaoArtefactClass daoClass) {
		Class javaClass = daoClass.clazz

		try {
			daoClass.transactional &&
					!AnnotationUtils.findAnnotation(javaClass, Transactional) &&
					!javaClass.methods.any { Method m -> AnnotationUtils.findAnnotation(m, Transactional)!=null }
		} catch (e) {
			return false
		}
	}

	def getDaoArtefactsConfig() {
		ConfigurationHolder.config.grails.plugins.daoartefacts
	}

	/**
	 * 
	 */
	String getDataSourceName() {
		def dataSourceName = daoArtefactsConfig.datasource.name;
		if (!dataSourceName) {
			dataSourceName = null
		}

		return dataSourceName
	}

	/**
	 *
	 */
	List<String> getAllDataSources() {
		def allDatasources = daoArtefactsConfig.datasource.all;
		if (!allDatasources) {
			allDatasources = []
		}

		return allDatasources
	}

	/**
	 * 
	 */
	Boolean otherDsBean(String beanName) {
		List<String> allDs = allDataSources;
		allDs.remove(dataSourceName)
		Boolean otherDao = false;
		for (oDs in allDs) {
			otherDao = beanName.indexOf("${oDs}Dao") > -1;
			if (otherDao) {
				break;
			}
		}

		return otherDao
	}
	
	/**
	 * 
	 */
	private def getDaoFactory(application) {
		if (!daoFactory) {
			ConfigSlurper confSlurper = new ConfigSlurper();
			def beansConf = null;
			for (daoResourcesClass in application.daoArtefactResourcesClasses) {
				def confB = confSlurper.parse(daoResourcesClass.clazz);
				if (!beansConf) {
					beansConf = confB;
				} else {
					beansConf = beansConf.merge(confB);
				}
			}
	
			daoFactory = beansConf;
		}
		
		return daoFactory;
	}
}