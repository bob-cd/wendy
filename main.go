package main

import (
	"log/slog"
	"os"
	"path"

	"github.com/bob-cd/wendy/cmd"
	"github.com/bob-cd/wendy/pkg"
	"github.com/lispyclouds/climate"
	"github.com/pb33f/libopenapi"
	v3 "github.com/pb33f/libopenapi/datamodel/high/v3"
	"github.com/spf13/cobra"
)

func bootstrap(rootCmd *cobra.Command) (*libopenapi.DocumentModel[v3.Document], error) {
	apiDir, err := pkg.GetApiDir()
	if err != nil {
		return nil, err
	}
	apiPath := path.Join(apiDir, "api.yaml")

	_, err = os.Stat(apiPath)
	if os.IsNotExist(err) {
		return nil, nil
	}

	model, err := climate.LoadFileV3(apiPath)
	if err != nil {
		return nil, err
	}

	if err = climate.BootstrapV3(rootCmd, *model, cmd.Handlers); err != nil {
		return nil, err
	}

	return model, nil
}

func bailIfErr(err error) {
	if err != nil {
		slog.Error("Error", "err", err)
		os.Exit(1)
	}
}

func main() {
	rootCmd := cobra.Command{
		Use:   "wendy",
		Short: "Bob's other half",
		Long:  "A comprehensive TUI for Bob allowing for control and complex pipeline orchestrations",
	}
	options := []pkg.Option{
		{Name: "endpoint", Desc: "Bob's endpoint", Default: "http://localhost:7777"},
		{Name: "api-path", Desc: "Path on which the OpenAPI spec can be found", Default: "/api.yaml"},
	}

	bailIfErr(pkg.LoadConfig(options))
	model, err := bootstrap(&rootCmd)
	bailIfErr(err)

	rootCmd.AddCommand(cmd.ConfigureCmd(options), cmd.BootstrapCmd(), cmd.ApplyCmd(model))
	rootCmd.Run = func(_ *cobra.Command, _ []string) {
		if model == nil {
			slog.Warn("Wendy is not bootstrapped, please run the boostrap command")
		}

		rootCmd.Help()
	}

	if err := rootCmd.Execute(); err != nil {
		os.Exit(1)
	}
}
