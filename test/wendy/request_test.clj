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

(ns wendy.request-test
  (:require [clojure.test :refer :all]
            [wendy.request :refer :all]))

(deftest extract-params-test
  (testing "Correct extraction of path params"
    (is (= {:path-params  {:foo 1
                           :bar "bar"}
            :query-params {}
            :body-param   {}}
           (extract-params
             {:foo        1
              :bar        "bar"
              :_arguments []}
             '({:as      "Foo"
                :option  "foo"
                :type    :int
                :in      "path"
                :default :present}
               {:as      "Bar"
                :option  "bar"
                :type    :string
                :in      "path"
                :default :present})))))
  (testing "Correct extraction of query params"
    (is (= {:path-params  {:foo "foo"
                           :bar "bar"}
            :query-params {:baz 1
                           :meh "meh"}
            :body-param   {}}
           (extract-params
             {:foo        "foo"
              :bar        "bar"
              :baz        1
              :meh        "meh"
              :_arguments []}
             '({:as      "Foo"
                :option  "foo"
                :type    :string
                :in      "path"
                :default :present}
               {:as      "Bar"
                :option  "bar"
                :type    :string
                :in      "path"
                :default :present}
               {:as      "Baz"
                :option  "baz"
                :type    :int
                :in      "query"
                :default :present}
               {:as      "Meh"
                :option  "meh"
                :type    :string
                :in      "query"
                :default :present})))))
  (testing "Correct extraction of body param"
    (is (= {:path-params  {:foo "foo"
                           :bar "bar"}
            :query-params {:baz 1
                           :meh "meh"}
            :body-param   {:data {:foo "bar"}}}
           (extract-params
             {:foo        "foo"
              :bar        "bar"
              :baz        1
              :meh        "meh"
              :data       {:foo "bar"}
              :_arguments []}
             '({:as      "Foo"
                :option  "foo"
                :type    :string
                :in      "path"
                :default :present}
               {:as      "Bar"
                :option  "bar"
                :type    :string
                :in      "path"
                :default :present}
               {:as      "Baz"
                :option  "baz"
                :type    :int
                :in      "query"
                :default :present}
               {:as      "Meh"
                :option  "meh"
                :type    :string
                :in      "query"
                :default :present}
               {:as      "Body"
                :option  "data"
                :type    :slurp
                :in      "body"
                :default :present}))))))
