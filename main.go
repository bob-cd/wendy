package main

import (
	"log/slog"
	"os"
	"strconv"

	"github.com/lispyclouds/climate"
	"github.com/spf13/cobra"
)

func bailIfErr(err error) {
	if err != nil {
		slog.Error("Error", "err", err)
		os.Exit(1)
	}
}

func showParams(opts *cobra.Command, params []climate.ParamMeta, paramIn string) {
	flags := opts.Flags()

	for _, param := range params {
		switch param.Type {
		case climate.String:
			v, _ := flags.GetString(param.Name)
			slog.Info(paramIn, "value", v)
		case climate.Integer:
			v, _ := flags.GetInt(param.Name)
			slog.Info(paramIn, "value", strconv.FormatInt(int64(v), 10))
		case climate.Number:
			v, _ := flags.GetFloat64(param.Name)
			slog.Info(paramIn, "value", strconv.FormatFloat(v, 'g', -1, 64))
		case climate.Boolean:
			v, _ := flags.GetBool(param.Name)
			slog.Info(paramIn, "value", strconv.FormatBool(v))
		}
	}
}

func handler(opts *cobra.Command, args []string, data climate.HandlerData) error {
	slog.Info("HTTP Method", "method", data.Method)
	slog.Info("HTTP Path", "path", data.Path)

	if data.RequestBodyParam != nil {
		v, _ := opts.Flags().GetString(data.RequestBodyParam.Name)

		slog.Info(
			"Request Body",
			"name",
			data.RequestBodyParam.Name,
			"type",
			data.RequestBodyParam.Type,
			"value",
			v,
		)
	}

	showParams(opts, data.PathParams, "path")
	showParams(opts, data.HeaderParams, "header")
	showParams(opts, data.QueryParams, "query")
	showParams(opts, data.CookieParams, "cookie")

	return nil
}

func main() {
	model, err := climate.LoadFileV3("api.yaml")
	bailIfErr(err)

	rootCmd := &cobra.Command{
		Use:   "wendy",
		Short: "Bob's ackshual controller",
	}
	handlers := map[string]climate.Handler{
		"ArtifactStoreCreate":    handler,
		"ArtifactStoreDelete":    handler,
		"ArtifactStoreList":      handler,
		"ClusterInfo":            handler,
		"GetApiSpec":             handler,
		"GetError":               handler,
		"GetEvents":              handler,
		"HealthCheck":            handler,
		"PipelineArtifactFetch":  handler,
		"PipelineCreate":         handler,
		"PipelineDelete":         handler,
		"PipelineList":           handler,
		"PipelineLogs":           handler,
		"PipelinePause":          handler,
		"PipelineRuns":           handler,
		"PipelineStart":          handler,
		"PipelineStatus":         handler,
		"PipelineStop":           handler,
		"PipelineUnpause":        handler,
		"Query":                  handler,
		"ResourceProviderCreate": handler,
		"ResourceProviderDelete": handler,
		"ResourceProviderList":   handler,
	}

	bailIfErr(climate.BootstrapV3(rootCmd, *model, handlers))

	rootCmd.Execute()
}
