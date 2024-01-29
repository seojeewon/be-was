package webserver;

import java.io.IOException;
import java.util.Arrays;

public interface MethodType {
    HttpMessage service(HttpMessage request) throws IOException;

}
