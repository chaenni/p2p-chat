package ch.hsr.dsa.p2pchat;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

public class BlockChainMagic {

    public static void main(String[] args) {
        Web3j web3 = Web3j.build(new HttpService("https://ropsten.infura.io/v3/3de592e69c4e4c579efad4f1138a5e9e"));

        web3.web3ClientVersion().observable().subscribe(x -> {
            String clientVersion = x.getWeb3ClientVersion();
            System.out.println(clientVersion);
        });
    }
}
