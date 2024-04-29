import ch.teko.pvs.CPUSimulation;
import ch.teko.pvs.InterruptServiceRoutine;
import ch.teko.pvs.ProcessSimulation;
import ch.teko.pvs.SystemSimulation;

public class ShortestJobFirst {

    public void execute() {
        var system = new SystemSimulation();
        var cpu = new CPUSimulation();
        var processes = new ProcessList<ProcessSimulation>();
        processes.add(new ProcessSimulation(800));
        processes.add(new ProcessSimulation(100));
        processes.add(new ProcessSimulation(200));
        processes.add(new ProcessSimulation(400));
        var allProesses = ProcessList.copyList(processes);

        var isr = new InterruptServiceRoutine(){
            @Override
            public void doRoutine() {
                var process = getNextProcessOrNull(processes);
                if (process != null) {
                    processes.printProcesses(allProesses, process);
                    cpu.setProcess(process);
                }
            }
        };

        cpu.setInterruptServiceRoutine(isr);
        system.addCPU(cpu);
        system.startSystem();

        while (!processes.isAllFinished(processes)) {
            keepAlive();
        }

        processes.printProcesses(allProesses, "system finished;          ");
        system.shutdownSystem();
    }
    
    private ProcessSimulation getNextProcessOrNull(ProcessList<ProcessSimulation> processes) {
        if (processes.size() <= 0) {
            return null;
        }

        removeFinishedJobs(processes);
        return getShortestJob(processes);
    }

    private void removeFinishedJobs(ProcessList<ProcessSimulation> processes) {
        for (int i = processes.size() - 1; i > 0; i--) {
            var p = processes.get(i);
            if (p.isFinished()){
                processes.remove(p);
            }
        }
    }

    private ProcessSimulation getShortestJob(ProcessList<ProcessSimulation> processes) {
        var min = Integer.MAX_VALUE;
        var shortestDuration = processes.get(0);
        for (var p : processes) {
            var curr = getInstructionsToGo(p);
            if (curr < min) {
                min = curr;
                shortestDuration = p;
            }
        }

        return shortestDuration;
    }

    private int getInstructionsToGo(ProcessSimulation process) {
        return process.getInstructionCount() - process.getCurrentInstructionCount();
    }

    private void keepAlive() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
