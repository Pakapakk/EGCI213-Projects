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

    public static void bookingCalculation(
            ArrayList<Booking> bookings,
            ArrayList<Tour> tours,
            ArrayList<Installment> installments
    ) {
        // --- tour lookup ---
        Map<String, Tour> tourByCode = new HashMap<>();
        for (Tour t : tours) tourByCode.put(t.getCode(), t);

        // --- customers (cashback wallet) ---
        Map<String, Customer> customers = new HashMap<>();

        // --- summaries ---
        Map<String, TourSummary> gtSummary = new HashMap<>();
        Map<String, TourSummary> hpSummary = new HashMap<>();

        for (String code : new String[]{"GT1", "GT2", "GT3", "GT4"}) {
            gtSummary.put(code, new TourSummary(code));
        }
        for (String code : new String[]{"HP1", "HP2", "HP3", "HP4"}) {
            hpSummary.put(code, new TourSummary(code));
        }

        // --- process bookings ---
        for (Booking booking : bookings) {
            Tour tour = tourByCode.get(booking.getTourCode());
            if (tour == null) continue;

            Customer cust = customers.get(booking.getCustomerId());
            if (cust == null) {
                cust = new Customer(booking.getCustomerId());
                customers.put(cust.getCustomerId(), cust);
            }

            BookingInfo info = tour.buildInfo(booking);
            long total = info.totalPayment;

            // installments
            long[] pays = calculateInstallments(total, installments);

            // cashback
            double currentCashback = cust.getCashback();
            double futureCashback = Math.round(total * 0.01 * 100.0) / 100.0;

            int lastIdx = pays.length - 1;
            double lastBefore = pays[lastIdx];

            // only last installment can be paid by cashback
            double used = Math.min(currentCashback, lastBefore);
            double lastAfter = lastBefore - used;

            // update wallet: current - used + future
            cust.setCashback(currentCashback - used);
            cust.addCashback(futureCashback);

            // ----- PRINT -----
            System.out.printf(
                    "Booking %s, customer %s, current cashback = %,.2f%n",
                    booking.getBookingId(),
                    booking.getCustomerId(),
                    currentCashback
            );

            System.out.printf(
                    "%10s program %s, %d persons (%d single + %d double rooms)%n",
                    "",
                    booking.getTourCode(),
                    info.persons,
                    info.singleRooms,
                    info.doubleRooms
            );

            System.out.printf(
                    "%10s total payment %4s= %,15.2f   future  cashback (%,10.2f)%n",
                    "",
                    "",
                    (double) total,
                    futureCashback
            );

            for (int i = 0; i < pays.length; i++) {
                if (i < lastIdx) {
                    System.out.printf(
                            "%12s installment %d   = %,15.2f%n",
                            "",
                            i + 1,
                            (double) pays[i]
                    );
                } else {
                    System.out.printf(
                            "%12s installment %d   = %,15.2f  -current cashback (%,10.2f) = %,13.2f%n",
                            "",
                            i + 1,
                            lastBefore,
                            used,
                            lastAfter
                    );
                }
            }
            System.out.println();

            int travelers = info.persons;

            Map<String, TourSummary> target = booking.isGroupTour() ? gtSummary : hpSummary;
            TourSummary ts = target.get(booking.getTourCode());
            if (ts == null) { // safety, though prefilled
                ts = new TourSummary(booking.getTourCode());
                target.put(booking.getTourCode(), ts);
            }
            ts.addBooking(booking.getBookingId(), travelers, total);
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