package Project1_6481328;

public abstract class Tour {
    protected String code;

    public String getCode() { return code; }
    public abstract long calculateTotal(Booking booking);
    public abstract BookingInfo buildInfo(Booking booking);
    public abstract void print();

}

class GroupTour extends Tour {
    private final int rate15_20, rate21_30, rate31plus, singleSupplement;

    public GroupTour(String[] c) {
        this.code = c[0];
        this.rate15_20 = Integer.parseInt(c[1]);
        this.rate21_30 = Integer.parseInt(c[2]);
        this.rate31plus = Integer.parseInt(c[3]);
        this.singleSupplement = Integer.parseInt(c[4]);
    }

    public static void printHeader() {
        System.out.println("Group Tours: price per person");
        System.out.println("--------------------------------------------------------------------------");
        System.out.printf("%-6s%15s%15s%15s%20s%n",
                "Code", "15-20 persons", "21-30 persons", ">=31 persons", "Single Supplement");
        System.out.println("--------------------------------------------------------------------------");
    }

    @Override
    public long calculateTotal(Booking b) {
        return buildInfo(b).totalPayment;
    }

    @Override
    public BookingInfo buildInfo(Booking GroupTourBooking) {
        int persons = GroupTourBooking.getN1();
        int single = GroupTourBooking.getN2();
        int remaining = persons - single;

        int doubleRooms;
        if (remaining <= 0)         doubleRooms = 0;
        else if (remaining == 1)    doubleRooms = 1;
        else                        doubleRooms = remaining / 2;

        int rate;
        if (persons >= 15 && persons <= 20)      rate = rate15_20;
        else if (persons >= 21 && persons <= 30) rate = rate21_30;
        else                                     rate = rate31plus;

        long total = (long) persons * rate + (long) single * singleSupplement;

        return new BookingInfo(persons, single, doubleRooms, total);
    }

    @Override
    public void print() {
        System.out.printf("%-6s%,14d%,15d%,15d%,16d%n", code, rate15_20, rate21_30, rate31plus, singleSupplement);
    }
}

class HolidayPackage extends Tour {
    private final int singleRate, doubleRate;

    public HolidayPackage(String[] c) {
        this.code = c[0];
        this.singleRate = Integer.parseInt(c[1]);
        this.doubleRate = Integer.parseInt(c[2]);
    }

    public static void printHeader() {
        System.out.println("\nHoliday Packages: price per person");
        System.out.println("----------------------------------------------------");
        System.out.printf("%-6s%20s%20s%n",
                "Code", "1 person(Single)", "2 persons(Double)");
        System.out.println("----------------------------------------------------");
    }

    @Override
    public long calculateTotal(Booking b) {
        return buildInfo(b).totalPayment;
    }

    @Override
    public BookingInfo buildInfo(Booking HolidayPackageBooking) {
        int singleRooms = HolidayPackageBooking.getN1();
        int doubleRooms = HolidayPackageBooking.getN2();
        int persons = singleRooms + 2 * doubleRooms;

        long total = (long) singleRooms * singleRate + (long) doubleRooms * 2L * doubleRate;

        return new BookingInfo(persons, singleRooms, doubleRooms, total);
    }

    @Override
    public void print() {
        System.out.printf("%-6s%,16d%,18d%n", code, singleRate, doubleRate);
    }
}
