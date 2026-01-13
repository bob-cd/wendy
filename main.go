package main

import (
	"context"
	"log/slog"
	"os"
	"path"

	"github.com/bob-cd/wendy/cmd"
	"github.com/bob-cd/wendy/pkg"
	"github.com/lispyclouds/climate"
	"github.com/pb33f/libopenapi"
	v3 "github.com/pb33f/libopenapi/datamodel/high/v3"
	"github.com/urfave/cli/v3"
)

func bootstrap(rootCmd *cli.Command) (*libopenapi.DocumentModel[v3.Document], error) {
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

	if err = climate.BootstrapV3UrfaveCli(rootCmd, *model, cmd.Handlers); err != nil {
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
	rootCmd := cli.Command{
		Name:        "wendy",
		Usage:       "Bob's other half",
		Description: "A comprehensive TUI for Bob allowing for control and complex pipeline orchestrations",
	}
	options := []pkg.Option{
		{Name: "endpoint", Desc: "Bob's endpoint", Default: "http://localhost:7777"},
		{Name: "api-path", Desc: "Path on which the OpenAPI spec can be found", Default: "/api.yaml"},
	}

	bailIfErr(pkg.LoadConfig(options))
	model, err := bootstrap(&rootCmd)
	bailIfErr(err)

	rootCmd.Commands = append(rootCmd.Commands, cmd.ConfigureCmd(options), cmd.BootstrapCmd(), cmd.ApplyCmd(model))
	rootCmd.Action = func(_ context.Context, cmd *cli.Command) error {
		exitCode := 0

		if model == nil {
			slog.Warn("Wendy is not bootstrapped, please run the bootstrap command")
			exitCode = 1
		}

		cli.ShowSubcommandHelpAndExit(cmd, exitCode)

		return nil
	}

	bailIfErr(rootCmd.Run(context.Background(), os.Args))
}
