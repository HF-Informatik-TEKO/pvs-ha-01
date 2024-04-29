import java.util.ArrayList;

import ch.teko.pvs.CPUSimulation;
import ch.teko.pvs.InterruptServiceRoutine;
import ch.teko.pvs.ProcessSimulation;
import ch.teko.pvs.SystemSimulation;

public class RoundRobin {
    private static int processNumber = -1;

    public void execute() {
        var system = new SystemSimulation();
        var cpu = new CPUSimulation();
        var processes = new ProcessList();
        processes.add(new ProcessSimulation(800));
        processes.add(new ProcessSimulation(100));
        processes.add(new ProcessSimulation(200));
        processes.add(new ProcessSimulation(400));
        var allProesses = ProcessList.copyList(processes);

        var isr = new InterruptServiceRoutine(){
            @Override
            public void doRoutine() {
                var process = getNextProcess(processes);
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

    private void keepAlive() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static ProcessSimulation getNextProcess(ArrayList<ProcessSimulation> proc) {
        if (proc.size() == 0) {
            return null;
        }

        processNumber++;
        processNumber = processNumber % proc.size();
        var p = proc.get(processNumber);

        if (!p.isFinished()) {
            return p;
        }
        
        proc.remove(p);
        processNumber--;
        return getNextProcess(proc);
    }
}
    