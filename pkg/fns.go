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

func checkStatus(res *http.Response) error {
	if status := res.StatusCode; status >= 400 {
		b, _ := io.ReadAll(res.Body)
		return fmt.Errorf("Invalid call: %s Status: %d", string(b), status)
	}

	return nil
}

func Get(url string) (io.ReadCloser, error) {
	res, err := http.Get(url)
	if err != nil {
		return nil, err
	}

	if err = checkStatus(res); err != nil {
		return nil, err
	}

	return res.Body, nil
}

func Post(url string, body io.Reader) (io.Reader, error) {
	res, err := http.Post(url, "application/json", body)
	if err != nil {
		return nil, err
	}

	if err = checkStatus(res); err != nil {
		return nil, err
	}

	return res.Body, nil
}

func Delete(url string) (io.Reader, error) {
	req, err := http.NewRequest("DELETE", url, nil)
	if err != nil {
		return nil, err
	}

	res, err := http.DefaultClient.Do(req)
	if err != nil {
		return nil, err
	}

	if err = checkStatus(res); err != nil {
		return nil, err
	}

	return res.Body, nil
}

func Download(url, path string) error {
	res, err := Get(url)
	if err != nil {
		return err
	}

	slog.Info("Downloading", "fileName", path)

	w, err := os.Create(path)
	if err != nil {
		return err
	}
	_, err = io.Copy(w, res)
	if err != nil {
		return err
	}

	w.Sync()
	w.Close()

	slog.Info("Complete")

	return nil
}
