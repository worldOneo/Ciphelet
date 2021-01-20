package authenticator

import (
	"errors"

	"github.com/gocql/gocql"
	"github.com/worldOneo/Ciphelet/snowflake"
)

// ErrUserNotFound defines that the user wasnt found in the database
var ErrUserNotFound = errors.New("user not found")

// ErrCredentialsInvalid defines that the passed credentials are not valid
var ErrCredentialsInvalid = errors.New("the credentials invalid")

// UserID is a maping from a humanreadable 8 Digit human readable id to the users snowflake
type UserID struct {
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
	Generator  *snowflake.Generator
}

// GetUserKey queries the user and the password and the publickey.
func (a *Authenticator) GetUserKey(flake snowflake.Snowflake) (*CredentialUser, error) {

	query := a.CQLSession.Query("SELECT public_key, pw_hash FROM user_credentials WHERE user_id = ?;", flake)
	err := query.Exec()
	if err != nil {
		return nil, err
	}
	cUser := CredentialUser{}

	iter := query.Iter()
	if !iter.Scan(&cUser.Publickey, &cUser.Password) {
		return nil, ErrUserNotFound
	}
	iter.Close()
	cUser.Snowflake = flake
	return &cUser, nil
}

// RegisterUser registers a user into the server
func (a *Authenticator) RegisterUser(password string, publickey string) (userID *UserID, err error) {
	uID := a.Generator.GenSnowflake()
	hash, err := GeneratePassword(DefaultPasswordConfig, password)
	if err != nil {
		return nil, err
	}
	query := a.CQLSession.Query("INSERT INTO user_credentials(user_id, pw_hash, public_key) values(?, ?, ?);",
		uID, hash, publickey)
	err = query.Exec()
	if err != nil {
		return nil, err
	}
	return &UserID{
		Snowflake: uID,
	}, nil
}
