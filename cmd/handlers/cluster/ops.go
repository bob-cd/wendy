package cluster

import (
	"fmt"
	"io"
	"net/http"
	"net/url"

	"github.com/lispyclouds/climate"
	"github.com/spf13/cobra"
	"github.com/spf13/viper"
)

func get(path string) error {
	fullUrl, err := url.JoinPath(viper.GetString("endpoint"), path)
	if err != nil {
		return err
	}

	res, err := http.Get(fullUrl)
	if err != nil {
		return err
	}

	b, err := io.ReadAll(res.Body)
	if err != nil {
		return err
	}

	fmt.Println(string(b))

	return nil
}

func InfoHandler(_ *cobra.Command, _ []string, data climate.HandlerData) error {
	return get(data.Path)
}

func HealthCheckHandler(_ *cobra.Command, _ []string, data climate.HandlerData) error {
	return get(data.Path)
}
