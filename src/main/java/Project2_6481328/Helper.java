package Project2_6481328;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Helper {

    public static String prefix(int padWidth) {
        return String.format("%" + padWidth + "s >> ", Thread.currentThread().getName());
    }

    public static int calculatePadWidth(int tourNum, int guideNum) {
        int padWidth = Thread.currentThread().getName().length();
        for (int i = 0; i < tourNum; i++) {
            padWidth = Math.max(padWidth, ("TourThread_" + i).length());
        }
        for (int i = 0; i < guideNum; i++) {
            padWidth = Math.max(padWidth, ("GuideThread_" + i).length());
        }
        return padWidth;
    }

    public static String buildThreadList(String baseName, int count) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < count; i++) {
            if (i > 0) sb.append(", ");
            sb.append(baseName).append("_").append(i);
        }
        sb.append("]");
        return sb.toString();
    }

    public static int[] readConfig(String basePath, String defaultFileName) {
        int days = 0;
        int inboundNum = 0, inboundMax = 0;
        int outboundNum = 0, outboundMax = 0;
        int placeNum = 0;
        int tourNum = 0, tourMin = 0, tourMax = 0;
        int guideNum = 0;

        String fileName = defaultFileName;
        BufferedReader reader = null;
        Scanner scanner = new Scanner(System.in);

        while (reader == null) {
            try {
                reader = new BufferedReader(new FileReader(basePath + fileName));
            } catch (FileNotFoundException e) {
                synchronized (Main.PRINT_LOCK) {
                    System.out.println(e);
                    System.out.print("New file name = ");
                }
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
            throw new RuntimeException("Error reading config file.", e);
        }

        return new int[] {
                days,
                inboundNum, inboundMax,
                outboundNum, outboundMax,
                placeNum,
                tourNum, tourMin, tourMax,
                guideNum
        };
    }

    public static ArrayList<CityLimo> createCityLimos(int inboundNum, int inboundMax) {
        ArrayList<CityLimo> cityLimos = new ArrayList<>();
        for (int i = 0; i < inboundNum; i++) {
            cityLimos.add(new CityLimo("CityLimo_" + i, inboundMax));
        }
        return cityLimos;
    }

    public static ArrayList<AirportLimo> createAirportLimos(int outboundNum, int outboundMax) {
        ArrayList<AirportLimo> airportLimos = new ArrayList<>();
        for (int i = 0; i < outboundNum; i++) {
            airportLimos.add(new AirportLimo("AirportLimo_" + i, outboundMax));
        }
        return airportLimos;
    }

    public static ArrayList<Place> createPlaces(int placeNum) {
        ArrayList<Place> places = new ArrayList<>();
        for (int i = 0; i < placeNum; i++) {
            places.add(new Place("Place_" + i));
        }
        return places;
    }

    public static void printParameters(int padWidth, int days,
                                       ArrayList<CityLimo> cityLimos, int inboundMax,
                                       ArrayList<AirportLimo> airportLimos, int outboundMax,
                                       ArrayList<Place> places,
                                       int tourNum, int tourMin, int tourMax, int guideNum) {
        String p = prefix(padWidth);
        synchronized (Main.PRINT_LOCK) {
            System.out.println(p + "====================== Parameters ======================");
            System.out.println(p + "Days of simulation    : " + days);
            System.out.println(p + "city limo services    : " + cityLimos);
            System.out.println(p + "City limo capacity    : " + inboundMax + " per service");
            System.out.println(p + "Airport limo services : " + airportLimos);
            System.out.println(p + "Airport limo capacity : " + outboundMax + " per service");
            System.out.println(p + "Places                : " + places);
            System.out.println(p + "TourThreads           : " + buildThreadList("TourThread", tourNum));
            System.out.println(p + "Daily arrival         : min = " + tourMin + ", max = " + tourMax);
            System.out.println(p + "GuideThreads          : " + buildThreadList("GuideThread", guideNum));
        }
    }

    public static void printDayHeaderAndReset(int day, int padWidth,
                                              ArrayList<CityLimo> cityLimos,
                                              ArrayList<AirportLimo> airportLimos) {
        String p = prefix(padWidth);
        synchronized (Main.PRINT_LOCK) {
            System.out.println(p);
            System.out.println(p + "=========================================================");
            System.out.println(p + "Day " + day);

            for (CityLimo limo : cityLimos) {
                limo.resetSeats();
                System.out.printf("%sreset %-14s remaining seats = %5d%n",
                        p, limo.getName(), limo.getRemainingSeats());
            }

            for (AirportLimo limo : airportLimos) {
                limo.resetSeats();
                System.out.printf("%sreset %-14s remaining seats = %5d%n",
                        p, limo.getName(), limo.getRemainingSeats());
            }

            System.out.println(p);
        }
    }

    public static void printSummary(int padWidth, ArrayList<GuideThread> guideThreads) {
        String p = prefix(padWidth);

        synchronized (Main.PRINT_LOCK) {
            System.out.println(p);
            System.out.println(p + "================================================");
            System.out.println(p + "Summary");

            for (GuideThread guide : guideThreads) {
                System.out.printf("%s%s\ttotal customers = %5d%n",
                        p, guide.getName(), guide.getTotalCustomers());
            }
        }
    }

    public static void joinTours(ArrayList<TourThread> tourThreads) {
        for (TourThread tour : tourThreads) {
            try {
                tour.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while joining TourThreads.", e);
            }
        }
    }

    public static void joinGuides(ArrayList<GuideThread> guideThreads) {
        for (GuideThread guide : guideThreads) {
            try {
                guide.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while joining GuideThreads.", e);
            }
        }
    }
}