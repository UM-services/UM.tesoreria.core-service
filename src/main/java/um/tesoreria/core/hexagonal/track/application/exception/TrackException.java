package um.tesoreria.core.hexagonal.track.application.exception;

import java.text.MessageFormat;

public class TrackException extends RuntimeException {

    private static final long serialVersionUID = -8437455926582914496L;

    public TrackException(Long trackId) {
        super(MessageFormat.format("Cannot find Track {0}", trackId));
    }

    public TrackException() {
        super("Cannot find Track");
    }
}
