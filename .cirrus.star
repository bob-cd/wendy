def main():
    return [
        ("container", {"image": "eclipse-temurin:18-jre"}),
        (
            "task",
            {
                "script": "curl -sLO https://raw.githubusercontent.com/babashka/babashka/master/install"
            },
        ),
        ("task", {"script": "bb test"}),
    ]
