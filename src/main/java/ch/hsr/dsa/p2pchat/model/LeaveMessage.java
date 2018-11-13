package ch.hsr.dsa.p2pchat.model;

public class LeaveMessage implements Message {
    private User user;
    private Group group;

    public LeaveMessage(User user, Group group) {
        this.user = user;
        this.group = group;
    }

    public User getUser() {
        return user;
    }

    public Group getGroup() {
        return group;
    }

    @Override
    public String toString() {
        return "LeaveMessage{" +
            "user=" + user +
            ", group=" + group +
            '}';
    }
}
