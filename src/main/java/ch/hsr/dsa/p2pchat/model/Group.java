package ch.hsr.dsa.p2pchat.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

public class Group implements Serializable {
    private String name;
    private Collection<User> members;

    public Group(String name, Collection<User> members) {
        this.name = name;
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public Collection<User> getMembers() {
        return Collections.unmodifiableCollection(members);
    }
}
