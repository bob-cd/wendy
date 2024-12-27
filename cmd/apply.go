package cmd

import (
	"bytes"
	"encoding/json"
	"errors"
	"fmt"
	"log/slog"
	"net/url"
	"os"
	"reflect"

	"github.com/bob-cd/wendy/pkg"
	"github.com/pb33f/libopenapi"
	v3 "github.com/pb33f/libopenapi/datamodel/high/v3"
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

type manifest struct {
	ApiVersion   string         `yaml:"apiVersion"`
	Kind         string         `yaml:"kind"`
	IdentifiedBy []string       `yaml:"identifiedBy"`
	Spec         map[string]any `yaml:"spec"`
}

type Object map[string]any
type Model = libopenapi.DocumentModel[v3.Document]

// TODO: Any better way to do this? Maybe via climate?
func getOpInfo(model *Model, opId string) *string {
	for path, item := range model.Model.Paths.PathItems.FromOldest() {
		for _, op := range item.GetOperations().FromOldest() {
			if op.OperationId == opId {
				return &path
			}
		}
	}

	return nil
}

func getObject(model *Model, kind string, indentifiers map[string]string) (Object, error) {
	opId := conf[kind]["ListOp"]
	path := getOpInfo(model, opId)
	if path == nil {
		return nil, errors.New("Cannot find path for " + opId)
	}

	params := url.Values{}
	for k, v := range indentifiers {
		params.Set(k, v)
	}

	res, err := pkg.Get(pkg.FullUrl(*path) + "?" + params.Encode())
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

func different(obj1, obj2 map[string]any) bool {
	// TODO: Maybe show the diff?
	return !reflect.DeepEqual(obj1, obj2)
}

func apply(model *Model, manifestFile string) error {
	f, err := os.Open(manifestFile)
	if err != nil {
		return err
	}
	defer f.Close()

	var parsed manifest
	dec := yaml.NewDecoder(f)
	if err := dec.Decode(&parsed); err != nil {
		return err
	}

	if _, ok := conf[parsed.Kind]; !ok {
		return errors.New("Unsupported kind: " + parsed.Kind)
	}

	ids := make(map[string]string)
	for _, id := range parsed.IdentifiedBy {
		ids[id] = parsed.Spec[id].(string)
	}

	object, err := getObject(model, parsed.Kind, ids)
	if err != nil {
		return err
	}

	if different(object, parsed.Spec) {
		buffer := bytes.Buffer{}
		enc := json.NewEncoder(&buffer)
		if err := enc.Encode(&parsed.Spec); err != nil {
			return err
		}

		opId := conf[parsed.Kind]["CreateOp"]
		path := getOpInfo(model, opId)
		if path == nil {
			return errors.New("Cannot find path for " + opId)
		}

		_, err = pkg.Post(pkg.FullUrl(*path), &buffer)
		if err != nil {
			return err
		}

		slog.Info("Updated", "kind", parsed.Kind, "ids", fmt.Sprint(ids))

		return nil
	}

	slog.Info("Unchanged")

	return nil
}

func ApplyCmd(model *Model) *cobra.Command {
	cmd := cobra.Command{
		Use:   "apply",
		Short: "Declaratively apply manifests",
		RunE: func(cmd *cobra.Command, _ []string) error {
			manifestFile, _ := cmd.Flags().GetString("manifest")
			return apply(model, manifestFile)
		},
	}

	cmd.Flags().StringP("manifest", "m", "", "The manifest file to apply")

	return &cmd
}
