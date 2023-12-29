package com.ding.coding.queue;

import java.util.concurrent.TimeUnit;

public interface Delayed extends Comparable<Delayed>{
    long getDelay(TimeUnit timeUnit);

}
