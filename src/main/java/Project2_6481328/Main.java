package Project2_6481328;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 *
 * @author Pakapak Jungjaroen   6481328
 * @author Tanadol Chuntarasupt 6481259
 * @author Steven Jonatan       6881006
 * @author Jimin Kim            6680046
 */
public class Main {

    public static void main(String[] args) {
        int days = 0;
        int inboundNum = 0, inboundMax = 0;
        int outboundNum = 0, outboundMax = 0;
        int placeNum = 0;
        int tourNum = 0, tourMin = 0, tourMax = 0;
        int guideNum = 0;

        String basePath = "src/main/java/project2_6481328/";
        String fileName = "config_1.txt";
        BufferedReader reader = null;
        Scanner scanner = new Scanner(System.in);

        while (reader == null) {
            try {
                reader = new BufferedReader(new FileReader(basePath + fileName));
            } catch (FileNotFoundException e) {
                System.out.println(e);
                System.out.println("New file name = ");
                fileName = scanner.nextLine().trim();
            }
        }

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("#") || line.isEmpty()) continue;

                String[] parts = line.split(",");
                for (int i = 0; i < parts.length; i++) {
                    parts[i] = parts[i].trim();
                }

                switch (parts[0]) {
                    case "days":
                        days = Integer.parseInt(parts[1]);
                        break;
                    case "inbound_num_max":
                        inboundNum = Integer.parseInt(parts[1]);
                        inboundMax = Integer.parseInt(parts[2]);
                        break;
                    case "outbound_num_max":
                        outboundNum = Integer.parseInt(parts[1]);
                        outboundMax = Integer.parseInt(parts[2]);
                        break;
                    case "place_num":
                        placeNum = Integer.parseInt(parts[1]);
                        break;
                    case "tour_num_min_max":
                        tourNum = Integer.parseInt(parts[1]);
                        tourMin = Integer.parseInt(parts[2]);
                        tourMax = Integer.parseInt(parts[3]);
                        break;
                    case "guide_num":
                        guideNum = Integer.parseInt(parts[1]);
                        break;
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error reading config. Exiting.");
            return;
        }

        ArrayList<CityLimo> cityLimos = new ArrayList<>();
        for (int i = 0; i < inboundNum; i++) {
            cityLimos.add(new CityLimo("CityLimo_" + i, inboundMax));
        }

        ArrayList<AirportLimo> airportLimos = new ArrayList<>();
        for (int i = 0; i < outboundNum; i++) {
            airportLimos.add(new AirportLimo("AirportLimo_" + i, outboundMax));
        }

        ArrayList<Place> places = new ArrayList<>();
        for (int i = 0; i < placeNum; i++) {
            places.add(new Place("Place_" + i));
        }

        int padWidth = "main".length();
        for (int i = 0; i < tourNum; i++) {
            padWidth = Math.max(padWidth, ("TourThread_" + i).length());
        }
        for (int i = 0; i < guideNum; i++) {
            padWidth = Math.max(padWidth, ("GuideThread_" + i).length());
        }

        String p = String.format("%" + padWidth + "s >> ", "main");

        StringBuilder tourNames = new StringBuilder("[");
        for (int i = 0; i < tourNum; i++) {
            if (i > 0) tourNames.append(", ");
            tourNames.append("TourThread_").append(i);
        }
        tourNames.append("]");

        StringBuilder guideNames = new StringBuilder("[");
        for (int i = 0; i < guideNum; i++) {
            if (i > 0) guideNames.append(", ");
            guideNames.append("GuideThread_").append(i);
        }
        guideNames.append("]");

        // Print parameter summary
        System.out.println(p + "====================== Parameters ======================");
        System.out.println(p + "Days of simulation    : " + days);
        System.out.println(p + "city limo services    : " + cityLimos);
        System.out.println(p + "City limo capacity    : " + inboundMax + " per service");
        System.out.println(p + "Airport limo services : " + airportLimos);
        System.out.println(p + "Airport limo capacity : " + outboundMax + " per service");
        System.out.println(p + "Places                : " + places);
        System.out.println(p + "TourThreads           : " + tourNames);
        System.out.println(p + "Daily arrival         : min = " + tourMin + ", max = " + tourMax);
        System.out.println(p + "GuideThreads          : " + guideNames);

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
            guideThreads.add(new GuideThread("GuideThread_" + i, days, places,
                    dayStartBarrier, guideAssignDoneBarrier, guideReportBarrier,
                    dayEndBarrier, padWidth));
        }

        // Create TourThreads
        ArrayList<TourThread> tourThreads = new ArrayList<>();
        for (int i = 0; i < tourNum; i++) {
            tourThreads.add(new TourThread("TourThread_" + i, days, tourMin, tourMax,
                    cityLimos, airportLimos, guideThreads,
                    dayStartBarrier, outboundReportBarrier, afterOutboundBarrier,
                    inboundArrivalBarrier, inboundLimoBarrier, guideAssignDoneBarrier,
                    dayEndBarrier, padWidth));
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
            try {
                System.out.println(p);
                if (day > 0) {
                    System.out.println(p + "=========================================================");
                }
                System.out.println(p + "Day " + day);

                // Reset and print all limo seats
                for (CityLimo limo : cityLimos) {
                    limo.resetSeats();
                    System.out.printf("%sreset %-14s remaining seats = %5d\n",
                            p, limo.getName(), limo.getRemainingSeats());
                }
                for (AirportLimo limo : airportLimos) {
                    limo.resetSeats();
                    System.out.printf("%sreset %-14s remaining seats = %5d\n",
                            p, limo.getName(), limo.getRemainingSeats());
                }
                System.out.println(p);

                // Release all threads to start the day
                dayStartBarrier.await();

                // Wait for all threads to finish the day
                dayEndBarrier.await();

            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }

        for (TourThread tour : tourThreads) {
            try { tour.join(); } catch (InterruptedException e) { e.printStackTrace(); }
        }
        for (GuideThread guide : guideThreads) {
            try { guide.join(); } catch (InterruptedException e) { e.printStackTrace(); }
        }

        // Print summary
        System.out.println(p);
        System.out.println(p + "================================================");
        System.out.println(p + "Summary");

        guideThreads.sort((a, b) -> {
            int diff = b.getTotalCustomers() - a.getTotalCustomers();
            if (diff != 0) return diff;
            return a.getName().compareTo(b.getName());
        });

        for (GuideThread guide : guideThreads) {
            System.out.printf("%s%s\ttotal customers = %5d\n",
                    p, guide.getName(), guide.getTotalCustomers());
        }
    }
}
