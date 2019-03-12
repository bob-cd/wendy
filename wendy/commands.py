# This file is part of Wendy.
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as
# published by the Free Software Foundation, either version 3 of the
# License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.

import requests
import toml


def _base_url_from(config: dict):
    host = config["connection"]["host"]
    port = config["connection"]["port"]

    return f"http://{host}:{port}/api"


def can_we_build_it(config: dict):
    url = f"{_base_url_from(config)}/can-we-build-it"

    return requests.get(url).json()["message"]


def pipeline_create(config: dict, build_file: str):
    build = toml.load(build_file, _dict=dict)
    pipelines = [
        {"group": group, "name": name, "data": data}
        for group, pipeline in build.items()
        for name, data in pipeline.items()
    ]

    for pipeline in pipelines:
        group, name = pipeline["group"], pipeline["name"]

        url_parts = (_base_url_from(config), "pipeline", group, name)
        url = "/".join(url_parts)
        res = requests.post(url, json=pipeline["data"])

        if not res.ok:
            return f"Creation failed for {name} in {group}. Reason: {res.text}"

    return "Ok"


def pipeline_status(config: dict, group: str, name: str, number: int):
    url = f"{_base_url_from(config)}/pipeline/status/{group}/{name}/{number}"

    return requests.get(url).json()["message"]
