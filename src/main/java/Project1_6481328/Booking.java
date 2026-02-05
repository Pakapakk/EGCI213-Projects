package Project1_6481328;

public class Booking {
    private final String bookingId, customerId, tourCode;
    private final int n1, n2;
    private static int totalInstallments;

    public Booking(String[] c) {
        this.bookingId = c[0];
        this.customerId = c[1];
        this.tourCode = c[2];
        this.n1 = Integer.parseInt(c[3]);
        this.n2 = Integer.parseInt(c[4]);
    }

    public String getBookingId()    { return bookingId; }
    public String getCustomerId()   { return customerId; }
    public String getTourCode()     { return tourCode; }
    public int getN1()              { return n1; }
    public int getN2()              { return n2; }

    public boolean isGroupTour()        { return tourCode.startsWith("GT"); }
    public boolean isHolidayPackage()   { return tourCode.startsWith("HP"); }

    public static int getTotalInstallments()            { return totalInstallments; }
    public static void setTotalInstallments(int total)  { totalInstallments = total; }

    public void print() {
        System.out.printf("%s, %s, %s, %d, %d",
                this.bookingId, this.customerId, this.tourCode, this.n1, this.n2);
    }
}
