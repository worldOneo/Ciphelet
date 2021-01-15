package network

import (
	"log"

	"github.com/worldOneo/Ciphelet/encryption"
)

func (s *Server) register(rAction *registerAction, sess *Session) {
	requiredPacket := genericAction{}
	requiredPacket.Action = LoginAction
	password := rAction.Password
	key, err := encryption.GetPublicKey(rAction.Key)
	if err != nil {
		log.Printf("Invalid public key: \"%v\"", err)
		sess.Ws.WriteJSON(requiredPacket)
		return
	}
	token, err := challenge(requiredPacket, sess, key)
	if err == nil && token == sess.Challenge {
		sess.Challenged = true
		sKey, err := encryption.EncodeKey(key)
		if err != nil {
			sess.Ws.WriteJSON(requiredPacket)
			return
		}
		log.Print("CLIENT VALID REGISTER HANDSHAKE!!!!")
		userid, err := s.authenticator.RegisterUser(password, sKey)
		if err != nil {
			sess.Ws.WriteJSON(requiredPacket)
			return
		}
		requiredPacket.Action = RegisterAction
		requiredPacket.RegisterAction = &registerAction{}
		requiredPacket.RegisterAction.HumanID = userid.HumanID
		requiredPacket.RegisterAction.User = userid.Snowflake
		sess.Ws.WriteJSON(requiredPacket)
	}
	if err != nil {
		sess.Ws.WriteJSON(requiredPacket)
		return
	}
}
