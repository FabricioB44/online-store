package com.pluralsight;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Starter code for the Online Store workshop.
 * Learners will complete the TODO sections to make the program work.
 * <p>
 * What is already done for you: the two lists, the Home Screen loop and
 * the method headers. Each TODO says what that method has to do.
 * Product.java is complete; products.csv sits at the project root.
 * You will need extra imports (for example java.io.BufferedReader,
 * java.io.FileReader, java.io.IOException) as you fill the TODOs in.
 */
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

    /**
     * Reads product data from a file and populates the inventory list.
     * File format (pipe-delimited), the first line is a header row:
     * SKU|Product Name|Price|Department
     * <p>
     * Example line:
     * AV1051|JBL Bluetooth Speaker|89.95|Audio Video
     */
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
                case 3 -> System.out.println("Thank you for shopping with us!");
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
                case 3 -> System.out.println("Thank you for shopping with us!");
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

    /**
     * Shows the contents of the cart, calculates the total,
     * and offers the option to check out.
     */
    public static void displayCart(ArrayList<Product> inventory, ArrayList<Product> cart, Scanner scanner) {
        if (cart.isEmpty()){
            System.out.println("You have no items in your cart!\n");
            mainMenu(inventory, cart, scanner);
        }

        System.out.printf("\n%-10s %-40s %-10s %-15s%n",
                "ID", "NAME", "PRICE", "DEPARTMENT");
        System.out.println("--------------------------------------------------------------------------");

        double totalPrice = 0.00;

        for (Product product : cart) {
            totalPrice += product.getPrice();
            System.out.printf("%-10s %-40s $%-9.2f %-15s%n",
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    product.getDepartment());
        }

        System.out.println("-------------------------------------------------------------------------");
        System.out.printf("Total Price: %.2f", totalPrice);

        // displayProductsMenu(cart, scanner);


        // TODO:
        //   • list each product in the cart
        //   • compute the total cost
        //   • ask the user whether to check out (C) or return (X)
        //   • if C, call checkOut(cart, totalAmount, scanner)
    }

    /**
     * Handles the checkout process:
     * 1. Confirm that the user wants to buy.
     * 2. Accept payment and calculate change.
     * 3. Display a simple receipt.
     * 4. Clear the cart.
     */
    public static void checkOut(ArrayList<Product> cart,
                                double totalAmount,
                                Scanner scanner) {
        // TODO: implement steps listed above
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
