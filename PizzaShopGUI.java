import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

class Compliments {
    int coke200ml;
    int sprite500ml;

    Compliments() {
        coke200ml = 0;
        sprite500ml = 0;
    }
}

class Pizza extends Compliments {
    int[] type;

    Pizza(int pizzaTypes) {
        type = new int[pizzaTypes];
    }
}

class Customer {
    int id;
    String name;
    String address;
    double amount;
    Pizza pizzaOrder;

    Customer(int id, String name, String address, Pizza pizzaOrder) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.pizzaOrder = pizzaOrder;
    }
}

public class PizzaShopGUI extends JFrame {
    private JTextArea textArea1, textArea2;
    private JTextField idField, nameField, addressField;
    private JButton orderButton, historyButton;
    private JComboBox<String> pizzaComboBox;
    private JTextField[] quantityFields;

    private ArrayList<Customer> customerList = new ArrayList<>();

    public PizzaShopGUI() {
        setTitle("Pizza Shop");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridLayout(2, 2));

        textArea1 = new JTextArea();
        textArea2 = new JTextArea();
        textArea1.setEditable(false);
        textArea2.setEditable(false);

        mainPanel.add(new JScrollPane(textArea1));
        mainPanel.add(new JScrollPane(textArea2));

        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        pizzaComboBox = new JComboBox<>(new String[]{"Paneer 50", "Margherita 40", "Pepperoni 60"});
        quantityFields = new JTextField[pizzaComboBox.getItemCount()];
        for (int i = 0; i < quantityFields.length; i++) {
            quantityFields[i] = new JTextField();
            inputPanel.add(new JLabel(pizzaComboBox.getItemAt(i)));
            inputPanel.add(quantityFields[i]);
        }

        idField = new JTextField();
        nameField = new JTextField();
        addressField = new JTextField();

        inputPanel.add(new JLabel("Customer ID"));
        inputPanel.add(idField);
        inputPanel.add(new JLabel("Customer Name"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Customer Address"));
        inputPanel.add(addressField);

        orderButton = new JButton("Place-an-order");
        historyButton = new JButton("History");

        orderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    placeOrder();
                } catch (Exception ex) {
                    textArea1.setText(ex.getMessage());
                }
            }
        });

        historyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    showHistory();
                } catch (Exception ex) {
                    textArea2.setText(ex.getMessage());
                }
            }
        });

        inputPanel.add(orderButton);
        inputPanel.add(historyButton);

        add(mainPanel, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void placeOrder() throws Exception {
        int customerId;
        String customerName, customerAddress;

        try {
            customerId = Integer.parseInt(idField.getText());
            customerName = nameField.getText();
            customerAddress = addressField.getText();

            if (customerId <= 0 || customerName.isEmpty() || customerAddress.isEmpty()) {
                throw new Exception("Enter customer details");
            }
        } catch (NumberFormatException ex) {
            throw new Exception("Enter valid customer ID");
        }

        Pizza pizzaOrder = new Pizza(quantityFields.length);
        double totalAmount = 0;

        for (int i = 0; i < quantityFields.length; i++) {
            try {
                int quantity = Integer.parseInt(quantityFields[i].getText());
                if (quantity < 0) {
                    throw new Exception("Enter a valid quantity for selected items");
                }

                pizzaOrder.type[i] = quantity;
                // Replace the prices with the actual prices of the pizza types
                totalAmount += quantity * getPriceForPizzaType(i);
            } catch (NumberFormatException ex) {
                throw new Exception("Enter valid quantity for selected items");
            }
        }

        if (totalAmount > 500 && totalAmount <= 1000) {
            pizzaOrder.coke200ml++;
        } else if (totalAmount > 1000 && totalAmount <= 1500) {
            pizzaOrder.coke200ml += 2;
        } else if (totalAmount > 1500) {
            pizzaOrder.coke200ml++;
            pizzaOrder.sprite500ml++;
        }

        Customer customer = new Customer(customerId, customerName, customerAddress, pizzaOrder);
	customer.amount= totalAmount;
        // Check if the customer already exists in the list
        boolean customerExists = false;
        for (Customer c : customerList) {
            if (c.id == customer.id) {
                customerExists = true;
                c.amount += customer.amount;
                for (int i = 0; i < c.pizzaOrder.type.length; i++) {
                    c.pizzaOrder.type[i] += customer.pizzaOrder.type[i];
                }
                c.pizzaOrder.coke200ml += customer.pizzaOrder.coke200ml;
                c.pizzaOrder.sprite500ml += customer.pizzaOrder.sprite500ml;
                break;
            }
        }

        if (!customerExists) {
            customerList.add(customer);
        }

        displayOrderDetails(customer);
    }

    private double getPriceForPizzaType(int type) {
        // Replace with actual prices for each pizza type
        switch (type) {
            case 0: // Paneer 50
                return 50.0;
            case 1: // Margherita 40
                return 40.0;
            case 2: // Pepperoni 60
                return 60.0;
            default:
                return 0.0;
        }
    }

    private void displayOrderDetails(Customer customer) {
        textArea1.setText("Customer ID: " + customer.id +
                "\nCustomer Name: " + customer.name +
                "\nCustomer Address: " + customer.address +
                "\n\nCurrent Purchased Items:\n");

        for (int i = 0; i < customer.pizzaOrder.type.length; i++) {
            textArea1.append(pizzaComboBox.getItemAt(i) + ": " + customer.pizzaOrder.type[i] + "\n");
        }

        textArea1.append("\nComplimentary Items:\n");
        textArea1.append("Coke 200ml: " + customer.pizzaOrder.coke200ml + "\n");
        textArea1.append("Sprite 500ml: " + customer.pizzaOrder.sprite500ml + "\n");
        textArea1.append("\nTotal Purchased Amount: " + customer.amount + "\n");
    }

    private void showHistory() throws Exception {
        int customerId;
        try {
            customerId = Integer.parseInt(idField.getText());
            if (customerId <= 0) {
                throw new Exception("Enter customer ID for history");
            }
        } catch (NumberFormatException ex) {
            throw new Exception("Enter valid customer ID for history");
        }

        boolean customerFound = false;
        for (Customer c : customerList) {
            if (c.id == customerId) {
                customerFound = true;
                textArea2.setText("Customer ID: " + c.id +
                        "\nCustomer Name: " + c.name +
                        "\nCustomer Address: " + c.address +
                        "\n\nPurchased Items:\n");

                for (int i = 0; i < c.pizzaOrder.type.length; i++) {
                    textArea2.append(pizzaComboBox.getItemAt(i) + ": " + c.pizzaOrder.type[i] + "\n");
                }

                textArea2.append("\nComplimentary Items:\n");
                textArea2.append("Coke 200ml: " + c.pizzaOrder.coke200ml + "\n");
                textArea2.append("Sprite 500ml: " + c.pizzaOrder.sprite500ml + "\n");
                textArea2.append("\nTotal Purchased Amount: " + c.amount + "\n");
                break;
            }
        }

        if (!customerFound) {
            throw new Exception("Customer with ID " + customerId + " not found in history");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new PizzaShopGUI();
            }
        });
    }
}
