package ch.hsr.dsa.p2pchat;

import ch.hsr.dsa.p2pchat.contractadapter.Chat;
import ch.hsr.dsa.p2pchat.model.MessageState;
import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.Observable;
import java.io.IOException;
import java.math.BigInteger;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;

public class EthereumAdapter {

    private static final String TEST_NET_ENDPOINT = "https://ropsten.infura.io/v3/3de592e69c4e4c579efad4f1138a5e9e";
    private static final String CONTRACT_ADDRESS = "0x6F3a6E2450C1DF6F044B98c5fF29a21D3082f8A9";
    private final Web3j web3;
    private final Chat contract;

    private static final int GWEI = 1_000_000_000;

    public EthereumAdapter(String walletJsonPath, String walletPassword) {
        web3 = Web3j.build(new HttpService(TEST_NET_ENDPOINT));
        try {
            Credentials credentials = WalletUtils.loadCredentials(walletPassword, walletJsonPath);
            contract = Chat.load(CONTRACT_ADDRESS, web3, credentials, contractGasProvider(100_000, GWEI));
        } catch (IOException | CipherException e) {
            throw new RuntimeException(e);
        }
    }

    public Observable<TransactionReceipt> sendMessage(byte[] hash) {
        return RxJavaInterop.toV2Observable(contract.sendMessage(hash).observable());
    }

    public Observable<TransactionReceipt> acceptMessage(byte[] hash) {
        return RxJavaInterop.toV2Observable(contract.acceptMessage(hash).observable());
    }

    public Observable<TransactionReceipt> rejectMessage(byte[] hash) {
        return RxJavaInterop.toV2Observable(contract.rejectMessage(hash).observable());
    }

    public Observable<MessageState> getMessageState(byte[] hash) {
        return RxJavaInterop.toV2Observable(contract.getMessageState(hash)
            .observable()
            .map(index -> MessageState.values()[index.intValue()]));

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
