import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;

public class RentalSystem {
	private static RentalSystem instance;
	
	private static final String VEHICLE_FILE = "vehicles.txt";
	private static final String CUSTOMER_FILE = "customers.txt";
	private static final String RECORD_FILE = "rental_records.txt";
	
    private List<Vehicle> vehicles = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private RentalHistory rentalHistory = new RentalHistory();
    
    private RentalSystem() {
        loadData();
    }

    public static RentalSystem getInstance() {
        if (instance == null) {
            instance = new RentalSystem();
        }
        return instance;
    }

    

    public boolean addVehicle(Vehicle vehicle) {
        if (findVehicleByPlate(vehicle.getLicensePlate()) != null) {
            System.out.println("Vehicle already exists.");
            return false;
        }
        vehicles.add(vehicle);
        saveVehicle(vehicle);
        return true;
    }

    public boolean addCustomer(Customer customer) {
        if (findCustomerById(customer.getCustomerId()) != null) {
            System.out.println("Customer already exists.");
            return false;
        }
        customers.add(customer);
        saveCustomer(customer);
        return true;
    }
    

    private void saveVehicle(Vehicle vehicle) {
        try (java.io.PrintWriter pw = new java.io.PrintWriter(
                new java.io.FileWriter(VEHICLE_FILE, true))) {

            pw.println(vehicle.getClass().getSimpleName() + "," +
                       vehicle.getLicensePlate() + "," +
                       vehicle.getMake() + "," +
                       vehicle.getModel() + "," +
                       vehicle.getYear() + "," +
                       vehicle.getStatus());

        } catch (Exception e) {
            System.out.println("Error saving vehicle: " + e.getMessage());
        }
    }

    private void saveCustomer(Customer customer) {
        try (java.io.PrintWriter pw = new java.io.PrintWriter(
                new java.io.FileWriter(CUSTOMER_FILE, true))) {

            pw.println(customer.getCustomerId() + "," + customer.getCustomerName());

        } catch (Exception e) {
            System.out.println("Error saving customer: " + e.getMessage());
        }
    }

    private void saveRecord(RentalRecord record) {
        try (java.io.PrintWriter pw = new java.io.PrintWriter(
                new java.io.FileWriter(RECORD_FILE, true))) {

            pw.println(record.getCustomer().getCustomerId() + "," +
                       record.getVehicle().getLicensePlate() + "," +
                       record.getRecordType() + "," +
                       record.getRecordDate() + "," +
                       record.getTotalAmount());

        } catch (Exception e) {
            System.out.println("Error saving record: " + e.getMessage());
        }
    }


    private void loadData() {
        loadVehiclesFromFile();
        loadCustomersFromFile();
        loadRecordsFromFile();
    }

    private void loadVehiclesFromFile() {
        java.io.File file = new java.io.File(VEHICLE_FILE);
        if (!file.exists()) return;

        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length < 6) continue;

                String type = p[0];
                String plate = p[1];
                String make = p[2];
                String model = p[3];
                int year = Integer.parseInt(p[4]);
                String status = p[5];

                Vehicle v = null;
                if (type.equalsIgnoreCase("Car")) {
                    v = new Car(plate, make, model, year, 5);
                }
                else if (type.equalsIgnoreCase("Minibus")) {
                    v = new Minibus(plate, make, model, year, false);
                }
                else if (type.equalsIgnoreCase("PickupTruck")) {
                    v = new PickupTruck(plate, make, model, year, 1.0, false);
                }
                else if (type.equalsIgnoreCase("SportCar")) {
                    v = new SportCar(plate, make, model, year, 2, 300, false);
                }

