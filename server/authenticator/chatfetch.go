package authenticator

import "github.com/worldOneo/Ciphelet/snowflake"

// GetChats returns the open chats which the user has
func (a *Authenticator) GetChats(UserID snowflake.Snowflake) ([]snowflake.Snowflake, error) {
	query := a.CQLSession.Query("SELECT chat_id FROM chats WHERE user_id = ?", UserID)
	err := query.Exec()
	if err != nil {
		return nil, err
	}
	chats := []snowflake.Snowflake{}
	var id snowflake.Snowflake
	iter := query.Iter()
	for iter.Scan(&id) {
		chats = append(chats, id)
	}
	err = iter.Close()
	if err != nil {
		return nil, err
	}
	return chats, nil
}
