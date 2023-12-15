package com.bengkel.booking.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.bengkel.booking.models.BookingOrder;
import com.bengkel.booking.repositories.BookingOrderHistory;
import com.bengkel.booking.repositories.BookingOrderRepository;
import com.bengkel.booking.models.Customer;
import com.bengkel.booking.models.ItemService;
import com.bengkel.booking.models.MemberCustomer;
import com.bengkel.booking.repositories.CustomerRepository;
import com.bengkel.booking.repositories.ItemServiceRepository;

public class MenuService {
	private static List<Customer> listAllCustomers = CustomerRepository.getAllCustomer();
	private static List<ItemService> listAllItemService = ItemServiceRepository.getAllItemService();
	private static Scanner input = new Scanner(System.in);
	private static Customer loggedInCustomer;

    private static final int MAX_LOGIN_ATTEMPTS = 3;
	public static void run() {
		boolean isLooping = true;
        int loginAttempts = 0;

        do {
            if (loginAttempts >= MAX_LOGIN_ATTEMPTS) {
                System.out.println("Anda telah melebihi batas percobaan login. Aplikasi akan keluar.");
                break;
            }

            loginAttempts++;

            login();

            if (loggedInCustomer != null) {
                mainMenu();
            } else {
                System.out.println("Login gagal. Silakan coba lagi.");
            }
        } while (isLooping);
	}
	
	public static void login() {
		System.out.println("\nAplikasi Booking Bengkel");
        System.out.println("1. Login");
        System.out.println("0. Exit");

        int choice = Validation.validasiNumberWithRange("Pilih menu:", "Input harus berupa angka!", "^[0-9]+$", 1, 0);

        switch (choice) {
            case 1:
                performLogin();
                break;
            case 0:
                System.out.println("Terima kasih telah menggunakan aplikasi. Sampai jumpa!");
                System.exit(0);
            default:
                System.out.println("Pilihan tidak valid.");
        }
	}

	private static void performLogin() {
        System.out.println("\nLogin");

        String customerId = Validation.validasiInput("Masukkan Customer Id: ", "Input tidak valid!", "^[a-zA-Z0-9-]+$");
        String password = Validation.validasiInput("Masukkan Password: ", "Input tidak valid!", "^[a-zA-Z0-9]+$");

        for (Customer customer : listAllCustomers) {
            if (customer.getCustomerId().equals(customerId) && customer.getPassword().equals(password)) {
                loggedInCustomer = customer;
                System.out.println("\nLogin berhasil!");
                return;
            }
        }

        System.out.println("\nCustomer Id tidak ditemukan atau password salah!");
    }
	
	public static void mainMenu() {
		String[] listMenu = {"Informasi Customer", "Booking Bengkel", "Top Up Bengkel Coin", "Informasi Booking", "Logout"};
		int menuChoice = 0;
		boolean isLooping = true;
		
		do {
			PrintService.printMenu(listMenu, "\nBooking Bengkel Menu");
			menuChoice = Validation.validasiNumberWithRange("\nMasukan Pilihan Menu:", "\nInput Harus Berupa Angka!", "^[0-9]+$", listMenu.length-1, 0);
			System.out.println(menuChoice);
			
			switch (menuChoice) {
				case 1:
				showCustomerInfo();
				break;
			case 2:
				performBooking();
				break;
			case 3:
				topUpSaldoCoin();
				break;
			case 4:
				showBookingOrders();
				break;
			default:
				System.out.println("Logout");
				isLooping = false;
				break;
			}
		} while (isLooping);
		
		
	}
	
	//Silahkan tambahkan kodingan untuk keperluan Menu Aplikasi

	private static void showCustomerInfo() {
        System.out.println("\nInformasi Customer");
        PrintService.printCustomerInfo(loggedInCustomer);
    }

	private static void performBooking() {
		System.out.println("\nBooking Bengkel");

        // Mendapatkan Vehicle ID dari pengguna
        String vehicleId = Validation.validasiInput("Masukkan Vehicle Id: ", "Input tidak valid!", "^[a-zA-Z0-9-]+$");

        // Mendapatkan layanan yang tersedia untuk Vehicle ID yang diberikan
        List<ItemService> availableServices = BengkelService.getAvailableServices(vehicleId);

        if (availableServices.isEmpty()) {
            System.out.println("Tidak ada layanan yang tersedia untuk kendaraan dengan ID " + vehicleId);
            return;
        }

		PrintService.printItemServices(availableServices);

        List<ItemService> selectedServices = new ArrayList<>();
        boolean addMoreServices = true;

        // Meminta pengguna untuk memilih layanan dan menyelesaikan proses pemesanan
        do {
            // Meminta pengguna untuk memilih layanan
            int selectedServiceIndex = Validation.validasiNumberWithRange("Silahkan masukkan nomor Service yang dipilih: ",
                    "Input harus berupa angka!", "^[0-9]+$", availableServices.size(), 1);
        
            // Menambah layanan yang dipilih ke dalam selectedServices
            ItemService selectedService = availableServices.get(selectedServiceIndex - 1);
            selectedServices.add(selectedService);
        
            // Memeriksa apakah pengguna ingin menambahkan lebih banyak layanan
            addMoreServices = Validation.validasiYesNo("Apakah anda ingin menambahkan Service Lainnya? (Y/T): ");
        } while (addMoreServices);
        

        // Silahkan tambahkan logika perhitungan pembayaran berdasarkan layanan yang dipilih
        double totalServicePrice = calculateTotalServicePrice(selectedServices);

        // Menampilkan metode pembayaran yang tersedia
        String paymentMethod = showPaymentMethods();

        // Silahkan tambahkan logika pembayaran berdasarkan metode yang dipilih
        double totalPayment = calculateTotalPayment(totalServicePrice, paymentMethod);

        // Membuat objek BookingOrder
        BookingOrder bookingOrder = createBookingOrder(vehicleId, selectedServices, paymentMethod, totalServicePrice, totalPayment);

        // Menambahkan booking order ke histori
        BookingOrderHistory.addBookingOrder(bookingOrder);

        // Menampilkan informasi booking yang berhasil
        System.out.println("\nBooking Berhasil!!!");
        System.out.println("Total Harga Service : " + totalServicePrice);
        System.out.println("Total Pembayaran : " + totalPayment);

		PrintService.printBookingInfo(bookingOrder);
    }

