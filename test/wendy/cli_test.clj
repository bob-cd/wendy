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
               (.get result "config")))))
    (testing "pipeline start"
      (let [args   (into-array String ["pipeline"
                                       "start"
                                       "-g"
                                       "dev"
                                       "-n"
                                       "test"])
            result (.parseArgs parser args)]
        (is (= "start" (.get result "lifecycle-cmd")))
        (is (= "dev" (.get result "group")))
        (is (= "test" (.get result "name")))))
    (testing "pipeline status"
      (let [args   (into-array String ["pipeline"
                                       "status"
                                       "-g"
                                       "dev"
                                       "-n"
                                       "test"
                                       "-num"
                                       "1"])
            result (.parseArgs parser args)]
        (is (= "status" (.get result "lifecycle-cmd")))
        (is (= "dev" (.get result "group")))
        (is (= "test" (.get result "name")))
        (is (= "1" (.get result "number")))))
    (testing "pipeline stop"
      (let [args   (into-array String ["pipeline"
                                       "stop"
                                       "-g"
                                       "dev"
                                       "-n"
                                       "test"
                                       "-num"
                                       "1"])
            result (.parseArgs parser args)]
        (is (= "stop" (.get result "lifecycle-cmd")))
        (is (= "dev" (.get result "group")))
        (is (= "test" (.get result "name")))
        (is (= "1" (.get result "number")))))
    (testing "pipeline logs"
      (let [args   (into-array String ["pipeline"
                                       "logs"
                                       "-g"
                                       "dev"
                                       "-n"
                                       "test"
                                       "-num"
                                       "1"
                                       "-o"
                                       "10"
                                       "-l"
                                       "100"])
            result (.parseArgs parser args)]
        (is (= "logs" (.get result "lifecycle-cmd")))
        (is (= "dev" (.get result "group")))
        (is (= "test" (.get result "name")))
        (is (= "1" (.get result "number")))
        (is (= "10" (.get result "offset")))
        (is (= "100" (.get result "lines")))))
    (testing "pipeline delete"
      (let [args   (into-array String ["pipeline"
                                       "delete"
                                       "-g"
                                       "dev"
                                       "-n"
                                       "test"])
            result (.parseArgs parser args)]
        (is (= "delete" (.get result "lifecycle-cmd")))
        (is (= "dev" (.get result "group")))
        (is (= "test" (.get result "name")))))))
