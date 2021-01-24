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
- [X] Fetching groups
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
- [X] Fetching groups
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
As private/publickey a EC 256 bit key.
It uses NaCL to encrypt boxes based on that.