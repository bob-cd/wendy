package cmd

import (
	"log/slog"

	"github.com/bob-cd/wendy/cmd/handlers/artifact_stores"
	"github.com/bob-cd/wendy/cmd/handlers/cluster"
	"github.com/bob-cd/wendy/cmd/handlers/pipelines"
	"github.com/bob-cd/wendy/cmd/handlers/resource_providers"
	"github.com/lispyclouds/climate"
	"github.com/spf13/cobra"
)

func placeHandler(opts *cobra.Command, args []string, data climate.HandlerData) error {
	slog.Info("Oooh, you seem to have discovered a hidden one! I don't do much though!")
	return nil
}

func GetHandlers() map[string]climate.Handler {
	return map[string]climate.Handler{
		"ArtifactStoreCreate":    artifact_stores.CreateHandler,
		"ArtifactStoreDelete":    artifact_stores.DeleteHandler,
		"ArtifactStoreList":      artifact_stores.ListHandler,
		"ClusterInfo":            cluster.InfoHandler,
		"GetApiSpec":             placeHandler,
		"GetError":               placeHandler,
		"GetEvents":              placeHandler,
		"HealthCheck":            cluster.HealthCheckHandler,
		"PipelineArtifactFetch":  placeHandler,
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
		"Query":                  placeHandler,
		"ResourceProviderCreate": resource_providers.CreateHandler,
		"ResourceProviderDelete": resource_providers.DeleteHandler,
		"ResourceProviderList":   resource_providers.ListHandler,
	}
}
