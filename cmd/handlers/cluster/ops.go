package cluster

import (
	"net/http"

	"github.com/bob-cd/wendy/pkg"
	"github.com/lispyclouds/climate"
	"github.com/spf13/cobra"
)

func get(data climate.HandlerData) error {
	res, err := http.Get(pkg.FullUrl(data.Path))
	if err != nil {
		return err
	}

	return pkg.ShowMessage(res.Body)
}

func InfoHandler(_ *cobra.Command, _ []string, data climate.HandlerData) error {
	return get(data)
}

func HealthCheckHandler(_ *cobra.Command, _ []string, data climate.HandlerData) error {
	return get(data)
}
