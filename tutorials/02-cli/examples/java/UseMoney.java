// Java consumiendo la librería Kotlin: compila contra money.jar y, para correr, necesita además kotlin-stdlib.jar.
import dev.raul.oskar.learning.kotlin.tutorials.cli.money.Money;
import dev.raul.oskar.learning.kotlin.tutorials.cli.money.Monies;

public class UseMoney {
    public static void main(String[] args) {
        Money total = Monies.usd("99.99").plus(Monies.usd("0.01"));   // operator plus = método plus()
        System.out.println("desde Java: " + total + " (amount=" + total.getAmount() + ")");
    }
}
