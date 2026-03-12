package Project2_6481328;

/**
 *
 * @author Pakapak Jungjaroen 6481328
 * @author Tanadol Chuntarasupt 6481259
 * @author Steven Jonatan 6881006
 * @author Jimin Kim 6680046
 */
public class Place {

    private String name;
    private int totalVisitors;

    public Place(String name) {
        this.name = name;
        this.totalVisitors = 0;
    }

    public String getName() {
        return name;
    }

    public synchronized int getTotalVisitors() {
        return totalVisitors;
    }

    public synchronized int addVisitors(int visitors) {
        totalVisitors += visitors;
        return totalVisitors;
    }

    @Override
    public String toString() {
        return name;
    }
}
