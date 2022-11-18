import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Main {
    static void proceedExchange(int amount, List<List<Integer>> exchange, Stack<Integer> nominal) {
        while (!nominal.isEmpty() && nominal.peek() > amount) {
            nominal.pop();
        }
        if (!nominal.isEmpty()) {
            exchange.add(new ArrayList<>());
            int curNominal = nominal.peek();
            while (curNominal <= amount) {
                amount -= curNominal;
                exchange.get(exchange.size() - 1).add(curNominal);
            }
            nominal.pop();
            proceedExchange(amount, exchange, nominal);
        } else if (amount > 0) {
            exchange.clear();
        }
    }

    static public void main(String[] args) {
        Stack<Integer> nominal = new Stack<>();
        nominal.push(1);
        nominal.push(6);
        List<List<Integer>> exchange = new ArrayList<>();
        int amount = 100;
        proceedExchange(amount, exchange, nominal);
        if (exchange.isEmpty()) {
            System.out.println("Can not exchange");
        } else {
            System.out.printf("%d -> ", amount);
            for (int i = 0; i < exchange.size(); i++) {
                System.out.printf("%d[%d]", exchange.get(i).get(0), exchange.get(i).size());
                if (i != exchange.size() - 1) {
                    System.out.print(", ");
                }
            }
        }
    }
}
