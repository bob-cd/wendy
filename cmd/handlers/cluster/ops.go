package cluster

import (
	"net/http"
	"net/url"

	"github.com/bob-cd/wendy/pkg"
	"github.com/lispyclouds/climate"
	"github.com/spf13/cobra"
	"github.com/spf13/viper"
)

func get(data climate.HandlerData) error {
	fullUrl, err := url.JoinPath(viper.GetString("endpoint"), data.Path)
	if err != nil {
		return err
	}

	res, err := http.Get(fullUrl)
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
