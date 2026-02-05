package Project1_6481328;

public class Installment {
    private final int order;
    private final double percent;

    public Installment(String[] c) {
        this.order = Integer.parseInt(c[0]);
        this.percent = Double.parseDouble(c[1]);
    }

    public int getOrder()       { return this.order; }
    public double getPercent()  { return this.percent; }

    public static void printHeader(int totalInstallments) {
        System.out.printf("%d installments of payment\n", totalInstallments);
    }

    public void print() {
        System.out.printf("(%d) %.1f %% of total%n", this.order, this.percent);
    }
}
