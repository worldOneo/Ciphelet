package authenticator

import (
	"math/rand"
	"testing"
	"time"
)

func TestGenerateHumanID(t *testing.T) {
	rand.Seed(time.Now().UnixNano())
	for i := 0; i < 10_000; i++ {
		id := GenerateHumanID()
		match := HumanIDLayout.Match([]byte(id))
		if !match {
			t.Errorf("ID %v doesnt match", id)
			return
		}
	}
}

func BenchmarkGenerateHumanID(b *testing.B) {
	for i := 0; i < b.N; i++ {
		GenerateHumanID()
	}
}
