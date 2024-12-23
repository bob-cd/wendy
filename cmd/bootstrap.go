package cmd

import (
	"io"
	"net/http"
	"os"
	"path"

	"github.com/bob-cd/wendy/pkg"
	"github.com/spf13/cobra"
	"github.com/spf13/viper"
)

func BootstrapCmd() *cobra.Command {
	return &cobra.Command{
		Use:   "bootstrap",
		Short: "Bootstrap Wendy's commands from Bob",
		RunE: func(cmd *cobra.Command, _ []string) error {
			apiPath := viper.GetString("api_path")
			res, err := http.Get(pkg.FullUrl(apiPath))
			if err != nil {
				return err
			}

			w, err := os.Create(path.Join(apiPath, "api.yaml"))
			if err != nil {
				return err
			}
			_, err = io.Copy(w, res.Body)
			if err != nil {
				return err
			}

			w.Sync()
			w.Close()

			return nil
		},
	}
}
