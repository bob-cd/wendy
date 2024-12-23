package pkg

import (
	"encoding/json"
	"fmt"
	"io"
	"log/slog"
	"net/http"
	"net/url"
	"os"

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

func Download(url, path string) error {
	slog.Info("Downloading", "fileName", path)

	res, err := http.Get(url)
	if err != nil {
		return err
	}

	if res.StatusCode >= 400 {
		b, _ := io.ReadAll(res.Body)
		return fmt.Errorf("Staus %d Response %s", res.StatusCode, string(b))
	}

	w, err := os.Create(path)
	if err != nil {
		return err
	}
	_, err = io.Copy(w, res.Body)
	if err != nil {
		return err
	}

	w.Sync()
	w.Close()

	slog.Info("Complete")

	return nil
}
