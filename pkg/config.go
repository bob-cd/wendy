package pkg

import (
	"errors"
	"os"
	"path"

	"github.com/spf13/viper"
)

type Option struct {
	Name    string
	Desc    string
	Default string
}

func confPaths() (string, string, error) {
	confDir, err := os.UserConfigDir()
	if err != nil {
		return "", "", err
	}

	return path.Join(confDir, "wendy"), "conf.json", nil
}

func mkdir(path string) error {
	if _, err := os.Stat(path); err != nil {
		if errors.Is(err, os.ErrNotExist) {
			if err = os.MkdirAll(path, os.ModePerm); err != nil {
				return err
			}
		}
	}

	return nil
}

func GetApiDir() (string, error) {
	cacheDir, err := os.UserCacheDir()
	if err != nil {
		return "", err
	}

	apiDir := path.Join(cacheDir, "wendy")
	if err := mkdir(apiDir); err != nil {
		return "", err
	}

	return apiDir, nil
}

func LoadConfig(options []Option) error {
	confDir, confName, err := confPaths()
	if err != nil {
		return err
	}

	if err := mkdir(confDir); err != nil {
		return err
	}

	viper.AddConfigPath(confDir)
	viper.SetConfigName(confName)
	viper.SetConfigType("json")

	for _, option := range options {
		viper.SetDefault(option.Name, option.Default)
	}

	if err = viper.ReadInConfig(); err != nil {
		if _, ok := err.(viper.ConfigFileNotFoundError); !ok {
			return err
		}
	}

	return nil
}

func SaveConfig() error {
	confDir, confName, err := confPaths()
	if err != nil {
		return err
	}

	return viper.WriteConfigAs(path.Join(confDir, confName))
}
