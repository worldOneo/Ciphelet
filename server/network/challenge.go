package network

import (
	"github.com/worldOneo/Ciphelet/encryption"
)

func (s *Server) challenge(requiredPacket genericAction, sess *Session, publickey *[32]byte) (string, error) {
	//https://godoc.org/golang.org/x/crypto/nacl/box
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
