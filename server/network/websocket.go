package network

import (
	"log"
	"net/http"

	"github.com/gorilla/websocket"
	"github.com/worldOneo/Ciphelet/authenticator"
	"github.com/worldOneo/Ciphelet/snowflake"
)

// ActionType types of actions defined by string
type ActionType string

// A definition of action types
const (
	RegisterAction   ActionType = "register"
	LoginAction      ActionType = "login"
	ChallengeAction  ActionType = "challenge"
	GroupfetchAction ActionType = "groupfetch"
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
	humanIdentified
	flaked
	Password string `json:"password,omitempty"`
	Key      string `json:"key,omitempty"`
}

type groupfetchAction struct {
	ChatID []snowflake.Snowflake `json:"chatid"`
}

type genericAction struct {
	Action           ActionType        `json:"action"`
	ChallengeAction  *challengeAction  `json:"challengeAction,omitempty"`
	PublickeyAction  *publickeyAction  `json:"publickeyAction,omitempty"`
	LoginAction      *loginAction      `json:"loginAction,omitempty"`
	RegisterAction   *registerAction   `json:"registerAction,omitempty"`
	GroupfetchAction *groupfetchAction `json:"groupfetchAction,omitempty"`
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
		if session.Challenged {
			delete(s.sessions, session.UserID)
		}
		return nil
	})
	s.addSession(session)
	if !session.Challenged {
		log.Printf("Client isnt verified")
		return
	}
	s.sessions[session.UserID] = session
	for !session.Closed {
		action, err := getNextAction(session.Ws)
		if err != nil {
			log.Printf("Client failed! \"%v\"", err)
			session.Ws.Close()
			return
		}
		switch action.Action {
		case GroupfetchAction:
			response := &genericAction{}
			response.Action = GroupfetchAction + "success"
			chats, err := s.authenticator.GetChats(session.UserID)
			if err != nil {
				log.Printf("Failed fetching the chats: \"%v\"", err)
				continue
			}
			response.GroupfetchAction = &groupfetchAction{
				ChatID: chats,
			}
			session.Ws.WriteJSON(response)
			break
		default:
			log.Print("Recieved action: " + action.Action)
		}
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

func createGenericAction() genericAction {
	return genericAction{
		ChallengeAction: &challengeAction{},
		LoginAction:     &loginAction{},
		PublickeyAction: &publickeyAction{},
		RegisterAction:  &registerAction{},
	}
}
