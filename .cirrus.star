def main():
    return [
        ("container", {"image": "clojure:temurin-18-tools-deps-focal"}),
        (
            "task",
            {
                "deps_cache": {
                    "folder": "~/.m2/",
                    "reupload_on_changes": False,
                    "fingerprint_script": [
                        "cat bb.edn",
                    ],
                },
                "download_script": "bash < <(curl -s https://raw.githubusercontent.com/babashka/babashka/master/install)",
                "test_script": "bb test",
            },
        ),
    ]
