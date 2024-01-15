package Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.*;
// this class will represent the process so i tried to come up with all
// the stuff that i will need and will typically will be in a process
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Process {
    // unique universal identifier
    private UUID id;
    // the left as needed while we are working with the process
    // all of the rest proprieties are self-explanatory
    private int unitOfTimeLeft;
    private int totalUnitOfTime;
    private boolean done;
    private Instant arrivalTime;
    private int priority;
    private String data;

    public Process clone(Process p) {
        Process n = new Process();
        n.setId(p.getId());
        n.setUnitOfTimeLeft(p.getUnitOfTimeLeft());
        n.setTotalUnitOfTime(p.getTotalUnitOfTime());
        n.setDone(p.isDone());
        n.setArrivalTime(p.getArrivalTime());
        n.setPriority(p.getPriority());
        n.setData(p.getData());
        return n;
    }

    public static List<Process> cloneList(List<Process> processList) {
        List<Process> clonedList = new ArrayList<>();
        for (Process p : processList) {
            Process clonedProcess = new Process();
            clonedProcess.setId(p.getId());
            clonedProcess.setUnitOfTimeLeft(p.getUnitOfTimeLeft());
            clonedProcess.setTotalUnitOfTime(p.getTotalUnitOfTime());
            clonedProcess.setDone(p.isDone());
            clonedProcess.setArrivalTime(p.getArrivalTime());
            clonedProcess.setPriority(p.getPriority());
            clonedProcess.setData(p.getData());
            clonedList.add(clonedProcess);
        }
        return clonedList;
    }
}
