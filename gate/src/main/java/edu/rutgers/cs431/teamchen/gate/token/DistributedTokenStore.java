package edu.rutgers.cs431.teamchen.gate.token;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import edu.rutgers.cs431.teamchen.proto.ShareTokenRequest;
import edu.rutgers.cs431.teamchen.proto.ShareTokenResponse;
import edu.rutgers.cs431.teamchen.util.PeerHttpAddressProvider;
import edu.rutgers.cs431.teamchen.util.SystemConfig;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// A token distributor that talks to all other token distributors to share tokens
//
// Implements strategy 2
public class DistributedTokenStore implements TokenStore, HttpHandler {
    private final Lock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition();
    private final PeerHttpAddressProvider addressProvider;
    private ArrayList<String> tokens;

    public DistributedTokenStore(ArrayList<String> tokens, PeerHttpAddressProvider addrProvider, HttpServer server) {
        this.addressProvider = addrProvider;
        this.tokens = tokens;
        // registers the http handler for sharing token with other DistributedTokenStore
        server.createContext(SystemConfig.GATE_SHARE_TOKEN_PATH, this);
    }

    // make an HTTP connection to the gate peer for a token exchange
    private static String contactPeerForToken(URL peerAddr) throws IOException {
        Gson gson = new Gson();
        HttpURLConnection conn = (HttpURLConnection) (new URL(peerAddr, SystemConfig.GATE_SHARE_TOKEN_PATH)).openConnection();
        // Write a request to the server
        conn.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
        gson.toJson(new ShareTokenRequest(), writer);
        writer.flush();
        writer.close();

        // receives a token response
        InputStreamReader reader = new InputStreamReader(conn.getInputStream());
        ShareTokenResponse resp = gson.fromJson(reader, ShareTokenResponse.class);
        reader.close();
        return resp.token;
    }

    @Override
    public void addToken(String token) {
        lock.lock();
        tokens.add(token);
        notEmpty.notify();
        lock.unlock();
    }

    // serves token to other gate peer upon request
    @Override
    public void handle(HttpExchange ex) throws IOException {
        Gson gson = new Gson();

        // Read request
        InputStreamReader reqBody = new InputStreamReader(ex.getRequestBody());
        gson.fromJson(reqBody, ShareTokenRequest.class);
        reqBody.close();

        // Get the token
        String sharedToken = null;
        lock.lock();
        if (this.tokens.size() == 0) {
            sharedToken = this.tokens.get(0);
            this.tokens.remove(0);
        }
        lock.unlock();

        // Construct a response with the token if there's any
        ex.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
        ShareTokenResponse resp = new ShareTokenResponse();
        resp.token = sharedToken;

        // Reply with the token
        OutputStreamWriter writer = new OutputStreamWriter(ex.getResponseBody());
        gson.toJson(resp, writer);
        writer.flush();
        writer.close();
    }

    // request the peers for a token, this might not return a token from the peers
    private void requestPeersForToken() {
        for (URL addr : addressProvider.getAddresses()) {   // contact each peers til we get at most 1 token
            String token = null;
            try {
                token = contactPeerForToken(addr);
            } catch (IOException ex) {
                System.err.println("DistributeTokenStore: can't get token from peer " + addr.toString() + ": " + ex
                        .getMessage());
            }
            if (token != null) { // has received a token from one peer, add it to the pool
                this.addToken(token);
                break;
            }
        }
    }

    @Override
    public String getToken() throws InterruptedException {
        String token;

        lock.lock();
        try {
            while (tokens.size() == 0) {
                new Thread(() -> requestPeersForToken()).start(); // asks a peer for a token from another thread
                notEmpty.await(); // puts this thread to sleep
            }
            token = tokens.get(0);
            tokens.remove(0);
        } finally {
            lock.unlock();
        }

        return token;
    }

    @Override
    public int count() {
        return tokens.size();
    }


}
