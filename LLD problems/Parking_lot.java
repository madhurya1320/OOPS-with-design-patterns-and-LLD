import java.util.*;

// ---------- VEHICLE FACTORY ----------
interface Vehicle {
    String getType();
    int getSize(); // 1=Small, 2=Medium, 3=Large
}

class Bike implements Vehicle {
    public String getType() { return "Bike"; }
    public int getSize() { return 1; }
}
class Car implements Vehicle {
    public String getType() { return "Car"; }
    public int getSize() { return 2; }
}
class Truck implements Vehicle {
    public String getType() { return "Truck"; }
    public int getSize() { return 3; }
}

class VehicleFactory {
    public static Vehicle getVehicle(String type) {
        if (type.equalsIgnoreCase("Bike")) return new Bike();
        if (type.equalsIgnoreCase("Car")) return new Car();
        if (type.equalsIgnoreCase("Truck")) return new Truck();
        throw new IllegalArgumentException("Unknown vehicle type: " + type);
    }
}

// ---------- PARKING SPOT ----------
abstract class ParkingSpot {
    private int id;
    private boolean occupied;
    private Vehicle vehicle;
    private long entryTime; // track entry time

    public ParkingSpot(int id) {
        this.id = id;
        this.occupied = false;
        this.vehicle = null;
    }

    public int getId() { return id; }
    public boolean isOccupied() { return occupied; }
    public Vehicle getVehicle() { return vehicle; }
    public long getEntryTime() { return entryTime; }

    public void park(Vehicle v) {
        this.vehicle = v;
        this.occupied = true;
        this.entryTime = System.currentTimeMillis(); // record entry
        System.out.println(v.getType() + " parked at spot " + id);
    }

    public void leave(PaymentStrategy paymentStrategy) {
        if (!occupied) {
            System.out.println("Spot " + id + " is already empty!");
            return;
        }

        long exitTime = System.currentTimeMillis();
        double fee = BillingSystem.calculateFee(vehicle, entryTime, exitTime);

        paymentStrategy.pay(fee);
        System.out.println(vehicle.getType() + " leaving spot " + id);
        this.vehicle = null;
        this.occupied = false;
        System.out.println("Spot " + id + " is now free.");
    }

    abstract boolean canFit(Vehicle v);
}

class SmallSpot extends ParkingSpot {
    public SmallSpot(int id) { super(id); }
    public boolean canFit(Vehicle v) { return v.getSize() == 1; }
}
class MediumSpot extends ParkingSpot {
    public MediumSpot(int id) { super(id); }
    public boolean canFit(Vehicle v) { return v.getSize() <= 2; }
}
class LargeSpot extends ParkingSpot {
    public LargeSpot(int id) { super(id); }
    public boolean canFit(Vehicle v) { return true; }
}

// ---------- BILLING SYSTEM ----------
class BillingSystem {
    public static double calculateFee(Vehicle v, long entry, long exit) {
        long durationMillis = exit - entry;
        long durationHours = Math.max(1, durationMillis / (1000 * 60 * 60)); // at least 1 hour

        double rate;
        switch (v.getType()) {
            case "Bike": rate = 1.0; break;
            case "Car": rate = 2.0; break;
            case "Truck": rate = 3.0; break;
            default: rate = 2.0;
        }

        return rate * durationHours;
    }
}

// ---------- PAYMENT STRATEGY ----------
interface PaymentStrategy {
    void pay(double amount);
}

class CreditCardPayment implements PaymentStrategy {
    public void pay(double amount) {
        System.out.println("Paid $" + amount + " using Credit Card.");
    }
}
class PayPalPayment implements PaymentStrategy {
    public void pay(double amount) {
        System.out.println("Paid $" + amount + " using PayPal.");
    }
}
class CryptoPayment implements PaymentStrategy {
    public void pay(double amount) {
        System.out.println("Paid $" + amount + " using Cryptocurrency.");
    }
}

// ---------- PARKING LOT ----------
class ParkingLot {
    private List<ParkingSpot> spots = new ArrayList<>();

    public void addSpot(ParkingSpot spot) {
        spots.add(spot);
    }

    public ParkingSpot parkVehicle(Vehicle v) {
        for (ParkingSpot spot : spots) {
            if (!spot.isOccupied() && spot.canFit(v)) {
                spot.park(v);
                return spot;
            }
        }
        System.out.println("No spot available for " + v.getType());
        return null;
    }
}

// ---------- MAIN ----------
public class Main {
    public static void main(String[] args) throws InterruptedException {
        ParkingLot lot = new ParkingLot();

        // Add spots
        lot.addSpot(new SmallSpot(1));
        lot.addSpot(new MediumSpot(2));
        lot.addSpot(new LargeSpot(3));

        // Vehicles
        Vehicle bike = VehicleFactory.getVehicle("Bike");
        Vehicle car = VehicleFactory.getVehicle("Car");
        Vehicle truck = VehicleFactory.getVehicle("Truck");

        // Park vehicles
        ParkingSpot spot1 = lot.parkVehicle(bike);
        ParkingSpot spot2 = lot.parkVehicle(car);
        ParkingSpot spot3 = lot.parkVehicle(truck);

        // simulate time passing
        Thread.sleep(2000); // 2 seconds → still counted as 1 hour minimum

        // Leave with auto billing
        if (spot1 != null) spot1.leave(new CreditCardPayment());
        if (spot2 != null) spot2.leave(new PayPalPayment());
        if (spot3 != null) spot3.leave(new CryptoPayment());
    }
}
