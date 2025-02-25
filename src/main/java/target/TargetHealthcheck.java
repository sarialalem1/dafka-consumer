package target;

import configuration.Config;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import monitoring.Monitor;

public class TargetHealthcheck {

    private static final HttpClient client = HttpClient.newHttpClient();

    public static boolean check() {
        if (Config.TARGET_HEALTHCHECK == null) {
            return true;
        }

        try {
            final var request = HttpRequest
                .newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .uri(URI.create(Config.TARGET_BASE_URL + Config.TARGET_HEALTHCHECK))
                .build();

            var targetHealthcheckResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (targetHealthcheckResponse.statusCode() != 200) {
                Monitor.targetHealthcheckFailed(new Exception("received " + targetHealthcheckResponse.statusCode()));
                return false;
            }
            return true;
        } catch (Exception e) {
            Monitor.targetHealthcheckFailed(e);
            return false;
        }
    }
}
