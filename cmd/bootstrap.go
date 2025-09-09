package cmd

import (
	"log/slog"
	"path"

	"github.com/bob-cd/wendy/cmd/handlers"
	"github.com/bob-cd/wendy/cmd/handlers/cluster"
	"github.com/bob-cd/wendy/cmd/handlers/events"
	"github.com/bob-cd/wendy/cmd/handlers/pipelines"
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
	"ArtifactStoreCreate":    handlers.CreateHandler,
	"ArtifactStoreDelete":    handlers.DeleteHandler,
	"ArtifactStoreList":      handlers.ListHandler,
	"ClusterInfo":            cluster.InfoHandler,
	"GetEvents":              events.EventsHandler,
	"HealthCheck":            cluster.HealthCheckHandler,
	"PipelineArtifactFetch":  pipelines.ArtifactFetchHandler,
	"PipelineCreate":         handlers.CreateHandler,
	"PipelineDelete":         handlers.DeleteHandler,
	"PipelineList":           pipelines.ListHandler,
	"PipelineLogs":           pipelines.LogsHandler,
	"PipelinePause":          pipelines.PauseHandler,
	"PipelineRuns":           pipelines.RunsHandler,
	"PipelineStart":          pipelines.StartHandler,
	"PipelineStatus":         pipelines.StatusHandler,
	"PipelineStop":           pipelines.StopHandler,
	"PipelineUnpause":        pipelines.UnpauseHandler,
	"ResourceProviderCreate": handlers.CreateHandler,
	"ResourceProviderDelete": handlers.DeleteHandler,
	"ResourceProviderList":   handlers.ListHandler,
	"LoggerCreate":           handlers.CreateHandler,
	"LoggerDelete":           handlers.DeleteHandler,
	"LoggerList":             handlers.ListHandler,

	"GetApiSpec": placeHandler,
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
