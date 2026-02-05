package Project1_6481328;

public abstract class Tour {
    protected String code;

    public String getCode() { return code; }
    public abstract long calculateTotal(Booking booking);
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
    public long calculateTotal(Booking GroupTourBooking) {
        int persons = GroupTourBooking.getN1();
        int singleRequest = GroupTourBooking.getN2();
        int rates;

        if(persons >= 15 && persons <= 20)  rates = rate15_20;
        else if(persons >= 21 && persons <= 30)              rates = rate21_30;
        else                                rates = rate31plus;

        return (long) persons * rates + (long) singleRequest * singleSupplement;
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
    public long calculateTotal(Booking HolidayPackageBooking) {
        int singlePersons = HolidayPackageBooking.getN1();
        int doubleRooms = HolidayPackageBooking.getN2();

        long total = (long) singlePersons * singleRate + (long) (doubleRooms * 2) * doubleRate;

        return total;
    }

    @Override
    public void print() {
        System.out.printf("%-6s%,16d%,18d%n", code, singleRate, doubleRate);
    }
}
