package cmd

import (
	"io"
	"net/http"
	"net/url"
	"os"
	"path"

	"github.com/bob-cd/wendy/pkg"
	"github.com/spf13/cobra"
	"github.com/spf13/viper"
)

func saveAPISpec(r io.ReadCloser) error {
	cacheDir, err := os.UserCacheDir()
	if err != nil {
		return err
	}

	apiDir := path.Join(cacheDir, "wendy")
	if err = os.MkdirAll(apiDir, os.ModePerm); err != nil {
		return err
	}

	w, err := os.Create(path.Join(apiDir, "api.yaml"))
	if err != nil {
		return err
	}
	_, err = io.Copy(w, r)
	if err != nil {
		return err
	}

	w.Sync()
	w.Close()

	return nil
}

func BootstrapCmd(rootCmd *cobra.Command) *cobra.Command {
	return &cobra.Command{
		Use:   "bootstrap",
		Short: "Bootstrap Wendy's commands from Bob",
		RunE: func(cmd *cobra.Command, _ []string) error {
			if err := pkg.LoadConfig(); err != nil {
				return err
			}

			endpoint, apiPath := viper.GetString("endpoint"), viper.GetString("api_path")
			fetchUrl, err := url.JoinPath(endpoint, apiPath)
			if err != nil {
				return err
			}

			res, err := http.Get(fetchUrl)
			if err != nil {
				return err
			}

			return saveAPISpec(res.Body)
		},
	}
}
