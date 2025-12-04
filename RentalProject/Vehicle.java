public abstract class Vehicle {
    private String licensePlate;
    private String make;
    private String model;
    private int year;
    private VehicleStatus status;

    public enum VehicleStatus { Available, Held, Rented, UnderMaintenance, OutOfService }

    public static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase();
    }

    public Vehicle(String licensePlate, String make, String model, int year) {

        this.make = capitalize(make);
        this.model = capitalize(model);

        this.year = year;
        this.status = VehicleStatus.Available;

        setLicensePlate(licensePlate);
    }

    public Vehicle() {
        this(null, null, null, 0);
    }

    public void setLicensePlate(String plate) {
        if (plate == null) {
            this.licensePlate = null;
            return;
        }

        String upper = plate.toUpperCase();

        if (!upper.matches("^[A-Z]{3}[0-9]{3}$")) {
            throw new IllegalArgumentException("Invalid license plate format (must be AAA111)");
        }

        this.licensePlate = upper;
    }

    public void setStatus(VehicleStatus status) {
        this.status = status;
    }

    public String getLicensePlate() { return licensePlate; }

    public String getMake() { return make; }

    public String getModel() { return model;}

    public int getYear() { return year; }

    public VehicleStatus getStatus() { return status; }

    public String getInfo() {
        return "| " + licensePlate + " | " + make + " | " + model + " | " + year + " | " + status + " |";
    }

}
