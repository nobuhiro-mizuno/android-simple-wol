package jp.mizuno.simplewol.model;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public final class WolSender {
    private WolSender() { }

    public static void send(Pc pc) throws IOException {
        byte[] bytes = MagicPacket.create(pc.mac);
        InetAddress address = InetAddress.getByName(WolInput.broadcast(pc.broadcast));
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);
            socket.send(new DatagramPacket(bytes, bytes.length, address, pc.port));
        }
    }
}
