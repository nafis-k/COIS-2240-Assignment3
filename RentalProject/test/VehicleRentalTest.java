import static org.junit.jupiter.api.Assertions.*;

import Car;
import Customer;
import RentalSystem;
import Vehicle;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.time.LocalDate;

public class VehicleRentalTest {

    @Test
    public void testLicensePlateValidation() {
        assertDoesNotThrow(() -> new Car("Toyota", "Corolla", 2019, 5));
        assertDoesNotThrow(() -> new Car("Honda", "Civic", 2020, 4));
        assertDoesNotThrow(() -> new Car("Ford", "Focus", 2021, 4));

        assertThrows(IllegalArgumentException.class, () -> new Car("", "Test", "Car", 2024, 2));
        assertThrows(IllegalArgumentException.class, () -> new Car(null, "Test", "Car", 2024, 2));
        assertThrows(IllegalArgumentException.class, () -> new Car("AAA1000", "Test", "Car", 2024, 2));
        assertThrows(IllegalArgumentException.class, () -> new Car("ZZZ999", "Test", "Car", 2024, 2));
    }

    @Test
    public void testRentAndReturnVehicle() {
        RentalSystem system = RentalSystem.getInstance();

        Customer c = new Customer(9999, "Test User");
        Vehicle v = new Car("TestMake", "TestModel", 2024, 4);

        system.addCustomer(c);
        system.addVehicle(v);

        v.setLicensePlate("TES123");

        assertEquals(Vehicle.VehicleStatus.Available, v.getStatus());

        system.rentVehicle(v, c, LocalDate.now(), 100.0);
        assertEquals(Vehicle.VehicleStatus.Rented, v.getStatus());

        system.returnVehicle(v, c, LocalDate.now(), 0.0);
        assertEquals(Vehicle.VehicleStatus.Available, v.getStatus());
    }

    @Test
    public void testSingletonRentalSystem() throws Exception {
        Constructor<RentalSystem> ctor = RentalSystem.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(ctor.getModifiers()));

        RentalSystem a = RentalSystem.getInstance();
        RentalSystem b = RentalSystem.getInstance();

        assertSame(a, b);
    }
}
