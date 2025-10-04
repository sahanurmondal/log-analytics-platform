package miscellaneous.airbnb;

import java.util.*;
import java.util.stream.Collectors;
import java.util.Calendar;
import java.util.Date;

/**
 * Custom Question: Design Airbnb Booking System
 * 
 * Description:
 * Design a booking system for property rentals that supports:
 * - Property listing and search
 * - Availability management
 * - Booking creation and management
 * - Pricing strategy
 * - Review system
 * 
 * Company: Airbnb
 * Difficulty: Hard
 * Asked: System design interviews 2023-2024
 */
public class DesignBookingSystem {

    enum PropertyType {
        APARTMENT, HOUSE, VILLA, ROOM
    }

    enum BookingStatus {
        PENDING, CONFIRMED, CANCELLED, COMPLETED
    }

    class Property {
        String id;
        String hostId;
        String title;
        String description;
        PropertyType type;
        String address;
        double latitude;
        double longitude;
        int maxGuests;
        List<String> amenities;
        double basePrice;
        Map<String, Double> seasonalPricing;
        List<String> photos;
        double rating;
        int reviewCount;

        Property(String hostId, String title, PropertyType type, String address, int maxGuests, double basePrice) {
            this.id = UUID.randomUUID().toString();
            this.hostId = hostId;
            this.title = title;
            this.type = type;
            this.address = address;
            this.maxGuests = maxGuests;
            this.basePrice = basePrice;
            this.amenities = new ArrayList<>();
            this.seasonalPricing = new HashMap<>();
            this.photos = new ArrayList<>();
            this.rating = 0.0;
            this.reviewCount = 0;
        }
    }

    class Booking {
        String id;
        String propertyId;
        String guestId;
        Date checkIn;
        Date checkOut;
        int guests;
        double totalPrice;
        BookingStatus status;
        long createdAt;

        Booking(String propertyId, String guestId, Date checkIn, Date checkOut, int guests) {
            this.id = UUID.randomUUID().toString();
            this.propertyId = propertyId;
            this.guestId = guestId;
            this.checkIn = checkIn;
            this.checkOut = checkOut;
            this.guests = guests;
            this.status = BookingStatus.PENDING;
            this.createdAt = System.currentTimeMillis();
        }
    }

    class AvailabilityManager {
        private Map<String, Map<String, Boolean>> propertyAvailability = new HashMap<>();

        public void setAvailability(String propertyId, Date date, boolean available) {
            String dateKey = formatDate(date);
            propertyAvailability.computeIfAbsent(propertyId, k -> new HashMap<>()).put(dateKey, available);
        }

        public boolean isAvailable(String propertyId, Date checkIn, Date checkOut) {
            Map<String, Boolean> availability = propertyAvailability.get(propertyId);
            if (availability == null)
                return true;

            Calendar cal = Calendar.getInstance();
            cal.setTime(checkIn);

            while (!cal.getTime().after(checkOut)) {
                String dateKey = formatDate(cal.getTime());
                if (availability.getOrDefault(dateKey, true) == false) {
                    return false;
                }
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }

            return true;
        }

        public void blockDates(String propertyId, Date checkIn, Date checkOut) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(checkIn);

            while (!cal.getTime().after(checkOut)) {
                setAvailability(propertyId, cal.getTime(), false);
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }
        }

