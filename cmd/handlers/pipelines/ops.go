package pipelines

import (
	"fmt"
	"io"
	"net/http"
	"net/url"
	"os"
	"strings"

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

	b, err := io.ReadAll(res.Body)
	if err != nil {
		return err
	}

	fmt.Println(string(b))

	return nil
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

	b, err := io.ReadAll(res.Body)
	if err != nil {
		return err
	}

	fmt.Println(string(b))

	return nil
}
