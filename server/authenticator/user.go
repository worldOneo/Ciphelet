package authenticator

import (
	"errors"
	"log"
	"regexp"

	"github.com/gocql/gocql"
	"github.com/worldOneo/messenger/snowflake"
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
	Snowflake snowflake.Snowflake `json:"snowflake"`
	Password  string              `json:"password"`
}

// Authenticator has the ability to query user data from the database
type Authenticator struct {
	CQLSession *gocql.Session
}

//GetUser returns the userid from the db based on the 8 Digit id
// a userID is made up of any case 8 letters except I,L,O to avoid confusion between I and L and 0 and O
func (a *Authenticator) GetUser(humanID string) (UserID, error) {
	b := HumanIDLayout.Match([]byte(humanID))

	if !b {
		return UserID{}, ErrInvalidID
	}
	query := a.CQLSession.Query("SELECT user_id FROM user_id WHERE human_id = ?;", humanID)
	err := query.Exec()
	if err != nil {
		return UserID{}, err
	}
	var userIntID int64
	iter := query.Iter()
	if !iter.Scan(&userIntID) {
		return UserID{}, ErrUserNotFound
	}
	return UserID{
		HumanID:   humanID,
		Snowflake: snowflake.Snowflake(userIntID),
	}, nil
}

// VerifyUser queries the user and the password and compares them.
// If the passwords (hash) match the UserID (HumanID + Snowflake) is returned
// an error otherwise
func (a *Authenticator) VerifyUser(humanID string, password string) (UserID, error) {
	uID, err := a.GetUser(humanID)
	if err != nil {
		return UserID{}, err
	}
	query := a.CQLSession.Query("SELECT pw_hash FROM user_credentials WHERE user_id = ?;", uID.Snowflake)
	err = query.Exec()
	if err != nil {
		return UserID{}, err
	}
	var hash string
	iter := query.Iter()
	if !iter.Scan(&hash) {
		log.Printf("User %v found as user but with no credentials", humanID)
		return UserID{}, ErrUserNotFound
	}

	match, err := ComparePassword(password, hash)
	if match {
		return uID, nil
	}
	return UserID{}, ErrCredentialsInvalid
}
