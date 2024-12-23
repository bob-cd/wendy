package cmd

import (
	"log/slog"
	"path"

	"github.com/bob-cd/wendy/cmd/handlers/artifact_stores"
	"github.com/bob-cd/wendy/cmd/handlers/cluster"
	"github.com/bob-cd/wendy/cmd/handlers/pipelines"
	"github.com/bob-cd/wendy/cmd/handlers/resource_providers"
	"github.com/bob-cd/wendy/pkg"
	"github.com/lispyclouds/climate"
	"github.com/spf13/cobra"
	"github.com/spf13/viper"
)

func placeHandler(opts *cobra.Command, args []string, data climate.HandlerData) error {
	slog.Info("Oooh, you seem to have discovered a hidden one! I don't do much though!")
	return nil
}

var Handlers = map[string]climate.Handler{
	"ArtifactStoreCreate":    artifact_stores.CreateHandler,
	"ArtifactStoreDelete":    artifact_stores.DeleteHandler,
	"ArtifactStoreList":      artifact_stores.ListHandler,
	"ClusterInfo":            cluster.InfoHandler,
	"GetApiSpec":             placeHandler,
	"GetError":               placeHandler,
	"GetEvents":              placeHandler,
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
	"Query":                  placeHandler,
	"ResourceProviderCreate": resource_providers.CreateHandler,
	"ResourceProviderDelete": resource_providers.DeleteHandler,
	"ResourceProviderList":   resource_providers.ListHandler,
}

func BootstrapCmd() *cobra.Command {
	return &cobra.Command{
		Use:   "bootstrap",
		Short: "Bootstrap Wendy's commands from Bob",
		RunE: func(_ *cobra.Command, _ []string) error {
			apiPath := viper.GetString("api_path")

			return pkg.Download(pkg.FullUrl(apiPath), path.Join(apiPath, "api.yaml"))
		},
	}
}
