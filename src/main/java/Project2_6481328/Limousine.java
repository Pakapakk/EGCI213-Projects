package Project2_6481328;

/**
 *
 * @author Pakapak Jungjaroen 6481328
 * @author Tanadol Chuntarasupt 6481259
 * @author Steven Jonatan 6881006
 * @author Jimin Kim 6680046
 */
public class Limousine {

    private String name;
    private int maxSeats;
    private int remainingSeats;

    public Limousine(String name, int maxSeats) {
        this.name = name;
        this.maxSeats = maxSeats;
        this.remainingSeats = maxSeats;
    }

    public String getName() {
        return name;
    }

    public int getMaxSeats() {
        return maxSeats;
    }

    public synchronized int getRemainingSeats() {
        return remainingSeats;
    }

    public synchronized int book(int customers) {
        int booked = Math.min(customers, remainingSeats);
        remainingSeats -= booked;
        return booked;
    }

    public synchronized void resetSeats() {
        remainingSeats = maxSeats;
    }

    @Override
    public String toString() {
        return name;
    }
}
