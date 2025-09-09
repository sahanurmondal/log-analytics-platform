package design.hard;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Design Online Auction System
 *
 * Description: Design an auction system that supports:
 * - Create auctions with different types (English, Dutch, Sealed Bid)
 * - Place bids and handle bid validation
 * - Automatic auction closing and winner determination
 * - Bid history and notifications
 * 
 * Constraints:
 * - Support multiple concurrent auctions
 * - Handle bid conflicts and validation
 * - Real-time updates to bidders
 *
 * Follow-up:
 * - How to handle high-frequency bidding?
 * - Fraud detection mechanisms?
 * 
 * Time Complexity: O(log n) for bid placement, O(1) for most operations
 * Space Complexity: O(auctions * bids)
 * 
 * Company Tags: eBay, Amazon, Google
 */
public class DesignAuctionSystem {

    enum AuctionType {
        ENGLISH, // Ascending price auction
        DUTCH, // Descending price auction
        SEALED_BID // Single sealed bid auction
    }

    enum AuctionStatus {
        SCHEDULED, ACTIVE, PAUSED, COMPLETED, CANCELLED
    }

    enum BidStatus {
        PLACED, OUTBID, WINNING, WON, LOST, INVALID
    }

    class User {
        String userId;
        String username;
        double balance;
        double lockedFunds;
        Set<String> watchList;
        List<String> bidHistory;

        User(String userId, String username, double balance) {
            this.userId = userId;
            this.username = username;
            this.balance = balance;
            this.lockedFunds = 0.0;
            this.watchList = new HashSet<>();
            this.bidHistory = new ArrayList<>();
        }

        boolean hasBalance(double amount) {
            return balance - lockedFunds >= amount;
        }

        void lockFunds(double amount) {
            if (hasBalance(amount)) {
                lockedFunds += amount;
            }
        }

        void unlockFunds(double amount) {
            lockedFunds = Math.max(0, lockedFunds - amount);
        }

        void deductBalance(double amount) {
            balance -= amount;
            unlockFunds(amount);
        }
    }

    class Bid {
        String bidId;
        String auctionId;
        String userId;
        double amount;
        long timestamp;
        BidStatus status;
        boolean isAutoBid;
        double maxAmount; // For auto-bidding

        Bid(String auctionId, String userId, double amount) {
            this.bidId = UUID.randomUUID().toString();
            this.auctionId = auctionId;
            this.userId = userId;
            this.amount = amount;
            this.timestamp = System.currentTimeMillis();
            this.status = BidStatus.PLACED;
            this.isAutoBid = false;
        }

        Bid(String auctionId, String userId, double amount, double maxAmount) {
            this(auctionId, userId, amount);
            this.isAutoBid = true;
            this.maxAmount = maxAmount;
        }
    }

    class Auction {
        String auctionId;
        String sellerId;
        String title;
        String description;
        AuctionType type;
        AuctionStatus status;
        double startingPrice;
        double currentPrice;
        double reservePrice;
        double buyNowPrice;
        double bidIncrement;
        long startTime;
        long endTime;
        String currentWinnerId;
        List<Bid> bidHistory;
        Set<String> watchers;

        Auction(String sellerId, String title, AuctionType type, double startingPrice, long duration) {
            this.auctionId = UUID.randomUUID().toString();
            this.sellerId = sellerId;
            this.title = title;
            this.type = type;
            this.status = AuctionStatus.SCHEDULED;
            this.startingPrice = startingPrice;
            this.currentPrice = startingPrice;
            this.reservePrice = 0.0;
            this.buyNowPrice = 0.0;
            this.bidIncrement = startingPrice * 0.05; // 5% of starting price
            this.startTime = System.currentTimeMillis();
            this.endTime = startTime + duration;
            this.bidHistory = new ArrayList<>();
            this.watchers = new HashSet<>();
        }

        boolean isActive() {
            long now = System.currentTimeMillis();
            return status == AuctionStatus.ACTIVE && now >= startTime && now < endTime;
        }

        boolean hasEnded() {
            return System.currentTimeMillis() >= endTime || status == AuctionStatus.COMPLETED;
        }

        double getMinimumBid() {
            return currentPrice + bidIncrement;
        }

        void addWatcher(String userId) {
            watchers.add(userId);
        }

        void removeWatcher(String userId) {
            watchers.remove(userId);
        }
    }

