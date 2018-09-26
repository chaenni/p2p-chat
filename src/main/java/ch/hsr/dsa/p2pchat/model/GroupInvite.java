package ch.hsr.dsa.p2pchat.model;

public class GroupInvite implements Message {
    private String groupName;

    public GroupInvite(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
