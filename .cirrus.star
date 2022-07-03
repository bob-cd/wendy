def main():
    return [
        ("container", {"image": "eclipse-temurin:18-jre"}),
        (
            "task",
            {
                "script": "bash < <(curl -s https://raw.githubusercontent.com/babashka/babashka/master/install) && bb test"
            },
        ),
    ]
