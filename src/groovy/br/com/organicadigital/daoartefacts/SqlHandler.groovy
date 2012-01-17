package br.com.organicadigital.daoartefacts

import groovy.sql.Sql

import java.util.logging.Handler
import java.util.logging.Level
import java.util.logging.LogRecord

import org.apache.log4j.Logger

/**
 * Responsible for integrate logs of {@link Sql} from java.util.logging to org.apache.log4j.
 * 
 * @author <a href="mailto:lauro.becker@gmail.com">Lauro L. V. Becker</a>
 */
class SqlHandler extends Handler {

	private Logger log = Logger.getLogger(Sql)

	/**
	 * @see Handler#publish(LogRecord)
	 */
	@Override
	public void publish(LogRecord record) {
		Level level = record.level
		String message = record.message
		Throwable thrown = record.thrown
		if (Level.SEVERE == level) {
			log.error(message, thrown)
		} else if (level in [
			Level.INFO,
			Level.CONFIG,
			Level.FINE,
			Level.FINER,
			Level.FINEST
		]) {
			log.info(message, thrown)
		} else if (Level.ALL == level) {
			log.trace(message, thrown)
		}
	}

	@Override
	public void flush() {
	}

	@Override
	public void close() throws SecurityException {
	}
}
