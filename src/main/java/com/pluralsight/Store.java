package com.pluralsight;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class Store {

    public static void main(String[] args) {

        // Create lists for inventory and the shopping cart
        ArrayList<Product> inventory = new ArrayList<>();
        ArrayList<Product> cart = new ArrayList<>();

        // Load inventory from the data file (pipe-delimited: SKU|Product Name|Price|Department)
        loadInventory("products.csv", inventory);

        Scanner scanner = new Scanner(System.in);

        // Turned mainMenu into it's own method so it's easier to go back to it from other submenus
        mainMenu(inventory, cart, scanner);
        scanner.close();
    }

    // Loads inventory, splits each line and creates a new product with the values taken after the .split
    public static void loadInventory(String fileName, ArrayList<Product> inventory) {
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            bufferedReader.readLine();

            while ((line = bufferedReader.readLine()) != null) {
                String[] parts = line.split("\\|");
                String id = parts[0];
                String name = parts[1];
                double price = Double.parseDouble(parts[2]);
                String department = parts[3];

                Product newProduct = new Product(id, name, price, department);
                inventory.add(newProduct);
            }
            System.out.println(inventory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // mainMenu, no real changes from the started code beside a System.exit to eliminate some weird edge cases.
    public static void mainMenu(ArrayList<Product> inventory,
                                       ArrayList<Product> cart,
                                       Scanner scanner) {
        int choice = -1;
        while (choice != 3) {
            System.out.println("\nWelcome to the Online Store!");
            System.out.println("1. Show Products");
            System.out.println("2. Show Cart");
            System.out.println("3. Exit");
            System.out.print("Your choice: ");

            if (!scanner.hasNextInt()) {
                System.out.println("Please enter 1, 2, or 3.");
                scanner.nextLine();                 // discard bad input
                continue;
            }
            choice = scanner.nextInt();
            scanner.nextLine();                     // clear newline

            switch (choice) {
                case 1 -> displayProducts(inventory, cart, scanner);
                case 2 -> displayCart(inventory, cart, scanner);
                case 3 -> {
                    System.out.println("\nThank you for shopping with us!");
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice!");
            }
        }
    }


    // Displays all the products, since the list is long this list is separated from its subsequent menu since I dont want it printing every time.
    public static void displayProducts(ArrayList<Product> inventory,
                                       ArrayList<Product> cart,
                                       Scanner scanner) {

        System.out.printf("\n%-10s %-40s %-10s %-15s%n",
                "ID", "NAME", "PRICE", "DEPARTMENT");
        System.out.println("--------------------------------------------------------------------------");

        for (Product product : inventory) {
            System.out.printf("%-10s %-40s $%-9.2f %-15s%n",
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    product.getDepartment());
        }

        displayProductsMenu(inventory, cart, scanner);
    }

    // Reworked main menu as the product display menu since its while loop makes it easier to add multiple products at once.
    public static void displayProductsMenu(ArrayList<Product> inventory,
                                           ArrayList<Product> cart, Scanner scanner) {
        int choice = -1;
        while (choice != 3) {
            System.out.println("\nWhat would you like to do?");
            System.out.println("1. Add product to cart");
            System.out.println("2. Go back to the main menu");
            System.out.println("3. Exit");
            System.out.print("Your choice: ");

            if (!scanner.hasNextInt()) {
                System.out.println("Please enter 1, 2, or 3.");
                scanner.nextLine();                 // discard bad input
                continue;
            }
            choice = scanner.nextInt();
            scanner.nextLine();                     // clear newline

            switch (choice) {
                case 1 -> addToCart(inventory, cart, scanner);
                case 2 -> mainMenu(inventory, cart, scanner);
                case 3 -> {
                    System.out.println("\nThank you for shopping with us!");
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    // User inputs ID and using the findProductByID method either adds to card or displays error message
    public static void addToCart(ArrayList<Product> inventory,
                                 ArrayList<Product> cart, Scanner scanner) {
        System.out.print("\nPlease write the ID of the product you'd like to add to your cart: ");
        String selectedID = scanner.nextLine();

        Product foundProduct = findProductById(selectedID, inventory);
        if (foundProduct != null) {
            System.out.printf("\n%s successfully added to your cart.\n", foundProduct.getName());
            cart.add(foundProduct);
            displayProductsMenu(inventory, cart, scanner);
            return;
        };

        System.out.println("\nProduct ID not found.");
        displayProductsMenu(inventory, cart, scanner);
    }

    // Similar to displayProducts but without unnecessary info and with a hash to keep track of quantities, and unlike displayProducts since its not as long it prints after each user choice.
    public static void displayCart(ArrayList<Product> inventory, ArrayList<Product> cart, Scanner scanner) {
        if (cart.isEmpty()){
            System.out.println("\nYou have no items in your cart.");
            mainMenu(inventory, cart, scanner);
        }

        System.out.printf("\n%-10s %-40s %-6s %-10s%n",
                "ID", "NAME", "QTY", "PRICE");
        System.out.println("-------------------------------------------------------------------");

        HashMap<String, Integer> quantities = getQuantities(cart);
        double totalAmount = 0.00;
        HashSet<String> printed = new HashSet<>();

        for (Product product : cart) {

            if (printed.contains(product.getId())) {
                continue;
            }

            double addedPrice = product.getPrice() * quantities.get(product.getId());

            System.out.printf(
                    "%-10s %-40s %-6s $%-9.2f%n",
                    product.getId(),
                    product.getName(),
                    "x" + quantities.get(product.getId()),
                    addedPrice
            );

            printed.add(product.getId());
            totalAmount += addedPrice;
        }

        System.out.println("-------------------------------------------------------------------");
        System.out.printf("Total Price: $%.2f\n", totalAmount);

        displayCartMenu(inventory, cart, scanner, totalAmount);
    }

    // Similar to displayProductsMenu, added option to remove individual items from cart
    public static void displayCartMenu(ArrayList<Product> inventory,
                                           ArrayList<Product> cart, Scanner scanner, double totalAmount ) {
        int choice = -1;
        while (choice != 4) {
            System.out.println("\nWhat would you like to do?");
            System.out.println("1. Check out your cart");
            System.out.println("2. Remove item from cart");
            System.out.println("3. Go back to the main menu");
            System.out.println("4. Exit");
            System.out.print("Your choice: ");

            if (!scanner.hasNextInt()) {
                System.out.println("Please enter 1, 2, 3 or 4.");
                scanner.nextLine();                 // discard bad input
                continue;
            }
            choice = scanner.nextInt();
            scanner.nextLine();                     // clear newline

            switch (choice) {
                case 1 -> checkOut(inventory, cart, totalAmount, scanner);
                case 2 -> removeFromCart(inventory, cart, scanner);
                case 3 -> mainMenu(inventory, cart, scanner);
                case 4 -> {
                    System.out.println("\nThank you for shopping with us!");
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    // Promps user for the $$$, prints the receipt and creates a file copy of it
    public static void checkOut(ArrayList<Product> inventory,
                                ArrayList<Product> cart,
                                double totalAmount,
                                Scanner scanner) {
        double cashAmount;
        while (true) {
            System.out.println("\nHow much cash will you be paying with?");

            try {
                cashAmount = Double.parseDouble(scanner.nextLine());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid dollar amount.");
            }
        }

        if (cashAmount >= totalAmount) {
            System.out.println();
            System.out.println(buildReceipt(cart, totalAmount, cashAmount));
            logReceipt(cart, totalAmount, cashAmount);
            cart.clear();
            System.exit(0);
        }

        System.out.println("\nNot enough cash to complete the transaction.");
        displayCartMenu(inventory, cart, scanner, totalAmount);
    }

    // Used twice, once to print on screen and once with BufferedWriter in logReceipt() for the file copy.
    public static String buildReceipt(
            ArrayList<Product> cart,
            double totalAmount,
            double cashAmount) {

        StringBuilder receipt = new StringBuilder();

        HashMap<String, Integer> quantities = getQuantities(cart);
        HashSet<String> printed = new HashSet<>();

        receipt.append(String.format(
                "%-40s %-6s %-10s%n",
                "NAME", "QTY", "PRICE"));

        receipt.append("----------------------------------------------------------\n");

        for (Product product : cart) {

            if (printed.contains(product.getId())) {
                continue;
            }

            double addedPrice =
                    product.getPrice() * quantities.get(product.getId());

            receipt.append(String.format(
                    "%-40s %-6s $%-9.2f%n",
                    product.getName(),
                    "x" + quantities.get(product.getId()),
                    addedPrice
            ));

            printed.add(product.getId());
        }

        double changeAmount = cashAmount - totalAmount;

        receipt.append("----------------------------------------------------------\n");
        receipt.append(String.format("Total Price: $%.2f%n", totalAmount));
        receipt.append(String.format("Cash Paid: $%.2f%n", cashAmount));
        receipt.append(String.format("Change: $%.2f%n", changeAmount));
        receipt.append("----------------------------------------------------------\n");
        receipt.append("Thank you for shopping with us!\n");

        return receipt.toString();
    }

    // Logs date and time and creates file copy of the receipt
    public static void logReceipt(ArrayList<Product> cart,
                                    double totalAmount,
                                    double cashAmount) {
        try {
            LocalDateTime printTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
            String formattedPrintTime = printTime.format(formatter);

            File newFile = new File("receipts/" + formattedPrintTime + ".txt");
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(newFile));

            bufferedWriter.write(
                    buildReceipt(cart, totalAmount, cashAmount)
            );

            bufferedWriter.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Extra bonus method I made to remove individual items, pretty similar to addCart()
    public static void removeFromCart(ArrayList<Product> inventory,
                                ArrayList<Product> cart,
                                Scanner scanner) {
        System.out.print("\nPlease write the ID of the product you'd like to remove from your cart: ");
        String selectedID = scanner.nextLine();

        Product foundProduct = findProductById(selectedID, cart);
        if (foundProduct != null) {
            System.out.printf("\n%s successfully removed from your cart!\n", foundProduct.getName());
            cart.remove(foundProduct);
            displayCart(inventory, cart, scanner);
            return;
        };

        System.out.println("\nProduct ID not found.");
        displayCart(inventory, cart, scanner);
    }

    // Used to find by ID within inventory to add items to cart and also within cart to remove items
    public static Product findProductById(String id, ArrayList<Product> inventory) {
        for (Product product : inventory){
            if (id.equals(product.getId())){
                return product;
            }
        }
        return null;
    }

    // Used twice when printing the cart and the receipt, gets the quantities of each item on cart.
    public static HashMap<String, Integer> getQuantities(ArrayList<Product> cart) {
        HashMap<String, Integer> quantities = new HashMap<>();

        for (Product product : cart) {
            quantities.put(
                    product.getId(),
                    quantities.getOrDefault(product.getId(), 0) + 1
            );
        }

        return quantities;
    }
}
