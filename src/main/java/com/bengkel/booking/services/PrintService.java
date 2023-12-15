package com.bengkel.booking.services;

import java.util.List;

import com.bengkel.booking.models.BookingOrder;
import com.bengkel.booking.models.Car;
import com.bengkel.booking.models.Customer;
import com.bengkel.booking.models.ItemService;
import com.bengkel.booking.models.MemberCustomer;
import com.bengkel.booking.models.Vehicle;

public class PrintService {
	
	public static void printMenu(String[] listMenu, String title) {
		String line = "+---------------------------------+";
		int number = 1;
		String formatTable = " %-2s. %-25s %n";
		
		System.out.printf("%-25s %n", title);
		System.out.println(line);
		
		for (String data : listMenu) {
			if (number < listMenu.length) {
				System.out.printf(formatTable, number, data);
			}else {
				System.out.printf(formatTable, 0, data);
			}
			number++;
		}
		System.out.println(line);
		System.out.println();
	}
	
	public static void printVechicle(List<Vehicle> listVehicle) {
		String formatTable = "| %-2s | %-15s | %-10s | %-15s | %-15s | %-5s | %-15s |%n";
		String line = "+----+-----------------+------------+-----------------+-----------------+-------+-----------------+%n";
		System.out.format(line);
	    System.out.format(formatTable, "No", "Vechicle Id", "Warna", "Brand", "Transmisi", "Tahun", "Tipe Kendaraan");
	    System.out.format(line);
	    int number = 1;
	    String vehicleType = "";
	    for (Vehicle vehicle : listVehicle) {
	    	if (vehicle instanceof Car) {
				vehicleType = "Mobil";
			}else {
				vehicleType = "Motor";
			}
	    	System.out.format(formatTable, number, vehicle.getVehiclesId(), vehicle.getColor(), vehicle.getBrand(), vehicle.getTransmisionType(), vehicle.getYearRelease(), vehicleType);
	    	number++;
	    }
	    System.out.printf(line);
	}
	
	//Silahkan Tambahkan function print sesuai dengan kebutuhan.
	
	public static void printCustomerInfo(Customer customer) {
        System.out.println("\nInformasi Customer");
        System.out.println("\nCustomer Id: " + customer.getCustomerId());
        System.out.println("Nama: " + customer.getName());
        System.out.println("Customer Status: " + (customer instanceof MemberCustomer ? "Member" : "Non Member"));
        System.out.println("Alamat: " + customer.getAddress());

        if (customer instanceof MemberCustomer) {
            MemberCustomer memberCustomer = (MemberCustomer) customer;
            System.out.println("Saldo Koin: " + memberCustomer.getSaldoCoin());
        }

        System.out.println("\nList Kendaraan:");
        PrintService.printVechicle(customer.getVehicles());
    }

    public static void printItemServices(List<ItemService> listItemService) {
        String formatTable = "| %-2s | %-15s | %-15s | %-15s | %-10s |%n";
        String line = "+----+-----------------+-----------------+-----------------+------------+%n";
        System.out.format(line);
        System.out.format(formatTable, "No", "Service Id", "Nama Service", "Tipe Kendaraan", "Harga");
        System.out.format(line);
        int number = 1;
        for (ItemService itemService : listItemService) {
            System.out.format(formatTable, number, itemService.getServiceId(), itemService.getServiceName(),
                    itemService.getVehicleType(), itemService.getPrice());
            number++;
        }
        System.out.printf(line);
    }

    public static void printBookingInfo(BookingOrder bookingOrder) {
		System.out.println("\nBooking Order Menu");
		System.out.println("------------------------------------------------------------------------------------------------------");
		System.out.printf("|%-3s| %-18s| %-15s| %-15s| %-15s| %-15s| %-30s%n|", "No", "Booking Id", "Nama Customer", "Payment Method", "Total Service", "Total Payment", "List Service");
		System.out.println("------------------------------------------------------------------------------------------------------");
		System.out.printf("|%-3s| %-18s| %-15s| %-15s| %-15s| %-15s| %-30s%n|", "1", bookingOrder.getBookingId(), bookingOrder.getCustomer().getName(),
				bookingOrder.getPaymentMethod(), bookingOrder.getTotalServicePrice(), bookingOrder.getTotalPayment(), getServiceListString(bookingOrder.getSelectedServices()));
	
    }

	static String getServiceListString(List<ItemService> services) {
		StringBuilder serviceListString = new StringBuilder();
		for (ItemService service : services) {
			serviceListString.append(service.getServiceName()).append(", ");
		}
		// Hapus koma di akhir dan tambahkan newline
		return serviceListString.substring(0, serviceListString.length() - 2);
	}
}
