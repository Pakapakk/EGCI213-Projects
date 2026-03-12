package Project2_6481328;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

//import java.util.*;

/**
 *
 * @author Pakapak Jungjaroen 6481328
 * @author Tanadol Chuntarasupt 6481259
 * @author Steven Jonatan 6881006
 * @author Jimin Kim 6680046
 */
public class TourThread extends Thread {

    private int days;
    private int minArrival;
    private int maxArrival;
    private ArrayList<CityLimo> cityLimos;
    private ArrayList<AirportLimo> airportLimos;
    private ArrayList<GuideThread> guideThreads;

    private CyclicBarrier dayStartBarrier;
    private CyclicBarrier outboundReportBarrier;
    private CyclicBarrier afterOutboundBarrier;
    private CyclicBarrier inboundArrivalBarrier;
    private CyclicBarrier inboundLimoBarrier;
    private CyclicBarrier guideAssignDoneBarrier;
    private CyclicBarrier dayEndBarrier;

    private int inboundCustomers = 0;
    private int outboundCustomers = 0;
    private int padWidth;
    private Random random = new Random();

    public TourThread(String name, int days, int minArrival, int maxArrival,
                      ArrayList<CityLimo> cityLimos, ArrayList<AirportLimo> airportLimos,
                      ArrayList<GuideThread> guideThreads,
                      CyclicBarrier dayStartBarrier, CyclicBarrier outboundReportBarrier,
                      CyclicBarrier afterOutboundBarrier, CyclicBarrier inboundArrivalBarrier,
                      CyclicBarrier inboundLimoBarrier, CyclicBarrier guideAssignDoneBarrier,
                      CyclicBarrier dayEndBarrier, int padWidth) {
        super(name);
        this.days = days;
        this.minArrival = minArrival;
        this.maxArrival = maxArrival;
        this.cityLimos = cityLimos;
        this.airportLimos = airportLimos;
        this.guideThreads = guideThreads;
        this.dayStartBarrier = dayStartBarrier;
        this.outboundReportBarrier = outboundReportBarrier;
        this.afterOutboundBarrier = afterOutboundBarrier;
        this.inboundArrivalBarrier = inboundArrivalBarrier;
        this.inboundLimoBarrier = inboundLimoBarrier;
        this.guideAssignDoneBarrier = guideAssignDoneBarrier;
        this.dayEndBarrier = dayEndBarrier;
        this.padWidth = padWidth;
    }

    private String prefix() {
        return Helper.prefix(padWidth);
    }

    public static int awaitBarrier(CyclicBarrier barrier) {
        try {
            return barrier.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        for (int day = 1; day <= days; day++) {
            try {
                // Wait for main function to print day header and reset limos
                awaitBarrier(dayStartBarrier);

                // If even day, process outbound customers
                if (day % 2 == 0) {
                    // Report outbound customers
                    synchronized (Main.PRINT_LOCK) {
                        System.out.println(prefix() + "outbound customers = " + outboundCustomers);
                    }
                    awaitBarrier(outboundReportBarrier);

                    // Random 1 AirportLimo, book customers
                    AirportLimo limo = airportLimos.get(random.nextInt(airportLimos.size()));
                    int booked;
                    int remaining;
                    synchronized (limo) {
                        booked = limo.book(outboundCustomers);
                        remaining = limo.getRemainingSeats();
                    }
                    int metro = outboundCustomers - booked;
                    synchronized (Main.PRINT_LOCK) {
                        System.out.printf("%sput %4d customers on %s\tremaining seats = %5d%n",
                                prefix(), booked, limo.getName(), remaining);
                        if (metro > 0) {
                            System.out.printf("%sput %4d customers on metro%n", prefix(), metro);
                        }
                    }

                    // Reset outbound customers
                    outboundCustomers = 0;
                }

                // Wait for all TourThreads to complete outbound
                int afterOutbound = awaitBarrier(afterOutboundBarrier);

                // Print blank separator on even days between outbound and inbound
                if (day % 2 == 0 && afterOutbound == afterOutboundBarrier.getParties() - 1) {
                    synchronized (Main.PRINT_LOCK) {
                        System.out.println(prefix());
                    }
                }

                // Random inbound arrival
                inboundCustomers = random.nextInt(maxArrival - minArrival + 1) + minArrival;
                synchronized (Main.PRINT_LOCK) {
                    System.out.println(prefix() + "inbound  customers = " + inboundCustomers);
                }
                awaitBarrier(inboundArrivalBarrier);

                // Random 1 CityLimo, book customers
                CityLimo cityLimo = cityLimos.get(random.nextInt(cityLimos.size()));
                int booked;
                int remaining;
                synchronized (cityLimo) {
                    booked = cityLimo.book(inboundCustomers);
                    remaining = cityLimo.getRemainingSeats();
                }
                int metro = inboundCustomers - booked;
                synchronized (Main.PRINT_LOCK) {
                    System.out.printf("%sput %4d customers on %s \t\t remaining seats = %5d%n",
                            prefix(), booked, cityLimo.getName(), remaining);
                    if (metro > 0) {
                        System.out.printf("%sput %4d customers on metro%n", prefix(), metro);
                    }
                }
                awaitBarrier(inboundLimoBarrier);

                // Random 1 local guide and send customers
                GuideThread guide = guideThreads.get(random.nextInt(guideThreads.size()));
                guide.receiveCustomers(inboundCustomers);
                synchronized (Main.PRINT_LOCK) {
                    System.out.printf("%ssend %3d customers to %s%n",
                            prefix(), inboundCustomers, guide.getName());
                }

                // Add inbound to outbound for next even day
                outboundCustomers += inboundCustomers;

                // Signal guides that assignment is done
                int guideAssigned = awaitBarrier(guideAssignDoneBarrier);

                // Blank line separator
                if (guideAssigned == guideAssignDoneBarrier.getParties() - 1) {
                    synchronized (Main.PRINT_LOCK) {
                        System.out.println(prefix());
                    }
                }

                // Wait for day to end
                awaitBarrier(dayEndBarrier);

            } catch (RuntimeException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}