    private static void topUpSaldoCoin() {
		System.out.println("\nTop Up Saldo Coin");

		// Memeriksa apakah pelanggan yang masuk adalah member
		if (loggedInCustomer instanceof MemberCustomer) {
			MemberCustomer memberCustomer = (MemberCustomer) loggedInCustomer;
	
			// Mendapatkan jumlah top-up dari pengguna
			double topUpAmount = Validation.validasiNumberWithRange("Masukkan besaran Top Up: ", "Input tidak valid!", "^[0-9]+$", Integer.MAX_VALUE, 0);
	
			// Memperbarui saldo coin pelanggan member
			memberCustomer.setSaldoCoin(memberCustomer.getSaldoCoin() + topUpAmount);
	
			// Menampilkan informasi top-up yang berhasil
			System.out.println("\nTop Up Saldo Coin berhasil!");
			System.out.println("Saldo Coin saat ini: " + memberCustomer.getSaldoCoin());
		} else {
			System.out.println("\nMaaf fitur ini hanya untuk Member saja!");
		}
    }

    private static void showBookingOrders() {
		System.out.println("\nBooking Order Menu");

		List<BookingOrder> bookingOrders = BookingOrderRepository.getBookingOrdersByCustomer(loggedInCustomer);
		System.out.println(bookingOrders);
		if (bookingOrders.isEmpty()) {
			System.out.println("\nBelum ada Booking Order.");
		} else {
			System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------------");
			System.out.printf("|%-3s |%-18s |%-15s |%-15s |%-15s |%-15s |%-50s%n|", "No", "Booking Id", "Nama Customer", "Payment Method", "Total Service", "Total Payment", "List Service");
			System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------------");
			int counter = 1;
			for (BookingOrder bookingOrder : bookingOrders) {
				List<ItemService> selectedServices = bookingOrder.getSelectedServices();
				String serviceList = selectedServices.stream().map(ItemService::getServiceName).collect(Collectors.joining(", "));

				System.out.printf("|%-3d |%-18s |%-15s |%-15s |%-15s |%-15s |%-50s%n|", counter, bookingOrder.getBookingId(), bookingOrder.getCustomer().getName(),
						bookingOrder.getPaymentMethod(), bookingOrder.getTotalServicePrice(), bookingOrder.getTotalPayment(), serviceList);

				// PrintService.printBookingInfo(bookingOrder);
				counter++;
			}
			System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------------");

			// Meminta pengguna untuk kembali ke Main Menu
			Validation.validasiYesNo("Kembali ke Main Menu? (Y/T): ");
		}
    }

	private static double calculateTotalServicePrice(List<ItemService> selectedServices) {
		// Implementasi logika perhitungan total harga layanan
		return selectedServices.stream()
				.mapToDouble(ItemService::getPrice)
				.sum();
	}

	private static double calculateTotalPayment(double totalServicePrice, String paymentMethod) {
		// Implementasi logika perhitungan total pembayaran berdasarkan metode pembayaran
		if ("Saldo Coin".equals(paymentMethod)) {
			// Misalnya, memberikan diskon 10% untuk pembayaran dengan Saldo Coin
			return totalServicePrice * 0.9;
		} else {
			return totalServicePrice;
		}
	}

	private static BookingOrder createBookingOrder(String vehicleId, List<ItemService> 		selectedServices, String paymentMethod, double totalServicePrice, double totalPayment) {
		
		return new BookingOrder(vehicleId, loggedInCustomer, selectedServices, paymentMethod, totalServicePrice, totalPayment);
	}

	private static String showPaymentMethods() {
		System.out.println("\nSilahkan Pilih Metode Pembayaran:");
		System.out.println("1. Saldo Coin");
		System.out.println("2. Cash");
	
		int choice = Validation.validasiNumberWithRange("Pilih metode pembayaran:", "Input harus berupa angka!", "^[1-2]$", 2, 1);
	
		switch (choice) {
			case 1:
				return "Saldo Coin";
			case 2:
				return "Cash";
			default:
				// Ini seharusnya tidak terjadi jika validasi sudah benar
				System.out.println("Pilihan tidak valid. Pembayaran akan dianggap sebagai Cash.");
				return "Cash";
		}
	}
	
}
