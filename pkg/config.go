package pkg

import (
	"errors"
	"os"
	"path"

	"github.com/spf13/viper"
)

func confPaths() (string, string, error) {
	confDir, err := os.UserConfigDir()
	if err != nil {
		return "", "", err
	}

	return path.Join(confDir, "wendy"), "conf.json", nil
}

func GetApiDir() (string, error) {
	cacheDir, err := os.UserCacheDir()
	if err != nil {
		return "", err
	}

	apiDir := path.Join(cacheDir, "wendy")
	if _, err := os.Stat(apiDir); err != nil {
		if errors.Is(err, os.ErrNotExist) {
			if err = os.MkdirAll(apiDir, os.ModePerm); err != nil {
				return "", err
			}
		}
	}

	return apiDir, nil
}

func LoadConfig() error {
	confDir, confName, err := confPaths()
	if err != nil {
		return err
	}

	viper.AddConfigPath(confDir)
	viper.SetConfigName(confName)
	viper.SetConfigType("json")

	if err = viper.ReadInConfig(); err != nil {
		if _, ok := err.(viper.ConfigFileNotFoundError); !ok {
			return err
		}

		if err = os.MkdirAll(confDir, os.ModePerm); err != nil {
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
