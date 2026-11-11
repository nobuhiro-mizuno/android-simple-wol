package jp.mizuno.simplewol.model;

import java.util.Locale;
import java.util.regex.Pattern;

public final class WolInput {
    private static final Pattern MAC = Pattern.compile("(?i)^[0-9a-f]{2}([:-])[0-9a-f]{2}(?:\\1[0-9a-f]{2}){4}$");

    private WolInput() { }

    public static String name(String value) {
        String trimmed = value == null ? "" : value.trim();
        if (trimmed.isEmpty() || trimmed.length() > 40) throw new IllegalArgumentException("name");
        return trimmed;
    }

    public static String mac(String value) {
        String trimmed = value == null ? "" : value.trim();
        if (!MAC.matcher(trimmed).matches()) throw new IllegalArgumentException("mac");
        return trimmed.replace('-', ':').toUpperCase(Locale.ROOT);
    }

    public static String broadcast(String value) {
        String trimmed = value == null ? "" : value.trim();
        String[] parts = trimmed.split("\\.", -1);
        if (parts.length != 4) throw new IllegalArgumentException("broadcast");
        for (String part : parts) {
            if (part.isEmpty() || part.length() > 3) throw new IllegalArgumentException("broadcast");
            for (int i = 0; i < part.length(); i++) {
                if (part.charAt(i) < '0' || part.charAt(i) > '9') throw new IllegalArgumentException("broadcast");
            }
            int number = Integer.parseInt(part);
            if (number > 255) throw new IllegalArgumentException("broadcast");
        }
        return trimmed;
    }

    public static int port(String value) {
        try {
            if (value == null || !value.matches("[0-9]{1,5}")) throw new NumberFormatException();
            int port = Integer.parseInt(value);
            if (port < 1 || port > 65535) throw new NumberFormatException();
            return port;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("port", e);
        }
    }
}
