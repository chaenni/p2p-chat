 # P2P Chat Application for Assignment for Module Distributed Systems Advanced
 
 ## Run
 1. Run the Main Class ch.hsr.dsa.p2pchat.Main
 1. You will be asked some questions you need to answer
 1. Once you are connected you see your ip and port which you can give to other people who can use this data to join
 1. once the chat is running type /help to get a list of available commands
 1. Happy chatting

## How to Generate a Wallet?
1. To be able to store data in the ethereum blockchain, a wallet file is required. Follow these instructions to generate one: https://docs.web3j.io/command_line.html#wallet-tools
1. Transfer ethers to your wallet e.g. using Metamask
1. When using Metamask, you can get ethers here https://faucet.metamask.io/  

## How to change contract?
1. Copy changed contract from src/main/resources/Chat.sol to remix IDE
1. Click deploy
1. Click on the etherscan link displayed on bottom panel of remix
1. Wait for block to be mined, copy contract address from there
1. Paste new contract address to everywhere where it is used in the project
1. Generate java wrapper with Web3
    1. `> cd src/main/resources`
    1. `> solc Chat.sol --bin --abi --optimize -o . --overwrite`
    1. `> web3j solidity generate Chat.bin Chat.abi -o ../java/ -p ch.hsr.dsa.p2pchat.contractadapter` 