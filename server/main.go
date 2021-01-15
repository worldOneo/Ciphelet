package main

import (
	"log"
	"math/rand"
	"net/http"
	"time"

	"github.com/gorilla/mux"
	"github.com/worldOneo/Ciphelet/authenticator"
	"github.com/worldOneo/Ciphelet/database"
	"github.com/worldOneo/Ciphelet/network"
)

func main() {
	rand.Seed(time.Now().UnixNano())
	dbconn, err := database.CreateConnection("cassandra", "cassandra", "messenger", "localhost")
	if err != nil {
		log.Panic(err)
	}
	server := network.NewServer(&authenticator.Authenticator{
		CQLSession: dbconn,
	})

	r := mux.NewRouter()
	r.HandleFunc("/ws", server.Handler)

	log.Println("Starting on localhost:8080...")
	http.ListenAndServe(":8080", r)
}
