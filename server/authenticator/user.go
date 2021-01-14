package authenticator

import (
	"errors"
	"regexp"

	"github.com/gocql/gocql"
	"github.com/worldOneo/Ciphelet/snowflake"
)

// ErrUserNotFound defines that the user wasnt found in the database
var ErrUserNotFound = errors.New("user not found")

// ErrInvalidID defines that the passed id is not valid
var ErrInvalidID = errors.New("the id is invalid")

// ErrCredentialsInvalid defines that the passed credentials are not valid
var ErrCredentialsInvalid = errors.New("the credentials invalid")

// HumanIDLayout the layout of the HumanID
var HumanIDLayout, _ = regexp.Compile("[a-hjkmnp-zA-HJKMNP-Z]{8}")

// UserID is a maping from a humanreadable 8 Digit human readable id to the users snowflake
type UserID struct {
	HumanID   string              `json:"id"`
	Snowflake snowflake.Snowflake `json:"snowflake"`
}

// CredentialUser contains authentication data of a user
type CredentialUser struct {
	KeyedUser
	Password string `json:"password"`
}

// KeyedUser contains the publickey of a user
type KeyedUser struct {
	UserID
	Publickey string `json:"publickey"`
}

// Authenticator has the ability to query user data from the database
type Authenticator struct {
	CQLSession *gocql.Session
}

// GetUserKey queries the user and the password and the publickey.
func (a *Authenticator) GetUserKey(humanID string) (*CredentialUser, error) {
	if !IsHumanID(humanID) {
		return nil, ErrInvalidID
	}

	query := a.CQLSession.Query("SELECT public_key, pw_hash, user_id FROM user_credentials WHERE human_id = ?;", humanID)
	err := query.Exec()
	if err != nil {
		return nil, err
	}
	cUser := CredentialUser{}
	iter := query.Iter()
	if !iter.Scan(&cUser.Publickey, &cUser.Password, &cUser.UserID) {
		return nil, ErrUserNotFound
	}
	cUser.HumanID = humanID
	return &cUser, nil
}

// IsHumanID test a humanID string for its layout
func IsHumanID(humanID string) bool {
	return HumanIDLayout.Match([]byte(humanID))
}

// RegisterUser registers a user into the server
func RegisterUser(password string, publickey string) {

}
