package edu.rutgers.cs431.teamchen.gate.token;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// A token distributor that doesn't allow a gate to share the token with the others
//
// Implements strategy 1: Gates don't share tokens
public class NoShareTokenStore implements TokenStore {

    private final Lock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition();
    private ArrayList<String> tokens;

    public NoShareTokenStore(ArrayList<String> tokens) {
        this.tokens = tokens;
    }

    @Override
    public void addToken(String token) {
        lock.lock();
        tokens.add(token);
        notEmpty.signal();
        lock.unlock();
    }

    @Override
    public String getToken() throws InterruptedException {
        String retrieved;
        lock.lock();
        try {
            while (tokens.size() == 0) {
                notEmpty.await();
            }
            retrieved = tokens.get(0);
            tokens.remove(0);
        } finally {
            lock.unlock();
        }
        return retrieved;
    }

    public int count() {
        // TODO: should we wait with lock?
        return tokens.size();
    }
}
