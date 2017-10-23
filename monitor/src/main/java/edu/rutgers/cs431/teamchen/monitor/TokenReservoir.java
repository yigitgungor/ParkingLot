package edu.rutgers.cs431.teamchen.monitor;

import java.util.ArrayList;

public interface TokenReservoir {
    // gets the next batch of @param count token,
    // make sure they are unique
    ArrayList<String> next() throws Exception;

    class Basic implements TokenReservoir {
        // total number of batches to generate
        private long maxBatch;
        private long total;

        private int batchCounter = 0;
        private long leftOut;


        private long trackingNum = 0L;

        public Basic(long total, long maxBatch) {
            this.total = total;
            this.maxBatch = maxBatch;
            this.leftOut = total % maxBatch;
        }

        private long getJumpingRate() {
            return (total - leftOut) / maxBatch;
        }


        @Override
        public synchronized ArrayList<String> next() throws Exception {
            ArrayList<String> batch = new ArrayList<>();
            long upperBound = 0;
            if (batchCounter == maxBatch) {
                throw new Exception("token capacity exceeded.");
            } else if (batchCounter == maxBatch - 1) {
                upperBound = this.getJumpingRate();

            } else {
                upperBound = this.getJumpingRate() + this.leftOut;
            }

            for (long i = 0; i < upperBound; i++) {
                batch.add(Long.toString(trackingNum));
                trackingNum++;
            }
            return batch;
        }
    }
}
