package Project1_6481328;

import java.io.*;
import java.util.*;

public class Helper {
    private static final String PATH = "src/main/java/Project1_6481328/";

    public static ArrayList<InputHandler> readFile(String filename) {
        Scanner keyboardScanner = new Scanner(System.in);
        ArrayList<InputHandler> rows = new ArrayList<>();
        boolean fileLoaded = false;

        while (!fileLoaded) {
            try (Scanner fileScanner = new Scanner(new File(PATH + filename))) {
                fileLoaded = true;
                System.out.println("\nRead from " + PATH + filename);

                while (fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine();

                    if (line.trim().isEmpty()) continue;

                    if (line.trim().startsWith("#")) {
                        rows.add(new InputHandler(new String[0], line));
                        continue;
                    }

                    String[] cols = line.split(",");
                    for (int i = 0; i < cols.length; i++) {
                        cols[i] = cols[i].trim();
                    }
                    rows.add(new InputHandler(cols, line));
                }
            } catch (FileNotFoundException e) {
                System.err.println("\n" + e + " (The system cannot find the specified file) \nEnter the correct file name = ");
                filename = keyboardScanner.next();
            }
        }

        return rows;
    }

    public static ArrayList<Tour> readTours() {
        ArrayList<InputHandler> lines = readFile("tours.txt");
        ArrayList<Tour> tours = new ArrayList<>();

        for (InputHandler line : lines) {
            if (line.isComment()) continue;

            String[] c = line.getArgumentsString();
            String code = c[0];

            if (code.startsWith("GT")) tours.add(new GroupTour(c));
            else if (code.startsWith("HP")) tours.add(new HolidayPackage(c));
        }

        return tours;
    }

    public static void printTours(ArrayList<Tour> tours) {
        GroupTour.printHeader();
        for (Tour t : tours)
            if (t instanceof GroupTour gt)
                gt.print();

        HolidayPackage.printHeader();
        for (Tour t : tours)
            if (t instanceof HolidayPackage hp)
                hp.print();
    }

    public static ArrayList<Installment> readInstallments() {
        ArrayList<InputHandler> lines = readFile("installments.txt");
        ArrayList<Installment> list = new ArrayList<>();

        for (InputHandler line : lines) {
            if (line.isComment()) continue;
            list.add(new Installment(line.getArgumentsString()));
        }
        return list;
    }

    public static ArrayList<Booking> readBookings() {
        ArrayList<InputHandler> lines = readFile("bookings.txt");
        ArrayList<Booking> list = new ArrayList<>();

        for (InputHandler line : lines) {
            if (line.isComment()) continue;

            String[] c = line.getArgumentsString();
            try {
                if (c.length < 5) throw new ArrayIndexOutOfBoundsException();
                Booking b = new Booking(c);

                if (!(b.isGroupTour() || b.isHolidayPackage()))
                    throw new IllegalArgumentException("invalid tour code");

                list.add(b);
            } catch (Exception e) {
                System.out.printf("[%s] --> skip this booking%n", line.getOriginalInput());
            }
        }
        return list;
    }

    public static void printInstallments(ArrayList<Installment> installments) {
        int totalInstallments = installments.size() + 1;

        Installment.printHeader(totalInstallments);
        for (Installment ins : installments) ins.print();
        System.out.printf("(%d) remaining total%n", totalInstallments);
    }
}
