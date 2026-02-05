package Project1_6481328;

import java.util.*;

public class TourSummary implements Comparable<TourSummary> {

    private final String tourCode;
    private long totalTravelers, totalRevenue;
    private final List<String> bookingIds;

    public TourSummary(String tourCode) {
        this.tourCode = tourCode;
        this.totalTravelers = 0;
        this.totalRevenue = 0;
        this.bookingIds = new ArrayList<>();
    }

    public String getTourCode()         { return tourCode; }
    public long getTotalTravelers()     { return totalTravelers; }
    public long getTotalRevenue()       { return totalRevenue; }
    public List<String> getBookingIds() { return bookingIds; }

    public void addBooking(String bookingId, int travelers, long revenue) {
        bookingIds.add(bookingId);
        totalTravelers += travelers;
        totalRevenue += revenue;
    }

    @Override
    public int compareTo(TourSummary other) {
        int c1 = Long.compare(other.totalTravelers, this.totalTravelers);
        if (c1 != 0) return c1;

        int c2 = Long.compare(other.totalRevenue, this.totalRevenue);
        if (c2 != 0) return c2;

        return this.tourCode.compareTo(other.tourCode);
    }

    public void print() {
        System.out.printf(
                "%-4s  total travelers = %4d   total revenue = %,12.2f   bookings = %s%n",
                this.tourCode, this.totalTravelers, (double) this.totalRevenue, this.bookingIds
        );
    }
}
