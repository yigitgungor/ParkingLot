package edu.rutgers.cs431.teamchen.trafficgen;

public class MyClock {
  public long time;

  public MyClock() {
    time = System.currentTimeMillis();
  }

  public long getTime() {
    return time;
  }

  public void setTime(long time) {
    this.time = time;
  }

}
