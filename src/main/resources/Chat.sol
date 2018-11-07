pragma solidity ^0.4.0;
contract Chat {

    enum MessageState {NOT_EXIST, SENT, ACCEPTED, REJECTED}

    mapping (bytes32 => MessageState) messages;

    function sendMessage(bytes32 hash) public {
        messages[hash] = MessageState.SENT;
    }

    function getMessageState(bytes32 hash) public view returns (MessageState) {
        return messages[hash];
    }

    function acceptMessage(bytes32 hash) public {
        require(messages[hash] == MessageState.SENT);
        messages[hash] = MessageState.ACCEPTED;
    }

    function rejectMessage(bytes32 hash) public {
        require(messages[hash] == MessageState.SENT);
        messages[hash] = MessageState.REJECTED;
    }
}