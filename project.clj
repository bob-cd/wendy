;   This file is part of Wendy.
;
;   Wendy is free software: you can redistribute it and/or modify
;   it under the terms of the GNU General Public License as published by
;   the Free Software Foundation, either version 3 of the License, or
;   (at your option) any later version.
;
;   Wendy is distributed in the hope that it will be useful,
;   but WITHOUT ANY WARRANTY; without even the implied warranty of
;   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
;   GNU General Public License for more details.
;
;   You should have received a copy of the GNU General Public License
;   along with Wendy. If not, see <http://www.gnu.org/licenses/>.

(defproject wendy "0.1.0"
  :author "Rahul De <rahul@mailbox.org>"
  :url "https://github.com/bob-cd/wendy"
  :description "Bob's reference CLI and his SO"
  :license {:name "GPL 3.0"
            :url  "https://www.gnu.org/licenses/gpl-3.0.en.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [cheshire "5.9.0"]
                 [org.martinklepsch/clj-http-lite "0.4.1"]
                 [net.sourceforge.argparse4j/argparse4j "0.8.1"]
                 [com.electronwill.night-config/toml "3.6.0"]
                 [com.electronwill.night-config/json "3.6.0"]]
  :plugins [[lein-ancient "0.6.15"]
            [io.taylorwood/lein-native-image "0.3.1"]]
  :native-image {:name "wendy"
                 :opts ["--initialize-at-build-time"
                        "--report-unsupported-elements-at-runtime"
                        "--enable-url-protocols=http"
                        "-H:IncludeResourceBundles=net.sourceforge.argparse4j.internal.ArgumentParserImpl"]}
  :main wendy.main
  :target-path "target/%s"
  :global-vars {*warn-on-reflection* true}
  :aliases {"kaocha" ["with-profile" "+kaocha" "run" "-m" "kaocha.runner"]}
  :profiles {:kaocha
             {:dependencies [[lambdaisland/kaocha "0.0-554"]]}
             :uberjar
             {:aot          :all
              :native-image {:jvm-opts ["-Dclojure.compiler.direct-linking=true"]}}})
