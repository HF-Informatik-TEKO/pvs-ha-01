import java.util.StringJoiner;

import ch.teko.pvs.CPUSimulation;
import ch.teko.pvs.InterruptServiceRoutine;
import ch.teko.pvs.ProcessSimulation;
import ch.teko.pvs.SystemSimulation;

public class EarliestDeadlineFirst {

    private MyProcess currentProcess;
    private int time = -1;

    public void execute() {
        var system = new SystemSimulation();
        var cpu = new CPUSimulation();
        var processes = new ProcessList<MyProcess>();
        processes.add(new MyProcess(1000, 8, 1));
        processes.add(new MyProcess(1000, 5, 2));
        processes.add(new MyProcess(1000, 10, 4));
        var allProesses = ProcessList.copyList(processes);
        currentProcess = new MyProcess(0, 0, 0);

        var isr = new InterruptServiceRoutine(){
            @Override
            public void doRoutine() {
                var process = getNextProcessOrNull(processes);
                if (process != null) {
                    System.out.print(String.format("%02d: ", ++time));
                    processes.printProcesses(allProesses, process);
                    System.out.print(String.format("%02d: ", time));
                    ageProcessesAndPrintDeadline(allProesses);
                    
                    cpu.setProcess(process);
                }
            }

            private void ageProcessesAndPrintDeadline(ProcessList<MyProcess> allProesses) {
                var sj = new StringJoiner(" | ");
                for (var p : allProesses) {
                    p.age();
                    var text = String.format("%02d: d%02d p%02d", p.getProcessID(), p.getTimeTillDeadline(), p.timeTillSwitch);
        
                    sj.add(text);
                }

                System.out.println(sj.toString());
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
    
    private ProcessSimulation getNextProcessOrNull(ProcessList<MyProcess> processes) {
        if (processes.size() <= 0) {
            return null;
        }

        removeFinishedJobs(processes);
        
        if (!currentProcess.isTimeToSwitch()) {
            currentProcess.process();
            return currentProcess;
        } else {
            currentProcess.restoreProcess();
            var p = getEarliestDeadline(processes);
            currentProcess = p;
            p.process();
            return p;
        }
    }

    private void removeFinishedJobs(ProcessList<MyProcess> processes) {
        for (int i = processes.size() - 1; i > 0; i--) {
            var p = processes.get(i);
            if (p.isFinished()){
                processes.remove(p);
            }
        }
    }

    private MyProcess getEarliestDeadline(ProcessList<MyProcess> processes) {
        var min = Integer.MAX_VALUE;
        var shortestDuration = processes.get(0);

        for (var p : processes) {
            if (p.getTimeTillDeadline() < min) {
                min = p.getTimeTillDeadline();
                shortestDuration = p;
            }
        }

        return shortestDuration;
    }

    private void keepAlive() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public class MyProcess extends ProcessSimulation {

        private int timeTillDeadline;
        private int timeTillSwitch;

        public MyProcess(int instruction_number, int deadline, int execution_time) {
            super(instruction_number, deadline, execution_time);
            timeTillDeadline = super.getDeadline();
            timeTillSwitch = super.getExecutionTime();
        }

        public int getTimeTillDeadline() {
            return this.timeTillDeadline;
        }

        public boolean isTimeToSwitch() {
            return this.timeTillSwitch <= 0;
        }

        public void age() {
            timeTillDeadline--;
        }
        
        public void process() {
            timeTillSwitch--;
        }
        
        public void restoreProcess() {
            timeTillSwitch = super.getExecutionTime();
            this.timeTillDeadline += super.getDeadline() * 2;
        }
    }
}
