package Ari.IAProject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Deck implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private List<MagicCard> cards;

    public Deck(String name) {
        this.name = name;
        this.cards = new ArrayList<>();
        String filename = name + ".ser";
        try {
            File file = new File(filename);
            if (file.createNewFile()) {
                System.out.println("Deck file created: " + filename);
            } else {
                System.out.println("Deck file already exists: " + filename);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while creating the deck file: " + e.getMessage());
            e.printStackTrace();
        }
    }



    public void addCard(MagicCard card, String deckName) {
        cards.add(card);
        serializeCard(deckName);
    }



    public void serializeCard(String deckName) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(deckName + ".ser"))) {
            out.writeObject(this);
            System.out.println("Deck '" + name + "' serialized to: " + deckName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static Deck deserialize(String deckName) {
        String filename = deckName + ".ser";
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            Deck deck = (Deck) in.readObject();
            System.out.println("Deck '" + deckName + "' deserialized from: " + filename);
            return deck;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }



    public String getName() {
        return name;
    }

    public List<MagicCard> getCards() {
        return cards;
    }
}
