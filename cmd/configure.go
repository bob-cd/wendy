package cmd

import (
	"github.com/bob-cd/wendy/pkg"
	"github.com/charmbracelet/huh"
	"github.com/spf13/cobra"
	"github.com/spf13/viper"
)

func configure(key, prompt string) error {
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

func ConfigureCmd(options []pkg.Option) *cobra.Command {
	cmd := cobra.Command{
		Use:   "configure",
		Short: "Configure Wendy",
		Long:  "Configures Wendy interactively or via setting the flags",
		RunE: func(cmd *cobra.Command, _ []string) error {
			flags := cmd.Flags()

			for _, option := range options {
				if flags.Changed(option.Name) {
					value, _ := flags.GetString(option.Name)

					viper.Set(option.Name, value)
					continue
				}

				if err := configure(option.Name, option.Desc); err != nil {
					return err
				}
			}

			return pkg.SaveConfig()
		},
	}

	for _, option := range options {
		cmd.Flags().String(option.Name, option.Default, option.Desc)
	}

	return &cmd
}
