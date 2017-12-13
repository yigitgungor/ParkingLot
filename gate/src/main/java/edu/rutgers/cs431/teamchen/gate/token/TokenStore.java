package edu.rutgers.cs431.teamchen.gate.token;

public interface TokenStore {
    // returns a token, if there is no token then synchronously wait
    // until there is one
    String getToken() throws InterruptedException;

    // adds a token back into the store
    void addToken(String token);

    // returns the number of tokens currently inside the system.
    // Idempotent and for statistics purpose only, DO NOT USE for the program logic.
    int count();
}
