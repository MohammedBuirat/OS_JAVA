//********* Mohammed Buirat - 1192896 *********//
//********* mohammedbuiraat@gmail.com **********//
//********* Made at 15/1/2024 *********//
//********* Fell free to connect for any clarifications
// OS Final Project
// you can find a C# version of this project if you prefer it over java since i made in java then had to redo it all over again

// A project that will simulate how famous scheduling algorithms will be made to finish processes and how
// they will preform performance wise
// please note that the results of the algorithms will depend mainly on the way of creating the process in the
// processes factory so please change it as needed it to make the algorithm work as you want
// i will be given random unit of time bursts from 5 to 100
// arrival time from 0 to 20 where 0 will be the first process arrival time
package org.example;

import Models.Process;
import Models.SchedulingAlgorithmResult;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        // the num of the process in each iteration
        // i will use 8 as it was requested in the requirements
        int numOfProcess = 8;
        // i will store the sum of the time taken for all the scheduling algorithm iterations
        // so that i don't need to store each of the iterations so if i reached the 10the iteration i will just
        // get that value and divide it by 10
        // following this approach i was able to increase performance greatly and reduce memory usage
        // by an astonishing  6.103515625 MB
        var roundRobinSum = new SchedulingAlgorithmResult(0.0, 0.0);
        var firstComeFirstServeSum = new SchedulingAlgorithmResult(0, 0);
        var shortestJobFirstWithPreSum = new SchedulingAlgorithmResult(0, 0);
        var multiLevelQueueSum = new SchedulingAlgorithmResult(0, 0);

        // here i will store the values of the iteration needed so that i can print them into file
        // console or whatever i want which will store the avg for iterations {1, 10, 100, 1000, 10000, 100000}
        List<SchedulingAlgorithmResult> roundRobinResults = new ArrayList<>();
        List<SchedulingAlgorithmResult> firstComeFirstServeResults = new ArrayList<>();
        List<SchedulingAlgorithmResult> shortestJobFirstWithPreResults = new ArrayList<>();
        List<SchedulingAlgorithmResult> multiLevelQueueResults = new ArrayList<>();
        // the iterations that i want to store so that i can show later
        ArrayList<Integer> printAt = new ArrayList<>(Arrays.asList(1, 100, 1000, 10000, 100000));
        // i will be printing the 10ks iterations to give some kind of and indication to at which iteration we are at
        // since the 100K iterations will take like 10 sec

        for (int i = 1; i <= 100000; i++) {
            if(i % 10000 == 0){
                System.out.println(i);
            }
            // i will use a method in the main to generate the process
            List<Process> processes = getProcess(numOfProcess);
            List<Process> temp = Process.cloneList(processes);
            SchedulingAlgorithmResult fcfsResult = SchedulingAlgorithms.firstComeFirstServe(temp);
            // i will be cloning the process since there will be a modification in the process that i don't want
            // to be present when we pass the n process to the next scheduling algorithm
            temp = Process.cloneList(processes);
            SchedulingAlgorithmResult rrResult = SchedulingAlgorithms.roundRobin(temp);

            temp = Process.cloneList(processes);
            SchedulingAlgorithmResult sjfResult = SchedulingAlgorithms.shortestJobFirstWithPreemption(temp);

            temp = Process.cloneList(processes);
            SchedulingAlgorithmResult mlqResult = SchedulingAlgorithms.multiLevelQueue(temp);

            // editing the sum of the process with the result
            editSumResultByAddingNewResult(roundRobinSum, rrResult);
            editSumResultByAddingNewResult(firstComeFirstServeSum, fcfsResult);
            editSumResultByAddingNewResult(shortestJobFirstWithPreSum, sjfResult);
            editSumResultByAddingNewResult(multiLevelQueueSum, mlqResult);

            // if we reached an iteration that we need to print i will get the avg of the current iteration and store it
            if (printAt.contains(i)) {
                var newRR = new SchedulingAlgorithmResult
                (roundRobinSum.getAverageTurnAroundTime() / i,
                        roundRobinSum.getAverageWaitingTime() / i);
                var newFCFS = new SchedulingAlgorithmResult
                        (firstComeFirstServeSum.getAverageTurnAroundTime() / i,
                        firstComeFirstServeSum.getAverageWaitingTime() / i);
                var newSJF = new SchedulingAlgorithmResult
                        (shortestJobFirstWithPreSum.getAverageTurnAroundTime() / i,
                                shortestJobFirstWithPreSum.getAverageWaitingTime() / i);
                var newMLQ = new SchedulingAlgorithmResult
                        (multiLevelQueueSum.getAverageTurnAroundTime() / i,
                                multiLevelQueueSum.getAverageWaitingTime() / i);
                roundRobinResults.add(newRR);
                firstComeFirstServeResults.add(newFCFS);
                shortestJobFirstWithPreResults.add(newSJF);
                multiLevelQueueResults.add(newMLQ);
            }
        }
        printResults(firstComeFirstServeResults, "FCFS");
        printResults(shortestJobFirstWithPreResults, "SRTF");
        printResults(roundRobinResults, "RR");
        printResults(multiLevelQueueResults, "MLQ");
    }

    //method that will take the sum of shc algo results and add the new result ot that sum
    public static void editSumResultByAddingNewResult
            (SchedulingAlgorithmResult sum, SchedulingAlgorithmResult newResult) {
        var newSumTAT = sum.getAverageTurnAroundTime() + newResult.getAverageTurnAroundTime();
        sum.setAverageTurnAroundTime(newSumTAT);
        var newSumAWT = sum.getAverageWaitingTime() + newResult.getAverageWaitingTime();
        sum.setAverageWaitingTime(newSumAWT);
    }

    // just a method to print the results nothing fancy
    public static void printResults(List<SchedulingAlgorithmResult> results,
                                    String name) {
        System.out.println(name + ":");

        System.out.println("numOfIt:  1\t          100\t      1000\t     10000\t    100000");
        System.out.print("AWT\t\t");
        for (SchedulingAlgorithmResult schedulingAlgorithmResult : results) {
            System.out.printf("%.3f\t\t", schedulingAlgorithmResult.getAverageWaitingTime());
        }
        System.out.println();
        System.out.print("ATT\t\t");
        for (SchedulingAlgorithmResult result : results) {
            System.out.printf("%.3f\t\t", result.getAverageTurnAroundTime());
        }
        System.out.print("\n\n");
    }

    // method to get the process where it will take the number of process 'n' and give you nth randomly generated
    // process using the process Factory
    public static List<Process> getProcess(int numOfProcess) {
        List<Process> processes = new ArrayList<>();
        for (int j = 0; j < numOfProcess; j++) {
            processes.add(ProcessFactory.createProcess());
        }
        // we will sort those process by the time of arrival so that we can feed them to the scheduling algorithm
        // in a way that will simulate the real time arrival of processes
        processes.sort(Comparator.comparing(Process::getArrivalTime));
        return processes;
    }
}