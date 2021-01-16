# Terms

### HumanID
A 8 letter (64 bit) mixed case string of all alphabetic letters except L,I and O to avoid confusion in some fonts

### UserID
A snowflak identifying a user.

### Snowflake
64 bit unsiged int used as a UID its layout is:
First bit unused due to signing
42 Bits time stamp in ms (reaching a span of 139 Years) starting at the apps epoch (Custom epoch)
10 Bits worker ID every server has a unique worker id preventing multiple servers from generating the same flake
11 Bits sequence counting up preventing double snowflakes in one ms
We can generate 2048 Snowflakes / Worker / Ms over 139 Years

### \*Key
A private / publik key (or in combination "key pair") is a RSA key of 2048 bit strength.
The private key is **never** shared over the internet or other method.

### Session Challenge
A session is connection based.
To verify that the client is the one who has the private key (as everything else would be useless)
the server sends a small encrypted string (with the clients publickey) to the client 
the client is now challenged to decrypt it. If it returns the decrypted session challenge correct (30 sec timeout)
the sessions connection is valid.

# Verification: Login and Sign up

## Client
### Sign up
First the client generates a KeyPair.
The client opens a websocket to the server.
A user sends his password and his Publickey to the server.
The server then will return a HumanID associated with that password and session challenge encrypted by the publickey.
The client must decrypt the session challenge and return it to the server to validate the session.
This proves that the client has the privat key.

### Login
The client opens a websocket to the server
The client sends his HumanID and Password to the server.
The server response with a session challenge encrypted by the clients associated publickey if the password is correct.
The client must decrypt the session challenge and return it to the server to validate the session.

## Server
### Sign up
The server recieves the clients password and publickey.
The server generates a HumanID for the client and a UserID.
It inserts in the UserID and HumanID (both keys) in a table.
It inserts the Publickey with the UserID as key in a table.
It inserts the Hashed password with the UserID as key in a table.
The server creates a session, encrypts the session challenge with the users publickey and sends it with the HumanID back.

### Login
The server recieves the HumanID and the Password of the Client.
It selects the users snowflake based on his HumanID. (if not found response)
It selects the users hash and compares it with the password. (if password wrong response)
It selects the users publickey.
It creates a session and sends the session challenge encrypted with the publickey.
The client validates the session by sending back the decrypted challenge.

# Actions
Actions are transmitted as JSON-Object over the session connection
the actions are:

## register
`parameter: password, key`  
`response: humanID, userID`  
The clients sends his password and publickey to the server if both of are valid the server challenges the client.
if the client is able to solve the challenge the server will add the client
and respons with the new clients credentials.

## login
`parameter: humanid, password`  
The clients sends a login action with its human id and a password.
If the login fails the server will respond with a login action with an empty password and empty humanid
if the login is sucessfull it will respond with a challenge action

## challenge 
`parameter: token`  
If the client recieves a challenge action it has to decrypt the token and response
to the server with a challenge action with the decrypted token to verify the current connection.

## lookup
`parameter: humanid`  
`return: key, userid`  
if the server recieves the publickey action, the action has to contain the parameter human id.
It will then lookup the key and responds with a publickey action containing the human id and the publickey.

## creategroup
`parameter: []{userid, encryptedgroupkey}`  
Every chat is a group.
Private chats are groups with only 2 members.
The group owner encryps the groups key (512bits) with each members publickey and sends them to the server.

## messagesend _only client->server_
`parameter: encryptedmessage, chatid (reciever)`  
The client generates a secure AES-512 bit key and encrypts the message with that key.
The client than encrypts the AES key with the recievers publickey and sends it of to the server.

## messagerecieve _only server->client_
`parameter: message, chatid`  
The client recieves this message if it is currently connected and another clients sends a message to the clients HumanID

## messagefetch
`parameter: chatid, time`  
`return: []messages`  
If the client wants to fetch a chat based on its humanid it needs to send a messagefetch action.
The server will return the last 50 messages sent before that time.
The messages are all encrypted individually and need to be decrypted by the client.

## chatfetch
`noparmeter`  
`return: []chatid`  
chatfetch returns every chatid of the group the client has chatted with.

## groupfetch
`parameter: chatid`  
`return: []userid` 
groupfetch returns every user of a group

# Tables

## user_id (HumanID -> UserID)
This table maps the Human-Readable ids to the Snowflakes of the user
```SQL
CREATE TABLE user (
    user_id bigint,
    human_id text,
    PRIMARY KEY (user_id)
);
```

## user_credentials (UserID -> Hash)
This table stores the hashes of the users.
```SQL
CREATE TABLE user_credentials (
    user_id bigint,
    human_id text,
    pw_hash text,
    public_key text,
    PRIMARY KEY (human_id)
);
```

## chat_keys
This table stores the key generated for a group.
```SQL
CREATE TABLE chat_keys (
    chat_id bigint,
    user_id bigint,
    key_id bigint,
    chat_key text,
    PRIMARY KEY(chat_id, user_id, key_id)
);
```

## chat_messages
This table stores the messages written.
```SQL
CREATE TABLE chat_messages (
    chat_id bigint,
    bucket int,
    message_id bigint,
    author_id bigint,
    message text,
    PRIMARY KEY((chat_id, bucket), message_id)
);
```

## chats
This table stores who is in which chat
```SQL
CREATE TABLE chats (
    user_id bigint,
    chat_id bigint,
    PRIMARY KEY(user_id, chat_id)
);
```