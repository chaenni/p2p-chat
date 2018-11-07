package ch.hsr.dsa.p2pchat.contractadapter;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.6.0.
 */
public class Chat extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b506101e2806100206000396000f3006080604052600436106100615763ffffffff7c01000000000000000000000000000000000000000000000000000000006000350416630766a799811461006657806377ffe162146100805780639241f0bc14610098578063e12c9ca8146100d4575b600080fd5b34801561007257600080fd5b5061007e6004356100ec565b005b34801561008c57600080fd5b5061007e600435610139565b3480156100a457600080fd5b506100b0600435610182565b604051808260038111156100c057fe5b60ff16815260200191505060405180910390f35b3480156100e057600080fd5b5061007e600435610197565b600160008281526020819052604090205460ff16600381111561010b57fe5b1461011557600080fd5b600081815260208190526040902080546002919060ff19166001835b021790555050565b600160008281526020819052604090205460ff16600381111561015857fe5b1461016257600080fd5b600081815260208190526040902080546003919060ff1916600183610131565b60009081526020819052604090205460ff1690565b600081815260208190526040902080546001919060ff191682806101315600a165627a7a72305820cd8bc5bdbea41ee81bfafe8198d77fd053e7d11b75d0567d09aefff4b49b309b0029";

    public static final String FUNC_ACCEPTMESSAGE = "acceptMessage";

    public static final String FUNC_REJECTMESSAGE = "rejectMessage";

    public static final String FUNC_GETMESSAGESTATE = "getMessageState";

    public static final String FUNC_SENDMESSAGE = "sendMessage";

    @Deprecated
    protected Chat(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Chat(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Chat(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Chat(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteCall<TransactionReceipt> acceptMessage(byte[] hash) {
        final Function function = new Function(
                FUNC_ACCEPTMESSAGE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(hash)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> rejectMessage(byte[] hash) {
        final Function function = new Function(
                FUNC_REJECTMESSAGE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(hash)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> getMessageState(byte[] hash) {
        final Function function = new Function(FUNC_GETMESSAGESTATE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(hash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> sendMessage(byte[] hash) {
        final Function function = new Function(
                FUNC_SENDMESSAGE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(hash)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static RemoteCall<Chat> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Chat.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Chat> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Chat.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<Chat> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Chat.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Chat> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Chat.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    @Deprecated
    public static Chat load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Chat(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Chat load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Chat(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Chat load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new Chat(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Chat load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Chat(contractAddress, web3j, transactionManager, contractGasProvider);
    }
}
