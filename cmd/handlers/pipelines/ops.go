package pipelines

import (
	"io"
	"net/http"
	"net/url"
	"os"
	"strings"

	"github.com/bob-cd/wendy/pkg"
	"github.com/lispyclouds/climate"
	"github.com/spf13/cobra"
	"github.com/spf13/viper"
)

func PipelineListHandler(_ *cobra.Command, _ []string, data climate.HandlerData) error {
	fullUrl, err := url.JoinPath(viper.GetString("endpoint"), data.Path)
	if err != nil {
		return err
	}

	res, err := http.Get(fullUrl)
	if err != nil {
		return err
	}

	return pkg.ShowMessage(res.Body)
}

func PipelineCreateHandler(opts *cobra.Command, _ []string, data climate.HandlerData) error {
	fullUrl, err := url.JoinPath(viper.GetString("endpoint"), data.Path)
	if err != nil {
		return err
	}

	payload, _ := opts.Flags().GetString(data.RequestBodyParam.Name)

	var rdr io.Reader

	if strings.HasPrefix(payload, "@") {
		r, err := os.Open(strings.TrimPrefix(payload, "@"))
		if err != nil {
			return err
		}
		defer r.Close()
		rdr = r
	} else {
		rdr = strings.NewReader(payload)
	}

	res, err := http.Post(fullUrl, "application/json", rdr)
	if err != nil {
		return err
	}

	return pkg.ShowMessage(res.Body)
}
