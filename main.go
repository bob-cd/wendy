package main

import (
	"log/slog"
	"os"
	"path"

	"github.com/bob-cd/wendy/cmd"
	"github.com/bob-cd/wendy/pkg"
	"github.com/lispyclouds/climate"
	"github.com/spf13/cobra"
)

func checkBootstrap(rootCmd *cobra.Command) (bool, error) {
	apiDir, err := pkg.GetApiDir()
	if err != nil {
		return false, err
	}
	apiPath := path.Join(apiDir, "api.yaml")

	_, err = os.Stat(apiPath)
	if os.IsNotExist(err) {
		return false, nil
	}

	model, err := climate.LoadFileV3(apiPath)
	if err != nil {
		return false, err
	}

	if err = climate.BootstrapV3(rootCmd, *model, cmd.GetHandlers()); err != nil {
		return false, err
	}

	return true, nil
}

func bail(err error) {
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

	// TODO: Better
	bootstrapped, err := checkBootstrap(&rootCmd)
	bail(err)
	if !bootstrapped {
		slog.Warn("Wendy is not bootstrapped, please run 'bootstrap' first")
	}

	bail(pkg.LoadConfig())

	rootCmd.AddCommand(cmd.ConfigureCmd(), cmd.BootstrapCmd())

	if err := rootCmd.Execute(); err != nil {
		os.Exit(1)
	}
}
