package um.tesoreria.core.hexagonal.chequera.materiasInscriptas.application.usecases;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.LoggerFactory;

import java.util.List;

/** Captura los eventos de log de una clase mientras dura el test. */
public final class LogCapture implements AutoCloseable {

    private final Logger logger;
    private final Level previousLevel;
    private final ListAppender<ILoggingEvent> appender = new ListAppender<>();

    public LogCapture(Class<?> type) {
        logger = (Logger) LoggerFactory.getLogger(type);
        previousLevel = logger.getLevel();
        logger.setLevel(Level.DEBUG);
        appender.start();
        logger.addAppender(appender);
    }

    public List<ILoggingEvent> warnings() {
        return appender.list.stream().filter(e -> e.getLevel() == Level.WARN).toList();
    }

    @Override
    public void close() {
        logger.detachAppender(appender);
        logger.setLevel(previousLevel);
        appender.stop();
    }
}
