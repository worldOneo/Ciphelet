package network

import (
	"encoding/base64"
	"log"
	"math/rand"

	"github.com/worldOneo/Ciphelet/encryption"
)

func (s *Server) challenge(requiredPacket genericAction, sess *Session, publickey *[32]byte) (string, error) {
	//https://godoc.org/golang.org/x/crypto/nacl/box
	log.Print(base64.StdEncoding.EncodeToString(publickey[:]))
	sess.Ws.WriteJSON(genericAction{
		Action: ChallengeAction,
		ChallengeAction: &challengeAction{
			Token: encryption.B64Encrypt(publickey, s.privateKey, []byte(sess.Challenge)),
			PKey:  encryption.EncodeKey(s.publicKey),
		},
	})
	action, err := getNextAction(sess.Ws)
	if action.Action != ChallengeAction || err != nil {
		sess.Ws.WriteJSON(requiredPacket)
		return "", err
	}
	return action.ChallengeAction.Token, nil
}

const chars = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ"

func generateChallenge() string {
	b := make([]byte, 8)
	for i := 0; i < 8; i++ {
		b[i] = chars[rand.Intn(len(chars))]
	}
	return string(b)
}
