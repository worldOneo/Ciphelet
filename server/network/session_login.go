package network

import (
	"log"

	"github.com/worldOneo/Ciphelet/encryption"
)

func (s *Server) addSession(sess *Session) {
	conn := sess.Ws
	for !sess.Challenged {
		nextAction, err := getNextAction(conn)
		requiredPacket := genericAction{}
		requiredPacket.Action = LoginAction
		if err != nil || nextAction.Action != LoginAction {
			if err == nil && nextAction.Action == RegisterAction {
				s.register(nextAction.RegisterAction, sess)
				continue
			}
			if isSessionClosed(err, sess) {
				log.Print("Closed!")
				sess.Ws.Close()
				return
			}
			log.Printf("Didnt send login packet \"%v\" Acrion: \"%s\"", err, nextAction.Action)
			conn.WriteJSON(requiredPacket)
			continue
		}
		lAction := nextAction.LoginAction
		cUser, err := s.authenticator.GetUserKey(lAction.HumanID)
		if err != nil {
			log.Printf("Couldn't find user '%s': \"%v\"", lAction.HumanID, err)
			conn.WriteJSON(requiredPacket)
			continue
		}
		pubKey, err := encryption.GetPublicKey(cUser.Publickey)
		if err != nil {
			log.Printf("Couldn't find user key %v", err)
			conn.WriteJSON(requiredPacket)
		}

		token, err := challenge(requiredPacket, sess, pubKey)
		if err != nil || token != sess.Challenge {
			log.Printf("Couldn't encrypt %v", err)
			conn.WriteJSON(requiredPacket)
			continue
		}
		log.Printf("User: %v logged in", cUser.HumanID)
		sess.Challenged = true
		return
	}
	sess.Ws.Close()
	return
}
