package Project1_6481328;

import java.util.*;

public class PaymentCalculator {

    public static long[] calculateInstallments(long total, ArrayList<Installment> list) {
        int finalSize = list.size() + 1;
        long[] result = new long[finalSize];

        long paid = 0;
        for (int i = 0; i < list.size(); i++) {
            long amount = Math.round(total * list.get(i).getPercent() / 100.0);
            result[i] = amount;
            paid += amount;
        }
        result[finalSize - 1] = total - paid; // remaining
        return result;
    }

    public static void bookingCalculation(ArrayList<Booking> bookings, ArrayList<Tour> tours, ArrayList<Installment> installments) {
        Map<String, Tour> tourByCode = new HashMap<>();
        for (Tour t : tours) tourByCode.put(t.getCode(), t);

        // customers
        Map<String, Customer> customers = new HashMap<>();

        // summaries
        Map<String, TourSummary> gtSummary = new HashMap<>();
        Map<String, TourSummary> hpSummary = new HashMap<>();

        // pre-fill gt and hp so that summary will print empty list if no bookings as well
        for (String code : new String[]{"GT1", "GT2", "GT3", "GT4"}) {
            gtSummary.put(code, new TourSummary(code));
        }
        for (String code : new String[]{"HP1", "HP2", "HP3", "HP4"}) {
            hpSummary.put(code, new TourSummary(code));
        }

        for (Booking booking : bookings) {
            Tour tour = tourByCode.get(booking.getTourCode());
            if (tour == null) continue;

            Customer cust = customers.get(booking.getCustomerId());
//            boolean firstBooking = false;
            if (cust == null) {
                cust = new Customer(booking.getCustomerId());
                customers.put(cust.getCustomerId(), cust);
//                firstBooking = true;
            }

            long total = tour.calculateTotal(booking);

            long[] pays = calculateInstallments(total, installments);

            double currentCashback = cust.getCashback();
            double futureCashback = Math.round(total * 0.01 * 100.0) / 100.0;

            int lastIdx = pays.length - 1;
            double lastBefore = pays[lastIdx];
            double used = Math.min(currentCashback, lastBefore);
            double lastAfter = lastBefore - used;

            cust.setCashback(currentCashback - used);
            cust.addCashback(futureCashback);

            // ----- PRINT -----
            System.out.printf(
                    "Booking %s, customer %s, current cashback = %,.2f%n",
                    booking.getBookingId(),
                    booking.getCustomerId(),
                    currentCashback
            );

            if (booking.isGroupTour()) {
                // GT: n1 = total persons, n2 = single supplement requests
                int persons = booking.getN1();
                int single = booking.getN2();
                int doubleRooms = (persons - single) / 2;

                System.out.printf(
                        "%10s program %s, %d persons (%d single + %d double rooms)%n",
                        "",booking.getTourCode(), persons, single, doubleRooms
                );

            } else {
                // HP: n1 = single rooms, n2 = double rooms
                int singleRooms = booking.getN1();
                int doubleRooms = booking.getN2();
                int persons = singleRooms + 2 * doubleRooms;

                System.out.printf(
                        "%10s program %s, %d persons (%d single + %d double rooms)%n",
                        "",booking.getTourCode(), persons, singleRooms, doubleRooms
                );
            }


            System.out.printf(
                    "%10s total payment %4s= %,15.2f   future  cashback (%,10.2f)%n",
                    "","",(double) total, futureCashback
            );

            for (int i = 0; i < pays.length; i++) {
                if (i < lastIdx) {
                    System.out.printf("%12s installment %d   = %,15.2f%n",
                            "", i + 1, (double) pays[i]);
                } else {
                    System.out.printf("%12s installment %d   = %,15.2f  -current cashback (%,10.2f) = %,13.2f%n",
                            "", i + 1, lastBefore, currentCashback, lastAfter
                    );
                }
            }
            System.out.println();

            int travelers;
            if (booking.isGroupTour()){
                travelers = booking.getN1(); //for gt n1 is just the total num of people
            }else{
                travelers = booking.getN1() + (2* booking.getN2()); //for hp n1 = single rooms, n2 = double rooms (so multiply by 2)
            }

            Map<String, TourSummary> target = booking.isGroupTour() ? gtSummary : hpSummary; //if booking is gt, map to gt, else to hp
            TourSummary ts = target.get(booking.getTourCode());
            if (ts == null) { //if first time seeing this tour code (but since we prefilled, this is kinda useless :p)
                ts = new TourSummary(booking.getTourCode()); //create new TourSummary obj and adds it to the map so we can find again for next booking
                target.put(booking.getTourCode(), ts);
            }
            ts.addBooking(booking.getBookingId(), travelers, total); //send data to TourSummary class to update the total
        }

        // ----- PRINT SUMMARIES -----
        System.out.println("===== Group-Tour Summary =====");
        ArrayList<TourSummary> gtList = new ArrayList<>(gtSummary.values());
        Collections.sort(gtList);
        for (TourSummary ts : gtList) ts.print();

        System.out.println();
        System.out.println("===== Holiday-Package Summary =====");
        ArrayList<TourSummary> hpList = new ArrayList<>(hpSummary.values());
        Collections.sort(hpList);
        for (TourSummary ts : hpList) ts.print();
    }
}
