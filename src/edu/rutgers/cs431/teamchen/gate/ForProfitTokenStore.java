package edu.rutgers.cs431.teamchen.gate;

import java.util.ArrayList;

public class ForProfitTokenStore implements TokenStore {

    public ForProfitTokenStore(ArrayList<String> tokens){
        this.tokens = tokens;
    }
    private ArrayList<String> tokens;
    public String getToken() throws InterruptedException{
        // TODO
        return null;
    }

    @Override
    public void addToken(String token){
        // TODO
    }

    @Override
    public int count(){
        // TODO
        return 0;
    }
}
