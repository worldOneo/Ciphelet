package network

import (
	"log"
	"net/http"

	"github.com/gorilla/websocket"
	"github.com/worldOneo/Ciphelet/authenticator"
	"github.com/worldOneo/Ciphelet/encryption"
	"github.com/worldOneo/Ciphelet/snowflake"
)

// ActionType types of actions defined by string
type ActionType string

// A definition of action types
const (
	RegisterAction  ActionType = "register"
	LoginAction     ActionType = "login"
	ChallengeAction ActionType = "challenge"
)

// Server the open interface for applications
type Server struct {
	upgrader      *websocket.Upgrader
	authenticator *authenticator.Authenticator
	sessions      map[snowflake.Snowflake]*Session
}

// Session has a websocket and information about the connected user
type Session struct {
	Ws         *websocket.Conn
	UserID     snowflake.Snowflake
	Challenge  string
	Challenged bool
	Closed     bool
}

type humanIdentified struct {
	HumanID string `json:"humanid,omitempty"`
}

type flaked struct {
	User snowflake.Snowflake `json:"userid,omitempty"`
}

type loginAction struct {
	humanIdentified
	Password string `json:"password,omitempty"`
}

type challengeAction struct {
	Token string `json:"token,omitempty"`
}

type publickeyAction struct {
	flaked
	Key string `json:"key,omitempty"`
}

type registerAction struct {
	Password string `json:"password,omitempty"`
	Key      string `json:"key,omitempty"`
}

type genericAction struct {
	Action          ActionType       `json:"action"`
	ChallengeAction *challengeAction `json:"challengeAction,omitempty"`
	PublickeyAction *publickeyAction `json:"publickeyAction,omitempty"`
	LoginAction     *loginAction     `json:"loginAction,omitempty"`
	RegisterAction  *registerAction  `json:"registerAction,omitempty"`
}

// NewServer creates a new Server
func NewServer(auth *authenticator.Authenticator) *Server {
	return &Server{
		upgrader: &websocket.Upgrader{
			ReadBufferSize:  1024,
			WriteBufferSize: 1024,
		},
		sessions:      make(map[snowflake.Snowflake]*Session),
		authenticator: auth,
	}
}

// Handler the websocket handler to handle the server
func (s *Server) Handler(w http.ResponseWriter, r *http.Request) {
	log.Println("Connected!")
	conn, err := s.upgrader.Upgrade(w, r, nil)
	if err != nil {
		log.Println(err)
		return
	}
	session := &Session{
		Ws:         conn,
		Challenge:  authenticator.GenerateHumanID(),
		Challenged: false,
		UserID:     0,
		Closed:     false,
	}
	conn.SetCloseHandler(func(code int, text string) error {
		session.Closed = true
		return nil
	})
	s.addSession(session)
}

func (s *Server) addSession(sess *Session) {
	conn := sess.Ws
	for !sess.Challenged {
		nextAction, err := getNextAction(conn)
		requiredPacket := genericAction{}
		requiredPacket.Action = LoginAction
		if err != nil || nextAction.Action != LoginAction {
			if err == nil && nextAction.Action == RegisterAction {
				register(nextAction.RegisterAction, sess)
				continue
			}
			if isSessionClosed(err, sess) {
				log.Print("Closed!")
				sess.Ws.Close()
				return
			}
			log.Printf("Didnt send login packet \"%v\" Acrion: \"%s\"", err, nextAction.Action)
			if sess.Closed {
				return
			}
			conn.WriteJSON(requiredPacket)
			continue
		}

		lAction := nextAction.LoginAction
		cUser, err := s.authenticator.GetUserKey(lAction.HumanID)
		if err != nil {
			log.Printf("Couldn't find user \"%v\"", err)
			if isSessionClosed(err, sess) {
				return
			}
			conn.WriteJSON(requiredPacket)
			continue
		}
		pubKey, err := encryption.GetPublicKey(cUser.Publickey)

		if err != nil {
			log.Printf("Couldn't find user key %v", err)
			if sess.Closed {
				return
			}
			conn.WriteJSON(requiredPacket)
		}
		challenge, err := encryption.Encrypt(pubKey, []byte(sess.Challenge))
		if err != nil {
			log.Printf("Couldn't encrypt %v", err)
			if sess.Closed {
				return
			}
			conn.WriteJSON(requiredPacket)
		}
		cAction := challengeAction{}
		cAction.Token = string(challenge)
		conn.WriteJSON(genericAction{Action: ChallengeAction, ChallengeAction: &cAction})
	}
}

// getNextAction awaits the next action from a socket
func getNextAction(conn *websocket.Conn) (genericAction, error) {
	action := createGenericAction()
	err := conn.ReadJSON(&action)
	return action, err
}

func isSessionClosed(err error, sess *Session) bool {
	return websocket.IsCloseError(err, 1000, 1001, 1002, 1003,
		1005, 1006, 1007, 1008, 1009,
		1010, 1011, 1012, 1013, 1015) || sess.Closed
}

func register(rAction *registerAction, sess *Session) {
	requiredPacket := genericAction{}
	requiredPacket.Action = LoginAction
	key, err := encryption.GetPublicKey(rAction.Key)
	if err != nil {
		log.Printf("Invalid public key: \"%v\"", err)
		sess.Ws.WriteJSON(requiredPacket)
		return
	}
	userChallenge, err := encryption.B64Encrypt(key, []byte(sess.Challenge))
	if err != nil {
		log.Printf("Unable to encrypt challenge, %v", err)
		sess.Ws.WriteJSON(requiredPacket)
		return
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
		return
	}
	if action.ChallengeAction.Token == sess.Challenge {
		log.Print("CLIENT VALID REGISTER HANDSHAKE!!!!")
		sess.Ws.WriteJSON(requiredPacket)
		return
	}
}

func createGenericAction() genericAction {
	return genericAction{
		ChallengeAction: &challengeAction{},
		LoginAction:     &loginAction{},
		PublickeyAction: &publickeyAction{},
		RegisterAction:  &registerAction{},
	}
}
