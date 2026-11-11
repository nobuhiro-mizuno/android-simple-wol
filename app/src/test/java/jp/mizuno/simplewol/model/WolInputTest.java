package jp.mizuno.simplewol.model;

import org.junit.Test;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import static org.junit.Assert.*;

public class WolInputTest {
    @Test public void normalizesMac() {
        assertEquals("AA:BB:CC:DD:EE:FF", WolInput.mac(" aa-bb-cc-dd-ee-ff "));
    }

    @Test public void rejectsInvalidMac() {
        assertInvalid(() -> WolInput.mac("aa:bb-cc:dd:ee:ff"));
        assertInvalid(() -> WolInput.mac("aa:bb:cc:dd:ee"));
    }

    @Test public void validatesIpv4AndPort() {
        assertEquals("192.168.1.255", WolInput.broadcast("192.168.1.255"));
        assertEquals(9, WolInput.port("9"));
        assertInvalid(() -> WolInput.broadcast("192.168.1.256"));
        assertInvalid(() -> WolInput.broadcast("host.local"));
        assertInvalid(() -> WolInput.port("0"));
        assertInvalid(() -> WolInput.port("65536"));
    }

    @Test public void createsMagicPacket() {
        byte[] packet = MagicPacket.create("01:23:45:67:89:AB");
        assertEquals(102, packet.length);
        for (int i = 0; i < 6; i++) assertEquals((byte) 0xff, packet[i]);
        byte[] mac = {1, 0x23, 0x45, 0x67, (byte) 0x89, (byte) 0xab};
        for (int repeat = 0; repeat < 16; repeat++) {
            for (int j = 0; j < 6; j++) assertEquals(mac[j], packet[6 + repeat * 6 + j]);
        }
    }

    @Test public void sendsPacketOverUdp() throws Exception {
        try (DatagramSocket receiver = new DatagramSocket(0, InetAddress.getLoopbackAddress())) {
            receiver.setSoTimeout(2000);
            Pc pc = new Pc("test", "Test", "01:23:45:67:89:AB", "127.0.0.1", receiver.getLocalPort());
            WolSender.send(pc);
            DatagramPacket received = new DatagramPacket(new byte[128], 128);
            receiver.receive(received);
            assertEquals(102, received.getLength());
            byte[] expected = MagicPacket.create(pc.mac);
            for (int i = 0; i < expected.length; i++) {
                assertEquals(expected[i], received.getData()[i]);
            }
        }
    }

    private void assertInvalid(Runnable action) {
        try { action.run(); fail("Expected invalid input"); }
        catch (IllegalArgumentException expected) { }
    }
}
