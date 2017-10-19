/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package solution34;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.text.html.StyleSheet;

/**
 *
 * @author M M Abid Naziri (Roll:34)
 */
class Item {

    String name, color="", value;
    int x, y, size = 10;

    Item(String name, String value, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.value = value;
    }

    void setColor(String color) {
        this.color = color;
    }

    void setSize(int Size) {
        this.size = Size;
    }

    void setValue(String value) {
        this.value = value;
    }
}

class ConfigManager {

    Scanner sc;
    int currentLine = 0;

    ConfigManager(String fileName) throws FileNotFoundException {
        sc = new Scanner(new File(fileName));
    }

    boolean hasMoreItems() {
        return sc.hasNextLine();
    }

    Item nexItem() {
        String line = sc.nextLine();
        String elements[] = line.split(", ");
//        System.out.println(elements.length+" "+elements[0]);
        Item nextItem = new Item(elements[0], elements[1], Integer.parseInt(elements[2].split("\\s")[1]), Integer.parseInt(elements[3].split("\\s")[1]));
        if (elements.length > 4) {
            if (elements[4].split(": ")[0].equals("Size")) {
                nextItem.setSize(Integer.parseInt(elements[4].split(": ")[1]));
            } else {
                nextItem.setColor(elements[4].split(": ")[1]);
            }
        }
        if (elements.length > 5) {
            nextItem.setSize(Integer.parseInt(elements[5].split(": ")[1]));
        }
        return nextItem;
    }
}

/**
 * ***************************************************************************
 * ###################### Abstract Factory Pattern ###########################
 * ***************************************************************************
 */
interface DesignStyle {

    JButton addButton(Item item);

    JTextField addTextBox(Item item);
}

class SimplisticDesign implements DesignStyle {

    @Override
    public JButton addButton(Item item) {
        JButton button = new JButton(item.value);

        if (item.color.length() > 0) {
            Color color = new StyleSheet().stringToColor(item.color);
            button.setBackground(color);
            button.setForeground(DetailedDesign.getContrastColor(color));
        }

        button.setFocusPainted(false);
        button.setFont(new Font("Tahoma", Font.PLAIN, 10));
        button.setBounds(item.x, item.y, 100, 30);
        return button;
    }

    @Override
    public JTextField addTextBox(Item item) {
        JTextField textField = new JTextField(item.value);
        textField.setBounds(item.x, item.y, 200, 30);
        if (item.color.length() > 0) {
            Color color = new StyleSheet().stringToColor(item.color);
            textField.setForeground(color);
            textField.setBackground(DetailedDesign.getContrastColor(color));
        }
        textField.setBounds(item.x, item.y, 200, 30);
        textField.setFont(new Font("Tahoma", Font.PLAIN, 10));
        return textField;
    }

}

class DetailedDesign implements DesignStyle {

    public static Color getContrastColor(Color color) {
        double y = (299 * color.getRed() + 587 * color.getGreen() + 114 * color.getBlue()) / 1000;
        return y >= 128 ? Color.black : Color.white;
    }

    @Override
    public JButton addButton(Item item) {
        JButton button = new JButton(item.value);
        if (item.color.length() > 0) {
            Color color = new StyleSheet().stringToColor(item.color);
            button.setBackground(color);
            button.setForeground(DetailedDesign.getContrastColor(color));
        }
        button.setFocusPainted(false);
        button.setFont(new Font("Cambria", Font.BOLD, item.size));
        button.setBounds(item.x, item.y, item.value.length() * item.size, item.size * 2);
        return button;
    }

    @Override
    public JTextField addTextBox(Item item) {
        JTextField textField = new JTextField(item.value);
        textField.setBounds(item.x, item.y, item.value.length() * item.size, item.size * 2);
        textField.setFont(new Font("Cambria", Font.ITALIC, item.size));
        if (item.color.length() > 0) {
            Color color = new StyleSheet().stringToColor(item.color);
            textField.setForeground(color);
            textField.setBackground(DetailedDesign.getContrastColor(color));
        }
        return textField;
    }

}

/**
 * ***************************************************************************
 */
interface DesignFactory {

    DesignStyle getDesign();
}

class SimplisticFactory implements DesignFactory {

    @Override
    public DesignStyle getDesign() {
        return new SimplisticDesign();
    }
}

class DetailedFactory implements DesignFactory {

    @Override
    public DesignStyle getDesign() {
        return new DetailedDesign();
    }
}

/**
 * ***************************************************************************
 * ########################## Singleton Pattern ##############################
 * ***************************************************************************
 */
class WindowManager {

    private static WindowManager windowManager;
    DesignFactory designFactory;
    JFrame frame;
    Container contentPane;

    private WindowManager(DesignFactory designFactory) {
        this.designFactory = designFactory;
        this.frame = new JFrame();
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setTitle(this.designFactory.getClass().toString().split("\\.")[1].split("Factory")[0] + " Design");
        this.frame.setLayout(null);
        this.frame.setSize(1280, 720);
        contentPane = frame.getContentPane();
    }

    public static WindowManager getInstance(DesignFactory designFactory) {
        if (null == windowManager) {
            windowManager = new WindowManager(designFactory);
        }
        return windowManager;
    }

    void loadUI(ConfigManager configManager) {

        DesignStyle designStyle = designFactory.getDesign();
        while (configManager.hasMoreItems()) {
            Item nextItem = configManager.nexItem();
            if (nextItem.name.equals("Button")) {
                contentPane.add(designStyle.addButton(nextItem));
            } else {
                contentPane.add(designStyle.addTextBox(nextItem));
            }
        }

        frame.setVisible(true);
    }
}

/**
 * ***************************************************************************
 */
public class Solution34 {

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        // TODO code application logic here
        System.out.println("Choose the design style: \n\t1.Simplistic Design\n\t2.Hight Detailed Design");
        Scanner input = new Scanner(System.in);
        int choice = input.nextInt();
        WindowManager windowManager;
        if (choice == 1) {
            windowManager = WindowManager.getInstance(new SimplisticFactory());
        } else {
            windowManager = WindowManager.getInstance(new DetailedFactory());
        }
        String fileName = input.nextLine();
        while (true) {
            System.out.println("Enter the name of the config file: ");
            fileName = input.nextLine();
            if (fileName.split("\\.").length > 1 && fileName.split("\\.")[1].equals("conf")) {
                ConfigManager configManager = new ConfigManager(fileName);
                windowManager.loadUI(configManager);
            }
        }

    }

}
