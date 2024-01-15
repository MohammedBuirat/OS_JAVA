package Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
// a class to store the return of the scheduling algorithms
// i used lombok to reduce the amount of code needed to be written
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchedulingAlgorithmResult {
    private double averageTurnAroundTime;
    private double averageWaitingTime;

    public SchedulingAlgorithmResult clone() {
        return new SchedulingAlgorithmResult(averageTurnAroundTime, averageWaitingTime);
    }
}