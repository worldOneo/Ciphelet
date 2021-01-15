package network

import (
	"crypto/rsa"
	"log"

	"github.com/worldOneo/Ciphelet/encryption"
)

func challenge(requiredPacket genericAction, sess *Session, key *rsa.PublicKey) (string, error) {
	userChallenge, err := encryption.B64Encrypt(key, []byte(sess.Challenge))
	if err != nil {
		log.Printf("Unable to encrypt challenge, %v", err)
		sess.Ws.WriteJSON(requiredPacket)
		return "", err
	}
	sess.Ws.WriteJSON(genericAction{
		Action: ChallengeAction,
		ChallengeAction: &challengeAction{
			Token: userChallenge,
		},
	})
	action, err := getNextAction(sess.Ws)
	if action.Action != ChallengeAction || err != nil {
		sess.Ws.WriteJSON(requiredPacket)
		return "", err
	}
	return action.ChallengeAction.Token, nil
}
