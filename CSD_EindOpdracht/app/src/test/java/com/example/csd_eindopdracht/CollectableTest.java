package com.example.csd_eindopdracht;

import com.example.csd_eindopdracht.dataModel.collectable.Collectable;
import com.example.csd_eindopdracht.dataModel.collectable.YugiohCollectable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CollectableTest {
    Collectable collectable;
    Collectable darkMagician;

    @Before
    public void initialise(){
        collectable = new YugiohCollectable("test","test", 0, "test", "test");
        darkMagician = new YugiohCollectable("Dark Magician", "https://ygoprodeck.com/pics/46986414.jpg", 7, "46986414", "The ultimate wizard in terms of attack and defense.\n");
    }

    @Test
    public void createDarkMagician_Test(){
        collectable.setName("Dark Magician");
        collectable.setImgLink("https://ygoprodeck.com/pics/46986414.jpg");
        collectable.setLevel(7);
        collectable.setId("46986414");
        collectable.setDescription("The ultimate wizard in terms of attack and defense.\n");

        Assert.assertEquals(darkMagician.getName(), collectable.getName());
        Assert.assertEquals(darkMagician.getImgLink(), collectable.getImgLink());
        Assert.assertEquals(darkMagician.getLevel(), collectable.getLevel());
        Assert.assertEquals(darkMagician.getId(), collectable.getId());
        Assert.assertEquals(darkMagician.getDescription(), collectable.getDescription());
    }
}
