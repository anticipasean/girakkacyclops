package cyclops.pure.typeclasses.taglessfinal;

import lombok.ToString;
import lombok.Value;

public class Cases {
    @Value @lombok.With @ToString
    public static class Account {
        double balance;
        long id;

        public Account debit(double amount){
            return withBalance(balance-amount);
        }
        public Account credit(double amount){
            return withBalance(balance+amount);
        }
    }





}
