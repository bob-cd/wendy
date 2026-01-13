package cmd

import (
	"context"

	"github.com/bob-cd/wendy/pkg"
	"github.com/charmbracelet/huh"
	"github.com/spf13/viper"
	"github.com/urfave/cli/v3"
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

func ConfigureCmd(options []pkg.Option) *cli.Command {
	cmd := cli.Command{
		Name:  "configure",
		Usage: "Configures Wendy interactively or via setting the flags",
		Action: func(_ context.Context, cmd *cli.Command) error {
			for _, option := range options {
				if cmd.IsSet(option.Name) {
					value := cmd.String(option.Name)

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
		cmd.Set(option.Name, option.Default)
	}

	return &cmd
}
