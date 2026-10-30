package com.pluralsight;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class Store {

    public static void main(String[] args) {

        // Create lists for inventory and the shopping cart
        ArrayList<Product> inventory = new ArrayList<>();
        ArrayList<Product> cart = new ArrayList<>();

        // Load inventory from the data file (pipe-delimited: SKU|Product Name|Price|Department)
        loadInventory("products.csv", inventory);

        Scanner scanner = new Scanner(System.in);
        mainMenu(inventory, cart, scanner);
        scanner.close();
    }

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

    public static void addToCart(ArrayList<Product> inventory,
                                 ArrayList<Product> cart, Scanner scanner) {
        System.out.print("\nPlease write the ID of the product you'd like to add to your cart: ");
        String selectedID = scanner.nextLine();

        Product foundProduct = findProductById(selectedID, inventory);
        if (foundProduct != null) {
            System.out.printf("\n%s successfully added to your cart!\n", foundProduct.getName());
            cart.add(foundProduct);
            displayProductsMenu(inventory, cart, scanner);
            return;
        };

        System.out.println("\nProduct ID not found.");
        displayProductsMenu(inventory, cart, scanner);
    }


    public static void displayCart(ArrayList<Product> inventory, ArrayList<Product> cart, Scanner scanner) {
        if (cart.isEmpty()){
            System.out.println("\nYou have no items in your cart!");
            mainMenu(inventory, cart, scanner);
        }

        System.out.printf("\n%-10s %-40s %-10s %-15s%n",
                "ID", "NAME", "PRICE", "DEPARTMENT");
        System.out.println("--------------------------------------------------------------------------");

        double totalAmount = 0.00;

        for (Product product : cart) {
            totalAmount += product.getPrice();
            System.out.printf("%-10s %-40s $%-9.2f %-15s%n",
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    product.getDepartment());
        }

        System.out.println("-------------------------------------------------------------------------");
        System.out.printf("Total Price: %.2f\n", totalAmount);

        displayCartMenu(inventory, cart, scanner, totalAmount);
    }


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
            printReceipt(cart, totalAmount, cashAmount);
            cart.clear();
            System.exit(0);
        }

        System.out.println("\nNot enough cash to complete the transaction.");
        displayCartMenu(inventory, cart, scanner, totalAmount);
    }

    public static void printReceipt(ArrayList<Product> cart,
                                double totalAmount,
                                double cashAmount) {
        System.out.printf("\n%-40s %-10s",
                "NAME", "PRICE");
        System.out.println("\n--------------------------------------------------------------------------\n");

        for (Product product : cart) {
            System.out.printf("%-40s $%-9.2f\n",
                    product.getName(),
                    product.getPrice());
        }

        System.out.println("-------------------------------------------------------------------------");
        System.out.printf("Total Price: %.2f\n", totalAmount);
        System.out.printf("Cash Paid: %.2f\n", cashAmount);
        double changeAmount = cashAmount - totalAmount;
        System.out.printf("Change: %.2f\n", changeAmount);

        System.out.println("-------------------------------------------------------------------------");
        System.out.println("Thank you for shopping with us!");
    }

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



    public static Product findProductById(String id, ArrayList<Product> inventory) {
        for (Product product : inventory){
            if (id.equals(product.getId())){
                return product;
            }
        }
        return null;
    }
}
