package cmd

import (
	"fmt"
	"log/slog"

	"github.com/lispyclouds/climate"
	"github.com/spf13/cobra"
)

func handler(opts *cobra.Command, args []string, data climate.HandlerData) error {
	slog.Info(fmt.Sprintf("Called! %+v\n", data))
	return nil
}

func GetHandlers() map[string]climate.Handler {
	return map[string]climate.Handler{
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
}
