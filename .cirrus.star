def main():
    return [
        ("container", {"image": "clojure:temurin-18-tools-deps-focal"}),
        (
            "task",
            {
                "script": "bash < <(curl -s https://raw.githubusercontent.com/babashka/babashka/master/install) && bb test"
            },
        ),
    ]
