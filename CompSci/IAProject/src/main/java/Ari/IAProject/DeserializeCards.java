package Ari.IAProject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class DeserializeCards {
    public static void main(String[] args) {
        String filename = "cards.ser";
        List<MagicCard> deserializedCards = deserialize(filename);
        if (deserializedCards != null) {
            for (MagicCard card : deserializedCards) {
                System.out.println("Name: " + card.getName());
                System.out.println("Price: " + card.getPrice());
                System.out.println("Image URL: " + card.getImageUrl());
                System.out.println();
            }
        }
    }

    public static List<MagicCard> deserialize(String filename) {
        // Deserializes cards and puts them into an ArrayList for easier usage
        List<MagicCard> deserializedCards = new ArrayList<>();
        try (FileInputStream fileIn = new FileInputStream(filename);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            List<MagicCard> existingCards = (List<MagicCard>) in.readObject();
            deserializedCards.addAll(existingCards);
        } catch (IOException | ClassNotFoundException e) {
            // Stack tracing / error handling
            e.printStackTrace();
        }
        return deserializedCards;
    }
}

