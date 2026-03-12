package Project2_6481328;

import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;

/**
 *
 * @author Pakapak Jungjaroen   6481328
 * @author Tanadol Chuntarasupt 6481259
 * @author Steven Jonatan       6881006
 * @author Jimin Kim            6680046
 */
public class Main {

    public static final Object PRINT_LOCK = new Object();

    public static void main(String[] args) {
        Thread.currentThread().setName("main");
        Main mainApp = new Main();
        mainApp.runApp();
    }

    private int awaitBarrier(CyclicBarrier barrier) {
        try {
            return barrier.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
    }

    public void runApp() {
        String basePath = "src/main/java/Project2_6481328/";
        String fileName = "config_1.txt";

        int[] config = Helper.readConfig(basePath, fileName);

        int days        = config[0];
        int inboundNum  = config[1];
        int inboundMax  = config[2];
        int outboundNum = config[3];
        int outboundMax = config[4];
        int placeNum    = config[5];
        int tourNum     = config[6];
        int tourMin     = config[7];
        int tourMax     = config[8];
        int guideNum    = config[9];

        ArrayList<CityLimo> cityLimos = Helper.createCityLimos(inboundNum, inboundMax);
        ArrayList<AirportLimo> airportLimos = Helper.createAirportLimos(outboundNum, outboundMax);
        ArrayList<Place> places = Helper.createPlaces(placeNum);

        int padWidth = Helper.calculatePadWidth(tourNum, guideNum);

        Helper.printParameters(
                padWidth, days, cityLimos, inboundMax,
                airportLimos, outboundMax, places, tourNum, tourMin, tourMax, guideNum
        );

        // Create barriers
        int totalParties = tourNum + guideNum + 1; // +1 for main thread
        CyclicBarrier dayStartBarrier = new CyclicBarrier(totalParties);
        CyclicBarrier dayEndBarrier = new CyclicBarrier(totalParties);
        CyclicBarrier outboundReportBarrier = new CyclicBarrier(tourNum);
        CyclicBarrier afterOutboundBarrier = new CyclicBarrier(tourNum);
        CyclicBarrier inboundArrivalBarrier = new CyclicBarrier(tourNum);
        CyclicBarrier inboundLimoBarrier = new CyclicBarrier(tourNum);
        CyclicBarrier guideAssignDoneBarrier = new CyclicBarrier(tourNum + guideNum);
        CyclicBarrier guideReportBarrier = new CyclicBarrier(guideNum);

        // Create GuideThreads
        ArrayList<GuideThread> guideThreads = new ArrayList<>();
        for (int i = 0; i < guideNum; i++) {
            guideThreads.add(new GuideThread(
                    "GuideThread_" + i,
                    days,
                    places,
                    dayStartBarrier,
                    guideAssignDoneBarrier,
                    guideReportBarrier,
                    dayEndBarrier,
                    padWidth
            ));
        }

        // Create TourThreads
        ArrayList<TourThread> tourThreads = new ArrayList<>();
        for (int i = 0; i < tourNum; i++) {
            tourThreads.add(new TourThread(
                    "TourThread_" + i,
                    days,
                    tourMin,
                    tourMax,
                    cityLimos,
                    airportLimos,
                    guideThreads,
                    dayStartBarrier,
                    outboundReportBarrier,
                    afterOutboundBarrier,
                    inboundArrivalBarrier,
                    inboundLimoBarrier,
                    guideAssignDoneBarrier,
                    dayEndBarrier,
                    padWidth
            ));
        }

        // Start all threads
        for (GuideThread guide : guideThreads) {
            guide.start();
        }
        for (TourThread tour : tourThreads) {
            tour.start();
        }

        // Main thread day loop
        for (int day = 1; day <= days; day++) {
            Helper.printDayHeaderAndReset(day, padWidth, cityLimos, airportLimos);

            // Release all threads to start the day
            awaitBarrier(dayStartBarrier);

            // Wait for all threads to finish the day
            awaitBarrier(dayEndBarrier);
        }

        Helper.joinTours(tourThreads);
        Helper.joinGuides(guideThreads);

        guideThreads.sort((a, b) -> {
            int diff = b.getTotalCustomers() - a.getTotalCustomers();
            if (diff != 0) return diff;
            return a.getName().compareTo(b.getName());
        });

        Helper.printSummary(padWidth, guideThreads);
    }
}