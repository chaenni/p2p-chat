package ch.hsr.dsa.p2pchat.model;

public class GroupMessage implements Message {
    private Group group;
    private User fromUser;
    private String message;

    public GroupMessage(User fromUser, String message, Group group) {
        this.fromUser = fromUser;
        this.message = message;
        this.group = group;
    }
    public User getFromUser() {
        return fromUser;
    }

    public String getMessage() {
        return message;
    }


    public Group getGroup() {
        return group;
    }
}
