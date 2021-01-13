package network

import (
	"log"
	"net/http"

	"github.com/worldOneo/messenger/authenticator"

	"github.com/worldOneo/messenger/snowflake"

	"github.com/gorilla/websocket"
)

// ActionType types of actions defined by string
type ActionType string

// A definition of action types
const (
	LoginAction     ActionType = "login"
	ChallengeAction ActionType = "challene"
)

// Server the open interface for applications
type Server struct {
	upgrader      *websocket.Upgrader
	authenticator *authenticator.Authenticator
	sessions      map[string]*Session
}

// Session has a websocket and information about the connected user
type Session struct {
	Ws         *websocket.Conn
	UserID     snowflake.Snowflake
	Challenge  string
	Challenged bool
}

type humanIdentified struct {
	HumanID string `json:"humanid"`
}

type loginAction struct {
	humanIdentified
	Password string `json:"password"`
}

type challengeAction struct {
	Token string `json:"token"`
}

type publickeyAction struct {
	humanIdentified
	Key string `json:"key"`
}

type genericAction struct {
	Action ActionType `json:"action"`
	humanIdentified
	challengeAction
	publickeyAction
	loginAction
}

// NewServer creates a new Server
func NewServer(auth *authenticator.Authenticator) *Server {
	return &Server{
		upgrader: &websocket.Upgrader{
			ReadBufferSize:  1024,
			WriteBufferSize: 1024,
		},
		sessions:      make(map[string]*Session),
		authenticator: auth,
	}
}

func (s *Server) handler(w http.ResponseWriter, r *http.Request) {
	conn, err := s.upgrader.Upgrade(w, r, nil)
	if err != nil {
		log.Println(err)
		return
	}
	s.addSession(conn)
}

func (s *Server) addSession(conn *websocket.Conn) {
	var action genericAction
	err := conn.ReadJSON(&action)
	session := Session{
		Ws:         conn,
		Challenge:  authenticator.GenerateHumanID(),
		Challenged: false,
		UserID:     0,
	}

	for !session.Challenged {
		if err != nil || action.Action != LoginAction {
			if err = conn.WriteJSON(&loginAction{}); err != nil {
				conn.Close()
				return
			}
			continue
		}
		userID, err := s.authenticator.GetUser(action.HumanID)
	}

}
