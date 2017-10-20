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
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.w3c.dom.Document;
import javax.swing.text.html.StyleSheet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author M M Abid Naziri (Roll:34)
 */
class Item {

    String name, color = "", value;
    int x, y, size = 10;

    Item(String name, String value, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.value = value;
    }

    void setColor(String color) {
        if (color.length() > 0) {
            this.color = color;
        }
    }

    void setSize(int Size) {
        this.size = Size;
    }

    void setValue(String value) {
        this.value = value;
    }
}

/**
 ****************************************************
 * ################ Adapter Pattern #################
 * ***************************************************
 */
interface ConfigManager {

    boolean hasMoreItems();

    Item nextItem();
}

class InputConfigManager implements ConfigManager {

    int count = 0;
    Item nextItem;

    InputConfigManager(Item item) {
        this.nextItem = item;
        count++;
    }

    @Override
    public boolean hasMoreItems() {
        return count > 0;
    }

    @Override
    public Item nextItem() {
        count--;
        return this.nextItem;
    }

}

class NormalConfigManager implements ConfigManager {

    Scanner sc;
    int currentLine = 0;

    NormalConfigManager(String fileName) throws FileNotFoundException {
        sc = new Scanner(new File(fileName));
    }

    @Override
    public boolean hasMoreItems() {
        return sc.hasNextLine();
    }

