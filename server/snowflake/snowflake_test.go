package snowflake

import (
	"testing"
	"time"
)

func TestSnowflake(t *testing.T) {
	m := map[Snowflake]struct{}{}
	gen := NewGenerator(0)

	for i := 0; i < 10_000_000; i++ {
		if i%2048 == 0 {
			time.Sleep(time.Millisecond)
		}
		flake := gen.GenSnowflake()
		_, c := m[flake]
		if c {
			t.Errorf("Duplicated Snowflake: %b, Index: %v", flake, i)
			return
		}
		m[flake] = struct{}{}
	}
}

func BenchmarkSnowflake(b *testing.B) {
	gen := NewGenerator(0)
	for i := 0; i < b.N; i++ {
		gen.GenSnowflake()
	}
}
