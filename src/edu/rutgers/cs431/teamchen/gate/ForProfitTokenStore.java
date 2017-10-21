package edu.rutgers.cs431.teamchen.gate;

import java.util.ArrayList;

public class ForProfitTokenStore implements TokenStore {

    private final PeerHttpAddressProvider addressProvider;
    private ArrayList<String> tokens;

    public ForProfitTokenStore(ArrayList<String> tokens, PeerHttpAddressProvider addrProvider) {
        this.tokens = tokens;
        this.addressProvider = addrProvider;
    }

    public String getToken() throws InterruptedException {
        // TODO
        return null;
    }

    @Override
    public void addToken(String token) {
        // TODO
    }

    @Override
    public int count() {
        // TODO
        return 0;
    }
}
