package com.playground.basicpaxos.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProposalDB {
    private Logger logger = LoggerFactory.getLogger(ProposalDB.class);
    private final ConcurrentHashMap<String, Proposal> proposals;
    private final Map<String, Integer> promise;

    public ProposalDB() {
        proposals = new ConcurrentHashMap<>();
        promise = new HashMap<>();
    }

    public synchronized Integer makePromise(String name, Integer leastVersion) {
        Integer prevLeastVer = promise.get(name);
        if (prevLeastVer == null || prevLeastVer <= leastVersion) {
            promise.put(name, leastVersion);
            return leastVersion;
        } else {
            return prevLeastVer;
        }
    }

    public Integer getPromis(String name) {
        return promise.get(name);
    }

    public Proposal getProposal(String name) {
        Proposal proposal = proposals.get(name);
        return proposal == null ? Proposal.emptyProposal(name) : proposal;
    }

    public synchronized boolean commitProposal(Proposal proposal) {
        Integer prevLeastVer = promise.get(proposal.getName());
        if (prevLeastVer <= proposal.getVersion()) {
            proposals.put(proposal.getName(), proposal);
            makePromise(proposal.getName(), proposal.getVersion()+1);
            logger.debug("commited proposal {}", proposal);
            return true;
        } else {
            return false;
        }
    }

    public String dump() {
        StringBuffer sb = new StringBuffer();
        sb.append("PROPOSAL:");
        Enumeration<String> proposalKeys = proposals.keys();
        while (proposalKeys.hasMoreElements()) {
            String key = proposalKeys.nextElement();
            Proposal proposal =proposals.get(key);
            sb.append("["+proposal.getName()+", "+new String(proposal.getValue())+", "+proposal.getVersion()+"]");
        }
        sb.append("PROMISE:");
        for (String key: promise.keySet()) {
            sb.append("["+key+", "+promise.get(key)+"]");
        }
        return sb.toString();
    }
}
