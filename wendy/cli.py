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

import json

import click

from wendy import commands
from wendy.config import read_config

CONFIG = read_config()


def _respond(response):
    click.echo(json.dumps(response, indent=2, ensure_ascii=False))


@click.group()
def cli():
    pass


@click.command(help="Performs a health check on Bob.")
def can_we_build_it():
    _respond(commands.can_we_build_it(CONFIG))


@click.group(help="Commands to deal with the lifecycle of a pipeline.")
def pipeline():
    pass


@click.command(help="Returns the status of a pipeline.")
@click.option(
    "--group", "-g", required=True, help="Group of the pipeline.", type=str
)
@click.option(
    "--name", "-n", required=True, help="Name of the pipeline.", type=str
)
@click.option(
    "--number",
    "-num",
    required=True,
    help="The run number of the pipeline.",
    type=int,
)
def status(group: str, name: str, number: int):
    _respond(commands.pipeline_status(CONFIG, group, name, number))


pipeline.add_command(status)

cli.add_command(can_we_build_it)
cli.add_command(pipeline)
