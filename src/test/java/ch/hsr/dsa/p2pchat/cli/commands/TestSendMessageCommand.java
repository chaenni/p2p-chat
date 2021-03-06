package ch.hsr.dsa.p2pchat.cli.commands;

import ch.hsr.dsa.p2pchat.ChatHandler;
import ch.hsr.dsa.p2pchat.model.User;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class TestSendMessageCommand {

    @Test
    public void test() {
        var handler = mock(ChatHandler.class);
        var cmd = new SendMessageCommand();
        var userName ="user";
        var message = "this is a complicated message 12345! --";

        cmd.run((s) -> {}, handler, "/" + cmd.getName() + " " + userName + " " + message);
        verify(handler, atLeast(1)).sendMessage(new User(userName), message );
    }

    @Test
    public void testNoEndingWhiteSpace() {
        var handler = mock(ChatHandler.class);
        var cmd = new SendMessageCommand();
        var userName ="user";
        var message = "this";

        cmd.run((s) -> {}, handler, "/" + cmd.getName() + " " + userName + " " + message);
        verify(handler, atLeast(1)).sendMessage(new User(userName), message );
    }

    @Test
    public void testWhitespaces() {
        var handler = mock(ChatHandler.class);
        var cmd = new SendMessageCommand();
        var userName ="user";
        var message = "this is a complicated message 12345! --";

        cmd.run((s) -> {}, handler,"/" + cmd.getName() + "    " + userName + "     " + message);
        verify(handler, atLeast(1)).sendMessage(new User(userName), message );
    }
}
