package events

import (
	"bufio"
	"fmt"
	"net/http"
	"strings"

	"github.com/bob-cd/wendy/pkg"
	"github.com/lispyclouds/climate"
	"github.com/spf13/cobra"
)

func EventsHandler(_ *cobra.Command, _ []string, data climate.HandlerData) error {
	res, err := http.Get(pkg.FullUrl(data.Path))
	if err != nil {
		return err
	}

	r := bufio.NewReader(res.Body)
	for {
		b, err := r.ReadBytes('\n')
		if err != nil {
			return err
		}

		if len(b) <= 1 {
			continue
		}

		line := strings.TrimSpace(strings.TrimPrefix(string(b), "data: "))
		fmt.Println(line)
	}
}
