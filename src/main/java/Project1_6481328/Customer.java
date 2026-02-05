package Project1_6481328;

public class Customer {
    private final String customerId;
    private double cashback;

    public Customer(String customerId) {
        this.customerId = customerId;
        this.cashback = 0.0;
    }

    public String getCustomerId() { return customerId; }
    public double getCashback()   { return cashback; }

    public void setCashback(double cashback) { this.cashback = cashback; }
    public void addCashback(double added)    { this.cashback += added; }
}
