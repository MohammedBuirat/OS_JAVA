package org.example;

import java.time.Instant;
import java.util.Random;
import java.util.UUID;

import Models.Process;

public class ProcessFactory {

    static int minUnitTime = 5;
    static int maxUnitTime = 100;
    static int maxArrivalTime = 20;
    static int maxPriority = 3;
    public static Process createProcess() {
        // create process where time unit will be random in the given bound
        Instant time = Instant.now();
        Random rand = new Random();
        time = time.plusSeconds(rand.nextInt(maxArrivalTime));
        // to simulate real time process where process has multiple levels of priority
        int priority = rand.nextInt(maxPriority) + 1;
        int timeUnits = rand.nextInt(maxUnitTime - minUnitTime + 1) + minUnitTime;
        Process process = new Process();
        // to give the process a random id i gave it UUID to make it generated randomly with a unique universal id
        // so that we can neglect systems where they will be sharing and receiving process from multiple sources
        // if you don't know what is UUID i will greatly suggest to read about it a great topic to know
        process.setId(UUID.randomUUID());
        process.setUnitOfTimeLeft(timeUnits);
        process.setArrivalTime(time);
        process.setData("Some data placeholder");
        process.setPriority(priority);
        process.setDone(false);
        process.setTotalUnitOfTime(timeUnits);
        return process;
    }
}