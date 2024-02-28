package Ari.IAProject;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.io.File;

import static Ari.IAProject.DeserializeCards.deserialize;

public class Main {

    private static final String API_BASE_URL = "https://api.scryfall.com/";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the number of the utility you would like to use:");
        System.out.println("1. Add cards");
        System.out.println("2. Remove cards");
        System.out.println("3. Search for a card");
        System.out.println("4. Add a deck");
        System.out.println("5. Add a card to a deck");
        System.out.println("6. List all cards in a deck");
        System.out.println("7. Delete a deck");
        System.out.println("8. List off all cards");

        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            addCards(scanner);
        } else if (choice == 2) {
            RemoveFromFile.removeCard("cards.ser");
        } else if (choice == 3) {
            searchForCard(scanner, false);
        } else if (choice == 4) {
            System.out.println("What is the name of the deck you would like to create?");
            String deckName = scanner.nextLine();
            createDeck(deckName);
        } else if (choice == 5) {
            addCardToDeck(scanner);
        } else if (choice == 6) {
            printDeck(scanner);
        } else if (choice == 7) {
            deleteDeck(scanner);
        } else if (choice == 8) {
            listAllCards();
        } else {
            System.out.println("Invalid choice. Please enter a number between 1 and 6.");
        }
    }

    private static void addCards(Scanner scanner) {
        HttpClient httpClient = HttpClients.createDefault();

        SerializeCards serializeCards = new SerializeCards();
        DeserializeCards deserializeCards = new DeserializeCards();

        List<MagicCard> cards = new ArrayList<>();
        System.out.print("Enter the name of the card: ");
        String searchTerm = scanner.nextLine();

        String fields = "name,prices,image_uris";
        HttpGet request = new HttpGet(API_BASE_URL + "cards/search?q=" + searchTerm.replace(" ", "%20") + "&unique=prints&fields=" + fields);

        try {
            HttpResponse response = httpClient.execute(request);
            String responseBody = EntityUtils.toString(response.getEntity());

            Gson gson = new Gson();
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);

            if (jsonResponse.has("data") && jsonResponse.get("data").isJsonArray()) {
                JsonArray cardsArray = jsonResponse.getAsJsonArray("data");
                Set<String> uniqueNames = new HashSet<>();
                for (JsonElement cardElement : cardsArray) {
                    JsonObject cardObject = cardElement.getAsJsonObject();
                    String name = extractName(cardObject);
                    if (!uniqueNames.contains(name)) {
                        String price = extractPrice(cardObject);
                        String imageUrl = extractImageUrl(cardObject);

                        MagicCard magicCard = new MagicCard(name, price, imageUrl);
                        cards.add(magicCard);
                        uniqueNames.add(name);

                    }
                }
            } else {
                System.out.println("No cards found for the search term: " + searchTerm);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        serializeCards.serialize(cards, "cards.ser");

    }

    public static void createDeck(String deckName) {
        Deck deck = new Deck(deckName);
        System.out.println("Deck created with name: " + deck.getName());
    }

    public static void addCardToDeck(Scanner scanner) {

        MagicCard foundCard = searchForCard(scanner, true);
        if (foundCard != null) {
            Deck deck = loadOrCreateExistingDeck(scanner);

            System.out.println(deck.getName());
            deck.addCard(foundCard,deck.getName());
            System.out.println("Card added to the deck:");
            System.out.println("Name: " + foundCard.getName());
            System.out.println("Price: " + foundCard.getPrice());
            System.out.println("Image URL: " + foundCard.getImageUrl());
        } else {
            System.out.println("Card not found.");
        }
    }

    public static MagicCard searchForCard(Scanner scanner, Boolean exact) {
        HttpClient httpClient = HttpClients.createDefault();
        MagicCard magicCard = null;

        System.out.print("Enter the name of the card: ");
        String searchTerm = scanner.nextLine();

        String fields = "name,prices,image_uris";
        HttpGet request = new HttpGet(API_BASE_URL + "cards/search?q=" + searchTerm.replace(" ", "%20") + "&unique=prints&fields=" + fields);

        try {
            HttpResponse response = httpClient.execute(request);
            String responseBody = EntityUtils.toString(response.getEntity());

            Gson gson = new Gson();
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);

            if (jsonResponse.has("data") && jsonResponse.get("data").isJsonArray()) {
                JsonArray cardsArray = jsonResponse.getAsJsonArray("data");
                if (cardsArray.size() > 0) {
                    JsonObject cardObject = cardsArray.get(0).getAsJsonObject();
                    String name = extractName(cardObject);
                    String price = extractPrice(cardObject);
                    String imageUrl = extractImageUrl(cardObject);

                    magicCard = new MagicCard(name, price, imageUrl);
                } else {
                    System.out.println("No cards found for the search term: " + searchTerm);
                }
            } else {
                System.out.println("No cards found for the search term: " + searchTerm);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (magicCard != null) {
            System.out.println("Found card:");
            System.out.println("Name: " + magicCard.getName());
            System.out.println("Price: " + magicCard.getPrice());
            System.out.println("Image URL: " + magicCard.getImageUrl());
        }

        if (magicCard != null && exact == false) displayCard(magicCard);
        return magicCard;
    }

    public static Deck loadOrCreateExistingDeck(Scanner scanner) {
        System.out.println("Do you want to create a new deck or load an existing one? (new/load)");
        String choice = scanner.nextLine();
        if (choice.equalsIgnoreCase("new")) {
            System.out.println("Enter the name of the new deck: ");
            String deckName = scanner.nextLine();
            return new Deck(deckName);
        } else if (choice.equalsIgnoreCase("load")) {
            System.out.println("Enter the name of the existing deck: ");
            String deckName = scanner.nextLine();
            return loadDeckFromStorage(deckName);
        } else {
            System.out.println("Invalid choice. Creating a new deck by default.");
            System.out.println("Enter the name of the new deck: ");
            String deckName = "";
            if (scanner.hasNextLine()) {
                deckName = scanner.nextLine().trim();
            }
            return new Deck(deckName);
        }
    }


    public static Deck loadDeckFromStorage(String deckName) {
        String filename = deckName + ".ser";
        try (FileInputStream fileIn = new FileInputStream(filename);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            return (Deck) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading deck: " + e.getMessage());
            return null;
        }
    }

    public static void printDeck(Scanner scanner) {
        System.out.println("What is the name of the deck you would like to list?");
        String deckName = scanner.nextLine();
        Deck deck = Deck.deserialize(deckName);

        if (deck != null) {

            List<MagicCard> cards = deck.getCards();

            System.out.println("Cards in deck '" + deckName + "':");
            for (MagicCard card : cards) {
                System.out.println("Name: " + card.getName());
                System.out.println("Price: " + card.getPrice());
                System.out.println("Image URL: " + card.getImageUrl());
                System.out.println();
                displayCards(cards);
            }
        } else {
            System.out.println("Deck '" + deckName + "' not found.");
        }
    }

    public static void deleteDeck(Scanner scanner){
        System.out.println("What is the name of the deck you want to delete?");
        String deckName = scanner.nextLine() + ".ser";
        File file = new File(deckName);
        if (file.exists()){
            if (file.delete()){
                System.out.println("Deck " + deckName + " has been deleted successfully");
            }
            else
                System.out.println("Failed to delete deck " + deckName);
        }
        else
            System.out.println("Deck " + deckName + " doesn't exist");
    }




    public static void listAllCards() {
        String filename = "cards.ser";
        File file = new File(filename);
        if (file.exists()) {
            List<MagicCard> allCards = deserialize(filename);
            if (allCards != null) {
                for (MagicCard card : allCards) {
                    System.out.println("Name: " + card.getName());
                    System.out.println("Price: " + card.getPrice());
                    System.out.println("Image URL: " + card.getImageUrl());
                    System.out.println();
                }
                displayCards(allCards);
            } else {
                System.out.println("No cards found.");
            }
        } else {
            System.out.println("File doesn't exist.");
        }
    }

    private static void displayCards(List<MagicCard> cards) {
        JFrame frame = new JFrame("Magic Cards");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(0, 3));
        for (MagicCard card : cards) {
            displayCardImage(panel, card.getImageUrl());
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setSize(800, 600);
        frame.setVisible(true);
    }

    private static void displayCard(MagicCard card) {
        JFrame frame = new JFrame("Magic Cards");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(0, 3));
            displayCardImage(panel, card.getImageUrl());


        JScrollPane scrollPane = new JScrollPane(panel);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setSize(800, 600);
        frame.setVisible(true);
    }


    private static void displayCardImage(JPanel panel, String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            Image image = ImageIO.read(url);
            JLabel label = new JLabel(new ImageIcon(image));
            label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            panel.add(label);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static String extractName(JsonObject cardObject) {
        if (cardObject.has("name") && !cardObject.get("name").isJsonNull()) {
            return cardObject.get("name").getAsString();
        } else {
            return "Name not available";
        }
    }

    private static String extractPrice(JsonObject cardObject) {
        if (cardObject.has("prices") && !cardObject.get("prices").isJsonNull()) {
            JsonObject pricesObject = cardObject.getAsJsonObject("prices");
            if (pricesObject.has("usd") && !pricesObject.get("usd").isJsonNull()) {
                return pricesObject.get("usd").getAsString();
            } else {
                return "Price not available";
            }
        } else {
            return "Price not available";
        }
    }

    private static String extractImageUrl(JsonObject cardObject) {
        if (cardObject.has("image_uris") && !cardObject.get("image_uris").isJsonNull()) {
            JsonObject imageUrisObject = cardObject.getAsJsonObject("image_uris");
            if (imageUrisObject.has("normal") && !imageUrisObject.get("normal").isJsonNull()) {
                return imageUrisObject.get("normal").getAsString();
            } else {
                return "Image URL not available";
            }
        } else {
            return "Image URL not available";
        }
    }
}
