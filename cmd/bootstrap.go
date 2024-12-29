package cmd

import (
	"log/slog"
	"path"

	"github.com/bob-cd/wendy/cmd/handlers/artifact_stores"
	"github.com/bob-cd/wendy/cmd/handlers/cluster"
	"github.com/bob-cd/wendy/cmd/handlers/events"
	"github.com/bob-cd/wendy/cmd/handlers/pipelines"
	"github.com/bob-cd/wendy/cmd/handlers/resource_providers"
	"github.com/bob-cd/wendy/pkg"
	"github.com/lispyclouds/climate"
	"github.com/spf13/cobra"
	"github.com/spf13/viper"
)

func placeHandler(_ *cobra.Command, _ []string, _ climate.HandlerData) error {
	slog.Info("Oooh, you stumbled on a hidden one! I don't do much though!")
	return nil
}

var Handlers = map[string]climate.Handler{
	"ArtifactStoreCreate":    artifact_stores.CreateHandler,
	"ArtifactStoreDelete":    artifact_stores.DeleteHandler,
	"ArtifactStoreList":      artifact_stores.ListHandler,
	"ClusterInfo":            cluster.InfoHandler,
	"GetEvents":              events.EventsHandler,
	"HealthCheck":            cluster.HealthCheckHandler,
	"PipelineArtifactFetch":  pipelines.ArtifactFetchHandler,
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
	"ResourceProviderCreate": resource_providers.CreateHandler,
	"ResourceProviderDelete": resource_providers.DeleteHandler,
	"ResourceProviderList":   resource_providers.ListHandler,

	"GetApiSpec": placeHandler,
	"GetError":   placeHandler,
	"Query":      placeHandler,
}

func BootstrapCmd() *cobra.Command {
	return &cobra.Command{
		Use:   "bootstrap",
		Short: "Bootstrap Wendy's commands from Bob",
		RunE: func(_ *cobra.Command, _ []string) error {
			apiDir, err := pkg.GetApiDir()
			if err != nil {
				return err
			}

			return pkg.Download(pkg.FullUrl(viper.GetString("api-path")), path.Join(apiDir, "api.yaml"))
		},
	}
}
