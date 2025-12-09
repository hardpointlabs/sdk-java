package dev.hardpoint.sdk;

import dev.hardpoint.internal.http.Client;

import java.math.BigInteger;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

public final class HardpointSdk {

    public static void tenantCreated(TenantKey tenantKey, Signer signer) {
        var hc = Client.newClient();

        var creationTime = Instant.now().getEpochSecond();

        var requestBody = "{\"tenantId\":\"" + tenantKey + "\"}";
        var digest = Base64.getEncoder().encodeToString(computeSHA512(requestBody).getBytes(StandardCharsets.UTF_8));

        var request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-Digest", "sha512=:" + digest)
                .header("Content-Type", "application/json")
                .header("Signature-Input", "sig1=(\"@method\" \"content-digest\");created=" + creationTime + ";keyid=\"my-key\"")
                .header("Signature", "sig1=" + signer.apply(digest))
                .build();
    }

    public static Optional<String> getConnectionString(TenantKey tenantKey) {
        return Optional.empty();
    }

    private static String computeSHA512(String input) {
        MessageDigest md;
        try {
            // Get an instance of the SHA-512 MessageDigest
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-512 algorithm not found", e);
        }
            // Convert the input string to bytes and compute the hash
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert the byte array into a signum representation BigInteger
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert the BigInteger into a hexadecimal string
            String hashtext = no.toString(16);

            // Pad the hash with leading zeros to ensure it's 128 characters long
            while (hashtext.length() < 128) {
                hashtext = "0" + hashtext;
            }

            return hashtext;
    }
}
