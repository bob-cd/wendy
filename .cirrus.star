def main():
    return [
        ("env", {"CIRRUS_WORKING_DIR": "/root"}),
        ("container", {"image": "clojure:temurin-18-tools-deps-focal"}),
        (
            "task",
            {
                "deps_cache": {
                    "folders": [
                        ".m2/",
                        ".gitlibs/",
                    ],
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
