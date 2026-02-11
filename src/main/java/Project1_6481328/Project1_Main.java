package Project1_6481328;

import java.util.ArrayList;

/*
    Pakapak Jungjaroen 6481328
    Tanadol Chuntarasupt 6481259
    Name
    Name
*/

public class Project1_Main {
    public static void main(String[] args) {
        ArrayList<Tour> allTours = Helper.readTours();
        Helper.printTours(allTours);

        ArrayList<Installment> installments = Helper.readInstallments();
        Helper.printInstallments(installments);

        ArrayList<Booking> bookings = Helper.readBookings();

        System.out.println("===== Booking Processing =====");
        PaymentCalculator.bookingCalculation(bookings, allTours, installments);

    }
}