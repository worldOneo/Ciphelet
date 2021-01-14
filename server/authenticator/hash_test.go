package authenticator

import (
	"math/rand"
	"testing"
	"time"
)

func TestPassword(t *testing.T) {
	rand.Seed(time.Now().UnixNano())
	p := GenerateHumanID()
	t.Log(p)
	h, err := GeneratePassword(DefaultPasswordConfig, p)
	if err != nil {
		t.Fatal(err)
		return
	}
	t.Log(h)
	if b, err := ComparePassword(p, h); !b || err != nil {
		t.Fatal(err, b)
	}
}
