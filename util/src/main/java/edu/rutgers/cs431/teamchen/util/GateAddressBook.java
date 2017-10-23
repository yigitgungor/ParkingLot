package edu.rutgers.cs431.teamchen.util;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.rutgers.cs431.teamchen.proto.GateHttpAddressesChangeRequest;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// manages gate addressing
// doesn't store the http address of the one holding this object
public class GateAddressBook implements HttpHandler, PeerHttpAddressProvider {

    private final Lock peerHttpAddrsLock = new ReentrantLock();
    private ArrayList<URL> peerHttpAddrs;

    public void setPeerHttpAddresses(ArrayList<URL> peerAddrs) {
        peerHttpAddrsLock.lock();
        this.peerHttpAddrs = peerAddrs;
        peerHttpAddrsLock.unlock();
    }

    public void setPeerHttpAddresses2(ArrayList<String> peerAddrs) throws MalformedURLException {
        ArrayList<URL> addrs = new ArrayList<>();
        for (String addr : peerAddrs) {
            addrs.add(new URL(addr));
        }
        this.setPeerHttpAddresses(addrs);
    }

    public ArrayList<URL> getAddresses() {
        ArrayList<URL> res;
        peerHttpAddrsLock.lock();
        res = peerHttpAddrs;
        peerHttpAddrsLock.unlock();
        return res;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        Gson gson = new Gson();
        InputStreamReader reader = new InputStreamReader(ex.getRequestBody());
        GateHttpAddressesChangeRequest req = gson.fromJson(reader, GateHttpAddressesChangeRequest.class);
        reader.close();

        ex.sendResponseHeaders(HttpURLConnection.HTTP_OK, -1);
        ex.close();

        if (req.index >= 0) { // ignore < 0 index
            req.gateHttpAddrs.remove(req.index);
        }
        this.setPeerHttpAddresses2(req.gateHttpAddrs);
    }

}
