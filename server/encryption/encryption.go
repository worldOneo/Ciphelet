package encryption

import (
	"crypto/rand"
	"encoding/base64"
	"fmt"
	"io"

	"golang.org/x/crypto/nacl/box"
)

// GetPublicKey creates a publickey based of an b64 encoded string
func GetPublicKey(key string) (*[32]byte, error) {
	var pkey [32]byte
	deced, err := base64.StdEncoding.DecodeString(key)
	if err != nil {
		return nil, err
	}
	if len(deced) != 32 {
		return nil, fmt.Errorf("wrong input key size. length: %d expected 32", len(deced))
	}

	copy(pkey[:], deced)
	return &pkey, nil
}

// B64Encrypt encrypts a message with NaCL and returns the base64 value
func B64Encrypt(theirPublicKey, ourPrivateKey *[32]byte, msg []byte) string {
	var nonce [24]byte
	io.ReadFull(rand.Reader, nonce[:])
	encrypted := box.Seal(nonce[:], msg, &nonce, theirPublicKey, ourPrivateKey)
	return base64.StdEncoding.EncodeToString(encrypted)
}

// EncodeKey encodes the keys N
func EncodeKey(key *[32]byte) string {
	return base64.StdEncoding.EncodeToString(key[:])
}
