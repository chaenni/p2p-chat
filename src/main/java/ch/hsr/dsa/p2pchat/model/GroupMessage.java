package ch.hsr.dsa.p2pchat.model;

public class GroupMessage extends ChatMessage {
    private Group group;

    public GroupMessage(User fromUser, String message, Group group) {
        super(fromUser, message);
        this.group = group;
    }

    public Group getGroup() {
        return group;
    }
}
