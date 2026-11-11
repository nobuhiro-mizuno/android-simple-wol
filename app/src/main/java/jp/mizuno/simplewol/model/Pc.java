package jp.mizuno.simplewol.model;

public final class Pc {
    public final String id;
    public final String name;
    public final String mac;
    public final String broadcast;
    public final int port;

    public Pc(String id, String name, String mac, String broadcast, int port) {
        this.id = id;
        this.name = name;
        this.mac = mac;
        this.broadcast = broadcast;
        this.port = port;
    }
}
