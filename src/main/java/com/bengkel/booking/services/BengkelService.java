package com.bengkel.booking.services;

import java.util.List;
import java.util.stream.Collectors;

import com.bengkel.booking.models.Customer;
import com.bengkel.booking.models.ItemService;
import com.bengkel.booking.models.MemberCustomer;
import com.bengkel.booking.models.Vehicle;
import com.bengkel.booking.repositories.CustomerRepository;
import com.bengkel.booking.repositories.ItemServiceRepository;

public class BengkelService {
	
	//Silahkan tambahkan fitur-fitur utama aplikasi disini
	
	//Login
	
	//Info Customer
	
	//Booking atau Reservation
	
	//Top Up Saldo Coin Untuk Member Customer
	
	//Logout
	private static List<ItemService> listAllItemService = ItemServiceRepository.getAllItemService();
	private static List<Customer> listAllCustomers = CustomerRepository.getAllCustomer();

	public static List<ItemService> getAvailableServices(String vehicleId) {
		String vehicleType = getVehicleTypeByVehicleId(vehicleId);

		List<ItemService> availableServices = listAllItemService.stream()
				.filter(service -> service.getVehicleType().equalsIgnoreCase(vehicleType))
				.collect(Collectors.toList());

		if (availableServices.isEmpty()) {
			System.out.println("Tidak ada layanan yang tersedia untuk kendaraan dengan ID " + vehicleId);
		}

		return availableServices;
	}

	private static String getVehicleTypeByVehicleId(String vehicleId) {
		return listAllCustomers.stream()
				.flatMap(customer -> customer.getVehicles().stream())
				.filter(vehicle -> vehicle.getVehiclesId().equalsIgnoreCase(vehicleId))
				.findFirst()
				.map(Vehicle::getVehicleType)
				.orElse(null);
	}

}
