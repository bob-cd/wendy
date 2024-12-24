package cluster

import (
	"github.com/bob-cd/wendy/pkg"
	"github.com/lispyclouds/climate"
	"github.com/spf13/cobra"
)

func get(data climate.HandlerData) error {
	res, err := pkg.Get(pkg.FullUrl(data.Path))
	if err != nil {
		return err
	}

	return pkg.ShowMessage(res)
}

func InfoHandler(_ *cobra.Command, _ []string, data climate.HandlerData) error {
	return get(data)
}

func HealthCheckHandler(_ *cobra.Command, _ []string, data climate.HandlerData) error {
	return get(data)
}
