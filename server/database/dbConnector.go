package database

import "github.com/gocql/gocql"

//CreateConnection creates a connection based on the credentials, the keyspace and the addresses
func CreateConnection(username, password, keyspace string, addresses ...string) (*gocql.Session, error) {
	cluster := gocql.NewCluster(addresses...)
	cluster.Authenticator = gocql.PasswordAuthenticator{
		Username: username,
		Password: password,
	}
	cluster.Keyspace = keyspace
	cluster.Consistency = gocql.Quorum

	return cluster.CreateSession()
}
