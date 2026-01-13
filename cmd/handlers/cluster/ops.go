package cluster

import (
	"fmt"

	"github.com/bob-cd/wendy/pkg"
	"github.com/lispyclouds/climate"
	"github.com/urfave/cli/v3"
)

func InfoHandler(_ *cli.Command, _ []string, data climate.HandlerData) error {
	res, err := pkg.Get(pkg.FullUrl(data.Path))
	if err != nil {
		return err
	}

	return pkg.ShowMessage(res)
}

func HealthCheckHandler(_ *cli.Command, _ []string, data climate.HandlerData) error {
	res, err := pkg.Get(pkg.FullUrl(data.Path))
	if err != nil {
		return err
	}

	fmt.Println("Pong")

	return res.Close()
}
