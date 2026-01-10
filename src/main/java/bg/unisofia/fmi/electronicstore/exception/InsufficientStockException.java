package bg.unisofia.fmi.electronicstore.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String productName, int requested, int available) {
        super("Not enough stock for " + productName + ". Requested: " + requested + ", Available: " + available);
    }
}
