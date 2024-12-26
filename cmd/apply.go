package cmd

import (
	"bytes"
	"encoding/json"
	"errors"
	"fmt"
	"log/slog"
	"net/url"
	"os"
	"path"

	"github.com/bob-cd/wendy/pkg"
	"github.com/lispyclouds/climate"
	"github.com/spf13/cobra"
	"gopkg.in/yaml.v3"
)

var conf = map[string]map[string]string{
	"Pipeline": {
		"ListOp":   "PipelineList",
		"CreateOp": "PipelineCreate",
	},
	"ResourceProvider": {
		"ListOp":   "ResourceProviderList",
		"CreateOp": "ResourceProviderCreate",
	},
	"ArtifactStore": {
		"ListOp":   "ArtifactStoreList",
		"CreateOp": "ArtifactStoreCreate",
	},
}

type opInfo struct {
	path string
}

type manifest struct {
	ApiVersion   string         `yaml:"apiVersion"`
	Kind         string         `yaml:"kind"`
	IdentifiedBy []string       `yaml:"identifiedBy"`
	Spec         map[string]any `yaml:"spec"`
}

type Object map[string]any

// TODO: Any better way to do this? Maybe via climate?
func getOpInfo(opId string) (*opInfo, error) {
	apiDir, err := pkg.GetApiDir()
	if err != nil {
		return nil, err
	}
	apiPath := path.Join(apiDir, "api.yaml")

	_, err = os.Stat(apiPath)
	if os.IsNotExist(err) {
		return nil, errors.New("Wendy is not bootstrapped, please run the boostrap command")
	}

	model, err := climate.LoadFileV3(apiPath)
	if err != nil {
		return nil, err
	}

	for path, item := range model.Model.Paths.PathItems.FromOldest() {
		for _, op := range item.GetOperations().FromOldest() {
			if op.OperationId == opId {
				return &opInfo{path: path}, nil
			}
		}
	}

	return nil, errors.New("Cannot find " + opId)
}

func getObject(kind string, indentifiers map[string]string) (Object, error) {
	ops, ok := conf[kind]
	if !ok {
		return nil, errors.New("Unsupported kind: " + kind)
	}

	opInfo, err := getOpInfo(ops["ListOp"])
	if err != nil {
		return nil, err
	}

	params := url.Values{}
	for k, v := range indentifiers {
		params.Set(k, v)
	}

	res, err := pkg.Get(pkg.FullUrl(opInfo.path) + "?" + params.Encode())
	if err != nil {
		return nil, err
	}

	var response map[string][]Object
	dec := json.NewDecoder(res)
	if err := dec.Decode(&response); err != nil {
		return nil, err
	}

	objects := response["message"]
	if len(objects) == 0 {
		return nil, nil
	}

	return objects[0], nil
}

func different(obj1, obj2 any) bool {
	// TODO: Possibly better
	return !(fmt.Sprint(obj1) == fmt.Sprint(obj2))
}

func apply(cmd *cobra.Command, _ []string) error {
	m, _ := cmd.Flags().GetString("manifest")

	f, err := os.Open(m)
	if err != nil {
		return err
	}
	defer f.Close()

	var parsed manifest
	dec := yaml.NewDecoder(f)
	if err := dec.Decode(&parsed); err != nil {
		return err
	}

	ids := make(map[string]string)
	for _, id := range parsed.IdentifiedBy {
		ids[id] = parsed.Spec[id].(string)
	}

	object, err := getObject(parsed.Kind, ids)
	if err != nil {
		return err
	}

	if different(object, parsed.Spec) {
		buffer := bytes.Buffer{}
		enc := json.NewEncoder(&buffer)
		if err := enc.Encode(&parsed.Spec); err != nil {
			return err
		}

		ops, ok := conf[parsed.Kind]
		if !ok {
			return errors.New("Unsupported kind: " + parsed.Kind)
		}

		opInfo, err := getOpInfo(ops["CreateOp"])
		if err != nil {
			return err
		}

		_, err = pkg.Post(pkg.FullUrl(opInfo.path), &buffer)
		if err != nil {
			return err
		}

		slog.Info("Updated", "kind", parsed.Kind, "ids", fmt.Sprint(ids))

		return nil
	}

	slog.Info("Unchanged")

	return nil
}

func ApplyCmd() *cobra.Command {
	cmd := cobra.Command{
		Use:   "apply",
		Short: "Declaratively apply manifests",
		RunE:  apply,
	}

	cmd.Flags().StringP("manifest", "m", "", "The manifest file to apply")

	return &cmd
}
