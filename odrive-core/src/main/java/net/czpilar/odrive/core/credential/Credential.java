package net.czpilar.odrive.core.credential;

/**
 * Credential holder for OneDrive OAuth2 tokens.
 *
 * @author David Pilar (david@czpilar.net)
 */
public record Credential(String accessToken, String refreshToken) {
}
