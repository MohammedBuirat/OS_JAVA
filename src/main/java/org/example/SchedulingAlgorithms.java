package org.example;

import Models.SchedulingAlgorithmResult;
import Models.Process;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class SchedulingAlgorithms {
    // you can change the settings of the algorithms from those values
    private static final int RoundRobinSlice = 20;
    private static final int MLQFirstQueue = 10;
    private static final int MLQSecondQueue = 50;

    public static SchedulingAlgorithmResult firstComeFirstServe(List<Process> processes) {
        SchedulingAlgorithmResult result = new SchedulingAlgorithmResult();
        int sumOfTurnAroundTime = 0;
        int sumOfWaitingTime = 0;
        Instant currentTime = processes.get(0).getArrivalTime();
        // the start time as the first process arrival time
        for (Process process : processes) {
            // if we got a process that has not it's time is more than the current time then we will
            // increase the current time with that time so that we can take it and process it
            if (currentTime.compareTo(process.getArrivalTime()) < 0) {
                currentTime = process.getArrivalTime();
            }
            // we will finish it after it's time needed as we know in the FCFS the process will be finished fully
            Instant completionTime = currentTime.plusSeconds(process.getUnitOfTimeLeft());
            // TAT will be the time from arrival to finishing
            int turnAroundTime = getSecondsDifference(completionTime, process.getArrivalTime());
            sumOfTurnAroundTime += turnAroundTime;
            // WAT will be TAT - the time of
            int waitingTime = turnAroundTime - process.getTotalUnitOfTime();
            sumOfWaitingTime += waitingTime;
            currentTime = completionTime;
        }
        // return the TAT, AWT
        result.setAverageTurnAroundTime((double) sumOfTurnAroundTime / (double) processes.size());
        result.setAverageWaitingTime((double) sumOfWaitingTime / (double) processes.size());
        return result;
    }

    public static SchedulingAlgorithmResult shortestJobFirstWithPreemption(List<Process> processes) {
        int sumOfTurnAroundTime = 0;
        int sumOfWaitingTime = 0;
        SchedulingAlgorithmResult result = new SchedulingAlgorithmResult();
        int current = 0;
        // store the jobs that we reached there arrival time the pool of process that we will be comparing the
        // process that arrives with, yes i could store it in a min heap which will be mich better performance wise
        // but it wasn't worth it to add that extra complexity for 8 processes only
        List<Process> currentProcessing = new ArrayList<>();
        Instant currentTime = processes.get(0).getArrivalTime();
        // while there is processes that we haven't processed yet keep looping
        while (current < processes.size() || !currentProcessing.isEmpty()) {
            // add the process that have arrived to the pool of currently processed
            while (current < processes.size() && processes.get(current).getArrivalTime().compareTo(currentTime) <= 0) {
                currentProcessing.add(processes.get(current));
                current++;
            }
            // if there is no process that there time has arrived we will just increment the time so that we will reach the next
            // processes that will arrive
            if (currentProcessing.isEmpty()) {
                currentProcessing.add(processes.get(current));
                currentTime = processes.get(current).getArrivalTime();
                current++;
            }
            // get the shortest job currently
            Process minBurstLeftProcess = currentProcessing.stream()
                    .min(Comparator.comparingInt(Process::getUnitOfTimeLeft))
                    .orElse(null);

            if (minBurstLeftProcess != null) {
                // we will reduce that process time by 1
                minBurstLeftProcess.setUnitOfTimeLeft(minBurstLeftProcess.getUnitOfTimeLeft() - 1);
                currentTime = currentTime.plusSeconds(1);
                // if the process finished executing i will just calculate TAT , WAT like FCFS
                if (minBurstLeftProcess.getUnitOfTimeLeft() == 0) {
                    currentProcessing.remove(minBurstLeftProcess);
                    int turnAroundTime = getSecondsDifference(currentTime, minBurstLeftProcess.getArrivalTime());
                    sumOfTurnAroundTime += turnAroundTime;
                    int waitingTime = turnAroundTime - minBurstLeftProcess.getTotalUnitOfTime();
                    sumOfWaitingTime += waitingTime;
                }
            }
        }

        result.setAverageTurnAroundTime((double)sumOfTurnAroundTime / (double)processes.size());
        result.setAverageWaitingTime((double)sumOfWaitingTime / (double)processes.size());
        return result;
    }

    public static SchedulingAlgorithmResult roundRobin(List<Process> processes) {
        int sumOfTurnAroundTime = 0;
        int sumOfWaitingTime = 0;
        SchedulingAlgorithmResult result = new SchedulingAlgorithmResult();
        int current = 0;
        Instant currentTime = processes.get(0).getArrivalTime();
        // two queues one for the processes that have just arrived and we will make it with the highest priority
        // and another that will work like FCFS when the first queue is empty
        Queue<Process> firstQueue = new LinkedList<>();
        Queue<Process> lastQueue = new LinkedList<>();

        // while there is elements in the first, second , or process that haven't reached yet
        while (!firstQueue.isEmpty() || !lastQueue.isEmpty() || current < processes.size()) {
            // like SJF here
            while (current < processes.size() && processes.get(current).getArrivalTime().compareTo(currentTime) <= 0) {
                firstQueue.add(processes.get(current));
                current++;
            }
            // handling when there is no process that there arrival time has been reached
            if (firstQueue.isEmpty() && lastQueue.isEmpty() && current < processes.size()) {
                currentTime = processes.get(current).getArrivalTime();
                current++;
            }
            // we will handel all the elements in the first queue as it has the highest priority
            while (!firstQueue.isEmpty()) {
                Process process = firstQueue.poll();
                int timeToRemove = Math.min(RoundRobinSlice, process.getUnitOfTimeLeft());
                // remove the min between the time left and RR slice and reduce it if the process is finished we will calculate the
                // TAT, WAT
                process.setUnitOfTimeLeft(process.getUnitOfTimeLeft() - timeToRemove);
                currentTime = currentTime.plusSeconds(timeToRemove);

                if (process.getUnitOfTimeLeft() == 0) {
                    int turnAroundTime = getSecondsDifference(currentTime, process.getArrivalTime());
                    sumOfTurnAroundTime += turnAroundTime;
                    int waitingTime = turnAroundTime - process.getTotalUnitOfTime();
                    sumOfWaitingTime += waitingTime;
                } else {
                    lastQueue.add(process);
                }

                while (current < processes.size() && processes.get(current).getArrivalTime().isBefore(currentTime)) {
                    firstQueue.add(processes.get(current));
                    current++;
                }
            }
            // we will do just one process from the least priority second queue
            if (!lastQueue.isEmpty()) {
                Process process = lastQueue.poll();
                int time = process.getUnitOfTimeLeft();
                currentTime = currentTime.plusSeconds(time);
                int turnAroundTime = getSecondsDifference(currentTime, process.getArrivalTime());
                sumOfTurnAroundTime += turnAroundTime;
                int waitingTime = turnAroundTime - process.getTotalUnitOfTime();
                sumOfWaitingTime += waitingTime;
            }
        }

        result.setAverageTurnAroundTime((double) sumOfTurnAroundTime / (double) processes.size());
        result.setAverageWaitingTime((double) sumOfWaitingTime / (double) processes.size());
        return result;
    }

    // in the MLQ i will follow a priority approach where i won't go to the 2nd queues if there
    // is elements in the first and i won't go to the 3rd if there is in 2nd
    public static SchedulingAlgorithmResult multiLevelQueue(List<Process> processes) {
        SchedulingAlgorithmResult result = new SchedulingAlgorithmResult();
        int sumOfTurnAroundTime = 0;
        int sumOfWaitingTime = 0;
        int current = 0;
        Instant currentTime = processes.get(0).getArrivalTime();
        // we will have 5 queues
        // think of it as we have three levels the first and second levels will have two inner levels in them
        // since they will have RR in them
        // as for the last queue it will just work as a simple FCFS
        Queue<Process> q1 = new LinkedList<>();
        Queue<Process> q12 = new LinkedList<>();
        Queue<Process> q2 = new LinkedList<>();
        Queue<Process> q22 = new LinkedList<>();
        Queue<Process> q3 = new LinkedList<>();

        // while there is elements in the queues or process that haven't reached yet
        while (current < processes.size() || !q1.isEmpty() || !q2.isEmpty() || !q3.isEmpty()
        || !q12.isEmpty() || ! q22.isEmpty()) {
            while (current < processes.size() && processes.get(current).getArrivalTime().compareTo(currentTime) >= 0) {
                insertProcessToQueues(processes.get(current), q1, q2, q3);
                current++;
            }
            // every time we will take one item from one queue so that
            // if new processes entered the 1st queue after while finishg the process in it we won't process the
            // processes in the 2nd queue
            if (!q1.isEmpty() || !q12.isEmpty()) {
                // RR for one process
                if (!q1.isEmpty()) {
                    Process process = q1.poll();
                    int timeToRemove = Math.min(MLQFirstQueue, process.getUnitOfTimeLeft());
                    currentTime = currentTime.plusSeconds(timeToRemove);
                    process.setUnitOfTimeLeft(process.getUnitOfTimeLeft() - timeToRemove);

                    if (process.getUnitOfTimeLeft() == 0) {
                        int turnAroundTime = getSecondsDifference(currentTime, process.getArrivalTime());
                        sumOfTurnAroundTime += turnAroundTime;
                        int waitingTime = turnAroundTime - process.getTotalUnitOfTime();
                        sumOfWaitingTime += waitingTime;
                    } else {
                        q12.add(process);
                    }
                } else if (!q12.isEmpty()) {
                    Process process = q12.poll();
                    currentTime = currentTime.plusSeconds(process.getUnitOfTimeLeft());
                    process.setUnitOfTimeLeft(0);

                    int turnAroundTime = getSecondsDifference(currentTime, process.getArrivalTime());
                    sumOfTurnAroundTime += turnAroundTime;
                    int waitingTime = turnAroundTime - process.getTotalUnitOfTime();
                    sumOfWaitingTime += waitingTime;
                }
            } else if (!q2.isEmpty() || !q22.isEmpty()) {
                if (!q2.isEmpty()) {
                    Process process = q2.poll();
                    int timeToRemove = Math.min(MLQSecondQueue, process.getUnitOfTimeLeft());
                    currentTime = currentTime.plusSeconds(timeToRemove);
                    process.setUnitOfTimeLeft(process.getUnitOfTimeLeft() - timeToRemove);

                    if (process.getUnitOfTimeLeft() == 0) {
                        int turnAroundTime = getSecondsDifference(currentTime, process.getArrivalTime());
                        sumOfTurnAroundTime += turnAroundTime;
                        int waitingTime = turnAroundTime - process.getTotalUnitOfTime();
                        sumOfWaitingTime += waitingTime;
                    } else {
                        q22.add(process);
                    }
                } else if (!q22.isEmpty()) {
                    Process process = q22.poll();
                    currentTime = currentTime.plusSeconds(process.getUnitOfTimeLeft());
                    process.setUnitOfTimeLeft(0);

                    int turnAroundTime = getSecondsDifference(currentTime, process.getArrivalTime());
                    sumOfTurnAroundTime += turnAroundTime;
                    int waitingTime = turnAroundTime - process.getTotalUnitOfTime();
                    sumOfWaitingTime += waitingTime;
                }
                // simple FCFS for one process
            } else if (!q3.isEmpty()) {
                Process process = q3.poll();
                Instant completionTime = currentTime.plusSeconds(process.getUnitOfTimeLeft());
                int turnAroundTime = getSecondsDifference(completionTime, process.getArrivalTime());
                sumOfTurnAroundTime += turnAroundTime;
                int waitingTime = turnAroundTime - process.getUnitOfTimeLeft();
                sumOfWaitingTime += waitingTime;
                currentTime = completionTime;
            }
        }
        result.setAverageTurnAroundTime((double)sumOfTurnAroundTime / (double)processes.size());
        result.setAverageWaitingTime((double)sumOfWaitingTime / (double)processes.size());

        return result;
    }

    // helper method for MLQ to insert the process to the correct queue
    public static void insertProcessToQueues(Process p, Queue<Process> q1, Queue<Process> q2, Queue<Process> q3) {
        if (p.getPriority() == 1) {
            q1.add(p);
        } else if (p.getPriority() == 2) {
            q2.add(p);
        } else {
            q3.add(p);
        }
    }

    // helper method to calculate the time different between two time instants
    public static int getSecondsDifference(Instant firstInstant, Instant secondInstant) {
        Duration duration = Duration.between(firstInstant, secondInstant);
        long secondsDifference = Math.abs(duration.getSeconds());
        return (int) secondsDifference;
    }
}
