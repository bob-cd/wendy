package pipelines

import (
	"bufio"
	"fmt"
	"io"
	"net/url"
	"os"
	"os/signal"
	"strings"

	"github.com/bob-cd/wendy/pkg"
	"github.com/lispyclouds/climate"
	"github.com/spf13/cobra"
)

func ListHandler(opts *cobra.Command, _ []string, data climate.HandlerData) error {
	group, _ := opts.Flags().GetString("group")
	name, _ := opts.Flags().GetString("name")
	status, _ := opts.Flags().GetString("status")

	params := url.Values{}
	if group != "" {
		params.Set("group", group)
	}
	if name != "" {
		params.Set("name", name)
	}
	if status != "" {
		params.Set("status", status)
	}

	res, err := pkg.Get(pkg.FullUrl(data.Path) + "?" + params.Encode())
	if err != nil {
		return err
	}

	return pkg.ShowMessage(res)
}

func LogsHandler(opts *cobra.Command, _ []string, data climate.HandlerData) error {
	follow, _ := opts.Flags().GetBool("follow")
	url := pkg.FullUrl(data.Path)

	if follow {
		url += "?follow=true"
	}

	res, err := pkg.Get(url)
	if err != nil {
		return err
	}

	if !follow {
		_, err = io.Copy(os.Stdout, res)
		return err
	}

	rdr := bufio.NewReader(res)
	c := make(chan os.Signal, 1)
	signal.Notify(c, os.Interrupt)
	go func() {
		<-c
		res.Close()
		os.Exit(0)
	}()

	for {
		b, err := rdr.ReadBytes('\n')
		if err != nil {
			return err
		}

		if len(b) <= 1 {
			continue
		}

		fmt.Println(strings.TrimSpace(string(b)))
	}
}

func StartHandler(_ *cobra.Command, _ []string, data climate.HandlerData) error {
	res, err := pkg.Post(pkg.FullUrl(data.Path), nil)
	if err != nil {
		return err
	}

	return pkg.ShowMessage(res)
}

func StopHandler(_ *cobra.Command, _ []string, data climate.HandlerData) error {
	res, err := pkg.Post(pkg.FullUrl(data.Path), nil)
	if err != nil {
		return err
	}

	return pkg.ShowMessage(res)
}

func RunsHandler(_ *cobra.Command, _ []string, data climate.HandlerData) error {
	res, err := pkg.Get(pkg.FullUrl(data.Path))
	if err != nil {
		return err
	}

	return pkg.ShowMessage(res)
}

func StatusHandler(_ *cobra.Command, _ []string, data climate.HandlerData) error {
	res, err := pkg.Get(pkg.FullUrl(data.Path))
	if err != nil {
		return err
	}

	return pkg.ShowMessage(res)
}

func PauseHandler(_ *cobra.Command, _ []string, data climate.HandlerData) error {
	res, err := pkg.Post(pkg.FullUrl(data.Path), nil)
	if err != nil {
		return err
	}

	return pkg.ShowMessage(res)
}

func UnpauseHandler(_ *cobra.Command, _ []string, data climate.HandlerData) error {
	res, err := pkg.Post(pkg.FullUrl(data.Path), nil)
	if err != nil {
		return err
	}

	return pkg.ShowMessage(res)
}

func ArtifactFetchHandler(opts *cobra.Command, _ []string, data climate.HandlerData) error {
	name, _ := opts.Flags().GetString("artifact-name")
	return pkg.Download(pkg.FullUrl(data.Path), name+".tar")
}
