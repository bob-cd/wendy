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
	"github.com/urfave/cli/v3"
)

func ListHandler(opts *cli.Command, _ []string, data climate.HandlerData) error {
	group := opts.String("group")
	name := opts.String("name")
	status := opts.String("status")

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

func LogsHandler(opts *cli.Command, _ []string, data climate.HandlerData) error {
	follow := opts.Bool("follow")
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

func StartHandler(_ *cli.Command, _ []string, data climate.HandlerData) error {
	res, err := pkg.Post(pkg.FullUrl(data.Path), nil)
	if err != nil {
		return err
	}

	return pkg.ShowMessage(res)
}

func StopHandler(_ *cli.Command, _ []string, data climate.HandlerData) error {
	res, err := pkg.Post(pkg.FullUrl(data.Path), nil)
	if err != nil {
		return err
	}

	return pkg.ShowMessage(res)
}

func RunsHandler(_ *cli.Command, _ []string, data climate.HandlerData) error {
	res, err := pkg.Get(pkg.FullUrl(data.Path))
	if err != nil {
		return err
	}

	return pkg.ShowMessage(res)
}

func StatusHandler(_ *cli.Command, _ []string, data climate.HandlerData) error {
	res, err := pkg.Get(pkg.FullUrl(data.Path))
	if err != nil {
		return err
	}

	return pkg.ShowMessage(res)
}

func PauseHandler(_ *cli.Command, _ []string, data climate.HandlerData) error {
	res, err := pkg.Post(pkg.FullUrl(data.Path), nil)
	if err != nil {
		return err
	}

	return pkg.ShowMessage(res)
}

func UnpauseHandler(_ *cli.Command, _ []string, data climate.HandlerData) error {
	res, err := pkg.Post(pkg.FullUrl(data.Path), nil)
	if err != nil {
		return err
	}

	return pkg.ShowMessage(res)
}

func ArtifactFetchHandler(opts *cli.Command, _ []string, data climate.HandlerData) error {
	return pkg.Download(pkg.FullUrl(data.Path), opts.String("artifact-name")+".tar")
}
