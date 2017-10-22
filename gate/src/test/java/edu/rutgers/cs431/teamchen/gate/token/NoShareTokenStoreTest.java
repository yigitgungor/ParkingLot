package edu.rutgers.cs431.teamchen.gate.token;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Semaphore;


public class NoShareTokenStoreTest {

    private static final int TEST_ADD_TRIALS = 20;

    private ArrayList<String> makeTokens() {
        return new ArrayList<String>(
                Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h"));
    }

    @Test
    public void testAddConcurrently() {
        final ArrayList<String> tokens = makeTokens();

        int initCount = tokens.size();

        Semaphore waitGroup = new Semaphore(TEST_ADD_TRIALS);
        TokenStore ts = new NoShareTokenStore(tokens);
        for (int i = 0; i < TEST_ADD_TRIALS; i++) {
            final int trynum = i;
            new Thread(() -> {
                try {
                    waitGroup.acquire();
                    ts.addToken(Integer.toString(trynum));
                    waitGroup.release();
                } catch (InterruptedException e) {

                }
            }).start();
        }
        try {
            waitGroup.acquire(TEST_ADD_TRIALS);
        } catch (InterruptedException e) {
        }

        Assert.assertEquals(ts.count(), initCount + TEST_ADD_TRIALS);
    }

    @Test
    public void testAddSequentially() {
        final ArrayList<String> tokens = makeTokens();
        int initCount = tokens.size();

        TokenStore ts = new NoShareTokenStore(tokens);
        for (int i = 0; i < TEST_ADD_TRIALS; i++) {
            ts.addToken(Integer.toString(i));
        }

        Assert.assertEquals(ts.count(), initCount + TEST_ADD_TRIALS);
    }

}