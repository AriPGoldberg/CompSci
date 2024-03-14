package Ari.IAProject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import static Ari.IAProject.DeserializeCards.deserialize;

public class SerializeCards {

    public static void serialize(List<MagicCard> cards, String filename) {
        // Serializes cards into a .ser file
        List<MagicCard> existingCards = deserialize(filename);
        if (existingCards != null) {
            existingCards.addAll(cards);
            try (FileOutputStream fileOut = new FileOutputStream(filename);
                 ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
                out.writeObject(existingCards);
                System.out.println("Serialized " + cards.size() + " cards to " + filename);
            } catch (IOException e) {
                // Stack tracing / error handling
                e.printStackTrace();
            }
        } else {
            try (FileOutputStream fileOut = new FileOutputStream(filename);
                 ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
                out.writeObject(cards);
                System.out.println("Serialized " + cards.size() + " cards to " + filename);
            } catch (IOException e) {
                // Stack tracing / error handling
                e.printStackTrace();
            }
        }
    }


}
