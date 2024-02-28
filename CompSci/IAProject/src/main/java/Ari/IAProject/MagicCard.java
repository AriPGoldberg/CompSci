package Ari.IAProject;

import java.io.Serializable;

public class MagicCard implements Serializable {
    private static final long serialVersionUID = 5852912073948438763L;
    private String name;
    private String price;
    private String imageUrl;



    public MagicCard(String name, String price, String imageUrl) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }


    public String getImageUrl() {
        return imageUrl;
    }


    @Override
    public String toString() {
        return "Name: " + name + ", Price: " + price + ", Image URL: " + imageUrl;
    }

}

