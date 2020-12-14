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
  (:import (java.io File)))

(deftest test-extract-opts
  (testing "Extract options from parsed yaml"
    (is (= {:as "The group of the pipeline",
            :option "group",
            :type :string,
            :in "query"}
           (extract-opts
             {"name" "group",
              "required" false,
              "in" "query",
              "description" "The group of the pipeline",
              "schema" {"type" "string"}})))
    (is (= {:as "Foo description",
            :option "name",
            :type :foo,
            :in "query",
            :default :present}
           (extract-opts
             {"name" "name",
              "required" true,
              "in" "query",
              "description" "Foo description",
              "schema" {"type" "foo"}})))
    (is (= {:as "The group of the pipeline",
            :option "status",
            :type :string,
            :in "path",
            :default :present}
           (extract-opts
             {"name" "status",
              "required" true,
              "in" "path",
              "description" "The group of the pipeline",
              "schema" {"type" "string"}})))))

(deftest test-extract-body-opts
  (testing "Extract body option from parsed yaml"
    (is (= '({:as "foo",
              :option "data",
              :type :slurp,
              :in "body",
              :default :present})
           (extract-body-opt {"requestBody" {"description" "foo"}} '())))))


(deftest test-extract-subcommand
  (testing "Extracting subcommands"
    (is (= {:method :get,
            :command "foo",
            :path "/foo",
            :description "foo summary",
            :opts
            '({:as "The group of the pipeline",
               :option "status",
               :type :string,
               :in "path",
               :default :present})}
           (dissoc (extract-subcommand "/foo" (clojure.lang.MapEntry. :get {"operationId" "foo"
                                                                            "summary" "foo summary"
                                                                            "parameters" '({"name" "status",
                                                                                            "required" true,
                                                                                            "in" "path",
                                                                                            "description" "The group of the pipeline",
                                                                                            "schema" {"type" "string"}})}))
                   :runs)))
    (is (= {:method :get
            :command "foo",
            :path "/foo",
            :description "foo summary",
            :opts
            '({:as "The group of the pipeline",
               :option "foo",
               :type :string,
               :in "query",})}
           (dissoc (extract-subcommand "/foo" (clojure.lang.MapEntry. :get {"operationId" "foo"
                                                                            "summary" "foo summary"
                                                                            "parameters" '({"name" "foo",
                                                                                            "in" "query",
                                                                                            "description" "The group of the pipeline",
                                                                                            "schema" {"type" "string"}})}))
                   :runs)))
    (is (= {:method :post,
            :command "foo",
            :path "/foo",
            :description "foo summary",
            :opts
            '({:as "foo body",
               :option "data",
               :type :slurp,
               :in "body",
               :default :present}
              {:as "The group of the pipeline",
               :option "status",
               :type :string,
               :in "path",
               :default :present})}
           (dissoc (extract-subcommand "/foo" (clojure.lang.MapEntry. :post {"operationId" "foo"
                                                                             "summary" "foo summary"
                                                                             "parameters" '({"name" "status",
                                                                                             "required" true,
                                                                                             "in" "path",
                                                                                             "description" "The group of the pipeline",
                                                                                             "schema" {"type" "string"}})
                                                                             "requestBody" {"description" "foo body"}}))
                   :runs)))))
