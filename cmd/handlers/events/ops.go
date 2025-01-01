package events

import (
	"bufio"
	"encoding/json"
	"fmt"
	"log/slog"
	"os"
	"os/signal"

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

	rdr := bufio.NewReader(res)
	event := pkg.Event{}

	for {
		bytes, err := rdr.ReadBytes('\n')
		if err != nil {
			return err
		}

		if len(bytes) <= 1 {
			continue
		}

		// start from 6 to drop "data: "
		if err = json.Unmarshal(bytes[6:], &event); err != nil {
			slog.Warn("Unexpected event format", "event", string(bytes))
			continue
		}

		fmt.Printf("%+v\n", event)
	}
}