    interface AuctionListener {
        void onBidPlaced(String auctionId, Bid bid);

        void onAuctionEnded(String auctionId, String winnerId);

        void onOutbid(String userId, String auctionId, double newHighBid);
    }

    private Map<String, User> users;
    private Map<String, Auction> auctions;
    private Map<String, List<Bid>> userBids;
    private List<AuctionListener> listeners;
    private ScheduledExecutorService scheduler;

    public DesignAuctionSystem() {
        users = new HashMap<>();
        auctions = new HashMap<>();
        userBids = new HashMap<>();
        listeners = new ArrayList<>();
        scheduler = Executors.newScheduledThreadPool(5);

        // Start auction monitoring
        startAuctionMonitoring();
    }

    public void registerUser(String userId, String username, double initialBalance) {
        users.put(userId, new User(userId, username, initialBalance));
        userBids.put(userId, new ArrayList<>());
    }

    public String createAuction(String sellerId, String title, String description,
            AuctionType type, double startingPrice, long durationMs) {
        User seller = users.get(sellerId);
        if (seller == null) {
            throw new IllegalArgumentException("Seller not found");
        }

        Auction auction = new Auction(sellerId, title, type, startingPrice, durationMs);
        auction.description = description;
        auction.status = AuctionStatus.ACTIVE;

        auctions.put(auction.auctionId, auction);

        // Schedule auction end
        scheduler.schedule(() -> endAuction(auction.auctionId), durationMs, TimeUnit.MILLISECONDS);

        return auction.auctionId;
    }

    public boolean placeBid(String userId, String auctionId, double bidAmount) {
        return placeBid(userId, auctionId, bidAmount, false, 0.0);
    }

    public boolean placeAutoBid(String userId, String auctionId, double bidAmount, double maxAmount) {
        return placeBid(userId, auctionId, bidAmount, true, maxAmount);
    }

    private boolean placeBid(String userId, String auctionId, double bidAmount,
            boolean isAutoBid, double maxAmount) {
        User user = users.get(userId);
        Auction auction = auctions.get(auctionId);

        if (user == null || auction == null) {
            return false;
        }

        // Validation
        if (!auction.isActive()) {
            return false;
        }

        if (userId.equals(auction.sellerId)) {
            return false; // Seller cannot bid on own auction
        }

        double requiredAmount = isAutoBid ? maxAmount : bidAmount;
        if (!user.hasBalance(requiredAmount)) {
            return false; // Insufficient funds
        }

        synchronized (auction) {
            // Check minimum bid requirement
            if (bidAmount < auction.getMinimumBid()) {
                return false;
            }

            // Handle previous winner
            if (auction.currentWinnerId != null) {
                User previousWinner = users.get(auction.currentWinnerId);
                if (previousWinner != null) {
                    // Unlock previous winner's funds
                    previousWinner.unlockFunds(auction.currentPrice);
                    notifyOutbid(auction.currentWinnerId, auctionId, bidAmount);
                }
            }

            // Place new bid
            Bid bid = isAutoBid ? new Bid(auctionId, userId, bidAmount, maxAmount)
                    : new Bid(auctionId, userId, bidAmount);

            auction.bidHistory.add(bid);
            auction.currentPrice = bidAmount;
            auction.currentWinnerId = userId;

            // Lock funds
            user.lockFunds(requiredAmount);
            user.bidHistory.add(bid.bidId);
            userBids.get(userId).add(bid);

            // Handle auto-bidding logic
            handleAutoBidding(auction, bid);

            // Extend auction if bid placed in last 5 minutes
            extendAuctionIfNeeded(auction);

            // Notify listeners
            notifyBidPlaced(auctionId, bid);

            return true;
        }
    }

    private void handleAutoBidding(Auction auction, Bid newBid) {
        // Find other auto-bidders and potentially increase their bids
        for (Bid existingBid : auction.bidHistory) {
            if (existingBid.isAutoBid &&
                    !existingBid.userId.equals(newBid.userId) &&
                    existingBid.maxAmount > auction.currentPrice) {

                double nextBid = Math.min(
                        existingBid.maxAmount,
                        auction.currentPrice + auction.bidIncrement);

                if (nextBid > auction.currentPrice) {
                    // Place automatic counter-bid
                    Bid autoBid = new Bid(auction.auctionId, existingBid.userId, nextBid);
                    autoBid.isAutoBid = true;

                    auction.bidHistory.add(autoBid);
                    auction.currentPrice = nextBid;
                    auction.currentWinnerId = existingBid.userId;

                    // Update locked funds
                    User autoUser = users.get(existingBid.userId);
                    if (autoUser != null) {
                        autoUser.unlockFunds(existingBid.amount);
                        autoUser.lockFunds(nextBid);
                    }

                    notifyBidPlaced(auction.auctionId, autoBid);
                    break;
                }
            }
        }
    }

