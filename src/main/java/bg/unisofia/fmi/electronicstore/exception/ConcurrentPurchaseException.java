package bg.unisofia.fmi.electronicstore.exception;

public class ConcurrentPurchaseException extends RuntimeException {
    public ConcurrentPurchaseException(String message) {
        super(message);
    }
}
