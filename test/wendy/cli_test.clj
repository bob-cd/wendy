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

(ns wendy.cli-test
  (:require [clojure.test :refer :all]
            [wendy.cli :refer :all])
  (:import (java.io File)
           (net.sourceforge.argparse4j.inf ArgumentParser)))

(deftest test-top-level-commands
  (let [parser ^ArgumentParser (configured-parser)]
    (testing "health check"
      (let [args   (into-array String ["can-we-build-it"])
            result (.parseArgs parser args)]
        (is (= "can-we-build-it"
               (.get result "command")))))
    (testing "pipeline create default"
      (let [args   (into-array String ["pipeline"
                                       "create"])
            result (.parseArgs parser args)]
        (is (= "pipeline"
               (.get result "command")))
        (is (= "create"
               (.get result "lifecycle-cmd")))
        (is (= (str (System/getProperty "user.dir")
                    File/separator
                    "build.toml")
               (.get result "config")))))
    (testing "pipeline create with file"
      (let [args   (into-array String ["pipeline"
                                       "create"
                                       "-c"
                                       "/tmp/build.toml"])
            result (.parseArgs parser args)]
        (is (= "/tmp/build.toml"
               (.get result "config")))))))
