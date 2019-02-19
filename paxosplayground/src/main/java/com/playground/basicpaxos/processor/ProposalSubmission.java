package com.playground.basicpaxos.processor;

import com.playground.basicpaxos.store.Proposal;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class ProposalSubmission {

    private Proposal proposal;

    private Map<Integer, Proposal> lastProposals;

    public ProposalSubmission(Proposal proposal) {
        this.proposal = proposal;
        this.lastProposals = new HashMap<>();
    }

    public Proposal getProposal() {
        return proposal;
    }

    public String getName() {
        return proposal.getName();
    }

    public byte[] getVal() {
        return proposal.getValue();
    }

    public void addLastProposal(int sid, Proposal proposal) {
        lastProposals.put(sid, proposal);
    }

    public Collection<Proposal> getLastProposals() {
        return lastProposals.values();
    }

    public Proposal highestVersionedLastProposal() {
        int ver = -1;
        Proposal highestVersioned = null;
        for (Proposal proposal : lastProposals.values()) {
            if (proposal.getVersion() > ver) {
                highestVersioned = proposal;
                ver = highestVersioned.getVersion();
            }
        }
        return highestVersioned == null ? Proposal.EMPTY_PROPOSAL : highestVersioned;
    }
}
