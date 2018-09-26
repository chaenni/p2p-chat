package ch.hsr.dsa.p2pchat.model;

import java.io.Serializable;

public class Group implements Serializable {
    private String name;

    public Group(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