    @Override
    public Item nextItem() {
        String line = sc.nextLine();
        String elements[] = line.split(", ");
        Item nextItem = new Item(elements[0], elements[1], Integer.parseInt(elements[2].split("\\s")[1]), Integer.parseInt(elements[3].split("\\s")[1]));
        if (elements.length > 4) {
            if (elements[4].split(": ")[0].equalsIgnoreCase("Size")) {
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

class XMLConfigManager implements ConfigManager {

    XMLParser xmlParser;

    XMLConfigManager(String fileName) throws ParserConfigurationException, SAXException, IOException {
        xmlParser = new XMLParser(fileName);
    }

    @Override
    public boolean hasMoreItems() {
        return xmlParser.hasMoreTextBoxes() || xmlParser.hasMoreEditBoxes() || xmlParser.hasMoreButtons();
    }

    @Override
    public Item nextItem() {
        if (xmlParser.hasMoreButtons()) {
            return xmlParser.nextButton();
        } else if (xmlParser.hasMoreTextBoxes()) {
            return xmlParser.nextTextBox();
        } else {
            return xmlParser.nextEditBox();
        }
    }

}

class XMLParser {

    File file;
    DocumentBuilderFactory dBuilderFactory;
    DocumentBuilder dBuilder;
    Document document;
    NodeList buttons, textBoxes, editBoxes;
    int nButtons = 0, nTextBoxes = 0, nEditBoxes = 0;
    int cntButtons = 0, cntTextBoxes = 0, cntEditBoxes = 0;
    String textBoxName = "TextBox", editBoxName = "EditBox";
    boolean isButton = true, isTextBox = true, isEditBox = true;

    XMLParser(String fileName) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {
        file = new File(fileName);
        dBuilderFactory = DocumentBuilderFactory.newInstance();
        dBuilder = dBuilderFactory.newDocumentBuilder();
        document = dBuilder.parse(file);
        // Setting buttons
        if (document.getElementsByTagName("Button").getLength() > 0) {
            buttons = document.getElementsByTagName("Button");
        } else if (document.getElementsByTagName("button").getLength() > 0) {
            buttons = document.getElementsByTagName("button");
        } else {
            isButton = false;
        }
        // Setting editboxes
        if (document.getElementsByTagName("EditBox").getLength() > 0) {
            editBoxes = document.getElementsByTagName("EditBox");
            editBoxName = "EditBox";
        } else if (document.getElementsByTagName("Editbox").getLength() > 0) {
            editBoxes = document.getElementsByTagName("Editbox");
            editBoxName = "Editbox";
        } else if (document.getElementsByTagName("editbox").getLength() > 0) {
            editBoxes = document.getElementsByTagName("editbox");
            editBoxName = "editbox";
        } else if (document.getElementsByTagName("editBox").getLength() > 0) {
            editBoxes = document.getElementsByTagName("editBox");
            editBoxName = "editBox";
        } else {
            isEditBox = false;
        }
        // Setting textboxes
        if (document.getElementsByTagName("TextBox").getLength() > 0) {
            textBoxes = document.getElementsByTagName("TextBox");
            textBoxName = "TextBox";
        } else if (document.getElementsByTagName("Textbox").getLength() > 0) {
            textBoxes = document.getElementsByTagName("Textbox");
            textBoxName = "Textbox";
        } else if (document.getElementsByTagName("textbox").getLength() > 0) {
            textBoxes = document.getElementsByTagName("textbox");
            textBoxName = "textbox";
        } else if (document.getElementsByTagName("textBox").getLength() > 0) {
            textBoxes = document.getElementsByTagName("textBox");
            textBoxName = "textBox";
        } else {
            isTextBox = false;
        }
        if (isButton) {
            nButtons = buttons.getLength();
        }
        if (isTextBox) {
            nTextBoxes = textBoxes.getLength();
        }
        if (isEditBox) {

            nEditBoxes = editBoxes.getLength();
        }
    }

    boolean hasMoreButtons() {
        return cntButtons < nButtons;
    }

    boolean hasMoreTextBoxes() {
        return cntTextBoxes < nTextBoxes;
    }

    boolean hasMoreEditBoxes() {
        return cntEditBoxes < nEditBoxes;
    }

    Item nextButton() {
        Node buttonNode = buttons.item(cntButtons);
        Element element = (Element) buttonNode;
        String value = "";
        int x, y;
        // Parsing Value
        if (element.getElementsByTagName("Value").getLength() > 0) {
            value = element.getElementsByTagName("Value").item(0).getTextContent();
        } else if (element.getElementsByTagName("value").getLength() > 0) {
            value = element.getElementsByTagName("value").item(0).getTextContent();
        } else if (element.getAttribute("value").length() > 0) {
            value = element.getAttribute("value");
        } else {
            value = element.getAttribute("Value");
        }
        // Parsing X
        if (element.getElementsByTagName("X").getLength() > 0) {
            x = Integer.parseInt(element.getElementsByTagName("X").item(0).getTextContent());
        } else if (element.getElementsByTagName("x").getLength() > 0) {
            x = Integer.parseInt(element.getElementsByTagName("x").item(0).getTextContent());
        } else if (element.getAttribute("x").length() > 0) {
            x = Integer.parseInt(element.getAttribute("x"));
        } else {
            x = Integer.parseInt(element.getAttribute("X"));
        }
        // Parsing y
        if (element.getElementsByTagName("Y").getLength() > 0) {
            y = Integer.parseInt(element.getElementsByTagName("Y").item(0).getTextContent());
        } else if (element.getElementsByTagName("y").getLength() > 0) {
            y = Integer.parseInt(element.getElementsByTagName("x").item(0).getTextContent());
        } else if (element.getAttribute("y").length() > 0) {
            y = Integer.parseInt(element.getAttribute("y"));
        } else {
            y = Integer.parseInt(element.getAttribute("Y"));
        }

        Item button = new Item("Button", value, x, y);

        // Setting Color
        if (element.getElementsByTagName("Color").getLength() > 0) {
            button.setColor(element.getElementsByTagName("Color").item(0).getTextContent());
        } else if (element.getAttribute("Color").length() > 0) {
            button.setColor(element.getAttribute("Color"));
        } else if (element.getElementsByTagName("color").getLength() > 0) {
            button.setColor(element.getElementsByTagName("color").item(0).getTextContent());
        } else if (element.getAttribute("color").length() > 0) {
            button.setColor(element.getAttribute("color"));
        }
        // Setting Size
        if (element.getElementsByTagName("Size").getLength() > 0) {
            String size = element.getElementsByTagName("Size").item(0).getTextContent();
            button.setSize(Integer.parseInt(size));
        } else if (element.getAttribute("Size").length() > 0) {
            String size = element.getAttribute("Size");
            button.setSize(Integer.parseInt(size));
        } else if (element.getElementsByTagName("size").getLength() > 0) {
            String size = element.getElementsByTagName("size").item(0).getTextContent();
            button.setSize(Integer.parseInt(size));
        } else if (element.getAttribute("size").length() > 0) {
            String size = element.getAttribute("size");
            button.setSize(Integer.parseInt(size));
        }
        cntButtons++;
        return button;
    }

    Item nextTextBox() {
        Node textBoxNode = textBoxes.item(cntTextBoxes);
        Element element = (Element) textBoxNode;
        String value = "";
        int x, y;
        // Parsing Value
        if (element.getElementsByTagName("Value").getLength() > 0) {
            value = element.getElementsByTagName("Value").item(0).getTextContent();
        } else if (element.getElementsByTagName("value").getLength() > 0) {
            value = element.getElementsByTagName("value").item(0).getTextContent();
        } else if (element.getAttribute("value").length() > 0) {
            value = element.getAttribute("value");
        } else {
            value = element.getAttribute("Value");
        }
        // Parsing X
        if (element.getElementsByTagName("X").getLength() > 0) {
            x = Integer.parseInt(element.getElementsByTagName("X").item(0).getTextContent());
        } else if (element.getElementsByTagName("x").getLength() > 0) {
            x = Integer.parseInt(element.getElementsByTagName("x").item(0).getTextContent());
        } else if (element.getAttribute("x").length() > 0) {
            x = Integer.parseInt(element.getAttribute("x"));
        } else {
            x = Integer.parseInt(element.getAttribute("X"));
        }
        // Parsing y
        if (element.getElementsByTagName("Y").getLength() > 0) {
            y = Integer.parseInt(element.getElementsByTagName("Y").item(0).getTextContent());
        } else if (element.getElementsByTagName("y").getLength() > 0) {
            y = Integer.parseInt(element.getElementsByTagName("x").item(0).getTextContent());
        } else if (element.getAttribute("y").length() > 0) {
            y = Integer.parseInt(element.getAttribute("y"));
        } else {
            y = Integer.parseInt(element.getAttribute("Y"));
        }
        Item textBox = new Item(textBoxName, value, x, y);
        // Setting Color
        if (element.getElementsByTagName("Color").getLength() > 0) {
            textBox.setColor(element.getElementsByTagName("Color").item(0).getTextContent());
        } else if (element.getAttribute("Color").length() > 0) {
            textBox.setColor(element.getAttribute("Color"));
        } else if (element.getElementsByTagName("color").getLength() > 0) {
            textBox.setColor(element.getElementsByTagName("color").item(0).getTextContent());
        } else if (element.getAttribute("color").length() > 0) {
            textBox.setColor(element.getAttribute("color"));
        }
        // Setting Size
        if (element.getElementsByTagName("Size").getLength() > 0) {
            String size = element.getElementsByTagName("Size").item(0).getTextContent();
            textBox.setSize(Integer.parseInt(size));
        } else if (element.getAttribute("Size").length() > 0) {
            String size = element.getAttribute("Size");
            textBox.setSize(Integer.parseInt(size));
        } else if (element.getElementsByTagName("size").getLength() > 0) {
            String size = element.getElementsByTagName("size").item(0).getTextContent();
            textBox.setSize(Integer.parseInt(size));
        } else if (element.getAttribute("size").length() > 0) {
            String size = element.getAttribute("size");
            textBox.setSize(Integer.parseInt(size));
        }
        cntTextBoxes++;
        return textBox;
    }

    Item nextEditBox() {
        Node editBoxNode = editBoxes.item(cntEditBoxes);
        Element element = (Element) editBoxNode;
        String value = "";
        int x, y;
        // Parsing Value
        if (element.getElementsByTagName("Value").getLength() > 0) {
            value = element.getElementsByTagName("Value").item(0).getTextContent();
        } else if (element.getElementsByTagName("value").getLength() > 0) {
            value = element.getElementsByTagName("value").item(0).getTextContent();
        } else if (element.getAttribute("value").length() > 0) {
            value = element.getAttribute("value");
        } else {
            value = element.getAttribute("Value");
        }
        // Parsing X
        if (element.getElementsByTagName("X").getLength() > 0) {
            x = Integer.parseInt(element.getElementsByTagName("X").item(0).getTextContent());
        } else if (element.getElementsByTagName("x").getLength() > 0) {
            x = Integer.parseInt(element.getElementsByTagName("x").item(0).getTextContent());
        } else if (element.getAttribute("x").length() > 0) {
            x = Integer.parseInt(element.getAttribute("x"));
        } else {
            x = Integer.parseInt(element.getAttribute("X"));
        }
        // Parsing y
        if (element.getElementsByTagName("Y").getLength() > 0) {
            y = Integer.parseInt(element.getElementsByTagName("Y").item(0).getTextContent());
        } else if (element.getElementsByTagName("y").getLength() > 0) {
            y = Integer.parseInt(element.getElementsByTagName("x").item(0).getTextContent());
        } else if (element.getAttribute("y").length() > 0) {
            y = Integer.parseInt(element.getAttribute("y"));
        } else {
            y = Integer.parseInt(element.getAttribute("Y"));
        }
        Item editBox = new Item(editBoxName, value, x, y);
        // Setting Color
        if (element.getElementsByTagName("Color").getLength() > 0) {
            editBox.setColor(element.getElementsByTagName("Color").item(0).getTextContent());
        } else if (element.getAttribute("Color").length() > 0) {
            editBox.setColor(element.getAttribute("Color"));
        } else if (element.getElementsByTagName("color").getLength() > 0) {
            editBox.setColor(element.getElementsByTagName("color").item(0).getTextContent());
        } else if (element.getAttribute("color").length() > 0) {
            editBox.setColor(element.getAttribute("color"));
        }
        // Setting Size
        if (element.getElementsByTagName("Size").getLength() > 0) {
            String size = element.getElementsByTagName("Size").item(0).getTextContent();
            editBox.setSize(Integer.parseInt(size));
        } else if (element.getAttribute("Size").length() > 0) {
            String size = element.getAttribute("Size");
            editBox.setSize(Integer.parseInt(size));
        } else if (element.getElementsByTagName("size").getLength() > 0) {
            String size = element.getElementsByTagName("size").item(0).getTextContent();
            editBox.setSize(Integer.parseInt(size));
        } else if (element.getAttribute("size").length() > 0) {
            String size = element.getAttribute("size");
            editBox.setSize(Integer.parseInt(size));
        }
        cntEditBoxes++;
        return editBox;
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

    JTextField addEditBox(Item item);
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
        textField.setEditable(false);
        return textField;
    }

    @Override
    public JTextField addEditBox(Item item) {
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
            button.setForeground(color);
            button.setBackground(DetailedDesign.getContrastColor(color));
        }
        button.setFocusPainted(false);
        button.setFont(new Font("Cambria", Font.BOLD, item.size));
        button.setBounds(item.x, item.y, item.value.length() * item.size + 25, item.size * 2);
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
        textField.setEditable(false);
        return textField;
    }

    @Override
    public JTextField addEditBox(Item item) {
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
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SimplisticFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(SimplisticFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(SimplisticFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(SimplisticFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new SimplisticDesign();
    }
}

class DetailedFactory implements DesignFactory {

    @Override
    public DesignStyle getDesign() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DetailedFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(DetailedFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(DetailedFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(DetailedFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
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
            Item nextItem = configManager.nextItem();
            if (nextItem.name.equalsIgnoreCase("Button")) {
                contentPane.add(designStyle.addButton(nextItem));
            } else if (nextItem.name.equalsIgnoreCase("EditBox")) {
                contentPane.add(designStyle.addEditBox(nextItem));
            } else {
                contentPane.add(designStyle.addTextBox(nextItem));
            }
        }
        frame.revalidate();
        frame.repaint();
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
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws org.xml.sax.SAXException
     */
    public static void main(String[] args) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {
        // TODO code application logic here
        System.out.println("Choose the design style: \n\t1.Simplistic Design\n\t2.High Detailed Design");
        Scanner input = new Scanner(System.in);
        int choice = input.nextInt();
        WindowManager windowManager;
        if (choice == 1) {
            windowManager = WindowManager.getInstance(new SimplisticFactory());
        } else {
            windowManager = WindowManager.getInstance(new DetailedFactory());
        }
        input.nextLine();
        String fileName;
        while (true) {
            System.out.println("Enter \n\t- Input to configure manually\n\t- File Name to read the configurations from a file");
            fileName = input.nextLine();
            ConfigManager configManager;
            if (fileName.equalsIgnoreCase("input")) {
                System.out.println("Enter the type of the element: ");
                String name = input.nextLine();
                System.out.println("Enter the value: ");
                String value = input.nextLine();
                System.out.println("Enter the X co-ordinate: ");
                int x = input.nextInt();
                System.out.println("Enter the Y co-ordinate: ");
                int y = input.nextInt();
                Item item = new Item(name, value, x, y);
                System.out.println("Do you want to enter the color (Y/N): ");
                input.nextLine();
                String option = input.nextLine();
                if (option.equalsIgnoreCase("y")) {
                    System.out.println("Enter the color: ");
                    String color = input.nextLine();
                    item.setColor(color);
                }
                if (choice == 2) {
                    System.out.println("Do you want to enter the size (Y/N): ");
                    option = input.nextLine();
                    if (option.equalsIgnoreCase("y")) {
                        System.out.println("Enter the size: ");
                        int size = input.nextInt();
                        input.nextLine();
                        item.setSize(size);
                    }
                }
                configManager = new InputConfigManager(item);
            } else if (fileName.split("\\.").length > 1 && fileName.split("\\.")[1].equalsIgnoreCase("xml")) {
                configManager = new XMLConfigManager(fileName);
            } else {
                configManager = new NormalConfigManager(fileName);
            }
            windowManager.loadUI(configManager);
        }

    }

}
