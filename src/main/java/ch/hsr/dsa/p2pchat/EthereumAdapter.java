package ch.hsr.dsa.p2pchat;

import ch.hsr.dsa.p2pchat.contractadapter.Chat;
import java.io.IOException;
import java.math.BigInteger;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import rx.Observable;

public class EthereumAdapter {

    private static final String TEST_NET_ENDPOINT = "https://ropsten.infura.io/v3/3de592e69c4e4c579efad4f1138a5e9e";
    private static final String CONTRACT_ADDRESS = "0x6F3a6E2450C1DF6F044B98c5fF29a21D3082f8A9";
    private final Web3j web3;
    private final Chat contract;

    public EthereumAdapter(String walletJsonPath, String walletPassword) {
        web3 = Web3j.build(new HttpService(TEST_NET_ENDPOINT));
        try {
            Credentials credentials = WalletUtils.loadCredentials(walletPassword, walletJsonPath);
            contract = Chat.load(CONTRACT_ADDRESS, web3, credentials, contractGasProvider(70_000, 1));
        } catch (IOException | CipherException e) {
            throw new RuntimeException(e);
        }
    }

    public Observable<TransactionReceipt> sendMessage(byte[] hash) {
        return contract.sendMessage(hash).observable();
    }

    public Observable<TransactionReceipt> acceptMessage(byte[] hash) {
        return contract.acceptMessage(hash).observable();
    }

    public Observable<TransactionReceipt> rejectMessage(byte[] hash) {
        return contract.rejectMessage(hash).observable();
    }

    public Observable<Boolean> isSent(byte[] hash) {
        return contract.getMessageState(hash)
            .observable()
            .map(i -> i.intValue() > 0);
    }

    public Observable<Boolean> isAccepted(byte[] hash) {
        return contract.getMessageState(hash)
            .observable()
            .map(i -> i.intValue() == 2);
    }

    public Observable<Boolean> isRejected(byte[] hash) {
        return contract.getMessageState(hash)
            .observable()
            .map(i -> i.intValue() == 3);
    }

    public void shutdown() {
        web3.shutdown();
    }

    private static  ContractGasProvider contractGasProvider(int gasLimit, int gasPrice) {
        return new ContractGasProvider() {
            @Override
            public BigInteger getGasPrice(String contractFunc) {
                return BigInteger.valueOf(gasPrice);
            }

            @Override
            public BigInteger getGasPrice() {
                return BigInteger.valueOf(gasPrice);
            }

            @Override
            public BigInteger getGasLimit(String contractFunc) {
                return BigInteger.valueOf(gasLimit);
            }

            @Override
            public BigInteger getGasLimit() {
                return BigInteger.valueOf(gasLimit);
            }
        };
    }
}
