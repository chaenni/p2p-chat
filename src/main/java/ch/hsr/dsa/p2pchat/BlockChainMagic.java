package ch.hsr.dsa.p2pchat;

import ch.hsr.dsa.p2pchat.contractadapter.Chat;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;

public class BlockChainMagic {

    public static void main(String[] args) throws Exception {
        Web3j web3 = Web3j.build(new HttpService("https://ropsten.infura.io/v3/3de592e69c4e4c579efad4f1138a5e9e"));
        Credentials credentials = WalletUtils.loadCredentials("nopassword",
            BlockChainMagic.class.getClassLoader().getResource("wallet1.json").getPath());
        var contract = Chat.load("0x2bbc43cc49323e50b1d308f68f8a06279c1c533a", web3, credentials,
            new ContractGasProvider() {
                @Override
                public BigInteger getGasPrice(String contractFunc) {
                    return BigInteger.ONE;
                }

                @Override
                public BigInteger getGasPrice() {
                    return BigInteger.ONE;
                }

                @Override
                public BigInteger getGasLimit(String contractFunc) {
                    return BigInteger.valueOf(70_000);
                }

                @Override
                public BigInteger getGasLimit() {
                    return BigInteger.valueOf(70_000);
                }
            });
        //contract.sendMessage(hashString("abc")).send();
        contract.acceptMessage(hashString("abc")).send();
        System.out.println(contract.getMessageState(hashString("cde")).send());
        web3.shutdown();
    }

    private static byte[] hashString(String string) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(string.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