                if (v != null) {
                    v.setStatus(Vehicle.VehicleStatus.valueOf(status));
                    vehicles.add(v);
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading vehicles: " + e.getMessage());
        }
    }

    private void loadCustomersFromFile() {
        java.io.File file = new java.io.File(CUSTOMER_FILE);
        if (!file.exists()) return;

        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length < 2) continue;

                int id = Integer.parseInt(p[0]);
                String name = p[1];

                customers.add(new Customer(id, name));
            }
        } catch (Exception e) {
            System.out.println("Error loading customers: " + e.getMessage());
        }
    }

    private void loadRecordsFromFile() {
        java.io.File file = new java.io.File(RECORD_FILE);
        if (!file.exists()) return;

        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length < 5) continue;

                int customerId = Integer.parseInt(p[0]);
                String plate = p[1];
                String type = p[2];
                LocalDate date = LocalDate.parse(p[3]);
                double amount = Double.parseDouble(p[4]);

                Customer c = findCustomerById(customerId);
                Vehicle v = findVehicleByPlate(plate);

                if (c != null && v != null) {
                    rentalHistory.addRecord(new RentalRecord(v, c, date, amount, type));
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading records: " + e.getMessage());
        }
    }


    public void rentVehicle(Vehicle vehicle, Customer customer, LocalDate date, double amount) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Available) {
            vehicle.setStatus(Vehicle.VehicleStatus.Rented);
            RentalRecord record = new RentalRecord(vehicle, customer, date, amount, "RENT");
            rentalHistory.addRecord(record);
            saveRecord(record);
            System.out.println("Vehicle rented to " + customer.getCustomerName());
        }
        else {
            System.out.println("Vehicle is not available for renting.");
        }
    }

    public void returnVehicle(Vehicle vehicle, Customer customer, LocalDate date, double extraFees) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Rented) {
            vehicle.setStatus(Vehicle.VehicleStatus.Available);

            RentalRecord record = new RentalRecord(vehicle, customer, date, extraFees, "RETURN");
            rentalHistory.addRecord(record);
            saveRecord(record);

            System.out.println("Vehicle returned by " + customer.getCustomerName());
        }
        else {
            System.out.println("Vehicle is not rented.");
        }
    }

    public void displayVehicles(Vehicle.VehicleStatus status) {
        // Display appropriate title based on status
        if (status == null) {
            System.out.println("\n=== All Vehicles ===");
        } else {
            System.out.println("\n=== " + status + " Vehicles ===");
        }
        
        // Header with proper column widths
        System.out.printf("|%-16s | %-12s | %-12s | %-12s | %-6s | %-18s |%n", 
            " Type", "Plate", "Make", "Model", "Year", "Status");
        System.out.println("|--------------------------------------------------------------------------------------------|");
    	  
        boolean found = false;
        for (Vehicle vehicle : vehicles) {
            if (status == null || vehicle.getStatus() == status) {
                found = true;
                String vehicleType;
                if (vehicle instanceof Car) {
                    vehicleType = "Car";
                } else if (vehicle instanceof Minibus) {
                    vehicleType = "Minibus";
                } else if (vehicle instanceof PickupTruck) {
                    vehicleType = "Pickup Truck";
                } else {
                    vehicleType = "Unknown";
                }
                System.out.printf("| %-15s | %-12s | %-12s | %-12s | %-6d | %-18s |%n", 
                    vehicleType, vehicle.getLicensePlate(), vehicle.getMake(), vehicle.getModel(), vehicle.getYear(), vehicle.getStatus().toString());
            }
        }
        if (!found) {
            if (status == null) {
                System.out.println("  No Vehicles found.");
            } else {
                System.out.println("  No vehicles with Status: " + status);
            }
        }
        System.out.println();
    }

    public void displayAllCustomers() {
        for (Customer c : customers) {
            System.out.println("  " + c.toString());
        }
    }
    
    public void displayRentalHistory() {
        if (rentalHistory.getRentalHistory().isEmpty()) {
            System.out.println("  No rental history found.");
        } else {
            // Header with proper column widths
            System.out.printf("|%-10s | %-12s | %-20s | %-12s | %-12s |%n", 
                " Type", "Plate", "Customer", "Date", "Amount");
            System.out.println("|-------------------------------------------------------------------------------|");
            
            for (RentalRecord record : rentalHistory.getRentalHistory()) {                
                System.out.printf("| %-9s | %-12s | %-20s | %-12s | $%-11.2f |%n", 
                    record.getRecordType(), 
                    record.getVehicle().getLicensePlate(),
                    record.getCustomer().getCustomerName(),
                    record.getRecordDate().toString(),
                    record.getTotalAmount()
                );
            }
            System.out.println();
        }
    }
    
    public Vehicle findVehicleByPlate(String plate) {
        for (Vehicle v : vehicles) {
            if (v.getLicensePlate().equalsIgnoreCase(plate)) {
                return v;
            }
        }
        return null;
    }
    
    public Customer findCustomerById(int id) {
        for (Customer c : customers)
            if (c.getCustomerId() == id)
                return c;
        return null;
    }
}