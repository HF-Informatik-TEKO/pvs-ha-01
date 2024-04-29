
public class Main {

    public static void main(String[] args) {
        System.out.println("// Start Round Robin Exercise: ");
        new RoundRobin().execute();
        System.out.println("// End Round Robin Exercise.");

        System.out.println("// Start Shortest Job First Exercise: ");
        new ShortestJobFirst().execute();
        System.out.println("// End Shortest Job First Exercise.");

        System.out.println("// Start Earliest Deadline First Exercise: ");
        new EarliestDeadlineFirst().execute();
        System.out.println("// End Earliest Deadline First Exercise.");
    }
}
