# Ciphelet
Ciphelet is an [E2EE](https://en.wikipedia.org/wiki/End-to-end_encryption) Chat client which aims for a secure chat and wants to offer the ability of "forgetting" you in under 15 Seconds, deleting everything you wrote.

## Goals
### Server (Gateway)
- [X] Registration 
- [X] Login
- [ ] Sending messages
- [ ] Forwarding messages
- [ ] Creating groups
- [ ] Fetching keys
- [ ] Fetching groups
- [ ] Session manager for multiple gateways
- [ ] "Forget me"

### Server (Session manager)
- [ ] Forward messages

### Client (logic)
- [X] Registration
- [X] Login
- [ ] Sending messages
- [ ] Forwarding messages
- [ ] Creating groups
- [ ] Fetching keys
- [ ] Fetching groups
- [ ] "Forget me"

### Client (GUI)
- [X] Registration
- [X] Login
- [ ] Sending messages
- [ ] Creating groups
- [ ] "Forget me"

## Implementation
Details for the secruity implementation can be found in `SecruityWorkflow.md`

# Encryption
The Ciphelet client uses a Hybrid encryption.
It uses a combination of RSA 2048 OAEP and AES-256 GCM, the strongest commonly available (and fast enough) encryptions.

## How?
The idea is that if a client (Alice) wants to write another client (Bob) Alice fetches Bob's publickey from the server creates a AES-256 Key encrypts it with bobs public key and sends the encrypted AES key back to the server. Now Bob and Alice have a shared secret which can encrypt messages reasonable fast.