    private void extendAuctionIfNeeded(Auction auction) {
        long now = System.currentTimeMillis();
        long timeRemaining = auction.endTime - now;

        // Extend by 5 minutes if bid placed in last 5 minutes
        if (timeRemaining < 5 * 60 * 1000) { // 5 minutes in milliseconds
            auction.endTime = now + 5 * 60 * 1000;
        }
    }

    public void buyNow(String userId, String auctionId) {
        User user = users.get(userId);
        Auction auction = auctions.get(auctionId);

        if (user == null || auction == null || auction.buyNowPrice <= 0) {
            return;
        }

        if (!user.hasBalance(auction.buyNowPrice)) {
            return;
        }

        synchronized (auction) {
            if (auction.isActive()) {
                // End auction immediately with buy now
                auction.currentPrice = auction.buyNowPrice;
                auction.currentWinnerId = userId;
                auction.status = AuctionStatus.COMPLETED;

                user.deductBalance(auction.buyNowPrice);

                Bid buyNowBid = new Bid(auctionId, userId, auction.buyNowPrice);
                auction.bidHistory.add(buyNowBid);

                endAuction(auctionId);
            }
        }
    }

    private void endAuction(String auctionId) {
        Auction auction = auctions.get(auctionId);
        if (auction == null || auction.status == AuctionStatus.COMPLETED) {
            return;
        }

        synchronized (auction) {
            auction.status = AuctionStatus.COMPLETED;

            if (auction.currentWinnerId != null &&
                    auction.currentPrice >= auction.reservePrice) {

                // Successful auction
                User winner = users.get(auction.currentWinnerId);
                User seller = users.get(auction.sellerId);

                if (winner != null && seller != null) {
                    // Transfer payment
                    winner.deductBalance(auction.currentPrice);
                    seller.balance += auction.currentPrice * 0.95; // 5% platform fee

                    // Update bid statuses
                    for (Bid bid : auction.bidHistory) {
                        if (bid.userId.equals(auction.currentWinnerId)) {
                            bid.status = BidStatus.WON;
                        } else {
                            bid.status = BidStatus.LOST;
                            // Unlock funds for losing bidders
                            User loser = users.get(bid.userId);
                            if (loser != null) {
                                loser.unlockFunds(bid.amount);
                            }
                        }
                    }
                }

                notifyAuctionEnded(auctionId, auction.currentWinnerId);
            } else {
                // Reserve not met or no bids
                auction.currentWinnerId = null;

                // Unlock all funds
                for (Bid bid : auction.bidHistory) {
                    bid.status = BidStatus.LOST;
                    User bidder = users.get(bid.userId);
                    if (bidder != null) {
                        bidder.unlockFunds(bid.amount);
                    }
                }

                notifyAuctionEnded(auctionId, null);
            }
        }
    }

    public void addToWatchList(String userId, String auctionId) {
        User user = users.get(userId);
        Auction auction = auctions.get(auctionId);

        if (user != null && auction != null) {
            user.watchList.add(auctionId);
            auction.addWatcher(userId);
        }
    }

