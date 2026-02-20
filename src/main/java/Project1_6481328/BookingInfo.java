package Project1_6481328;

import java.io.*;
import java.util.*;

public class BookingInfo {
    public final int persons;
    public final int singleRooms;
    public final int doubleRooms;
    public final long totalPayment;

    public BookingInfo(int persons, int singleRooms, int doubleRooms, long totalPayment) {
        this.persons = persons;
        this.singleRooms = singleRooms;
        this.doubleRooms = doubleRooms;
        this.totalPayment = totalPayment;
    }
}