package main

import (
	"os"

	"github.com/bob-cd/wendy/cmd"
	"github.com/spf13/cobra"
)

func main() {
	rootCmd := cobra.Command{
		Use:   "wendy",
		Short: "Bob's other half",
		Long:  "A comprehensive TUI for Bob allowing for control and complex pipeline orchestrations",
	}

	rootCmd.AddCommand(cmd.ConfigureCmd(), cmd.BootstrapCmd(&rootCmd))

	if err := rootCmd.Execute(); err != nil {
		os.Exit(1)
	}
}
