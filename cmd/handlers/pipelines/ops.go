package pipelines

import (
	"io"
	"net/http"
	"os"
	"strings"

	"github.com/bob-cd/wendy/pkg"
	"github.com/lispyclouds/climate"
	"github.com/spf13/cobra"
)

func ListHandler(_ *cobra.Command, _ []string, data climate.HandlerData) error {
	res, err := http.Get(pkg.FullUrl(data.Path))
	if err != nil {
		return err
	}

	return pkg.ShowMessage(res.Body)
}

func CreateHandler(opts *cobra.Command, _ []string, data climate.HandlerData) error {
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

	res, err := http.Post(pkg.FullUrl(data.Path), "application/json", rdr)
	if err != nil {
		return err
	}

	return pkg.ShowMessage(res.Body)
}

func DeleteHandler(_ *cobra.Command, _ []string, data climate.HandlerData) error {
	req, err := http.NewRequest("DELETE", pkg.FullUrl(data.Path), nil)
	if err != nil {
		return err
	}

	res, err := http.DefaultClient.Do(req)
	if err != nil {
		return err
	}

	return pkg.ShowMessage(res.Body)
}

func LogsHandler(opts *cobra.Command, _ []string, data climate.HandlerData) error {
	res, err := http.Get(pkg.FullUrl(data.Path))
	if err != nil {
		return err
	}

	return pkg.ShowMessage(res.Body)
}
