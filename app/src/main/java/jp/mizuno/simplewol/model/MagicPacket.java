package jp.mizuno.simplewol.model;

public final class MagicPacket {
    private MagicPacket() { }

    public static byte[] create(String macAddress) {
        String normalized = WolInput.mac(macAddress);
        String[] parts = normalized.split(":");
        byte[] packet = new byte[102];
        for (int i = 0; i < 6; i++) packet[i] = (byte) 0xff;
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 6; j++) {
                packet[6 + i * 6 + j] = (byte) Integer.parseInt(parts[j], 16);
            }
        }
        return packet;
    }
}
