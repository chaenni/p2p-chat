package ch.hsr.dsa.p2pchat.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class Group implements Serializable {
    private String name;
    private Collection<User> members;

    public Group(String name, Collection<User> members) {
        this.name = name;
        this.members = members;
    }

    public Group(String name) {
        this(name, Collections.EMPTY_LIST);
    }


    public String getName() {
        return name;
    }

    public Collection<User> getMembers() {
        return Collections.unmodifiableCollection(members);
    }

    @Override
    public String toString() {
        return "Group{" +
            "name='" + name + '\'' +
            ", members=" + members.stream().map(Object::toString).collect(Collectors.joining(",")) +
            '}';
    }
}
