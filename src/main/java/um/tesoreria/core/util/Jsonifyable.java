package um.tesoreria.core.util;

public interface Jsonifyable {

    default String jsonify() {
        return Jsonifier.builder(this).build();
    }

}
