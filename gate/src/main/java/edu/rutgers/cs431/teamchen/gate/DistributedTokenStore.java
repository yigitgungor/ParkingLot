package edu.rutgers.cs431.teamchen.gate;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// A token distributor that talks to all other token distributors to share them
//
// Implements strategy 2
public class DistributedTokenStore implements TokenStore {
    private final Lock lock = new ReentrantLock();
    private final PeerHttpAddressProvider addressProvider;
    private ArrayList<String> tokens;

    public DistributedTokenStore(ArrayList<String> tokens, PeerHttpAddressProvider addrProvider) {
        this.addressProvider = addrProvider;
        this.tokens = tokens;
    }

    @Override
    public void addToken(String token) {
        lock.lock();
        tokens.add(token);
        lock.unlock();
    }

    @Override
    public String getToken() throws InterruptedException {
        lock.lock();
        // TODO: might not scale because the communication might take long, and  maybe the parking lot sends
        // back a token while this one method is waiting to receive one from another gate.

        lock.unlock();
        return null;
    }

    @Override
    public int count() {
        // TODO: should we wait?
        return tokens.size();
    }


}