    public List<Auction> searchAuctions(String query, AuctionType type, AuctionStatus status) {
        return auctions.values().stream()
                .filter(auction -> query == null ||
                        auction.title.toLowerCase().contains(query.toLowerCase()) ||
                        auction.description.toLowerCase().contains(query.toLowerCase()))
                .filter(auction -> type == null || auction.type == type)
                .filter(auction -> status == null || auction.status == status)
                .sorted((a, b) -> Long.compare(a.endTime, b.endTime))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public Map<String, Object> getAuctionDetails(String auctionId) {
        Auction auction = auctions.get(auctionId);
        if (auction == null) {
            return null;
        }

        Map<String, Object> details = new HashMap<>();
        details.put("auctionId", auction.auctionId);
        details.put("title", auction.title);
        details.put("description", auction.description);
        details.put("type", auction.type);
        details.put("status", auction.status);
        details.put("currentPrice", auction.currentPrice);
        details.put("bidCount", auction.bidHistory.size());
        details.put("timeRemaining", Math.max(0, auction.endTime - System.currentTimeMillis()));
        details.put("watcherCount", auction.watchers.size());

        if (auction.currentWinnerId != null) {
            User winner = users.get(auction.currentWinnerId);
            details.put("currentWinner", winner != null ? winner.username : "Unknown");
        }

        return details;
    }

    public void addListener(AuctionListener listener) {
        listeners.add(listener);
    }

    private void notifyBidPlaced(String auctionId, Bid bid) {
        for (AuctionListener listener : listeners) {
            try {
                listener.onBidPlaced(auctionId, bid);
            } catch (Exception e) {
                System.err.println("Error notifying listener: " + e.getMessage());
            }
        }
    }

    private void notifyAuctionEnded(String auctionId, String winnerId) {
        for (AuctionListener listener : listeners) {
            try {
                listener.onAuctionEnded(auctionId, winnerId);
            } catch (Exception e) {
                System.err.println("Error notifying listener: " + e.getMessage());
            }
        }
    }

    private void notifyOutbid(String userId, String auctionId, double newHighBid) {
        for (AuctionListener listener : listeners) {
            try {
                listener.onOutbid(userId, auctionId, newHighBid);
            } catch (Exception e) {
                System.err.println("Error notifying listener: " + e.getMessage());
            }
        }
    }

    private void startAuctionMonitoring() {
        scheduler.scheduleWithFixedDelay(() -> {
            long now = System.currentTimeMillis();

            for (Auction auction : auctions.values()) {
                if (auction.status == AuctionStatus.ACTIVE && now >= auction.endTime) {
                    endAuction(auction.auctionId);
                }
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    public void shutdown() {
        scheduler.shutdown();
    }

    public static void main(String[] args) throws InterruptedException {
        DesignAuctionSystem auctionSystem = new DesignAuctionSystem();

        // Register users
        auctionSystem.registerUser("seller1", "John Seller", 0);
        auctionSystem.registerUser("bidder1", "Alice Bidder", 1000);
        auctionSystem.registerUser("bidder2", "Bob Bidder", 1500);
        auctionSystem.registerUser("bidder3", "Charlie Bidder", 2000);

        // Add listener
        auctionSystem.addListener(new AuctionListener() {
            @Override
            public void onBidPlaced(String auctionId, Bid bid) {
                System.out.println("New bid placed on " + auctionId + ": $" + bid.amount + " by " +
                        auctionSystem.users.get(bid.userId).username);
            }

            @Override
            public void onAuctionEnded(String auctionId, String winnerId) {
                if (winnerId != null) {
                    System.out.println("Auction " + auctionId + " won by " +
                            auctionSystem.users.get(winnerId).username);
                } else {
                    System.out.println("Auction " + auctionId + " ended with no winner");
                }
            }

            @Override
            public void onOutbid(String userId, String auctionId, double newHighBid) {
                System.out.println("User " + auctionSystem.users.get(userId).username +
                        " was outbid on " + auctionId + ". New high bid: $" + newHighBid);
            }
        });

        // Create auction (5 second duration for testing)
        String auctionId = auctionSystem.createAuction("seller1", "Vintage Watch",
                "Beautiful vintage watch in excellent condition",
                AuctionType.ENGLISH, 100.0, 5000);

        System.out.println("Created auction: " + auctionId);
        System.out.println("Auction details: " + auctionSystem.getAuctionDetails(auctionId));

        // Place bids
        auctionSystem.placeBid("bidder1", auctionId, 150.0);
        Thread.sleep(500);

        auctionSystem.placeBid("bidder2", auctionId, 200.0);
        Thread.sleep(500);

        // Place auto-bid
        auctionSystem.placeAutoBid("bidder3", auctionId, 250.0, 400.0);
        Thread.sleep(500);

        auctionSystem.placeBid("bidder1", auctionId, 300.0);

        // Wait for auction to end
        Thread.sleep(6000);

        System.out.println("\nFinal auction details: " + auctionSystem.getAuctionDetails(auctionId));

        // Show user balances
        for (User user : auctionSystem.users.values()) {
            System.out.println(user.username + " - Balance: $" + user.balance +
                    ", Locked: $" + user.lockedFunds);
        }

        auctionSystem.shutdown();
    }
}
