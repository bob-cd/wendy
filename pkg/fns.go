package pkg

import (
	"encoding/json"
	"fmt"
	"io"
	"net/url"

	"github.com/spf13/viper"
)

func ShowMessage(rdr io.Reader) error {
	var decoded map[string]any

	dec := json.NewDecoder(rdr)
	if err := dec.Decode(&decoded); err != nil {
		return err
	}

	toPrint, err := json.MarshalIndent(decoded["message"], "", "  ")
	if err != nil {
		return err
	}

	fmt.Println(string(toPrint))

	return nil
}

func FullUrl(path string) string {
	joined, _ := url.JoinPath(viper.GetString("endpoint"), path)
	return joined
}
