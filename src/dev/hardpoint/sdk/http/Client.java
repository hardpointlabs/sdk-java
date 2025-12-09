package dev.hardpoint.sdk.http;

import java.net.http.HttpClient;
import java.time.Duration;

public final class Client {

    public static HttpClient newClient() {
        var hc = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        return hc;
    }
}
