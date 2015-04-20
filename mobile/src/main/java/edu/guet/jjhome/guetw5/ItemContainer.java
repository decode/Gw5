package edu.guet.jjhome.guetw5;

import java.util.ArrayList;

import edu.guet.jjhome.guetw5.model.Item;

public class ItemContainer {
    public int count;
    public ArrayList<Item> items = new ArrayList<>();

    public ItemContainer() { count = 0; }

    public void add(Item item) {
        items.add(item);
        count++;
    }
}
