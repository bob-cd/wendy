package cmd

import (
	"github.com/bob-cd/wendy/pkg"
	"github.com/charmbracelet/huh"
	"github.com/spf13/cobra"
	"github.com/spf13/viper"
)

func config(key, prompt string) error {
	var input string
	currentValue := viper.GetString(key)

	err := huh.NewInput().
		Title(prompt).
		Description("hit enter to accept the current value").
		Value(&input).
		Placeholder(currentValue).
		Run()
	if err != nil {
		return err
	}

	if input == "" {
		input = currentValue
	}

	viper.Set(key, input)

	return nil
}

func ConfigureCmd() *cobra.Command {
	return &cobra.Command{
		Use:   "configure",
		Short: "Interactively configure Wendy",
		RunE: func(_ *cobra.Command, _ []string) error {
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
