package events

import (
	"bufio"
	"fmt"
	"os"
	"os/signal"
	"strings"

	"github.com/bob-cd/wendy/pkg"
	"github.com/lispyclouds/climate"
	"github.com/spf13/cobra"
)

func EventsHandler(_ *cobra.Command, _ []string, data climate.HandlerData) error {
	res, err := pkg.Get(pkg.FullUrl(data.Path))
	if err != nil {
		return err
	}

	c := make(chan os.Signal, 1)
	signal.Notify(c, os.Interrupt)
	go func() {
		<-c
		res.Close()
		os.Exit(0)
	}()

	r := bufio.NewReader(res)
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
