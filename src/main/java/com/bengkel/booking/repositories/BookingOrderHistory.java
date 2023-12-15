package com.bengkel.booking.repositories;

import java.util.ArrayList;
import java.util.List;

import com.bengkel.booking.models.BookingOrder;
import com.bengkel.booking.models.Customer;

public class BookingOrderHistory {

    private static List<BookingOrder> bookingOrders = new ArrayList<>();

    public static void addBookingOrder(BookingOrder bookingOrder) {
        bookingOrders.add(bookingOrder);
    }

    public static List<BookingOrder> getBookingOrdersByCustomer(Customer customer) {
        List<BookingOrder> customerBookingOrders = new ArrayList<>();
        for (BookingOrder bookingOrder : bookingOrders) {
            if (bookingOrder.getCustomer().equals(customer)) {
                customerBookingOrders.add(bookingOrder);
            }
        }
        return customerBookingOrders;
    }

}
