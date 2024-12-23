package cmd

import (
	"fmt"
	"log/slog"

	"github.com/bob-cd/wendy/cmd/handlers/cluster"
	"github.com/bob-cd/wendy/cmd/handlers/pipelines"
	"github.com/lispyclouds/climate"
	"github.com/spf13/cobra"
)

func handler(opts *cobra.Command, args []string, data climate.HandlerData) error {
	slog.Info(fmt.Sprintf("Dummy hndler called with %+v\n", data))
	return nil
}

func GetHandlers() map[string]climate.Handler {
	return map[string]climate.Handler{
		"ArtifactStoreCreate":    handler,
		"ArtifactStoreDelete":    handler,
		"ArtifactStoreList":      handler,
		"ClusterInfo":            cluster.InfoHandler,
		"GetApiSpec":             handler,
		"GetError":               handler,
		"GetEvents":              handler,
		"HealthCheck":            cluster.HealthCheckHandler,
		"PipelineArtifactFetch":  handler,
		"PipelineCreate":         pipelines.CreateHandler,
		"PipelineDelete":         pipelines.DeleteHandler,
		"PipelineList":           pipelines.ListHandler,
		"PipelineLogs":           pipelines.LogsHandler,
		"PipelinePause":          pipelines.PauseHandler,
		"PipelineRuns":           pipelines.RunsHandler,
		"PipelineStart":          pipelines.StartHandler,
		"PipelineStatus":         pipelines.StatusHandler,
		"PipelineStop":           pipelines.StopHandler,
		"PipelineUnpause":        pipelines.UnpauseHandler,
		"Query":                  handler,
		"ResourceProviderCreate": handler,
		"ResourceProviderDelete": handler,
		"ResourceProviderList":   handler,
	}
}
