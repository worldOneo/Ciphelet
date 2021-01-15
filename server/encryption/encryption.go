package encryption

import (
	"crypto/rand"
	"crypto/rsa"
	"crypto/sha256"
	"crypto/x509"
	"encoding/base64"
)

// GetPublicKey creates a publickey based of an b64 encoded string
func GetPublicKey(key string) (*rsa.PublicKey, error) {
	bytes, err := base64.StdEncoding.DecodeString(key)
	if err != nil {
		return nil, err
	}

	pKey, err := x509.ParsePKIXPublicKey(bytes)
	if err != nil {
		return nil, err
	}
	return pKey.(*rsa.PublicKey), nil
}

//Encrypt encrypts a message with a rsa PublicKey
func Encrypt(key *rsa.PublicKey, msg []byte) ([]byte, error) {
	return rsa.EncryptOAEP(sha256.New(), rand.Reader, key, msg, nil)
}

// B64Encrypt encrypts a message with a rsa PublicKey and returns the base64 value
func B64Encrypt(key *rsa.PublicKey, msg []byte) (string, error) {
	bs, err := Encrypt(key, msg)
	if err != nil {
		return "", err
	}
	return base64.StdEncoding.EncodeToString(bs), nil
}
