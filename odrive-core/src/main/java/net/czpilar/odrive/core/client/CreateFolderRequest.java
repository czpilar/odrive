package net.czpilar.odrive.core.client;

import java.util.Map;

/**
 * Request body for creating a folder via Microsoft Graph API.
 *
 * @param name   folder name
 * @param folder empty map indicating this is a folder (required by Graph API)
 * @author David Pilar (david@czpilar.net)
 */
public record CreateFolderRequest(String name, Map<String, Object> folder) {

    public CreateFolderRequest(String name) {
        this(name, Map.of());
    }
}
