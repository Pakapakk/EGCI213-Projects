package Project2_6481328;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 *
 * @author Pakapak Jungjaroen 6481328
 * @author Tanadol Chuntarasupt 6481259
 * @author Steven Jonatan 6881006
 * @author Jimin Kim 6680046
 */
public class GuideThread extends Thread {

    private int days;
    private ArrayList<Place> places;

    private CyclicBarrier dayStartBarrier;
    private CyclicBarrier guideAssignDoneBarrier;
    private CyclicBarrier guideReportBarrier;
    private CyclicBarrier dayEndBarrier;

    private int todayCustomers = 0;
    private int totalCustomers = 0;
    private int padWidth;
    private Random random = new Random();

    public GuideThread(String name, int days, ArrayList<Place> places,
            CyclicBarrier dayStartBarrier, CyclicBarrier guideAssignDoneBarrier,
            CyclicBarrier guideReportBarrier, CyclicBarrier dayEndBarrier,
            int padWidth) {
        super(name);
        this.days = days;
        this.places = places;
        this.dayStartBarrier = dayStartBarrier;
        this.guideAssignDoneBarrier = guideAssignDoneBarrier;
        this.guideReportBarrier = guideReportBarrier;
        this.dayEndBarrier = dayEndBarrier;
        this.padWidth = padWidth;
    }

    private String prefix() {
        return String.format("%" + padWidth + "s >> ", Thread.currentThread().getName());
    }

    public synchronized void receiveCustomers(int customers) {
        todayCustomers += customers;
        totalCustomers += customers;
    }

    public synchronized int getTotalCustomers() {
        return totalCustomers;
    }

    @Override
    public void run() {
        for (int day = 1; day <= days; day++) {
            try {
                // Wait for main to print day header
                dayStartBarrier.await();

                // Wait for all TourThreads to finish sending customers
                guideAssignDoneBarrier.await();

                // Report today's customers
                System.out.printf("%stotal customer today = %5d\n",
                        prefix(), todayCustomers);
                guideReportBarrier.await();

                // If has customers, visit a random place
                if (todayCustomers > 0) {
                    Place place = places.get(random.nextInt(places.size()));
                    int newTotal = place.addVisitors(todayCustomers);
                    System.out.printf("%stake %3d customers to %s\t\t\t total visitors = %6d\n",
                            prefix(), todayCustomers, place.getName(), newTotal);
                }

                // Reset today customers
                todayCustomers = 0;

                // Wait for day to end
                dayEndBarrier.await();

            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }
}
