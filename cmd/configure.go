package cmd

import (
	"errors"

	"github.com/bob-cd/wendy/pkg"
	"github.com/charmbracelet/huh"
	"github.com/spf13/cobra"
	"github.com/spf13/viper"
)

func config(key, prompt string) error {
	var input string

	huh.NewInput().
		Title(prompt).
		Value(&input).
		Placeholder(viper.GetString(key)).
		Run()

	if input == "" {
		return errors.New("Cancelled")
	}

	viper.Set(key, input)

	return nil
}

func ConfigureCmd() *cobra.Command {
	return &cobra.Command{
		Use:   "configure",
		Short: "Interactively configure Wendy",
		RunE: func(cmd *cobra.Command, _ []string) error {
			options := map[string]string{
				"endpoint": "Bob's endpoint",
				"api_path": "Path on which the OpenAPI spec can be found",
			}

			for k, v := range options {
				if err := config(k, v); err != nil {
					return err
				}
			}

			return pkg.SaveConfig()
		},
	}
}
