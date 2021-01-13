package authenticator

import "math/rand"

const chars = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ"

// GenerateHumanID generates a random userid fitting the user id pattern
func GenerateHumanID() string {
	b := make([]byte, 8)
	for i := 0; i < 8; i++ {
		b[i] = chars[rand.Intn(len(chars))]
	}
	return string(b)
}
