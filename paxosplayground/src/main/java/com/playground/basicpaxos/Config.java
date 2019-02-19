package com.playground.basicpaxos;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Config {
    private Set<Integer> serverIds = new HashSet<>();
    private Map<Integer, InetSocketAddress> peerAddrMap = new HashMap<>();
    private Map<Integer, InetSocketAddress> clientAddrMap = new HashMap<>();

    // expecting
    // server1:xxx.xxx.xx.xx:8888:9999,server2:xxx.xxx.xxx.xx:8888:9999
    //      sid   ip       peer  client
    public static Config parse(String raw) {
        Config config = new Config();
        String[] serverRaws = raw.split(",");
        for (String serverRaw : serverRaws) {
            String[] serverSegs = serverRaw.split(":");
            int sid = Integer.parseInt(serverSegs[0].substring("server".length()));
            String ip = serverSegs[1];
            int peerPort = Integer.parseInt(serverSegs[2]);
            int clientPort = Integer.parseInt(serverSegs[3]);

            config.serverIds.add(sid);
            config.peerAddrMap.put(sid, new InetSocketAddress(ip, peerPort));
            config.clientAddrMap.put(sid, new InetSocketAddress(ip, clientPort));
        }
        return config;
    }

    public Set<Integer> getServerIds() {
        return serverIds;
    }

    public void setServerIds(Set<Integer> serverIds) {
        this.serverIds = serverIds;
    }

    public Map<Integer, InetSocketAddress> getPeerAddrMap() {
        return peerAddrMap;
    }

    public void setPeerAddrMap(Map<Integer, InetSocketAddress> peerAddrMap) {
        this.peerAddrMap = peerAddrMap;
    }

    public int quorumSize() {
        return serverIds.size() / 2 + 1;
    }

    public Map<Integer, InetSocketAddress> getClientAddrMap() {
        return clientAddrMap;
    }

    public void setClientAddrMap(Map<Integer, InetSocketAddress> clientAddrMap) {
        this.clientAddrMap = clientAddrMap;
    }
}