        private String formatDate(Date date) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return String.format("%04d-%02d-%02d",
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.DAY_OF_MONTH));
        }
    }

    class PricingEngine {
        public double calculatePrice(Property property, Date checkIn, Date checkOut, int guests) {
            double basePrice = property.basePrice;
            int nights = calculateNights(checkIn, checkOut);

            // Seasonal pricing
            double seasonalMultiplier = getSeasonalMultiplier(property, checkIn, checkOut);

            // Demand-based pricing
            double demandMultiplier = getDemandMultiplier(property, checkIn, checkOut);

            // Guest count adjustment
            double guestMultiplier = guests > 2 ? 1.0 + (guests - 2) * 0.1 : 1.0;

            return basePrice * nights * seasonalMultiplier * demandMultiplier * guestMultiplier;
        }

        private int calculateNights(Date checkIn, Date checkOut) {
            long diffInMs = checkOut.getTime() - checkIn.getTime();
            return (int) (diffInMs / (1000 * 60 * 60 * 24));
        }

        private double getSeasonalMultiplier(Property property, Date checkIn, Date checkOut) {
            // Simplified seasonal pricing logic
            Calendar cal = Calendar.getInstance();
            cal.setTime(checkIn);
            int month = cal.get(Calendar.MONTH);

            // Summer months (June-August) have higher pricing
            if (month >= 5 && month <= 7) {
                return 1.3;
            }
            // Winter months (December-February) have lower pricing
            else if (month == 11 || month <= 1) {
                return 0.8;
            }

            return 1.0;
        }

        private double getDemandMultiplier(Property property, Date checkIn, Date checkOut) {
            // Calculate demand based on booking density
            long bookingCount = bookings.values().stream()
                    .filter(b -> b.propertyId.equals(property.id))
                    .filter(b -> datesOverlap(b.checkIn, b.checkOut, checkIn, checkOut))
                    .count();

            return Math.min(1.0 + bookingCount * 0.1, 2.0);
        }

        private boolean datesOverlap(Date start1, Date end1, Date start2, Date end2) {
            return start1.before(end2) && end1.after(start2);
        }
    }

    class SearchEngine {
        public List<Property> searchProperties(String location, Date checkIn, Date checkOut,
                int guests, PropertyType type, double maxPrice) {
            return properties.values().stream()
                    .filter(p -> location == null || p.address.toLowerCase().contains(location.toLowerCase()))
                    .filter(p -> type == null || p.type == type)
                    .filter(p -> p.maxGuests >= guests)
                    .filter(p -> availabilityManager.isAvailable(p.id, checkIn, checkOut))
                    .filter(p -> {
                        double price = pricingEngine.calculatePrice(p, checkIn, checkOut, guests);
                        return maxPrice == 0 || price <= maxPrice;
                    })
                    .sorted((p1, p2) -> Double.compare(p2.rating, p1.rating))
                    .collect(Collectors.toList());
        }
    }

    private Map<String, Property> properties = new HashMap<>();
    private Map<String, Booking> bookings = new HashMap<>();
    private AvailabilityManager availabilityManager = new AvailabilityManager();
    private PricingEngine pricingEngine = new PricingEngine();
    private SearchEngine searchEngine = new SearchEngine();

    public String createBooking(String propertyId, String guestId, Date checkIn, Date checkOut, int guests) {
        Property property = properties.get(propertyId);
        if (property == null || property.maxGuests < guests) {
            return null;
        }

        if (!availabilityManager.isAvailable(propertyId, checkIn, checkOut)) {
            return null;
        }

        Booking booking = new Booking(propertyId, guestId, checkIn, checkOut, guests);
        booking.totalPrice = pricingEngine.calculatePrice(property, checkIn, checkOut, guests);

        bookings.put(booking.id, booking);
        availabilityManager.blockDates(propertyId, checkIn, checkOut);

        return booking.id;
    }

    public boolean confirmBooking(String bookingId) {
        Booking booking = bookings.get(bookingId);
        if (booking != null && booking.status == BookingStatus.PENDING) {
            booking.status = BookingStatus.CONFIRMED;
            return true;
        }
        return false;
    }

    public boolean cancelBooking(String bookingId) {
        Booking booking = bookings.get(bookingId);
        if (booking != null && booking.status != BookingStatus.COMPLETED) {
            booking.status = BookingStatus.CANCELLED;

            // Free up the dates
            Calendar cal = Calendar.getInstance();
            cal.setTime(booking.checkIn);

            while (!cal.getTime().after(booking.checkOut)) {
                availabilityManager.setAvailability(booking.propertyId, cal.getTime(), true);
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }

            return true;
        }
        return false;
    }

    public List<Property> searchProperties(String location, Date checkIn, Date checkOut,
            int guests, PropertyType type, double maxPrice) {
        return searchEngine.searchProperties(location, checkIn, checkOut, guests, type, maxPrice);
    }

    public static void main(String[] args) {
        DesignBookingSystem bookingSystem = new DesignBookingSystem();

        // Create property
        Property property = bookingSystem.new Property("host1", "Beautiful Apartment", PropertyType.APARTMENT,
                "New York, NY", 4, 150.0);
        bookingSystem.properties.put(property.id, property);

        // Create booking
        Calendar cal = Calendar.getInstance();
        Date checkIn = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 3);
        Date checkOut = cal.getTime();

        String bookingId = bookingSystem.createBooking(property.id, "guest1", checkIn, checkOut, 2);
        System.out.println("Booking created: " + bookingId);

        // Confirm booking
        boolean confirmed = bookingSystem.confirmBooking(bookingId);
        System.out.println("Booking confirmed: " + confirmed);

        // Search properties
        List<Property> results = bookingSystem.searchProperties("New York", checkIn, checkOut, 2, null, 0);
        System.out.println("Search results: " + results.size());
    }
}
