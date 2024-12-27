package pipelines

import (
	"io"
	"net/url"
	"os"
	"strings"

	"github.com/bob-cd/wendy/pkg"
	"github.com/lispyclouds/climate"
	"github.com/spf13/cobra"
)

func ListHandler(opts *cobra.Command, _ []string, data climate.HandlerData) error {
	group, _ := opts.Flags().GetString("group")
	name, _ := opts.Flags().GetString("name")
	status, _ := opts.Flags().GetString("status")

	params := url.Values{}
	if group != "" {
		params.Set("group", group)
	}
	if name != "" {
		params.Set("name", name)
	}
	if status != "" {
		params.Set("status", status)
	}

	res, err := pkg.Get(pkg.FullUrl(data.Path) + "?" + params.Encode())
	if err != nil {
		return err
	}

	return pkg.ShowMessage(res)
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

	res, err := pkg.Post(pkg.FullUrl(data.Path), rdr)
	if err != nil {
		return err
	}

	return pkg.ShowMessage(res)
}

func DeleteHandler(_ *cobra.Command, _ []string, data climate.HandlerData) error {
	res, err := pkg.Delete(pkg.FullUrl(data.Path))
	if err != nil {
		return err
	}

	return pkg.ShowMessage(res)
}

func LogsHandler(_ *cobra.Command, _ []string, data climate.HandlerData) error {
	res, err := pkg.Get(pkg.FullUrl(data.Path))
	if err != nil {
		return err
	}

	return pkg.ShowMessage(res)
}

func StartHandler(_ *cobra.Command, _ []string, data climate.HandlerData) error {
	res, err := pkg.Post(pkg.FullUrl(data.Path), nil)
	if err != nil {
		return err
	}

	return pkg.ShowMessage(res)
}

func StopHandler(_ *cobra.Command, _ []string, data climate.HandlerData) error {
	res, err := pkg.Post(pkg.FullUrl(data.Path), nil)
	if err != nil {
		return err
	}

	return pkg.ShowMessage(res)
}

func RunsHandler(_ *cobra.Command, _ []string, data climate.HandlerData) error {
	res, err := pkg.Get(pkg.FullUrl(data.Path))
	if err != nil {
		return err
	}

	return pkg.ShowMessage(res)
}

func StatusHandler(_ *cobra.Command, _ []string, data climate.HandlerData) error {
	res, err := pkg.Get(pkg.FullUrl(data.Path))
	if err != nil {
		return err
	}

	return pkg.ShowMessage(res)
}

func PauseHandler(_ *cobra.Command, _ []string, data climate.HandlerData) error {
	res, err := pkg.Post(pkg.FullUrl(data.Path), nil)
	if err != nil {
		return err
	}

	return pkg.ShowMessage(res)
}

func UnpauseHandler(_ *cobra.Command, _ []string, data climate.HandlerData) error {
	res, err := pkg.Post(pkg.FullUrl(data.Path), nil)
	if err != nil {
		return err
	}

	return pkg.ShowMessage(res)
}

func ArtifactFetchHandler(opts *cobra.Command, _ []string, data climate.HandlerData) error {
	name, _ := opts.Flags().GetString("artifact-name")
	return pkg.Download(pkg.FullUrl(data.Path), name+".tar")
}
