# The Ethereum Fetcher - REST Server

## Docker Setup
To launch the application in Docker, use the command:
```sh
docker compose up
```
This command starts multiple services, including the necessary dependencies for the REST server.

**Note**: A sample working transaction hash from Infura is provided, as none of the previously described transaction hashes worked during testing:
```
transactionHashes=0xbb3a336e3f823ec18197f1e13ee875700f08f03e2cab75f0d0b118dabb44cba0
```

## Description & Requirements
Your task is to create a REST API server that returns information for certain Ethereum transactions identified by their transaction hashes.

### Endpoint: `/lime/eth?transactionHashes`
The server should handle a GET request on an endpoint with the path `/lime/eth`.

It should have a single parameter `transactionHashes` - a list containing transaction hash strings. The server is expected to fetch the transactions from Ethereum and return a response in the following format:

```json
{
  "transactions": [
    {
      "transactionHash": "string", // the hex encoded transaction hash of the transaction
      "transactionStatus": "number", // the status of the transaction either 1 (success) or 0 (failure)
      "blockHash": "string", // the hex encoding of the hash of the block the transaction was included in
      "blockNumber": "number", // the number of the block the transaction was included in
      "from": "string", // the Ethereum address of the transaction sender
      "to": "string|null", // the Ethereum address of the transaction receiver or null when it's a contract creation transaction
      "contractAddress": "string|null", // the Ethereum address of the newly created contract if this transaction is contract creation
      "logsCount": "number", // number of log objects which this transaction generated
      "input": "string", // the hex encoding of the data sent along with the transaction
      "value": "string" // the value transferred in wei
    },
    {
      ...
    }
  ]
}
```

### Saving Information About Fetched Transactions in a Database
The server saves the information for each transaction fetched from Ethereum into a PostgreSQL database named `postgres`.

Subsequent calls for a transaction identified by its hash should fetch the information from the database instead of querying the Ethereum node.

**Note**: Running the application should create all the necessary tables automatically.

### Endpoint: `/lime/all`
The server should handle a GET request at an endpoint named `/lime/all`. It should have no required parameters and return a list of all transactions saved in the database. The response format should be the same as `/lime/eth`.

### [Optional] Endpoint: `/lime/eth/:rlphex`
The server should handle a GET request at an endpoint with the path `/lime/eth/:rlphex`.

It should have a single parameter `rlphex` - a hexadecimal representation of an RLP-encoded list of transaction hashes. This endpoint is an upgrade over the first one, with the only difference being the method of gathering transaction hashes: here, you need to decode the RLP list to get them.

## Running the Server

### Environment Variables
The server should support the following environment variables:
- `API_PORT`: The port where the API will be listening for requests.
- `ETH_NODE_URL`: URL to an Ethereum node that will be used for polling.
- `DB_CONNECTION_URL`: URL for connecting to your database (you may include username and password in the URL or have them as separate environment variables).
- `JWT_SECRET`: The JWT secret used for the authentication part of the task.

Any other environment variables you use should have default values and be optional.

### Running and Stopping the Server

The server should be run by the following command:
- **Java**: `gradle bootRun`

The server should be stopped by sending a kill signal or using `Ctrl+C`.

## Documentation
Replace this readme with the documentation of the project outlining:

- **Architecture of the Server**: Design decisions and overview.
- **How to Run the Server**: Detailed steps for setting up and running the server.
- **Requests and Responses**: Describe available API endpoints, including parameters and expected responses.

## Example Requests and Responses
The following example request-response pairs are expected on the Ethereum Sepolia network:

### Endpoint: `/lime/eth?transactionHashes`
**Request**:
```sh
curl -X GET "http://127.0.0.1:{PORT}/lime/eth?transactionHashes=0xbb3a336e3f823ec18197f1e13ee875700f08f03e2cab75f0d0b118dabb44cba0"
```
**Response**:
```json
{
  "transactions": [
    {
      "transactionHash": "0xbb3a336e3f823ec18197f1e13ee875700f08f03e2cab75f0d0b118dabb44cba0",
      "transactionStatus": 1,
      "blockHash": "0xabc123...",
      "blockNumber": 1234567,
      "from": "0xabcde123...",
      "to": "0x12345abcd...",
      "contractAddress": null,
      "logsCount": 3,
      "input": "0x1234abcd...",
      "value": "500000000000000000"
    }
  ]
}
```

### Endpoint: `/lime/all`
**Request**:
```sh
curl -X GET "http://127.0.0.1:{PORT}/lime/all"
```
**Response**:
```json
{
  "transactions": [
    {
      "transactionHash": "0xbb3a336e3f823ec18197f1e13ee875700f08f03e2cab75f0d0b118dabb44cba0",
      "transactionStatus": 1,
      "blockHash": "0xabc123...",
      "blockNumber": 1234567,
      "from": "0xabcde123...",
      "to": "0x12345abcd...",
      "contractAddress": null,
      "logsCount": 3,
      "input": "0x1234abcd...",
      "value": "500000000000000000"
    }
  ]
}
```

## Running the Server Locally
Make sure to use the appropriate environment variables to configure your PostgreSQL database and Infura Ethereum node URL before running the server.

To run the server locally:
1. Configure the environment variables in your `.env` file or pass them while starting the application.
2. Use `gradle bootRun` to start the server.

