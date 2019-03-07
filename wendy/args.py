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

from argparse import ArgumentParser
from functools import partial

from wendy.commands import dispatch
from wendy.config import read_config


def make_arg_parser() -> ArgumentParser:
    config = read_config("defaults.toml")

    parser = ArgumentParser(description="Wendy: Bob's CLI and best friend")
    subparsers = parser.add_subparsers()

    ping_parser = subparsers.add_parser("can-we-build-it")
    host = config["connection"]["host"]
    port = config["connection"]["port"]
    ping_parser.add_argument(
        "url",
        type=str,
        nargs="?",
        default=f"http://{host}:{port}/api/can-we-build-it",
    )
    dispatcher = partial(dispatch, "can-we-build-it")
    ping_parser.set_defaults(func=dispatcher)

    return parser
