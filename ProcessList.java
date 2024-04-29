import java.util.ArrayList;
import java.util.StringJoiner;

import ch.teko.pvs.ProcessSimulation;

public class ProcessList<T extends ProcessSimulation> extends ArrayList<T>  {

    public static <T extends ProcessSimulation> ProcessList<T> copyList(ProcessList<T> processes) {
        var allProesses = new ProcessList<T>();
        for (var e : processes) {
            allProesses.add(e);
        }
        
        return allProesses;
    }

    public boolean isAllFinished(ArrayList<T> proc) {
        for (var p : proc) {
            if (!p.isFinished()) {
                return false;
            }
        }

        return true;
    }

    public void printProcesses(ArrayList<T> allProesses, String name) {
        var text = getProcessString(allProesses, name);
        System.out.println(text);
    }

    public void printProcesses(ArrayList<T> allProesses, ProcessSimulation currentProcess) {
        var name = String.format("isr switch to process %02d; ", currentProcess.getProcessID());
        var text2 = getProcessString(allProesses, name);
        var replaced = text2.replace(String.format(" %02d: ", currentProcess.getProcessID()), String.format(">%02d: ", currentProcess.getProcessID()));
        System.out.println(replaced);
    }

    private String getProcessString(ArrayList<T> allProesses, String name) {
        var sj = new StringJoiner(" | ");
        for (var process : allProesses) {
            var text = String.format("%02d: %03d/%d", process.getProcessID(), process.getCurrentInstructionCount(), process.getInstructionCount());

            if (process.isFinished()) {
                text = String.format("%02d:#%03d/%d", process.getProcessID(), process.getCurrentInstructionCount(), process.getInstructionCount());
            }
            sj.add(text);
        }

        return name + sj.toString();
    }
}
