package Ari.IAProject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RemoveFromFile {

    public static void removeCard(String filename) {
        List<MagicCard> cards = deserialize(filename);
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the name of the card to remove: ");
        String cardName = scanner.nextLine().trim();
        scanner.close();
        boolean removed = cards.removeIf(card -> card.getName().equalsIgnoreCase(cardName));
        if (removed) {
            System.out.println("Card '" + cardName + "' successfully removed.");
        } else {
            System.out.println("Card '" + cardName + "' not found in the list.");
        }
        serialize(cards, filename);
    }



    public static List<MagicCard> deserialize(String filename) {
        try (FileInputStream fileIn = new FileInputStream(filename);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            return (List<MagicCard>) in.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filename);
        } catch (IOException | ClassNotFoundException e) {
            // Stack tracing / error handling
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static void serialize(List<MagicCard> cards, String filename) {
        try (FileOutputStream fileOut = new FileOutputStream(filename);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(cards);
            System.out.println("Serialized data is saved in " + filename);
        } catch (IOException e) {
            // Stack tracing / error handling
            e.printStackTrace();
        }
    }
}
